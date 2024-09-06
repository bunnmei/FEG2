package space.webkombinat.feg2.ViewModel

import android.app.Activity
import android.content.Intent
import androidx.collection.emptyLongSet
import androidx.compose.material.icons.Icons
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import space.webkombinat.feg2.DB.Profile.ProfileRepository
import space.webkombinat.feg2.Data.ChartDataState
import space.webkombinat.feg2.Data.Constants.MAX_TEMP
import space.webkombinat.feg2.Data.Constants.MIN_TEMP
import space.webkombinat.feg2.Data.LoggerState
import space.webkombinat.feg2.Data.OparateButton
import space.webkombinat.feg2.Data.OperateButton
import space.webkombinat.feg2.Data.StopWatchState
import space.webkombinat.feg2.Data.UserPreferencesRepository
import space.webkombinat.feg2.Service.Line
import space.webkombinat.feg2.Service.RunningService
import space.webkombinat.feg2.ViewModel.LogDetailViewModel.UiState
import javax.inject.Inject
import kotlin.math.log

@HiltViewModel
class ChartViewModel @Inject constructor(
   val loggerState: LoggerState,
   val repository: ProfileRepository,
   val userPreferencesRepository: UserPreferencesRepository,
   ): ViewModel() {

   val usbState = loggerState.loadUsb()
   val stopWatchState = loggerState.loadStopWatchState()
   val dataState = loggerState.loadDataState()
   val clackState = loggerState.loadClackState()
   val ET_temp = loggerState.get_ET_temp()
   val BT_temp = loggerState.get_BT_temp()

   val ET_chart = loggerState.read_ET_chart()
   val BT_chart = loggerState.read_BT_chart()

   val time = loggerState.get_time()
   val clear = mutableStateOf(false)

   val savedId = userPreferencesRepository.isId
   var chartList: SnapshotStateList<Line> = mutableStateListOf()
   var chartList_BT: SnapshotStateList<Line> = mutableStateListOf()

   var clack: MutableState<Pair<Int?, Int?>> = mutableStateOf(Pair(null, null))

   val topRange = userPreferencesRepository.isTopRange
   val bottomRange = userPreferencesRepository.isBottomRange
   val fontSize = userPreferencesRepository.isMemoryFontSize

   fun load(
      height : Float
   ) {
      val lineChart = mutableStateListOf<Line>()
      val lineChart_BT = mutableStateListOf<Line>()
      val clackData = mutableStateOf(Pair<Int?,Int?>(null,null))
      viewModelScope.launch {
         savedId.collect {id ->

            if (id < 0){
               return@collect
            }

            val one_temp_range = height / (loggerState.max_range.value - loggerState.min_range.value)
            val data = repository.profileAndChartData(id)
            if(data == null) {
               userPreferencesRepository.saveId(-1)
               return@collect
            }
            clackData.value = Pair(data.profile.clack_f, data.profile.clack_s)

            var prevChar = 0
            var prevChar_BT = 0

            data.chart.sortedBy { it.point_index } .forEach { chart ->

               val ET_temp = chart.temp / 1000 //上三桁
               val BT_temp = chart.temp % 1000 //下三桁

               if (lineChart.isEmpty()) {
                  val old_x = 0f
                  val old_y = height - ((ET_temp - loggerState.min_range.value) * one_temp_range)

                  val line = Line(
                     start = Offset(old_x, old_y),
                     end = Offset(old_x, old_y),
                  )
                  prevChar = ET_temp
                  lineChart.add(line)
               } else {
                  val old_x = (lineChart.size - 1) * 5f
                  val old_y = height - ((prevChar - loggerState.min_range.value) * one_temp_range)
                  val new_x = (lineChart.size) * 5f
                  val new_y = height - ((ET_temp - loggerState.min_range.value) * one_temp_range)
                  val line = Line(
                     start = Offset(old_x, old_y),
                     end = Offset(new_x, new_y)
                  )
                  prevChar = ET_temp
                  lineChart.add(line)
               }

               //BT
               if (lineChart_BT.isEmpty()) {
                  val old_x = 0f
                  val old_y = height - ((BT_temp - loggerState.min_range.value) * one_temp_range)

                  val line = Line(
                     start = Offset(old_x, old_y),
                     end = Offset(old_x, old_y),
                  )
                  prevChar_BT = BT_temp
                  lineChart_BT.add(line)
               } else {
                  val old_x = (lineChart_BT.size - 1) * 5f
                  val old_y = height - ((prevChar_BT - loggerState.min_range.value) * one_temp_range)
                  val new_x = (lineChart_BT.size) * 5f
                  val new_y = height - ((BT_temp - loggerState.min_range.value) * one_temp_range)
                  val line = Line(
                     start = Offset(old_x, old_y),
                     end = Offset(new_x, new_y)
                  )
                  prevChar_BT = BT_temp
                  lineChart_BT.add(line)
               }
            }
         }

      }
      println("last finish")
      clack = clackData
      chartList = lineChart
      chartList_BT = lineChart_BT
   }



   @Composable
   fun colorBranch(btns: OperateButton): Color {
      when(btns) {
         OperateButton.Clack1 -> {
            if (clackState.value.first == null && stopWatchState.value == StopWatchState.Started) {
               return MaterialTheme.colorScheme.primaryContainer
            }
            return MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
         }
         OperateButton.Clack2 -> {
            if (clackState.value.second == null && stopWatchState.value == StopWatchState.Started) {
               return MaterialTheme.colorScheme.primaryContainer
            }
            return MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
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

   fun setFirstClack(appCtx: Activity) {
      Intent(appCtx, RunningService::class.java).also { intent ->
         intent.action = RunningService.Action.CLACK_F.toString()
         appCtx.startService(intent)
      }
   }

   fun setSecondClack(appCtx: Activity) {
      Intent(appCtx, RunningService::class.java).also { intent ->
         intent.action = RunningService.Action.CLACK_S.toString()
         appCtx.startService(intent)
      }
   }
   fun action(name: OperateButton, appCtx: Activity){
      when(name) {
         OperateButton.Clack1 -> {
            if(
               stopWatchState.value == StopWatchState.Started &&
               clackState.value.first == null
               ){
               setFirstClack(appCtx)
            }
         }
         OperateButton.Clack2 -> {
            if(stopWatchState.value == StopWatchState.Started &&
               clackState.value.second == null
               ){
               setSecondClack(appCtx)
            }
         }
         OperateButton.Clear -> {
            if (dataState.value == ChartDataState.Unsaved && stopWatchState.value == StopWatchState.Stopped){
               clear.value = true
            }

            if (dataState.value == ChartDataState.Saved && stopWatchState.value == StopWatchState.Stopped){
               clearData(appCtx)
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

   fun setHeight(height: Float) {
         loggerState.display_height.value = height
   }

   fun set_range(range: String, it: Int) {
      if (range == "top"){
         loggerState.max_range.value = it
      } else {
         loggerState.min_range.value = it
      }
   }
}