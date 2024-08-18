package space.webkombinat.feg2.Data

import androidx.compose.runtime.MutableIntState
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.unit.lerp
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import space.webkombinat.feg2.Service.Line
import javax.inject.Inject

class LoggerState @Inject constructor() : LoggerStore {
    private val usbConnect = mutableStateOf(false)
    private val stopWatchState = mutableStateOf(StopWatchState.Idle)
    private val dataState = mutableStateOf(ChartDataState.Null)
    private val clackState = mutableStateOf(Pair<Int?,Int?>(null,null))

    private val time = mutableStateOf("00:00")

    private val ET_temp = mutableStateOf(0)
    private val BT_temp = mutableStateOf(0)

    private val ET_before_temp = mutableIntStateOf(0)
    private val BT_before_temp = mutableIntStateOf(0)

    private val ET_chart = mutableStateListOf<Line>()
    private val BT_chart = mutableStateListOf<Line>()


    fun set_ET_before_temp (temp: Int) {
        ET_before_temp.intValue = temp
    }

    fun set_BT_before_temp (temp: Int) {
        BT_before_temp.intValue = temp
    }

    fun get_ET_before_temp (): MutableIntState {
        return ET_before_temp
    }
    fun get_BT_before_temp (): MutableIntState {
        return BT_before_temp
    }

    fun ET_chart_isEmpty(): Boolean {
        return ET_chart.isEmpty()
    }

    fun BT_chart_isEmpty(): Boolean {
        return BT_chart.isEmpty()
    }

    fun add_ET_chart(line: Line) {
        ET_chart.add(line)
    }

    fun add_BT_chart(line: Line) {
        BT_chart.add(line)
    }

    fun read_ET_chart(): SnapshotStateList<Line> {
        return ET_chart
    }

    fun read_BT_chart(): SnapshotStateList<Line> {
        return BT_chart
    }

    fun clear_ET_chart() {
        ET_chart.clear()
    }

    fun clear_BT_chart() {
        BT_chart.clear()
    }

    fun get_time() : MutableState<String> {
        return time
    }
    fun set_time(update_time: String) {
        time.value = update_time
    }

    fun get_BT_temp(): MutableState<Int> {
        return BT_temp
    }
    fun set_BT_temp(temp: Int) {
        BT_temp.value = temp
    }
    fun get_ET_temp(): MutableState<Int> {
        return ET_temp
    }
    fun set_ET_temp(temp: Int) {
        ET_temp.value = temp
    }

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



