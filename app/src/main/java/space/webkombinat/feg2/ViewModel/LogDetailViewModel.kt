package space.webkombinat.feg2.ViewModel

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.geometry.Offset
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectIndexed
import kotlinx.coroutines.launch
import space.webkombinat.feg2.DB.Profile.ProfileRepository
import space.webkombinat.feg2.Data.Constants
import space.webkombinat.feg2.Data.Constants.MAX_TEMP
import space.webkombinat.feg2.Data.Constants.MIN_TEMP
import space.webkombinat.feg2.Service.Line
import javax.inject.Inject

@HiltViewModel
class LogDetailViewModel @Inject constructor(
    private val repository: ProfileRepository,
    savedStateHandle: SavedStateHandle
): ViewModel() {
    private val id: Long = savedStateHandle.get<Long>("profileId") ?: throw IllegalArgumentException("id is required")
//    private val lists = repository.profileAndChart(id)
    var chartList: SnapshotStateList<Line>? = null
    sealed interface UiState {
        data object Initial: UiState
        data object Loading: UiState
        data object LoadSuccess: UiState
    }

    private val _uiState = MutableStateFlow<UiState>(UiState.Initial)
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    fun load(
        height : Float
    ) {
        _uiState.value = UiState.Loading
        val lineChart = mutableStateListOf<Line>()
        viewModelScope.launch {
            val lists = repository.profileAndChart(id)
            val one_temp_range = height / (MAX_TEMP - MIN_TEMP)
            lists.collect { list ->
//                println(list)
                var prevChar = 0
                list.forEach { profileLinkChart ->
                    profileLinkChart.chart.sortedBy { it.point_index }.forEach {
                        println(it.temp)
                        if (lineChart.isEmpty()) {
                            val old_x = 0f
                            val old_y = height - ((it.temp - 70) * one_temp_range)

                            val line = Line(
                                start = Offset(old_x, old_y),
                                end = Offset(old_x, old_y),
                            )
                            prevChar = it.temp
                            lineChart.add(line)
                        } else {
                            val old_x = (lineChart.size - 1) * 5f
                            val old_y = height - ((prevChar - 70) * one_temp_range)
                            val new_x = (lineChart.size) * 5f
                            val new_y = height - ((it.temp - 70) * one_temp_range)
                            val line = Line(
                                start = Offset(old_x, old_y),
                                end = Offset(new_x, new_y)
                            )
                            prevChar = it.temp
                            lineChart.add(line)
                        }
                    }
                }
            }

        }
        println("last finish")
        chartList = lineChart
        _uiState.value = UiState.LoadSuccess

    }
}