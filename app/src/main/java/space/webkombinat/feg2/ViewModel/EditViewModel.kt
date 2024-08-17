package space.webkombinat.feg2.ViewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import space.webkombinat.feg2.DB.Profile.ProfileEntity
import space.webkombinat.feg2.DB.Profile.ProfileRepository
import javax.inject.Inject

@HiltViewModel
class EditViewModel @Inject constructor(
    val repository: ProfileRepository
): ViewModel() {

    private val _data = MutableStateFlow(ProfileEntity(id = 0, name = null, clack_f = null, clack_s = null, description = null, createAt = System.currentTimeMillis()))
    val data = _data.value

    private val _textNameHolder = MutableStateFlow("")
    val textNameHolder = _textNameHolder

    private val _textDescHolder = MutableStateFlow("")
    val textDescHolder = _textDescHolder

    sealed interface UiState {
        data object Initial : UiState
        data object Loading : UiState
        data object LoadSuccess : UiState
    }

    private val _uiState = MutableStateFlow<UiState>(UiState.Initial)
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    fun load(id: Long) {
        _uiState.value = UiState.Loading
        viewModelScope.launch {
            _data.value = repository.get(id)
            _textNameHolder.value = _data.value.name ?: ""
            _textDescHolder.value = _data.value.description ?: ""
            _uiState.value = UiState.LoadSuccess
        }
    }

    fun setName(str: String){
        _textNameHolder.value = str
    }

    fun setDesc(str: String){
        _textDescHolder.value = str
    }

    fun save(){
        viewModelScope.launch {
            val prof = _data.value.copy(name = textNameHolder.value, description = textDescHolder.value)
            repository.update(prof)
        }
    }

}