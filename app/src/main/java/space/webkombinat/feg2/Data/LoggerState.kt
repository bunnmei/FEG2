package space.webkombinat.feg2.Data

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.unit.lerp
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject

class LoggerState @Inject constructor() : LoggerStore {
    private val usbConnect = mutableStateOf(false)
    private val stopWatchState = mutableStateOf(StopWatchState.Idle)
    private val dataState = mutableStateOf(ChartDataState.Null)
    private val clackState = mutableStateOf(Pair<Int?,Int?>(null,null))
    override fun usbConnect() {
        usbConnect.value = true
    }

    override fun usbDisConnect() {
        usbConnect.value = false
    }

    override fun stopWatchStart() {
        stopWatchState.value = StopWatchState.Started
    }

    override fun stopWatchStop() {
        stopWatchState.value = StopWatchState.Stopped
    }

    override fun stopWatchIdle() {
        stopWatchState.value = StopWatchState.Idle
    }

    override fun dataClear() {
        dataState.value = ChartDataState.Null
        clackState.value = Pair(null, null)
    }

    override fun dataUnsaved() {
        dataState.value = ChartDataState.Unsaved
    }

    override fun dataSaving() {
        dataState.value = ChartDataState.Saving
    }

    override fun dataSaved() {
        dataState.value = ChartDataState.Saved
    }


    override fun setClackF(data: Int?) {
        clackState.value = Pair(data, clackState.value.second)
    }
    override fun setClackS(data: Int?) {
        clackState.value = Pair(clackState.value.first, data)
    }

    override fun loadUsb(): MutableState<Boolean> {
        return usbConnect
    }

    override fun loadStopWatchState(): MutableState<StopWatchState> {
        return stopWatchState
    }

    override fun loadDataState(): MutableState<ChartDataState> {
        return dataState
    }

    override fun loadClackState(): MutableState<Pair<Int?, Int?>> {
        return clackState
    }
}



