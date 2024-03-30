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
import space.webkombinat.feg2.ViewModel.ChartViewModel

@Composable
fun OpeButtons(
    rotate: MutableState<Boolean>,
    vm: ChartViewModel,
    modifier: Modifier = Modifier
) {
    val list = listOf(
        Triple(OparateButton.Usb, OparateButton.UsbOff, vm.usbState),
        Triple(OparateButton.Play, null,  vm.stopState),
        Triple(OparateButton.Done, null, null),
        Triple(OparateButton.DoneAll, null, null),
        Triple(OparateButton.Stop, null, vm.stopState2),
        Triple(OparateButton.Keep, null, vm.keep),
        Triple(OparateButton.Clear,null, vm.clear),
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
                            if (it.third != null && it.third?.value == true){
                                vm.action(name = it.first.name, appCtx = appCtx)
                            } else if (it.second != null) {
                                vm.action(name = it.first.name, appCtx = appCtx)
                            }
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