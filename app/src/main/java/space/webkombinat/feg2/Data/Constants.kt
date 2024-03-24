package space.webkombinat.feg2.Data

import android.hardware.usb.UsbConstants

object Constants {
    const val USB_PERMISSION:String = "USB_PERMISION"
    const val BAUD_RATE:Int = 115200
    const val DATA_BIT = 8
    const val STOP_BIT = 0
    const val PARITY = 0
    val MSG_BYTE_ARRAY = byteArrayOf(
        (BAUD_RATE and 0xff).toByte(),
        ((BAUD_RATE shr 8) and 0xff).toByte(),
        ((BAUD_RATE shr 16) and 0xff).toByte(),
        ((BAUD_RATE shr 24) and 0xff).toByte(),
        STOP_BIT.toByte(),
        PARITY.toByte(),
        DATA_BIT.toByte()
    )
    const val REQUEST_TYPE = UsbConstants.USB_TYPE_CLASS or 0x1
    const val TDR = 0 or 0x01
    const val TIMEOUT = 5000

    const val CHART_MINUTE = 30
    const val ONE_MINUTE_WIDTH = 300f
    const val ONE_SECOND_WIDTH = 5f
    const val BUFFER_WIDTH = 100f
    const val CANVAS_WIDTH = CHART_MINUTE * ONE_MINUTE_WIDTH + BUFFER_WIDTH

    const val MIN_TEMP = 70
    const val MAX_TEMP = 230
    const val TEMP_STEP = 10
    const val TEMP_RANGE = (MAX_TEMP - MIN_TEMP) / 10

}

