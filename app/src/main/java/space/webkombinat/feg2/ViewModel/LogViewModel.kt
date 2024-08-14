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
import space.webkombinat.feg2.Data.UserPreferencesRepository
import java.sql.Timestamp
import java.text.SimpleDateFormat
import javax.inject.Inject

@HiltViewModel
class LogViewModel @Inject constructor(
    private val repository: ProfileRepository,
    val userPreferencesRepository: UserPreferencesRepository,
): ViewModel() {
    val profiles = repository.getAll()
    val saveId = userPreferencesRepository.isId

    fun formatTime(time: Long): String{
        val dtf = SimpleDateFormat("yyyy/MM/dd HH:mm:ss")
        return dtf.format(time)
    }

    fun removeId() {
        viewModelScope.launch {
            userPreferencesRepository.saveId(-1)
        }
    }
}