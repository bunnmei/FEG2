package space.webkombinat.feg2.Data

import android.hardware.usb.UsbConstants
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.geometry.Offset
import space.webkombinat.feg2.Service.Line

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
    const val MAX_TEMP = 300
    const val TEMP_STEP = 10
    const val TEMP_RANGE = (MAX_TEMP - MIN_TEMP) / 10

    fun MAKE_LINE(list: SnapshotStateList<Line>, screen_hight: Float, before_temp: Int, current_temp: Int, range:Float): Line{
        val list_size = list.size

        val old_x = (list_size - 1) * 5f
        val old_y = screen_hight - ((before_temp - 70) * range)
        val new_x = list_size * 5f
        val new_y = screen_hight - ((current_temp - 70) * range)

        val line = Line(
            start = Offset(old_x, old_y),
            end = Offset(new_x, new_y)
        )

        return line
    }
}

