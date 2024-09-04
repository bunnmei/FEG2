package space.webkombinat.feg2.View

import android.annotation.SuppressLint
import android.app.Activity
import androidx.collection.emptyIntSet
import androidx.collection.emptyLongSet
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateIntOffsetAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.flow.collectLatest
import space.webkombinat.feg2.Data.ChartDataState
import space.webkombinat.feg2.View.component.ChartBox
import space.webkombinat.feg2.View.component.MoveAnim
import space.webkombinat.feg2.View.component.OpeButtons
import space.webkombinat.feg2.View.component.StatusPanel
import space.webkombinat.feg2.View.component.TempCanvas
import space.webkombinat.feg2.ViewModel.ChartViewModel
import kotlin.math.roundToInt

@SuppressLint("SuspiciousIndentation")
@Composable
fun Chart(
//    tempList: SnapshotStateList<Line>,
//    tempList_BT: SnapshotStateList<Line>,
//    time: MutableState<String>,
//    temp: MutableState<Int>,
//    temp_BT: MutableState<Int>,
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

    val savedId = vm.savedId.collectAsState(emptyLongSet())
    val fontSize = vm.fontSize.collectAsState(0)
    val height = LocalContext.current.resources.displayMetrics.heightPixels.toFloat()
    var topRange by remember { mutableStateOf(230) }
    var bottomRange by remember { mutableStateOf(70) }
    LaunchedEffect(vm.topRange) {
        vm.topRange.collectLatest {
            topRange = it
            vm.set_range("top", it)
            vm.load(height)
        }
    }
    LaunchedEffect(vm.bottomRange) {
        vm.bottomRange.collectLatest {
            bottomRange = it
            vm.set_range("bottom", it)
            vm.load(height)
        }
    }
    LaunchedEffect(Unit) {
        vm.setHeight(height)
    }
//    println("Chart ReCompose")

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
            tempList = vm.ET_chart,
            tempList_BT = vm.BT_chart,
            bottomShow = bottomShow,
            font_size = fontSize.value,
            vm = vm
        )
        StatusPanel(time= vm.time, temp=vm.ET_temp, temp_BT=vm.BT_temp)
//            StatusPanel(str = data.second, color= Color(0x77CC0F50))
//            StatusPanel(str = tempStr, color= Color(0x770A5C90))
//            StatusPanel(str = data.second, color= Color(0xFFDC5785))
//            StatusPanel(str = tempStr, color= Color(0xFF548DB1))

        TempCanvas(
            topRange = topRange,
            bottomRange = bottomRange,
            color = MaterialTheme.colorScheme.primary
        )

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

        MoveAnim(
            show = bottomShow.value
        ) {
            OpeButtons(rotate = rotate, vm = vm)
        }

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
    LaunchedEffect(savedId.value) {
        vm.load(height)
    }
}