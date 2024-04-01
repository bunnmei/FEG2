package space.webkombinat.feg2.ViewModel

import android.app.Activity
import android.content.Intent
import androidx.compose.material.icons.Icons
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import space.webkombinat.feg2.Data.ChartDataState
import space.webkombinat.feg2.Data.LoggerState
import space.webkombinat.feg2.Data.OparateButton
import space.webkombinat.feg2.Data.OperateButton
import space.webkombinat.feg2.Data.StopWatchState
import space.webkombinat.feg2.Service.RunningService
import javax.inject.Inject

@HiltViewModel
class ChartViewModel @Inject constructor(
   val loggerState: LoggerState
): ViewModel() {
   val usbState = loggerState.loadUsb()
   val stopWatchState = loggerState.loadStopWatchState()
   val dataState = loggerState.loadDataState()
   val clear = mutableStateOf(false)
   @Composable
   fun colorBranch(btns: OperateButton): Color {
      when(btns) {
         OperateButton.Clack1 -> {
            return MaterialTheme.colorScheme.primaryContainer
         }
         OperateButton.Clack2 -> {
            return MaterialTheme.colorScheme.primaryContainer
         }
         OperateButton.Clear -> {
            if (stopWatchState.value == StopWatchState.Stopped){
               return MaterialTheme.colorScheme.primaryContainer
            }
            return MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
         }
         OperateButton.Play -> {
            if(usbState.value &&
               (stopWatchState.value == StopWatchState.Idle) ||
               (stopWatchState.value == StopWatchState.Stopped)
            ){
               return MaterialTheme.colorScheme.primaryContainer
            }
            return MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
         }
         OperateButton.Save -> {
            if (stopWatchState.value == StopWatchState.Stopped &&
               dataState.value == ChartDataState.Unsaved
            ){
               return MaterialTheme.colorScheme.primaryContainer
            }
            return MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
         }
         OperateButton.Stop -> {
            if(stopWatchState.value == StopWatchState.Started){
               return MaterialTheme.colorScheme.primaryContainer
            }
            return MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
         }
         OperateButton.USB -> {
            return MaterialTheme.colorScheme.primaryContainer
         }
      }
   }

   fun iconBranch(ope: OperateButton): Pair<ImageVector, String> {
      if (ope.icons.second != null) {
         if (usbState.value) {
            return Pair(ope.icons.first, ope.label.first)
         } else {
            return Pair(ope.icons.second!!, ope.label.second!!)
         }
      } else {
         return Pair(ope.icons.first, ope.label.first)
      }
   }

   fun clearData(appCtx: Activity) {
      Intent(appCtx, RunningService::class.java).also { intent ->
         intent.action = RunningService.Action.CLEAR_ALL.toString()
         appCtx.startService(intent)
      }
   }
   fun action(name: OperateButton, appCtx: Activity){
      when(name) {
         OperateButton.Clack1 -> {}
         OperateButton.Clack2 -> {}
         OperateButton.Clear -> {
            if (dataState.value == ChartDataState.Saved){
               clearData(appCtx)
            } else {
               clear.value = true
            }
         }
         OperateButton.Play -> {
            if (stopWatchState.value != StopWatchState.Started && usbState.value){
               Intent(appCtx, RunningService::class.java).also { intent ->
                  intent.action = RunningService.Action.TIMER_START.toString()
                  appCtx.startService(intent)
               }
            }
         }
         OperateButton.Save -> {
            if (dataState.value == ChartDataState.Unsaved && stopWatchState.value == StopWatchState.Stopped){
               Intent(appCtx, RunningService::class.java).also { intent ->
                  intent.action = RunningService.Action.KEEP.toString()
                  appCtx.startService(intent)
               }
            }
         }
         OperateButton.Stop -> {
            if (stopWatchState.value == StopWatchState.Started){
               Intent(appCtx, RunningService::class.java).also { intent ->
                  intent.action = RunningService.Action.TIMER_STOP.toString()
                  appCtx.startService(intent)
               }
            }
         }
         OperateButton.USB -> {
            if (usbState.value){
               Intent(appCtx, RunningService::class.java).also { intent ->
                  intent.action = RunningService.Action.USB_STOP.toString()
                  appCtx.startService(intent)
               }
            } else {
               Intent(appCtx, RunningService::class.java).also { intent ->
                  intent.action = RunningService.Action.USB_START.toString()
                  appCtx.startService(intent)
               }
            }
         }
      }
   }
}