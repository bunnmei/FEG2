package space.webkombinat.feg2.View

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.focusModifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import space.webkombinat.feg2.Data.ChartDataState
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
    tempList: SnapshotStateList<Line>,
    tempList_BT: SnapshotStateList<Line>,
    time: MutableState<String>,
    temp: MutableState<Int>,
    temp_BT: MutableState<Int>,
    vm: ChartViewModel,
    modifier: Modifier = Modifier,
    bottomShow: MutableState<Boolean>,
    click: () -> Unit,
) {
    val rotate = remember {
        mutableStateOf(false)
    }
    val appCtx = LocalContext.current as Activity
    val dataState = vm.dataState
    val alert = vm.clear
    val clack = vm.clackState
    println("Chart ReCompose")

    Box(
        modifier = modifier
            .fillMaxSize()
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = click
            )
    ){
        ChartBox(
            clack = clack,
            tempList = tempList,
            tempList_BT = tempList_BT,
            bottomShow = bottomShow
        )
        StatusPanel(time= time, temp=temp, temp_BT=temp_BT)
//            StatusPanel(str = data.second, color= Color(0x77CC0F50))
//            StatusPanel(str = tempStr, color= Color(0x770A5C90))
//            StatusPanel(str = data.second, color= Color(0xFFDC5785))
//            StatusPanel(str = tempStr, color= Color(0xFF548DB1))

        TempCanvas(color = MaterialTheme.colorScheme.primary)

//      mask
        AnimatedVisibility(
            visible = rotate.value,
            enter = fadeIn(),
            exit = fadeOut()
        ) {

            Box(modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.secondary.copy(alpha = 0.6f))
                .clickable {
                    rotate.value = false
                }
            )
        }

        OpeButtons(rotate = rotate, vm = vm)
        
        if (dataState.value == ChartDataState.Saving){
            Box(modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
                contentAlignment = Alignment.Center
            ){
                Text(
                    text = "保存中"
                )
            }
        }

        if (alert.value) {
            AlertDialog(
                text = {
                    Text(text = "データを保存していません。\nクリアしますか。")
                },
                onDismissRequest = {
                    alert.value = false
                },
                dismissButton = {
                    TextButton(onClick = {
                        alert.value = false
                    }) {
                        Text(text = "キャンセル")
                    }
                },
                confirmButton = {
                    TextButton(onClick = {
                        vm.clearData(appCtx = appCtx)
                        alert.value = false
                    }) {
                        Text(text = "OK")
                    }
                })
        }

    }
}