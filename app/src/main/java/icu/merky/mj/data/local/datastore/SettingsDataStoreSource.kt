package icu.merky.mj.data.local.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import icu.merky.mj.domain.model.ModelApiConfig
import icu.merky.mj.domain.model.ModelApiConfigHistoryEntry
import icu.merky.mj.domain.model.PromptConfig
import icu.merky.mj.domain.model.SystemSettings
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException

class SettingsDataStoreSource(
    private val dataStore: DataStore<Preferences>
) {
    private val xorSecret = "yuki_companion_secret"

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

    fun observeModelApiConfig(): Flow<ModelApiConfig> = dataStore.data
        .catch { throwable ->
            if (throwable is IOException) {
                emit(emptyPreferences())
            } else {
                throw throwable
            }
        }
        .map { preferences ->
            ModelApiConfig(
                endpoint = preferences[Keys.MODEL_ENDPOINT].orEmpty(),
                apiKey = decrypt(preferences[Keys.MODEL_API_KEY_ENC].orEmpty()),
                model = preferences[Keys.MODEL_NAME].orEmpty()
            )
        }

    suspend fun saveModelApiConfig(config: ModelApiConfig) {
        dataStore.edit { preferences ->
            preferences[Keys.MODEL_ENDPOINT] = config.endpoint
            preferences[Keys.MODEL_API_KEY_ENC] = encrypt(config.apiKey)
            preferences[Keys.MODEL_NAME] = config.model
        }
    }

    fun observeModelApiConfigHistory(): Flow<List<ModelApiConfigHistoryEntry>> = dataStore.data
        .catch { throwable ->
            if (throwable is IOException) {
                emit(emptyPreferences())
            } else {
                throw throwable
            }
        }
        .map { preferences ->
            decodeHistory(preferences[Keys.MODEL_SUCCESS_HISTORY].orEmpty())
        }

    fun observePromptConfig(): Flow<PromptConfig> = dataStore.data
        .catch { throwable ->
            if (throwable is IOException) {
                emit(emptyPreferences())
            } else {
                throw throwable
            }
        }
        .map { preferences ->
            PromptConfig(
                chat1SystemPrompt = preferences[Keys.CHAT1_SYSTEM_PROMPT].orEmpty(),
                chat2DiarySystemPrompt = preferences[Keys.CHAT2_DIARY_SYSTEM_PROMPT].orEmpty(),
                chat3LazyReplySystemPrompt = preferences[Keys.CHAT3_LAZY_REPLY_SYSTEM_PROMPT].orEmpty()
            )
        }

    suspend fun savePromptConfig(config: PromptConfig) {
        dataStore.edit { preferences ->
            preferences[Keys.CHAT1_SYSTEM_PROMPT] = config.chat1SystemPrompt
            preferences[Keys.CHAT2_DIARY_SYSTEM_PROMPT] = config.chat2DiarySystemPrompt
            preferences[Keys.CHAT3_LAZY_REPLY_SYSTEM_PROMPT] = config.chat3LazyReplySystemPrompt
        }
    }

    fun observeActivePlayerId(): Flow<Long> = dataStore.data
        .catch { throwable ->
            if (throwable is IOException) {
                emit(emptyPreferences())
            } else {
                throw throwable
            }
        }
        .map { preferences ->
            preferences[Keys.ACTIVE_PLAYER_ID] ?: DEFAULT_PLAYER_ID
        }

    suspend fun getActivePlayerIdOrNull(): Long? {
        val preferences = dataStore.data
            .catch { throwable ->
                if (throwable is IOException) {
                    emit(emptyPreferences())
                } else {
                    throw throwable
                }
            }
            .first()
        return preferences[Keys.ACTIVE_PLAYER_ID]?.takeIf { it > 0L }
    }

    suspend fun setActivePlayerId(playerId: Long) {
        dataStore.edit { preferences ->
            preferences[Keys.ACTIVE_PLAYER_ID] = playerId
        }
    }

    suspend fun appendModelApiConfigSuccess(entry: ModelApiConfigHistoryEntry, limit: Int = 5) {
        dataStore.edit { preferences ->
            val existing = decodeHistory(preferences[Keys.MODEL_SUCCESS_HISTORY].orEmpty())
            val deduped = existing.filterNot {
                it.endpoint == entry.endpoint &&
                    it.apiKey == entry.apiKey &&
                    it.model == entry.model
            }
            val updated = (listOf(entry) + deduped).take(limit)
            preferences[Keys.MODEL_SUCCESS_HISTORY] = encodeHistory(updated)
        }
    }

    private fun encrypt(raw: String): String {
        if (raw.isEmpty()) return ""
        val key = xorSecret
        val transformed = raw.mapIndexed { index, char ->
            (char.code xor key[index % key.length].code).toChar()
        }.joinToString(separator = "")
        return android.util.Base64.encodeToString(
            transformed.toByteArray(Charsets.UTF_8),
            android.util.Base64.NO_WRAP
        )
    }

    private fun decrypt(encoded: String): String {
        if (encoded.isEmpty()) return ""
        return runCatching {
            val decoded = String(
                android.util.Base64.decode(encoded, android.util.Base64.NO_WRAP),
                Charsets.UTF_8
            )
            val key = xorSecret
            decoded.mapIndexed { index, char ->
                (char.code xor key[index % key.length].code).toChar()
            }.joinToString(separator = "")
        }.getOrDefault("")
    }

    private fun encodeHistory(entries: List<ModelApiConfigHistoryEntry>): String {
        val jsonArray = JSONArray()
        entries.forEach { entry ->
            jsonArray.put(
                JSONObject()
                    .put("endpoint", entry.endpoint)
                    .put("model", entry.model)
                    .put("apiKey", encrypt(entry.apiKey))
                    .put("testedAt", entry.testedAt)
            )
        }
        return jsonArray.toString()
    }

    private fun decodeHistory(serialized: String): List<ModelApiConfigHistoryEntry> {
        if (serialized.isBlank()) {
            return emptyList()
        }
        val jsonParsed = runCatching {
            val jsonArray = JSONArray(serialized)
            buildList {
                for (index in 0 until jsonArray.length()) {
                    val item = jsonArray.optJSONObject(index) ?: continue
                    add(
                        ModelApiConfigHistoryEntry(
                            endpoint = item.optString("endpoint", ""),
                            model = item.optString("model", ""),
                            apiKey = decrypt(item.optString("apiKey", "")),
                            testedAt = item.optLong("testedAt", 0L)
                        )
                    )
                }
            }
        }.getOrDefault(emptyList())
        if (jsonParsed.isNotEmpty()) {
            return jsonParsed
        }

        return serialized.lines().mapNotNull { line ->
            val parts = splitEscapedPipes(line)
            if (parts.size != 4) {
                null
            } else {
                val testedAt = parts[3].toLongOrNull() ?: return@mapNotNull null
                ModelApiConfigHistoryEntry(
                    endpoint = unescape(parts[0]),
                    model = unescape(parts[1]),
                    apiKey = decrypt(parts[2]),
                    testedAt = testedAt
                )
            }
        }
    }

    private fun splitEscapedPipes(raw: String): List<String> {
        val out = mutableListOf<String>()
        val current = StringBuilder()
        var escape = false
        for (char in raw) {
            if (escape) {
                current.append(char)
                escape = false
            } else if (char == '\\') {
                escape = true
                current.append(char)
            } else if (char == '|') {
                out += current.toString()
                current.clear()
            } else {
                current.append(char)
            }
        }
        out += current.toString()
        return out
    }

    private fun unescape(raw: String): String {
        val output = StringBuilder()
        var escape = false
        for (char in raw) {
            if (escape) {
                output.append(
                    when (char) {
                        'n' -> '\n'
                        else -> char
                    }
                )
                escape = false
            } else if (char == '\\') {
                escape = true
            } else {
                output.append(char)
            }
        }
        if (escape) {
            output.append('\\')
        }
        return output.toString()
    }

    private object Keys {
        val API_BASE_URL = stringPreferencesKey("api_base_url")
        val STREAMING_ENABLED = booleanPreferencesKey("streaming_enabled")
        val MODEL_ENDPOINT = stringPreferencesKey("model_endpoint")
        val MODEL_API_KEY_ENC = stringPreferencesKey("model_api_key_enc")
        val MODEL_NAME = stringPreferencesKey("model_name")
        val MODEL_SUCCESS_HISTORY = stringPreferencesKey("model_success_history")
        val CHAT1_SYSTEM_PROMPT = stringPreferencesKey("chat1_system_prompt")
        val CHAT2_DIARY_SYSTEM_PROMPT = stringPreferencesKey("chat2_diary_system_prompt")
        val CHAT3_LAZY_REPLY_SYSTEM_PROMPT = stringPreferencesKey("chat3_lazy_reply_system_prompt")
        val ACTIVE_PLAYER_ID = longPreferencesKey("active_player_id")
    }

    private companion object {
        const val DEFAULT_PLAYER_ID = 1L
    }

}
