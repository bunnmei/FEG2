package space.webkombinat.feg2.ViewModel

import android.provider.ContactsContract.Data
import android.util.Log
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import space.webkombinat.feg2.DB.Chart.ChartEntity
import space.webkombinat.feg2.DB.Profile.ProfileEntity
import space.webkombinat.feg2.DB.Profile.ProfileRepository
import java.sql.Timestamp
import java.text.SimpleDateFormat
import javax.inject.Inject

@HiltViewModel
class LogViewModel @Inject constructor(
    private val repository: ProfileRepository
): ViewModel() {
    val profiles = repository.getAll()
    private val profile = MutableStateFlow(ProfileEntity(id = 0, name = null, clack_f = null, clack_s = null, description = null, createAt = System.currentTimeMillis()))
    private val charts  = MutableStateFlow<List<ChartEntity>>(emptyList())

    fun formatTime(time: Long): String{
        val dtf = SimpleDateFormat("yyyy/MM/dd HH:mm:ss")
        return dtf.format(time)
    }

    fun load(id: Long) {
        viewModelScope.launch {
            val data = repository.profileAndChartData(id)
            data.chart.forEach {
                repository.deleteChart(it)
                println(it)
            }
            repository.deleteProfile(data.profile)
            println(data.profile)
        }


    }
}