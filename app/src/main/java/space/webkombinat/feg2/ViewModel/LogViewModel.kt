package space.webkombinat.feg2.ViewModel

import android.provider.ContactsContract.Data
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import space.webkombinat.feg2.DB.Profile.ProfileRepository
import java.sql.Timestamp
import java.text.SimpleDateFormat
import javax.inject.Inject

@HiltViewModel
class LogViewModel @Inject constructor(
    private val repository: ProfileRepository
): ViewModel() {
    val profiles = repository.getAll()

    fun formatTime(time: Long): String{
        val dtf = SimpleDateFormat("yyyy/MM/dd HH:mm:ss")
        return dtf.format(time)
    }
}