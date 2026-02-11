package icu.merky.mj.domain.usecase

import icu.merky.mj.core.result.AppResult
import icu.merky.mj.domain.model.ModelApiConfig
import icu.merky.mj.domain.model.ModelApiConfigHistoryEntry
import icu.merky.mj.domain.repository.ModelApiConfigRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class SaveModelApiConfigUseCaseTest {
    @Test
    fun `returns validation failure when endpoint is blank`() = runTest {
        val repository = FakeModelApiConfigRepository()
        val useCase = SaveModelApiConfigUseCase(repository)

        val result = useCase(
            ModelApiConfig(
                endpoint = "",
                apiKey = "sk-test",
                model = "gpt-4o-mini"
            )
        )

        assertTrue(result is AppResult.Failure)
    }

    @Test
    fun `trims and saves config on success`() = runTest {
        val repository = FakeModelApiConfigRepository()
        val useCase = SaveModelApiConfigUseCase(repository)

        val result = useCase(
            ModelApiConfig(
                endpoint = " https://example.com ",
                apiKey = " sk-test ",
                model = " gpt-4o-mini "
            )
        )

        assertTrue(result is AppResult.Success)
        val saved = repository.currentConfig
        assertEquals("https://example.com", saved.endpoint)
        assertEquals("sk-test", saved.apiKey)
        assertEquals("gpt-4o-mini", saved.model)
    }

    private class FakeModelApiConfigRepository : ModelApiConfigRepository {
        private val current = MutableStateFlow(
            ModelApiConfig(
                endpoint = "",
                apiKey = "",
                model = ""
            )
        )
        private val history = MutableStateFlow<List<ModelApiConfigHistoryEntry>>(emptyList())

        val currentConfig: ModelApiConfig
            get() = current.value

        override fun observeCurrentConfig(): Flow<ModelApiConfig> = current

        override fun observeSuccessHistory(): Flow<List<ModelApiConfigHistoryEntry>> = history

        override suspend fun saveCurrentConfig(config: ModelApiConfig) {
            current.value = config
        }

        override suspend fun appendSuccessHistory(entry: ModelApiConfigHistoryEntry) {
            history.value = listOf(entry) + history.value
        }
    }
}
