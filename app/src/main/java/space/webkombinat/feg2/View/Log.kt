package space.webkombinat.feg2.View

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import space.webkombinat.feg2.ViewModel.LogViewModel

@Composable
fun Log(
    vm: LogViewModel
) {
    Column(modifier = Modifier.fillMaxSize()) {
        Text(text = "LOG")
    }
}