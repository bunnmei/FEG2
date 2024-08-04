package space.webkombinat.feg2.Data

import androidx.compose.runtime.MutableState
import kotlinx.coroutines.flow.Flow

interface LoggerStore {
    fun usbConnect()
    fun usbDisConnect()

    fun stopWatchStart()
    fun stopWatchStop()
    fun stopWatchIdle()
    fun dataClear()
    fun dataUnsaved()
    fun dataSaving()
    fun dataSaved()

    fun setClackF(data: Int?)
    fun setClackS(data: Int?)

    fun loadUsb(): MutableState<Boolean>
    fun loadStopWatchState(): MutableState<StopWatchState>
    fun loadDataState(): MutableState<ChartDataState>
    fun loadClackState(): MutableState<Pair<Int?, Int?>>
}
enum class StopWatchState{
    Idle,
    Started,
    Stopped,
}

enum class ChartDataState{
    Null,
    Unsaved,
    Saving,
    Saved,
}
