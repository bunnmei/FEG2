package space.webkombinat.feg2.Data

import androidx.compose.runtime.MutableIntState
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.lerp
import androidx.compose.ui.unit.min
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import space.webkombinat.feg2.Data.Constants.MAKE_LINE
import space.webkombinat.feg2.Data.Constants.MAX_TEMP
import space.webkombinat.feg2.Data.Constants.MIN_TEMP
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

    private val ET_temp_list = mutableStateListOf<Int>()
    private val BT_temp_list = mutableStateListOf<Int>()

    private var ET_chart = mutableStateListOf<Line>()
    private var BT_chart = mutableStateListOf<Line>()

    var display_height = mutableFloatStateOf(0f)
    var max_range = mutableIntStateOf(0)
    var min_range = mutableIntStateOf(0)

    fun add_temp_list() {
        val one_temp_range = display_height.value / (max_range.value - min_range.value)
        if(ET_chart.isEmpty()){
            val old_x = 0f
            val old_y = display_height.value - ((ET_temp.value - min_range.value) * one_temp_range)

            val line = Line(
                start = Offset(old_x, old_y),
                end = Offset(old_x, old_y),
            )
            add_ET_chart(line)
            set_ET_before_temp(ET_temp.value)
        } else {
            val line = MAKE_LINE(
                list = read_ET_chart(),
                screen_hight = display_height.value,
                before_temp = get_ET_before_temp().value,
                current_temp = get_ET_temp().value,
                range = one_temp_range,
                min_range = min_range.value
            )
            add_ET_chart(line)
            set_ET_before_temp(get_ET_temp().value)
        }

        if (BT_chart_isEmpty()) {
            val old_x = 0f
            val old_y = display_height.value - ((get_BT_temp().value - min_range.value) * one_temp_range)

            val line = Line(
                start = Offset(old_x, old_y),
                end = Offset(old_x, old_y),
            )
            add_BT_chart(line)
            set_BT_before_temp(get_BT_temp().value)
        } else {
            val line = MAKE_LINE(
                list = read_BT_chart(),
                screen_hight = display_height.value,
                before_temp = get_BT_before_temp().value,
                current_temp = get_BT_temp().value,
                range = one_temp_range,
                min_range = min_range.value
            )
            add_BT_chart(line)
            set_BT_before_temp(get_BT_temp().value)
        }
    }

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

    fun ET_temp_list_isEmpty(): Boolean {
        return ET_temp_list.isEmpty()
    }

    fun BT_temp_list_isEmpty(): Boolean {
        return BT_temp_list.isEmpty()
    }

    fun add_ET_chart(line: Line) {
        ET_chart.add(line)
    }

    fun add_BT_chart(line: Line) {
        BT_chart.add(line)
    }

    fun add_ET_list() {
        ET_temp_list.add(ET_temp.value)
    }

    fun add_BT_list() {
        BT_temp_list.add(BT_temp.value)
    }
    fun read_ET_chart(): SnapshotStateList<Line> {
        return ET_chart
    }

    fun read_BT_chart(): SnapshotStateList<Line> {
        return BT_chart
    }

    fun read_ET_temp_list(): SnapshotStateList<Int> {
        return ET_temp_list
    }

    fun read_BT_temp_list(): SnapshotStateList<Int> {
        return BT_temp_list
    }

    fun clear_ET_chart() {
        ET_chart.clear()
    }

    fun clear_BT_chart() {
        BT_chart.clear()
    }

    fun clear_ET_temp_list() {
        ET_temp_list.clear()
    }

    fun clear_BT_temp_list() {
        BT_temp_list.clear()
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



