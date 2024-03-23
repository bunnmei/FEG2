package space.webkombinat.feg2.Service

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.app.Service
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
import androidx.compose.runtime.mutableStateOf
import androidx.core.app.NotificationCompat
import space.webkombinat.feg2.Data.Constants.MSG_BYTE_ARRAY
import space.webkombinat.feg2.Data.Constants.REQUEST_TYPE
import space.webkombinat.feg2.Data.Constants.TDR
import space.webkombinat.feg2.Data.Constants.TIMEOUT
import space.webkombinat.feg2.Data.Constants.USB_PERMISSION
import space.webkombinat.feg2.R
import java.util.Timer
import kotlin.concurrent.fixedRateTimer
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

class RunningService: Service() {
    private var time: Duration = Duration.ZERO
    private lateinit var timer: Timer

    private val receiver = USBPermissionReceiver()

    private lateinit var usbManager: UsbManager
    private lateinit var usbInterface: UsbInterface
    private var endpointIn: UsbEndpoint? = null
    private lateinit var thread: Thread

    var timeString = mutableStateOf("00:00")
        private set

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
            Action.TIMER_START.toString() -> {}
            Action.TIMER_STOP.toString() -> {}
            Action.USB_CONNECT.toString() -> usb_connect()
        }

        return super.onStartCommand(intent, flags, startId)
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun usb_start() {
        val notification = NotificationCompat.Builder(this, "running_channel")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentText("フォアグラウンドサービス")
            .setContentText("hogehoge")
            .build()
        startForeground(1, notification, FOREGROUND_SERVICE_TYPE_DATA_SYNC)

//        timer_start()
        usb_init()
   }

    private fun usb_init(){
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


    private fun timer_start() {
        timer = fixedRateTimer(initialDelay = 1000L, period = 1000L) {
            time = time.plus(1.seconds)
            time.toComponents { hours, minutes, seconds, nanoseconds ->
                println("${minutes.pad()} : ${seconds.pad()}")
                this@RunningService.timeString.value = "${minutes.pad()}:${seconds.pad()}"
            }
        }
    }

    private fun usb_connect(){
        println("キャッチコネクト")
        val device: UsbDevice
        if (usbManager.deviceList.isNotEmpty()){

            device = usbManager.deviceList.values.first()
            for (i in 0 until device.interfaceCount){
                if(device.getInterface(i).interfaceClass == UsbConstants.USB_CLASS_CDC_DATA) {
                    usbInterface = device.getInterface(i)
//                    println(intf.interfaceClass)
                    for (j in 0 until usbInterface.endpointCount){
                        if(
                            usbInterface.getEndpoint(j).type == UsbConstants.USB_ENDPOINT_XFER_BULK
                            && usbInterface.getEndpoint(j).direction == UsbConstants.USB_DIR_IN
                        ){
                            println("endpint の発見 ${usbInterface.getEndpoint(j)}")
                            endpointIn = usbInterface.getEndpoint(j)
                        }
                    }
                }
            }

            val connection = usbManager.openDevice(device)
            connection.claimInterface(usbInterface, true)
               val msgNum =  connection.controlTransfer(
                    REQUEST_TYPE,0x20,0,0,
                    MSG_BYTE_ARRAY, MSG_BYTE_ARRAY.size, TIMEOUT
                )
                connection.controlTransfer(
                    REQUEST_TYPE,0x22, TDR,0,
                    null,0, TIMEOUT
                )

                println(msgNum)
                thread = Thread {
                    println("$endpointIn")
                    println("$connection")
                    println("${usbManager.hasPermission(device)}")
                    while (true) {
                        val buffer = ByteArray(64)
                        val byteRead = connection.bulkTransfer(endpointIn, buffer, buffer.size, TIMEOUT)
                        if (byteRead > 0){
                            val num = String(buffer, 0, byteRead)
                            println(num)
                        }
                    }
                }

                thread.start()


        }



    }

    private fun usb_stop() {

    }

    private fun Int.pad(): String {
        return this.toString().padStart(2, '0')
    }


    @SuppressLint("UnspecifiedRegisterReceiverFlag")
    override fun onCreate() {
        super.onCreate()
        val filter = IntentFilter()
        filter.addAction(USB_PERMISSION)

        registerReceiver(receiver, filter)
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(receiver)
    }

    enum class Action {
        USB_START,
        USB_STOP,
        USB_CONNECT,
        TIMER_START,
        TIMER_STOP,
    }

//    private fun hoge(){
//
//    }

//    companion object {
//        fun usb_connect() {
//            println("サービス内の関数を呼び出した")
//        }
//    }
}