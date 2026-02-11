package icu.merky.mj.domain.repository

import icu.merky.mj.domain.model.PromptConfig
import kotlinx.coroutines.flow.Flow

interface PromptConfigRepository {
    fun observePromptConfig(): Flow<PromptConfig>
    suspend fun savePromptConfig(config: PromptConfig)
}
