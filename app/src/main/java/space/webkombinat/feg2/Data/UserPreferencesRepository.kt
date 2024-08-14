package space.webkombinat.feg2.Data

import androidx.datastore.core.DataStore
import androidx.datastore.core.IOException
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.longPreferencesKey
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class UserPreferencesRepository @Inject constructor(
    private val dataStore: DataStore<Preferences>
) {

    private companion object {
        val IS_ID = longPreferencesKey("is_id")
        const val TAG = "UserPreferencesRepo"
    }

    suspend fun saveId(id: Long) {
        dataStore.edit { preferences ->
            preferences[IS_ID] = id
        }
    }

    val isId = dataStore.data
        .catch {
            if (it is IOException){
                emit(emptyPreferences())
            } else {
                throw it
            }
        }
        .map { preferences ->
            preferences[IS_ID] ?: -1
        }
}