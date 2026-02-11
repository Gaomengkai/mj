package icu.merky.mj.data.local.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import icu.merky.mj.domain.model.SystemSettings
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

class SettingsDataStoreSource(
    private val dataStore: DataStore<Preferences>
) {
    fun observeSettings(): Flow<SystemSettings> = dataStore.data
        .catch { throwable ->
            if (throwable is IOException) {
                emit(emptyPreferences())
            } else {
                throw throwable
            }
        }
        .map { preferences ->
            SystemSettings(
                apiBaseUrl = preferences[Keys.API_BASE_URL].orEmpty(),
                streamingEnabled = preferences[Keys.STREAMING_ENABLED] ?: true
            )
        }

    suspend fun updateApiBaseUrl(url: String) {
        dataStore.edit { preferences ->
            preferences[Keys.API_BASE_URL] = url
        }
    }

    suspend fun updateStreamingEnabled(enabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[Keys.STREAMING_ENABLED] = enabled
        }
    }

    private object Keys {
        val API_BASE_URL = stringPreferencesKey("api_base_url")
        val STREAMING_ENABLED = booleanPreferencesKey("streaming_enabled")
    }
}
