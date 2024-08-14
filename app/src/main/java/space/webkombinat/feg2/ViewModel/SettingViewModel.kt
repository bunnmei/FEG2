package space.webkombinat.feg2.ViewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import space.webkombinat.feg2.Data.UserPreferencesRepository
import javax.inject.Inject

@HiltViewModel
class SettingViewModel @Inject constructor(
    val userPreferencesRepository: UserPreferencesRepository,
):ViewModel() {
    val ui_theme = userPreferencesRepository.isTheme

    fun setTheme(theme: Int) {
        viewModelScope.launch {
            userPreferencesRepository.saveTheme(theme)
        }
    }
}