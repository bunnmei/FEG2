package space.webkombinat.feg2.View

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import space.webkombinat.feg2.View.component.ChartBox
import space.webkombinat.feg2.View.component.TempCanvas
import space.webkombinat.feg2.ViewModel.LogDetailViewModel

@Composable
fun LogDetail(
    vm: LogDetailViewModel,
    bottomShow: MutableState<Boolean>,
    modifier: Modifier = Modifier,
    click: () -> Unit,
) {
    val uiState by vm.uiState.collectAsState()
    val height = LocalContext.current.resources.displayMetrics.heightPixels.toFloat()

    val list_ET = vm.chartList
    val list_BT = vm.chartList_BT
    val clack_f = vm.clack
    when(uiState) {
        LogDetailViewModel.UiState.Initial,
        LogDetailViewModel.UiState.LoadSuccess -> {
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
                        tempList = list_ET,
                        tempList_BT = list_BT,
                        clack = clack_f,
                        bottomShow = bottomShow,
                    )
                    TempCanvas(color = MaterialTheme.colorScheme.primary)
                }
        }
        LogDetailViewModel.UiState.Loading -> {
            Box(
                modifier = Modifier
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ){
                CircularProgressIndicator()
            }
        }
    }

    LaunchedEffect(key1 = uiState) {
        when(uiState) {
            LogDetailViewModel.UiState.Initial -> {
                vm.load(height = height)
            }
            LogDetailViewModel.UiState.LoadSuccess -> {}
            LogDetailViewModel.UiState.Loading -> {}
        }
    }
}