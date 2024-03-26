package space.webkombinat.feg2.View

import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import space.webkombinat.feg2.ViewModel.LogDetailViewModel

@Composable
fun LogDetail(
    vm: LogDetailViewModel,
    modifier: Modifier = Modifier
) {
    Button(onClick = { vm.load() }) {
        Text(text = "Load")
    }
}