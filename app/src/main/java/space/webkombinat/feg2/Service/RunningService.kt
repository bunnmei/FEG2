package space.webkombinat.feg2.Service

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC
import android.hardware.usb.UsbConstants
import android.hardware.usb.UsbDevice
import android.hardware.usb.UsbEndpoint
import android.hardware.usb.UsbInterface
import android.hardware.usb.UsbManager
import android.hardware.usb.UsbRequest
import android.os.Binder
import android.os.Build
import android.os.IBinder
import androidx.annotation.RequiresApi
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.currentComposer
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runInterruptible
import space.webkombinat.feg2.DB.Chart.ChartEntity
import space.webkombinat.feg2.DB.Chart.ChartRepository
import space.webkombinat.feg2.DB.Profile.ProfileEntity
import space.webkombinat.feg2.DB.Profile.ProfileRepository
import space.webkombinat.feg2.Data.ChartDataState
import space.webkombinat.feg2.Data.Constants.MAX_TEMP
import space.webkombinat.feg2.Data.Constants.MIN_TEMP
import space.webkombinat.feg2.Data.Constants.MSG_BYTE_ARRAY
import space.webkombinat.feg2.Data.Constants.REQUEST_TYPE
import space.webkombinat.feg2.Data.Constants.TDR
import space.webkombinat.feg2.Data.Constants.TIMEOUT
import space.webkombinat.feg2.Data.Constants.USB_PERMISSION
import space.webkombinat.feg2.Data.LoggerState
import space.webkombinat.feg2.R
import java.util.Timer
import javax.inject.Inject
import kotlin.concurrent.fixedRateTimer
import kotlin.math.log
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

@AndroidEntryPoint
class RunningService: Service() {
    @Inject lateinit var repoP: ProfileRepository
    @Inject lateinit var repoC: ChartRepository
    @Inject lateinit var loggerState: LoggerState
    @Inject lateinit var notificationManager: NotificationManager
    @Inject lateinit var notificationBuilder: NotificationCompat.Builder

    private var time: Duration = Duration.ZERO
    private lateinit var timer: Timer

    private val receiver = USBPermissionReceiver()
    private val usbController = USBController()
    var timeString = mutableStateOf("00:00")
        private set
    var tempString = mutableStateOf(0)
    var tempString_BT = mutableStateOf(0)

    var correctTemp = mutableStateListOf<Int>()
    var correctTemp_BT = mutableStateListOf<Int>()

    var lineChart = mutableStateListOf<Line>()
    var lineChart_BT = mutableStateListOf<Line>()
    var thread: Thread? = null

    inner class TimerAndTemp: Binder() {
        fun getService(): RunningService = this@RunningService
    }

    override fun onBind(p0: Intent?): IBinder? {
        return TimerAndTemp()
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when(intent?.action) {
            Action.USB_START.toString() -> usb_start()
            Action.USB_STOP.toString() -> usb_stop()
            Action.USB_DISCONNECT.toString() -> usb_stop()
            Action.USB_CONNECT.toString() -> usb_connect()
            Action.TIMER_START.toString() -> {
                timer_start()
            }
            Action.TIMER_STOP.toString() -> timer_stop()
            Action.KEEP.toString() -> repo()
            Action.CLEAR_ALL.toString() -> clearAll()
            Action.NOTIFICATION_STOP.toString() -> {
                keeping()
            }
            Action.CLACK_F.toString() -> {
                clack("F")
            }
            Action.CLACK_S.toString() -> {
                clack("S")
            }
        }

        return super.onStartCommand(intent, flags, startId)
    }

    private fun clack(f: String) {
        if (f == "F"){
            time.toComponents { hours, minutes, seconds, nanoseconds ->
                loggerState.setClackF((minutes * 60) + seconds)
            }
        } else {
            time.toComponents { hours, minutes, seconds, nanoseconds ->
                loggerState.setClackS((minutes * 60) + seconds)
            }
        }
    }
    private fun usb_start() {
        val usbState = loggerState.loadUsb()
        if (usbState.value){
            return
        }
        val usbManager = getSystemService(USB_SERVICE) as UsbManager
        usbController.requestPermission(usbManager, applicationContext)
   }

    private fun usb_stop() {
        loggerState.usbDisConnect()
        try {
            println("usb_stopが呼ばれた")
            usbController.close()
            println("closeが呼ばれたよ")
            thread?.interrupt()
            println("interruptが呼ばれたよ")
            thread = null
        } catch (e:Exception){
            println("usb_stopError $e")
        }
    }

    private fun updateForeground() {
        notificationManager.notify(
            1,
            notificationBuilder
                .setContentText("時間 ${timeString.value} 温度 ${tempString.value}")
                .build()
        )
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun timer_start() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "running_channel",
                "Running Notifications",
                NotificationManager.IMPORTANCE_MIN
            )
            notificationManager.createNotificationChannel(channel)
        }
        startForeground(1, notificationBuilder.build(), FOREGROUND_SERVICE_TYPE_DATA_SYNC)

        val ctx = applicationContext.resources
        val screenHeight = ctx.displayMetrics.heightPixels.toFloat()

        timer = fixedRateTimer(initialDelay = 1000L, period = 1000L) {
            time = time.plus(1.seconds)
            time.toComponents { hours, minutes, seconds, nanoseconds ->
//                println("${minutes.pad()}:${seconds.pad()}")
                this@RunningService.timeString.value = "${minutes.pad()}:${seconds.pad()}"
                make_list(screenHeight)
                updateForeground()
            }
        }
        loggerState.stopWatchStart()
        loggerState.dataUnsaved()
    }

    private fun keeping () {
        println("notificationが呼ばれたよ")
        val dataState = loggerState.loadDataState()
        if (dataState.value == ChartDataState.Saving){
            return
        }
        if(this::timer.isInitialized && dataState.value == ChartDataState.Unsaved){
            timer_stop()
            repo()
        }
    }

    private fun timer_stop() {
        loggerState.stopWatchStop()
        timer.cancel()
        notificationManager.cancel(1)
        stopForeground(STOP_FOREGROUND_REMOVE)
    }
    private fun usb_connect(){
        loggerState.usbConnect()
        usbController.connect()
        if (thread == null){
            thread = Thread {

                while (usbController.checkConnection()){
                    val num = usbController.read(500)
                    if (num != null && num.second != null && num.first !=null){
                        tempString.value = num.first!!
                        tempString_BT.value = num.second!!
                    }
                    try {
                        Thread.sleep(1000)
                    } catch (e: Exception){
                        println(e)
                    }

                }
            }
            thread!!.start()
        }
    }

    private fun repo() {
        val scope = CoroutineScope(Dispatchers.IO)

        loggerState.dataSaving()
        scope.launch {
            if(correctTemp.isNotEmpty()){
                try {
                    val profile = ProfileEntity(
                        id = 0,
                        name = null,
                        description = null,
                        clack_f = loggerState.loadClackState().value.first,
                        clack_s = loggerState.loadClackState().value.second,
                        createAt = System.currentTimeMillis()
                    )

                    val id = repoP.insertProfile(profile)

                    correctTemp.forEachIndexed{ index, temp ->

                        println("保存1")

                        val ETandBT_temp = temp * 1000 + correctTemp_BT[index]
                        println("${ETandBT_temp} hozon")

                        val char = ChartEntity(
                            id = 0,
                            profileId = id,
                            point_index = index,
                            temp = ETandBT_temp
                        )

                        val num = repoC.insertChart(char)
                        if (num == (correctTemp.size -1)){
                            loggerState.dataSaved()
                        }
//                        println("保存3 $num -- ${correctTemp.size}")
//                        if (index == (correctTemp.size -1)){
//                            loggerState.dataSaved()
//                        }
                    }
                } catch (e: Exception) {
                    println("保存中にエラー ${e}")
                }
            }
        }

    }

    private fun clearAll() {
        time = Duration.ZERO
        timeString.value = "00:00"
        tempString.value = 0
        correctTemp.clear()
        correctTemp_BT.clear()
        lineChart.clear()
        lineChart_BT.clear()
        loggerState.stopWatchIdle()
        loggerState.dataClear()
        stopSelf()
    }
    private fun make_list(screenHeight: Float){
        correctTemp.add(tempString.value)
        correctTemp_BT.add(tempString_BT.value)
        val one_temp_range = screenHeight / (MAX_TEMP - MIN_TEMP)
        if(lineChart.isEmpty()){
            val old_x = 0f
            val old_y = screenHeight - ((correctTemp.last() - 70)* one_temp_range)

            val line = Line(
                start = Offset(old_x, old_y),
                end = Offset(old_x, old_y),
            )
            lineChart.add(line)
        } else {
            val old_x = (lineChart.size - 1) * 5f
            val old_y = screenHeight - ((correctTemp[correctTemp.lastIndex - 1] - 70) * one_temp_range)
            val new_x = (lineChart.size) * 5f
            val new_y = screenHeight - ((correctTemp.last() - 70) * one_temp_range)

            val line = Line(
                start = Offset(old_x, old_y),
                end = Offset(new_x, new_y)
            )
            lineChart.add(line)
        }

        if (lineChart_BT.isEmpty()) {
            val old_x = 0f
            val old_y = screenHeight - ((correctTemp_BT.last() - 70)* one_temp_range)

            val line = Line(
                start = Offset(old_x, old_y),
                end = Offset(old_x, old_y),
            )
            lineChart_BT.add(line)
        } else {
            val old_x = (lineChart_BT.size - 1) * 5f
            val old_y = screenHeight - ((correctTemp_BT[correctTemp_BT.lastIndex - 1] - 70) * one_temp_range)
            val new_x = (lineChart_BT.size) * 5f
            val new_y = screenHeight - ((correctTemp_BT.last() - 70) * one_temp_range)

            val line = Line(
                start = Offset(old_x, old_y),
                end = Offset(new_x, new_y)
            )
            lineChart_BT.add(line)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("UnspecifiedRegisterReceiverFlag")
    override fun onCreate() {
        super.onCreate()
        val filter = IntentFilter().apply {
            addAction(USB_PERMISSION)
            addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED)
            addAction(UsbManager.ACTION_USB_DEVICE_DETACHED)
        }
        registerReceiver(receiver, filter, RECEIVER_NOT_EXPORTED)
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(receiver)
    }

    enum class Action {
        USB_START,
        USB_STOP,
        TIMER_START,
        TIMER_STOP,
        USB_CONNECT,
        USB_DISCONNECT,
        KEEP,
        CLEAR_ALL,
        NOTIFICATION_STOP,
        CLACK_F,
        CLACK_S,
    }

    private fun Int.pad(): String {
        return this.toString().padStart(2, '0')
    }
}

data class Line(
    val start: Offset,
    val end: Offset,
    val color: Color = Color.Cyan.copy(blue = 0.5f),
    val strokeWidth: Dp = 1.dp
)
