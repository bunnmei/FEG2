package space.webkombinat.feg2.View.component

import android.app.Activity
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import space.webkombinat.feg2.Data.OparateButton
import space.webkombinat.feg2.Data.OperateButton
import space.webkombinat.feg2.ViewModel.ChartViewModel

@Composable
fun OpeButtons(
    rotate: MutableState<Boolean>,
    vm: ChartViewModel,
    modifier: Modifier = Modifier
) {
    val list = listOf(
        OperateButton.USB,
        OperateButton.Play,
        OperateButton.Clack1,
        OperateButton.Clack2,
        OperateButton.Stop,
        OperateButton.Save,
        OperateButton.Clear
    )
    val appCtx = LocalContext.current as Activity
    Column(
        modifier = modifier
            .fillMaxHeight(),
//                .background(Color.Black.copy(alpha = 0.2f)),
        verticalArrangement = Arrangement.Bottom
    ) {
        Row {
            Spacer(modifier = modifier.width(33.dp))
            Column {
                list.forEach {
                    OpeButton(
                        show = rotate.value,
                        vm = vm,
                        operation = it
                    ){
                        vm.action(name = it, appCtx = appCtx)
                    }
                    Spacer(modifier = modifier.height(10.dp))
                }
            }
        }
        Spacer(modifier = modifier.height(10.dp))
        Row {
            Spacer(modifier = modifier.width(30.dp))
            Column {
                FloatingButton(rotate = rotate)
            }
        }
        Spacer(modifier = modifier.height(90.dp))
    }
}