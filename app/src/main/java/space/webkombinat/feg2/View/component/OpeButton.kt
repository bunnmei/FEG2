package space.webkombinat.feg2.View.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import space.webkombinat.feg2.Data.OparateButton

@Composable
fun OpeButton(show: Boolean, operation: OparateButton,modifier: Modifier = Modifier, click:() -> Unit) {
    AnimatedVisibility(
        visible = show,
        enter = scaleIn(),
        exit = scaleOut()
    ) {

        Box(
            modifier = modifier
                .width(50.dp)
                .height(50.dp)
                .background(
                    MaterialTheme.colorScheme.secondaryContainer,
                    RoundedCornerShape(50)
                )
                .clickable { click() }
            ,
            contentAlignment = Alignment.Center
        ){
            Icon(
                imageVector = operation.icon,
                contentDescription = operation.name,
                modifier = modifier.size(20.dp)
            )
        }
    }
}