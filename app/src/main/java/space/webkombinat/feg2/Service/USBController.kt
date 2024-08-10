package space.webkombinat.feg2.Service

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.hardware.usb.UsbConstants
import android.hardware.usb.UsbDevice
import android.hardware.usb.UsbDeviceConnection
import android.hardware.usb.UsbEndpoint
import android.hardware.usb.UsbInterface
import android.hardware.usb.UsbManager
import space.webkombinat.feg2.Data.Constants
import space.webkombinat.feg2.Data.Constants.USB_PERMISSION

class USBController {
    private var usbManager: UsbManager? = null
    private var usbInterface: UsbInterface? = null
    private var endpointIn: UsbEndpoint? = null
    private var endpointOut: UsbEndpoint? = null
    private var usbDeviceConnection: UsbDeviceConnection? = null

    fun requestPermission(
        usbMan: UsbManager,
        ctx: Context
    ){
        usbManager = usbMan
        if (usbManager != null){
            val usbPermission = PendingIntent.getBroadcast(
                ctx,
                0,
                Intent(Constants.USB_PERMISSION),
                PendingIntent.FLAG_IMMUTABLE
            )
            if (usbManager!!.deviceList.isNotEmpty()){
                val device = usbManager!!.deviceList.values.first()
                usbManager!!.requestPermission(device, usbPermission)
            } else {
                println("デバイスが接続されていません。")
            }
        }
    }

    fun connect(){
        val device = getUsbInterface()
        if (device != null){
            serialReadSetup(device)
        }
    }

    fun read(timeOut: Int = 500): Pair<Int?, Int?>? {
        try {
            if (usbDeviceConnection != null && endpointIn != null){
                val buffer = ByteArray(64)
                val readStr = "READ".toByteArray()
                val sendData = usbDeviceConnection!!.bulkTransfer(endpointOut, readStr, readStr.size, timeOut)
                if (sendData < 0) {
                    println("send error")
                }
                val byteRead = usbDeviceConnection!!.bulkTransfer(endpointIn, buffer, buffer.size, timeOut)
                if (byteRead > 0){
                    val recData = String(buffer, 0, byteRead)
                    val split = recData.split(",")
                    if (split[1].isNotEmpty()){
                        val num = split[1].toIntOrNull()
                        val num2 = split[2].toIntOrNull()
                        if (num != null){
                            println(num)
                            return Pair(num, num2)
                        }
                        return null
                    }
                }
            }
            return null
        } catch (e: Exception){
            println("bulk read Error $e")
            return null
        }
    }

    fun close(){
        usbManager = null
        usbInterface = null
        endpointIn = null
        endpointOut = null
        usbDeviceConnection = null
    }

    fun checkConnection(): Boolean {
        if (usbDeviceConnection != null && endpointIn != null){
            return true
        } else {
            return false
        }
    }



    private fun getUsbInterface(): UsbDevice? {
        var device: UsbDevice? = null
        if (usbManager != null){
            if (usbManager!!.deviceList.isNotEmpty()){
                device = usbManager!!.deviceList.values.first()
                for (i in 0 until device.interfaceCount){
                    if(device.getInterface(i).interfaceClass == UsbConstants.USB_CLASS_CDC_DATA) {
                        usbInterface = device.getInterface(i)
                        for (j in 0 until usbInterface!!.endpointCount){
                            if(
                                usbInterface!!.getEndpoint(j).type == UsbConstants.USB_ENDPOINT_XFER_BULK
                                && usbInterface!!.getEndpoint(j).direction == UsbConstants.USB_DIR_IN
                            ){
                                endpointIn = usbInterface!!.getEndpoint(j)
                            }
                            if (
                                usbInterface!!.getEndpoint(j).type == UsbConstants.USB_ENDPOINT_XFER_BULK
                                && usbInterface!!.getEndpoint(j).direction == UsbConstants.USB_DIR_OUT
                            ) {
                                endpointOut = usbInterface!!.getEndpoint(j)
                            }
                        }
                    }
                }
            }
        }
        return device
    }

    private fun serialReadSetup(
        usbDevice: UsbDevice
    ){
        if (usbManager != null){
            if (usbDeviceConnection == null){
                usbDeviceConnection = usbManager!!.openDevice(usbDevice)
                usbDeviceConnection!!.claimInterface(usbInterface, true)
                usbDeviceConnection!!.controlTransfer(
                    Constants.REQUEST_TYPE,0x20,0,0,
                    Constants.MSG_BYTE_ARRAY, Constants.MSG_BYTE_ARRAY.size, Constants.TIMEOUT
                )
                usbDeviceConnection!!.controlTransfer(
                    Constants.REQUEST_TYPE,0x22, Constants.TDR,0,
                    null,0, Constants.TIMEOUT
                )

            }
        }
    }
}