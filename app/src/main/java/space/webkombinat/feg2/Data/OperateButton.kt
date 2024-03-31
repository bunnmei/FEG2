package space.webkombinat.feg2.Data

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.Usb
import androidx.compose.material.icons.filled.UsbOff
import androidx.compose.ui.graphics.vector.ImageVector


sealed class OperateButton(
    val icons: Pair<ImageVector, ImageVector?>,
    val label: Pair<String, String?>
){
    object USB: OperateButton(
        icons = Pair(Icons.Filled.Usb, Icons.Filled.UsbOff),
        label = Pair("接続","切断")
    )

    object Play: OperateButton(
        icons = Pair(Icons.Filled.PlayArrow, null),
        label = Pair("スタート", null)
    )

    object Clack1: OperateButton(
        icons = Pair(Icons.Filled.Done, null),
        label = Pair("１ハゼ", null)
    )

    object Clack2: OperateButton(
        icons = Pair(Icons.Filled.DoneAll, null),
        label = Pair("２ハゼ", null)
    )

    object Stop: OperateButton(
        icons = Pair(Icons.Filled.Stop, null),
        label = Pair("ストップ", null)
    )

    object Save: OperateButton(
        icons = Pair(Icons.Filled.Download, null),
        label = Pair("保存", null)
    )

    object Clear: OperateButton(
        icons = Pair(Icons.Filled.Refresh, null),
        label = Pair("クリア", null)
    )
}

