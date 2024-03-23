package space.webkombinat.feg2.View

import android.app.Activity
import android.app.Application
import android.content.Intent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import space.webkombinat.feg2.Service.RunningService

@Composable
fun Dammy(textStr: String) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val ctx = LocalContext.current
        val appCtx = ctx as Activity
        Text(text = textStr)
        Button(onClick = {
            Intent(appCtx, RunningService::class.java).also {
                it.action = RunningService.Action.USB_START.toString()
                appCtx.startService(it)
            }
        }) {
            Text(text = "start_service")
        }
    }
}