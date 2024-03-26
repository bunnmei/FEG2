package space.webkombinat.feg2.ViewModel

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import space.webkombinat.feg2.DB.Profile.ProfileRepository
import javax.inject.Inject

@HiltViewModel
class LogViewModel @Inject constructor(
    private val repository: ProfileRepository
): ViewModel() {
    val profiles = repository.getAll()
}