package icu.merky.mj.data.repository

import icu.merky.mj.data.local.datastore.SettingsDataStoreSource
import icu.merky.mj.domain.model.PromptConfig
import icu.merky.mj.domain.repository.PromptConfigRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class PromptConfigRepositoryImpl @Inject constructor(
    private val settingsDataStoreSource: SettingsDataStoreSource
) : PromptConfigRepository {
    override fun observePromptConfig(): Flow<PromptConfig> {
        return settingsDataStoreSource.observePromptConfig()
    }

    override suspend fun savePromptConfig(config: PromptConfig) {
        settingsDataStoreSource.savePromptConfig(config)
    }
}
