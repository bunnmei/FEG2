package space.webkombinat.feg2.ViewModel

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.geometry.Offset
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import space.webkombinat.feg2.DB.Profile.ProfileRepository
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
//  private val lists = repository.profileAndChart(id)
    var chartList: SnapshotStateList<Line> = mutableStateListOf()
    var chartList_BT: SnapshotStateList<Line> = mutableStateListOf()
    var clack: MutableState<Pair<Int?, Int?>> = mutableStateOf(Pair(null, null))

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
        val lineChart_BT = mutableStateListOf<Line>()
        val clackData = mutableStateOf(Pair<Int?,Int?>(null,null))

        viewModelScope.launch {
            val lists = repository.profileAndChart(id)
            val one_temp_range = height / (MAX_TEMP - MIN_TEMP)
            lists.collect { list ->
//                println(list)
                val profile_clack_f = list[0].profile.clack_f
                val profile_clack_s = list[0].profile.clack_s
                clackData.value = Pair(profile_clack_f, profile_clack_s)
                var prevChar = 0
                var prevChar_BT = 0

                list.forEach { profileLinkChart ->
                    profileLinkChart.chart.sortedBy { it.point_index }.forEach {
//                        println(it.temp)
                        val ET_temp = it.temp / 1000 //上三桁
                        val BT_temp = it.temp % 1000 //下三桁
                        println("${ET_temp}---${BT_temp}")

                        //ET
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
            }

        }
        println("last finish")
        clack = clackData
        chartList = lineChart
        chartList_BT = lineChart_BT
        _uiState.value = UiState.LoadSuccess
    }
}