package space.webkombinat.feg2.Data

import androidx.datastore.core.DataStore
import androidx.datastore.core.IOException
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.preferencesOf
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class UserPreferencesRepository @Inject constructor(
    private val dataStore: DataStore<Preferences>
) {

    private companion object {
        val IS_ID = longPreferencesKey("is_id")
        val IS_THEME = intPreferencesKey("is_theme")
        val IS_TOP_RANGE = intPreferencesKey("is_top_range")
        val IS_BOTTOM_RANGE = intPreferencesKey("is_bottom_range")

        const val TAG = "UserPreferencesRepo"
    }

    suspend fun saveTopRange(temp: Int){
        dataStore.edit { preferences ->
            preferences[IS_TOP_RANGE] = temp
        }
    }

    val isTopRange = dataStore.data
        .catch {
            if (it is IOException) {
                emit(emptyPreferences())
            } else {
                throw it
            }
        }.map { preferences ->
            preferences[IS_TOP_RANGE] ?: 230
        }

    suspend fun saveBottomRange(temp: Int){
        dataStore.edit { preferences ->
            preferences[IS_BOTTOM_RANGE] = temp
        }
    }

    val isBottomRange = dataStore.data
        .catch {
            if (it is IOException) {
                emit(emptyPreferences())
            } else {
                throw it
            }
        }.map { preferences ->
            preferences[IS_BOTTOM_RANGE] ?: 70
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

    suspend fun saveTheme(id: Int){
        dataStore.edit { preferences ->
            preferences[IS_THEME] = id
        }
    }

    val isTheme = dataStore.data
        .catch {
            if(it is IOException) {
                emit(emptyPreferences())
            } else {
                throw it
            }
        }
        .map {preferences ->
            preferences[IS_THEME] ?: 2
        }

}