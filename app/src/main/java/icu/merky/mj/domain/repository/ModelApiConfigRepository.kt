package icu.merky.mj.domain.repository

import icu.merky.mj.domain.model.ModelApiConfig
import icu.merky.mj.domain.model.ModelApiConfigHistoryEntry
import kotlinx.coroutines.flow.Flow

interface ModelApiConfigRepository {
    fun observeCurrentConfig(): Flow<ModelApiConfig>
    fun observeSuccessHistory(): Flow<List<ModelApiConfigHistoryEntry>>
    suspend fun saveCurrentConfig(config: ModelApiConfig)
    suspend fun appendSuccessHistory(entry: ModelApiConfigHistoryEntry)
}
