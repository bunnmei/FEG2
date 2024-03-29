package space.webkombinat.feg2.View

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import space.webkombinat.feg2.Data.OparateButton
import space.webkombinat.feg2.Service.Line
import space.webkombinat.feg2.Service.RunningService
import space.webkombinat.feg2.View.component.ChartBox
import space.webkombinat.feg2.View.component.FloatingButton
import space.webkombinat.feg2.View.component.OpeButton
import space.webkombinat.feg2.View.component.OpeButtons
import space.webkombinat.feg2.View.component.StatusPanel
import space.webkombinat.feg2.View.component.TempCanvas
import space.webkombinat.feg2.ViewModel.ChartViewModel

@SuppressLint("SuspiciousIndentation")
@Composable
fun Chart(
    tempStr: String,
    timeStr: String,
    tempList: SnapshotStateList<Line>,
    vm: ChartViewModel,
    modifier: Modifier = Modifier,
    click: () -> Unit
) {
    val rotate = remember {
        mutableStateOf(false)
    }

    Box(
    modifier = modifier
        .fillMaxSize()
        .clickable(
            interactionSource = remember { MutableInteractionSource() },
            indication = null,
            onClick = click
        )
    ){
        Column(modifier = Modifier.fillMaxSize()) {
            StatusPanel(str = timeStr)
            StatusPanel(str = tempStr)
        }
        ChartBox(tempList = tempList)

        TempCanvas()

        OpeButtons(rotate = rotate, vm = vm)
    }
}