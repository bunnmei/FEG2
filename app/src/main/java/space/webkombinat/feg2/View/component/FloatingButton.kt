package space.webkombinat.feg2.View.component

import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.size
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.unit.dp
import space.webkombinat.feg2.Data.OparateButton

@Composable
fun FloatingButton(rotate: MutableState<Boolean>, modifier: Modifier = Modifier) {

    val rotateAnime by animateIntAsState(
        targetValue = if(rotate.value) 45 else 0,
        animationSpec = tween(
            durationMillis = 150,
        ), label = ""
    )
    FloatingActionButton(
        modifier = modifier
            .rotate(rotateAnime.toFloat()),
        onClick = {
            rotate.value = !rotate.value
        }
    ){
        Icon(
            imageVector = OparateButton.Open.icon,
            contentDescription = OparateButton.Open.name,
            modifier = modifier.size(24.dp)
        )
    }
}