package space.webkombinat.feg2.Service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Context.USB_SERVICE
import android.content.Intent
import android.hardware.usb.UsbManager
import space.webkombinat.feg2.Data.Constants.USB_PERMISSION

class USBPermissionReceiver: BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        when(intent?.action) {
            USB_PERMISSION -> {
                synchronized(this) {
                    val manager = context?.getSystemService(USB_SERVICE) as? UsbManager
                    val device = manager?.deviceList?.values?.first()
                    val hasPermission = manager?.hasPermission(device)
                    if(hasPermission!!) {
                        Intent(context, RunningService::class.java).also {
                            it.action = RunningService.Action.USB_CONNECT.toString()
                            context.startService(it)
                        }
                    }
                }
            }

            UsbManager.ACTION_USB_DEVICE_ATTACHED -> {
                println("USBが接続されたよ")
            }

            UsbManager.ACTION_USB_DEVICE_DETACHED -> {
                println("USBが切断されたよ")
                Intent(context, RunningService::class.java).also {
                    it.action = RunningService.Action.USB_DISCONNECT.toString()
                    context?.startService(it)
                }
            }
        }
    }
}