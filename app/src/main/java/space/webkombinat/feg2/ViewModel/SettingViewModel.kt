package space.webkombinat.feg2.ViewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import space.webkombinat.feg2.Data.UserPreferencesRepository
import javax.inject.Inject

@HiltViewModel
class SettingViewModel @Inject constructor(
    val userPreferencesRepository: UserPreferencesRepository,
):ViewModel() {
    val ui_theme = userPreferencesRepository.isTheme
    val top_range = userPreferencesRepository.isTopRange
    val bottom_range = userPreferencesRepository.isBottomRange

    fun setTopRange(temp: Int) {
        viewModelScope.launch {
            userPreferencesRepository.saveTopRange(temp)
        }
    }

    fun setBottomRange(temp: Int) {
        viewModelScope.launch {
            userPreferencesRepository.saveBottomRange(temp)
        }
    }

    fun setTheme(theme: Int) {
        viewModelScope.launch {
            userPreferencesRepository.saveTheme(theme)
        }
    }
}