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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
    
    val dataState = vm.dataState

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
            tempList = tempList,
            color = MaterialTheme.colorScheme.primary,
            color_line = MaterialTheme.colorScheme.tertiary
        )

        Column(modifier = Modifier.fillMaxSize()) {
            StatusPanel(str = timeStr)
            StatusPanel(str = tempStr)
        }
        TempCanvas(color = MaterialTheme.colorScheme.primary)

//        mask
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
                .background(MaterialTheme.colorScheme.secondary),
                contentAlignment = Alignment.Center
            ){
                Text(text = "保存中")
            }
        }

    }
}