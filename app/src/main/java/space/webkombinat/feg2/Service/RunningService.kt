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
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import space.webkombinat.feg2.DB.Chart.ChartEntity
import space.webkombinat.feg2.DB.Chart.ChartRepository
import space.webkombinat.feg2.DB.Profile.ProfileEntity
import space.webkombinat.feg2.DB.Profile.ProfileRepository
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

    private lateinit var usbManager: UsbManager
    private lateinit var usbInterface: UsbInterface
    private var endpointIn: UsbEndpoint? = null
    private var endpointOut: UsbEndpoint? = null
    private var thread: Thread? = null

    var timeString = mutableStateOf("00:00")
        private set

    var tempString = mutableStateOf(0)
    var correctTemp = mutableStateListOf<Int>()

    var lineChart = mutableStateListOf<Line>()
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
            Action.TIMER_START.toString() -> timer_start()
            Action.TIMER_STOP.toString() -> timer_stop()
            Action.CLEAR_ALL.toString() -> clearAll()
            Action.KEEP.toString() -> repo()
        }

        return super.onStartCommand(intent, flags, startId)
    }

    private fun usb_start() {
        val usbState = loggerState.loadUsb()
        if (usbState.value){
            return
        }
        usbManager = getSystemService(USB_SERVICE) as UsbManager

        println("usbmane  ------- $usbManager")
        val ctx = applicationContext
        val usbPermission = PendingIntent.getBroadcast(
            ctx,
            0,
            Intent(USB_PERMISSION),
            PendingIntent.FLAG_IMMUTABLE
        )
        if (usbManager.deviceList.isNotEmpty()){

            val device = usbManager.deviceList.values.first()
            usbManager.requestPermission(device, usbPermission)
        } else {
            println("デバイスが接続されていません。")
        }
   }

    private fun usb_stop() {
        loggerState.usbDisConnect()
        thread?.interrupt()
        thread = null
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
        loggerState.stopWatchSleep()
        loggerState.stopWatchStop()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "running_channel",
                "Running Notifications",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager.createNotificationChannel(channel)
        }
        startForeground(1, notificationBuilder.build(), FOREGROUND_SERVICE_TYPE_DATA_SYNC)


        timer = fixedRateTimer(initialDelay = 1000L, period = 1000L) {
            time = time.plus(1.seconds)
            time.toComponents { hours, minutes, seconds, nanoseconds ->
                println("${minutes.pad()}:${seconds.pad()}")
                this@RunningService.timeString.value = "${minutes.pad()}:${seconds.pad()}"
                make_list()
                updateForeground()
            }
        }
    }



    private fun timer_stop() {
        loggerState.stopWatchSleepStop()
        loggerState.saved()
        loggerState.cleared()
        loggerState.stopWatchSleep()
        timer.cancel()
    }
    private fun usb_connect(){
        loggerState.usbConnect()
        loggerState.stopWatchStart()
        val device: UsbDevice
        if (usbManager.deviceList.isNotEmpty()){

            device = usbManager.deviceList.values.first()
            for (i in 0 until device.interfaceCount){
                if(device.getInterface(i).interfaceClass == UsbConstants.USB_CLASS_CDC_DATA) {
                    usbInterface = device.getInterface(i)
                    for (j in 0 until usbInterface.endpointCount){
                        if(
                            usbInterface.getEndpoint(j).type == UsbConstants.USB_ENDPOINT_XFER_BULK
                            && usbInterface.getEndpoint(j).direction == UsbConstants.USB_DIR_IN
                        ){
                            endpointIn = usbInterface.getEndpoint(j)
                        }
                        if (
                            usbInterface.getEndpoint(j).type == UsbConstants.USB_ENDPOINT_XFER_BULK
                            && usbInterface.getEndpoint(j).direction == UsbConstants.USB_DIR_OUT
                        ) {
                            endpointOut = usbInterface.getEndpoint(j)
                        }
                    }
                }
            }

            val connection = usbManager.openDevice(device)
            connection.claimInterface(usbInterface, true)
                connection.controlTransfer(
                    REQUEST_TYPE,0x20,0,0,
                    MSG_BYTE_ARRAY, MSG_BYTE_ARRAY.size, TIMEOUT
                )
                connection.controlTransfer(
                    REQUEST_TYPE,0x22, TDR,0,
                    null,0, TIMEOUT
                )

                if (thread == null){
                    thread = Thread {
                        while (true) {
                            val buffer = ByteArray(64)
                            val byteRead = connection.bulkTransfer(endpointIn, buffer, buffer.size, 1100)
                            if (byteRead > 0){
                                val num = String(buffer, 0, byteRead).toInt()
                                if (num < 230){
                                    tempString.value = num
                                }
                            }
                        }
                    }
                    thread!!.start()
                }
        }
    }
    private fun repo() {
        val scope = CoroutineScope(Dispatchers.IO)
        println("ボゾン開始")
        scope.launch {
            if(correctTemp.isNotEmpty()){
                try {
                    val profile = ProfileEntity(
                        id = 0,
                        name = null,
                        description = null,
                        createAt = System.currentTimeMillis()
                    )

                    val id = repoP.insertProfile(profile)

                    correctTemp.forEachIndexed{ index, temp ->
                        val char = ChartEntity(
                            id = 0,
                            profileId = id,
                            point_index = index,
                            temp = temp
                        )

                        val num = repoC.insertChart(char)
                        if (num == (correctTemp.size -1)){
                            println("${index} ${num} len---${correctTemp.size - 1}")
                        }

                    }

                } catch (e: Exception) {

                    println("保存中にエラー${e}")
                }
            }
        }
        println("ボゾン終了？")

    }

    private fun clearAll() {
        time = Duration.ZERO
        timeString.value = "00:00"
        tempString.value = 0
        correctTemp.clear()
        lineChart.clear()
        stopSelf()
    }
    private fun make_list(){
        val ctx = applicationContext.resources
        val screenHeight = ctx.displayMetrics.heightPixels.toFloat()

        correctTemp.add(tempString.value)
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

    }

    @SuppressLint("UnspecifiedRegisterReceiverFlag")
    override fun onCreate() {
        super.onCreate()
        val filter = IntentFilter().apply {
            addAction(USB_PERMISSION)
            addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED)
            addAction(UsbManager.ACTION_USB_DEVICE_DETACHED)
        }
        registerReceiver(receiver, filter)
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
        CLEAR_ALL
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
