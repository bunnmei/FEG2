package space.webkombinat.feg2.Service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Context.USB_SERVICE
import android.content.Intent
import android.hardware.usb.UsbManager
import space.webkombinat.feg2.Data.Constants.USB_PERMISSION

class USBPermissionReceiver: BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        if (USB_PERMISSION == intent?.action) {
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
    }
}