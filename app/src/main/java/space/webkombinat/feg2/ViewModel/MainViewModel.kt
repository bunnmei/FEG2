package space.webkombinat.feg2.ViewModel

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import space.webkombinat.feg2.Data.UserPreferencesRepository
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    val userPreferencesRepository: UserPreferencesRepository,
): ViewModel() {
    val mode = userPreferencesRepository.isTheme
}