package space.webkombinat.feg2.Data

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.unit.lerp
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject

class LoggerState @Inject constructor() : LoggerStore {
    private val usbConnect = mutableStateOf(false)
    private val stopWatchStart = mutableStateOf(false)
    private val keep = mutableStateOf(false)
    private val clear = mutableStateOf(true)


    override fun usbConnect() {
        usbConnect.value = true
    }

    override fun usbDisConnect() {
        usbConnect.value = false
    }

    override fun stopWatchStart() {
        stopWatchStart.value = true
    }

    override fun stopWatchStop() {
        stopWatchStart.value = false
    }

    override fun saved(){
        keep.value = true
    }

    override fun unsaved(){
        keep.value = false
    }

    override fun cleared() {
        clear.value = true
    }

    override fun uncleared() {
        clear.value = false
    }

    override fun loadUsb(): MutableState<Boolean> {
        return usbConnect
    }

    override fun loadStart(): MutableState<Boolean> {
        return stopWatchStart
    }

    override fun loadKeep(): MutableState<Boolean> {
        return keep
    }

    override fun loadClear(): MutableState<Boolean> {
        return clear
    }
}

