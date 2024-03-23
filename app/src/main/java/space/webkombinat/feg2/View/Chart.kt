package space.webkombinat.feg2.View

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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import space.webkombinat.feg2.Data.OparateButton
import space.webkombinat.feg2.View.component.ChartBox
import space.webkombinat.feg2.View.component.FloatingButton
import space.webkombinat.feg2.View.component.OpeButton
import space.webkombinat.feg2.View.component.StatusPanel

@Composable
fun Chart(
    modifier: Modifier = Modifier,
    click: () -> Unit
) {
    val list = listOf(
        OparateButton.Usb,
        OparateButton.Play,
        OparateButton.Done,
        OparateButton.DoneAll,
        OparateButton.Stop,
        OparateButton.Clear
    )
    var rotate = remember {
        mutableStateOf(false)
    }
        Box(
        modifier = modifier
            .fillMaxSize()
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
            ) {
                click()
            }
    ){
        Column(modifier = Modifier.fillMaxSize()) {
            StatusPanel(str = "00:00")
            StatusPanel(str = "123")
        }
        ChartBox()
        Column(
            modifier = modifier
                .fillMaxHeight(),
//                .background(Color.Black.copy(alpha = 0.2f)),
            verticalArrangement = Arrangement.Bottom
        ) {
            Row {
                Spacer(modifier = modifier.width(25.dp))
                Column {
                    list.forEach {
                            OpeButton(
                                show = rotate.value,
                                operation = it
                            ){}
                            Spacer(modifier = modifier.height(10.dp))
                    }
                    FloatingButton(rotate = rotate)
                }
            }
            Spacer(modifier = modifier.height(80.dp))
        }
    }
}