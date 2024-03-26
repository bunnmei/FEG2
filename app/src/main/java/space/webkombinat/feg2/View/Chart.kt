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
import space.webkombinat.feg2.View.component.StatusPanel
import space.webkombinat.feg2.View.component.TempCanvas

@SuppressLint("SuspiciousIndentation")
@Composable
fun Chart(
    tempStr: String,
    timeStr: String,
    tempList: SnapshotStateList<Line>,
    modifier: Modifier = Modifier,
    click: () -> Unit
) {
    val list = listOf(
        OparateButton.Usb,
        OparateButton.Play,
        OparateButton.Done,
        OparateButton.DoneAll,
        OparateButton.Stop,
        OparateButton.Clear
    )
    val rotate = remember {
        mutableStateOf(false)
    }

    val appCtx = LocalContext.current as Activity
        Box(
        modifier = modifier
            .fillMaxSize()
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
            ) {
                click()
            }
    ){
        Column(modifier = Modifier.fillMaxSize()) {
            StatusPanel(str = timeStr)
            StatusPanel(str = tempStr)
        }
        ChartBox(tempList = tempList)

        TempCanvas()

        Column(
            modifier = modifier
                .fillMaxHeight(),
//                .background(Color.Black.copy(alpha = 0.2f)),
            verticalArrangement = Arrangement.Bottom
        ) {
            Row {
                Spacer(modifier = modifier.width(33.dp))
                Column {
                    list.forEach {
                            OpeButton(
                                show = rotate.value,
                                operation = it
                            ){
                                println("ボタンが押されたよ")
                                when(it.name) {
                                    OparateButton.Usb.name -> {
                                        Intent(appCtx, RunningService::class.java).also { intent ->
                                            intent.action = RunningService.Action.USB_START.toString()
                                            appCtx.startService(intent)
                                        }
                                    }
                                    OparateButton.Play.name -> {
                                        Intent(appCtx, RunningService::class.java).also { intent ->
                                            intent.action = RunningService.Action.TIMER_START.toString()
                                            appCtx.startService(intent)
                                        }
                                    }
                                    OparateButton.Done.name -> {
                                        Intent(appCtx, RunningService::class.java).also { intent ->
                                            intent.action = RunningService.Action.DONE_1.toString()
                                            appCtx.startService(intent)
                                        }
                                    }
                                    OparateButton.Stop.name -> {
                                        Intent(appCtx, RunningService::class.java).also { intent ->
                                            intent.action = RunningService.Action.TIMER_STOP.toString()
                                            appCtx.startService(intent)
                                        }
                                    }
                                }
                            }
                            Spacer(modifier = modifier.height(10.dp))
                    }
                }
            }
            Row {
                Spacer(modifier = modifier.width(30.dp))
                Column {
                    FloatingButton(rotate = rotate)
                }
            }
            Spacer(modifier = modifier.height(90.dp))
        }
    }
}