package space.webkombinat.feg2.Data

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.Usb
import androidx.compose.material.icons.filled.UsbOff
import androidx.compose.ui.graphics.vector.ImageVector

sealed class OparateButton(
    val name:String,
    val icon: ImageVector,
) {
    object Usb: OparateButton(name = "USB", icon = Icons.Default.Usb)
    object UsbOff: OparateButton(name = "USBOff", icon = Icons.Default.UsbOff)
    object Play: OparateButton(name = "Play", icon = Icons.Default.PlayArrow)
    object Done: OparateButton(name = "Done", icon = Icons.Default.Done)
    object DoneAll: OparateButton(name = "DoneAll", icon = Icons.Default.DoneAll)
    object Stop: OparateButton(name = "Stop", icon = Icons.Default.Stop)
    object Keep: OparateButton(name = "Keep", icon = Icons.Default.Download)

    object Clear: OparateButton(name = "Clear", icon = Icons.Default.RestartAlt)
    object Open: OparateButton(name = "Open", icon = Icons.Default.Add)
}