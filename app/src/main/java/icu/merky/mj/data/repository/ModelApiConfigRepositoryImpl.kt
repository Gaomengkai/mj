package icu.merky.mj.data.repository

import icu.merky.mj.data.local.datastore.SettingsDataStoreSource
import icu.merky.mj.domain.model.ModelApiConfig
import icu.merky.mj.domain.model.ModelApiConfigHistoryEntry
import icu.merky.mj.domain.repository.ModelApiConfigRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ModelApiConfigRepositoryImpl @Inject constructor(
    private val settingsDataStoreSource: SettingsDataStoreSource
) : ModelApiConfigRepository {
    override fun observeCurrentConfig(): Flow<ModelApiConfig> {
        return settingsDataStoreSource.observeModelApiConfig()
    }

    override fun observeSuccessHistory(): Flow<List<ModelApiConfigHistoryEntry>> {
        return settingsDataStoreSource.observeModelApiConfigHistory()
    }

    override suspend fun saveCurrentConfig(config: ModelApiConfig) {
        settingsDataStoreSource.saveModelApiConfig(config)
        settingsDataStoreSource.updateApiBaseUrl(config.endpoint)
    }

    override suspend fun appendSuccessHistory(entry: ModelApiConfigHistoryEntry) {
        settingsDataStoreSource.appendModelApiConfigSuccess(entry)
    }
}
