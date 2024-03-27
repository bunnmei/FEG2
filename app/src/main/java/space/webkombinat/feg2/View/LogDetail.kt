package space.webkombinat.feg2.View

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import space.webkombinat.feg2.View.component.ChartBox
import space.webkombinat.feg2.View.component.TempCanvas
import space.webkombinat.feg2.ViewModel.LogDetailViewModel

@Composable
fun LogDetail(
    vm: LogDetailViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by vm.uiState.collectAsState()
    val height = LocalContext.current.resources.displayMetrics.heightPixels.toFloat()

    when(uiState) {
        LogDetailViewModel.UiState.Initial,
        LogDetailViewModel.UiState.LoadSuccess -> {
            vm.chartList?.let {
                Box(
                    modifier = modifier.fillMaxSize()
                ){

                    ChartBox(tempList = it)
                    TempCanvas()
                }
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