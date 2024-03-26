package space.webkombinat.feg2.ViewModel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import space.webkombinat.feg2.DB.Profile.ProfileRepository
import javax.inject.Inject

@HiltViewModel
class LogDetailViewModel @Inject constructor(
    private val repository: ProfileRepository,
    savedStateHandle: SavedStateHandle
): ViewModel() {
    private val id: Long = savedStateHandle.get<Long>("profileId") ?: throw IllegalArgumentException("id is required")

    fun load() {
        viewModelScope.launch {
            try {
                val profile = repository.profileAndChart(id)
                println(profile)
            } catch (e: Exception){

            }
        }
    }
}