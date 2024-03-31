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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import space.webkombinat.feg2.Data.OperateButton
import space.webkombinat.feg2.ViewModel.ChartViewModel

@Composable
fun OpeButton(
    show: Boolean,
    operation: OperateButton,
    vm: ChartViewModel,
    modifier: Modifier = Modifier,
    click:() -> Unit)
{
    val usbState = vm.usbState
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

                val iconAndLabel = vm.iconBranch(operation)
                Icon(
                    imageVector = iconAndLabel.first,
                    contentDescription = iconAndLabel.second,
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
                    text = if (operation == OperateButton.USB)
                    {
                        if(usbState.value){
                            operation.label.second!!
                        } else {
                            operation.label.first
                        }
                    } else{
                        operation.label.first
                    },
                    color = MaterialTheme.colorScheme.onBackground
                )
            }
        }
    }
}
