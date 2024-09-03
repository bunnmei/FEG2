package space.webkombinat.feg2.View

import androidx.collection.emptyFloatSet
import androidx.collection.emptyIntSet
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.flow.collectLatest
import space.webkombinat.feg2.Data.StopWatchState
import space.webkombinat.feg2.View.component.CheckBoxPanel
import space.webkombinat.feg2.ViewModel.SettingViewModel

@Composable
fun Setting(
    modifier: Modifier = Modifier,
    vm: SettingViewModel
) {

    val checked by vm.ui_theme.collectAsState(emptyIntSet())
    var topRange by remember { mutableStateOf(230) }
    LaunchedEffect(vm.top_range) {
        vm.top_range.collectLatest { new ->
            topRange = new
        }
    }
    var bottomRange by remember { mutableStateOf(70) }
    LaunchedEffect(vm.bottom_range) {
        vm.bottom_range.collectLatest { new ->
            bottomRange = new
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            text = "テーマ",
            fontSize = 12.sp,
            modifier = modifier
                .padding(start = 16.dp, end = 16.dp, top = 16.dp),
        )
        CheckBoxPanel(
            value = checked == 0,
            title = "システム",
            desc = "設定の状態(android 10以上)"
        ) { vm.setTheme(0) }

        CheckBoxPanel(
            value = checked == 1,
            title = "ダーク",
            desc = null
        ) { vm.setTheme(1) }

        CheckBoxPanel(
            value = checked == 2,
            title = "ライト",
            desc = "デフォルトの状態"
        ) { vm.setTheme(2) }

        Text(
            text = "グラフの温度 最高温度:${topRange} 最低温度:${bottomRange}",
            fontSize = 12.sp,
            modifier = modifier
                .padding(start = 16.dp, end = 16.dp, top = 16.dp),
        )
        Slider(
            modifier = modifier.padding(16.dp),
            enabled = vm.stopWatchState.value == StopWatchState.Idle,
            value = topRange.toFloat(),
            onValueChange = {
                vm.setTopRange(it.toInt())
            },
            valueRange = 200f..330f,
            steps = 12
        )

        Slider(
            modifier = modifier.padding(16.dp),
            enabled = vm.stopWatchState.value == StopWatchState.Idle,
            value = bottomRange.toFloat(),

            onValueChange = {
                vm.setBottomRange(it.toInt())
            },
            valueRange = 0f..100f,
            steps = 9
        )
    }
}