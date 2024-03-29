package space.webkombinat.feg2.ViewModel

import android.app.Activity
import android.content.Intent
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import space.webkombinat.feg2.Data.LoggerState
import space.webkombinat.feg2.Data.OparateButton
import space.webkombinat.feg2.Service.RunningService
import javax.inject.Inject

@HiltViewModel
class ChartViewModel @Inject constructor(
   val loggerState: LoggerState
): ViewModel() {
   val usbState = loggerState.loadUsb()
   val stopState = loggerState.loadStart()
   val keep = loggerState.loadKeep()
   val clear = loggerState.loadClear()

   @Composable
   fun colorBranch(btns: Triple<OparateButton, OparateButton?, MutableState<Boolean>?>): Color {
      if(btns.second != null) {
         return MaterialTheme.colorScheme.secondaryContainer
      } else {
         if (btns.third != null){
            if(btns.third!!.value){
               return MaterialTheme.colorScheme.secondaryContainer
            } else {
               return Color.Black.copy(alpha = 0.3f)
            }
         }

         return MaterialTheme.colorScheme.secondaryContainer
      }
   }

   fun iconBranch(btns: Triple<OparateButton, OparateButton?, MutableState<Boolean>?>): ImageVector {
      if(btns.second != null) {
         if(btns.third?.value!!){
            return btns.first.icon
         } else {
            return btns.second!!.icon
         }
      } else {
         return btns.first.icon
      }
   }

   fun action(name: String, appCtx: Activity){
      when(name) {
         OparateButton.Usb.name -> {
            Intent(appCtx, RunningService::class.java).also { intent ->
               intent.action = RunningService.Action.USB_START.toString()
               appCtx.startService(intent)
            }
         }
         OparateButton.Play.name -> {
            Intent(appCtx, RunningService::class.java).also { intent ->
               intent.action = RunningService.Action.TIMER_START.toString()
               appCtx.startService(intent)
            }
         }
         OparateButton.Done.name -> {
            Intent(appCtx, RunningService::class.java).also { intent ->
               intent.action = RunningService.Action.DONE_1.toString()
               appCtx.startService(intent)
            }
         }
         OparateButton.Stop.name -> {
            Intent(appCtx, RunningService::class.java).also { intent ->
               intent.action = RunningService.Action.TIMER_STOP.toString()
               appCtx.startService(intent)
            }
         }
         OparateButton.Clear.name -> {
            Intent(appCtx, RunningService::class.java).also { intent ->
               intent.action = RunningService.Action.CLEAR_ALL.toString()
               appCtx.startService(intent)
            }
         }
      }
   }
}