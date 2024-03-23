package space.webkombinat.feg2.View

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun Log() {
    Column(modifier = Modifier.fillMaxSize()) {
        Text(text = "LOG")
    }
}