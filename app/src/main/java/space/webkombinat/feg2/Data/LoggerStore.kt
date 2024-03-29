package space.webkombinat.feg2.Data

import androidx.compose.runtime.MutableState
import kotlinx.coroutines.flow.Flow

interface LoggerStore {
    fun usbConnect()
    fun usbDisConnect()
    fun stopWatchStart()
    fun stopWatchStop()
    fun saved()
    fun unsaved()
    fun cleared()
    fun uncleared()

    fun loadUsb(): MutableState<Boolean>
    fun loadStart(): MutableState<Boolean>
    fun loadKeep(): MutableState<Boolean>
    fun loadClear(): MutableState<Boolean>

}
