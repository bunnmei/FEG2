package space.webkombinat.feg2.ViewModel

import android.graphics.Point
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.geometry.Offset
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.forEach
import kotlinx.coroutines.launch
import space.webkombinat.feg2.DB.Chart.ChartEntity
import space.webkombinat.feg2.DB.Chart.ChartRepository
import space.webkombinat.feg2.DB.Profile.ProfileEntity
import space.webkombinat.feg2.DB.Profile.ProfileRepository
import space.webkombinat.feg2.Data.Constants.MAX_TEMP
import space.webkombinat.feg2.Data.Constants.MIN_TEMP
import space.webkombinat.feg2.Service.Line
import javax.inject.Inject

@HiltViewModel
class LogDetailViewModel @Inject constructor(
    val repository: ProfileRepository,
    savedStateHandle: SavedStateHandle
): ViewModel() {
    private val id: Long = savedStateHandle.get<Long>("profileId") ?: throw IllegalArgumentException("id is required")

    var chartList: SnapshotStateList<Line> = mutableStateListOf()
    var chartList_BT: SnapshotStateList<Line> = mutableStateListOf()
    var clack: MutableState<Pair<Int?, Int?>> = mutableStateOf(Pair(null, null))

    private var prof = MutableStateFlow(ProfileEntity(id = 0, name = null, clack_f = null, clack_s = null, description = null, createAt = System.currentTimeMillis()))
    private var points = MutableStateFlow<List<ChartEntity>>(emptyList())

    sealed interface UiState {
        data object Initial: UiState
        data object Loading: UiState
        data object LoadSuccess: UiState
        data object Deleting: UiState
        data object Deleted: UiState
    }

    private val _uiState = MutableStateFlow<UiState>(UiState.Initial)
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    fun load(
        height : Float
    ) {
        _uiState.value = UiState.Loading
        val lineChart = mutableStateListOf<Line>()
        val lineChart_BT = mutableStateListOf<Line>()
        val clackData = mutableStateOf(Pair<Int?,Int?>(null,null))
//        val lists = repository.profileAndChart(id)

        viewModelScope.launch {
            val one_temp_range = height / (MAX_TEMP - MIN_TEMP)
            val data = repository.profileAndChartData(id)
            prof.value = data.profile
            points.value = data.chart
            clackData.value = Pair(data.profile.clack_f, data.profile.clack_s)

            var prevChar = 0
            var prevChar_BT = 0

            points.value.sortedBy { it.point_index } .forEach { chart ->

                val ET_temp = chart.temp / 1000 //上三桁
                val BT_temp = chart.temp % 1000 //下三桁

                if (lineChart.isEmpty()) {
                    val old_x = 0f
                    val old_y = height - ((ET_temp - 70) * one_temp_range)

                    val line = Line(
                        start = Offset(old_x, old_y),
                        end = Offset(old_x, old_y),
                    )
                    prevChar = ET_temp
                    lineChart.add(line)
                } else {
                    val old_x = (lineChart.size - 1) * 5f
                    val old_y = height - ((prevChar - 70) * one_temp_range)
                    val new_x = (lineChart.size) * 5f
                    val new_y = height - ((ET_temp - 70) * one_temp_range)
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
                    val old_y = height - ((BT_temp - 70) * one_temp_range)

                    val line = Line(
                        start = Offset(old_x, old_y),
                        end = Offset(old_x, old_y),
                    )
                    prevChar_BT = BT_temp
                    lineChart_BT.add(line)
                } else {
                    val old_x = (lineChart_BT.size - 1) * 5f
                    val old_y = height - ((prevChar_BT - 70) * one_temp_range)
                    val new_x = (lineChart_BT.size) * 5f
                    val new_y = height - ((BT_temp - 70) * one_temp_range)
                    val line = Line(
                        start = Offset(old_x, old_y),
                        end = Offset(new_x, new_y)
                    )
                    prevChar_BT = BT_temp
                    lineChart_BT.add(line)
                }
            }

        }
        println("last finish")
        clack = clackData
        chartList = lineChart
        chartList_BT = lineChart_BT
        _uiState.value = UiState.LoadSuccess
    }

    fun removeData(str: Long) {
        _uiState.value = UiState.Deleting

        viewModelScope.launch {
            val data = repository.profileAndChartData(str)
            data.chart.forEach {
                repository.deleteChart(it)
//                println(it)
            }
            repository.deleteProfile(data.profile)
//            println(data.profile)
            _uiState.value = UiState.Deleted
        }
    }

}