package icu.merky.mj.domain.repository

import icu.merky.mj.domain.model.SystemSettings
import kotlinx.coroutines.flow.Flow

interface SystemSettingsRepository {
    fun observeSettings(): Flow<SystemSettings>
    suspend fun updateApiBaseUrl(url: String)
    suspend fun updateStreamingEnabled(enabled: Boolean)
}
