package icu.merky.mj.data.repository

import icu.merky.mj.data.local.datastore.SettingsDataStoreSource
import icu.merky.mj.domain.model.SystemSettings
import icu.merky.mj.domain.repository.SystemSettingsRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SystemSettingsRepositoryImpl @Inject constructor(
    private val settingsDataStoreSource: SettingsDataStoreSource
) : SystemSettingsRepository {
    override fun observeSettings(): Flow<SystemSettings> = settingsDataStoreSource.observeSettings()

    override suspend fun updateApiBaseUrl(url: String) {
        settingsDataStoreSource.updateApiBaseUrl(url)
    }

    override suspend fun updateStreamingEnabled(enabled: Boolean) {
        settingsDataStoreSource.updateStreamingEnabled(enabled)
    }
}
