package space.webkombinat.feg2.View.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import space.webkombinat.feg2.Data.OparateButton
import space.webkombinat.feg2.ViewModel.ChartViewModel

@Composable
fun OpeButton(
    show: Boolean,
    operation: Triple<OparateButton, OparateButton?, MutableState<Boolean>?>,
    vm: ChartViewModel, 
    modifier: Modifier = Modifier,
    click:() -> Unit)
{
    Row {
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
                        color = vm.colorBranch(btns = operation),
                        RoundedCornerShape(50)
                    )
                    .clickable {
                        click()
                    }
                ,
                contentAlignment = Alignment.Center
            ){

                Icon(
                    imageVector = vm.iconBranch(btns = operation),
                    contentDescription = operation.first.name,
                    modifier = modifier.size(20.dp)
                )
            }

        }
        Spacer(modifier = modifier.width(20.dp))
        AnimatedVisibility(
            visible = show,
            enter = fadeIn(),
            exit = fadeOut()
        ){
            Box(
                modifier = modifier
                    .height(50.dp),
                contentAlignment = Alignment.Center
            ){
                Text(
                    text = "保存",
                    color = Color.White
                )
            }
        }
    }
}
