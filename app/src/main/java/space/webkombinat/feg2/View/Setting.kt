package space.webkombinat.feg2.View

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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import space.webkombinat.feg2.View.component.CheckBoxPanel
import space.webkombinat.feg2.ViewModel.SettingViewModel

@Composable
fun Setting(
    modifier: Modifier = Modifier,
    vm: SettingViewModel
) {

    val checked by vm.ui_theme.collectAsState(emptyIntSet())
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
            desc = "設定の状態に合わせます(android 10以上)"
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

    }
}