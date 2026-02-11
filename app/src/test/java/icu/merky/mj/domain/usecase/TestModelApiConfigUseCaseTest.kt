package icu.merky.mj.domain.usecase

import icu.merky.mj.core.result.AppResult
import icu.merky.mj.domain.model.ChatMessage
import icu.merky.mj.domain.model.ModelApiConfig
import icu.merky.mj.domain.model.ModelApiConfigHistoryEntry
import icu.merky.mj.domain.repository.AIChatService
import icu.merky.mj.domain.repository.ModelApiConfigRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class TestModelApiConfigUseCaseTest {
    @Test
    fun `appends history when ping succeeds`() = runTest {
        val repository = FakeModelApiConfigRepository()
        val saveUseCase = SaveModelApiConfigUseCase(repository)
        val useCase = TestModelApiConfigUseCase(
            saveModelApiConfigUseCase = saveUseCase,
            modelApiConfigRepository = repository,
            aiChatService = SuccessAIChatService()
        )

        val input = ModelApiConfig(
            endpoint = "https://example.com",
            apiKey = "sk-test",
            model = "gpt-4o-mini"
        )

        val result = useCase(input)

        assertTrue(result is AppResult.Success)
        val history = repository.historyEntries
        assertEquals(1, history.size)
        assertEquals(input.endpoint, history.first().endpoint)
        assertEquals(input.apiKey, history.first().apiKey)
        assertEquals(input.model, history.first().model)
    }

    @Test
    fun `does not append history when ping fails`() = runTest {
        val repository = FakeModelApiConfigRepository()
        val saveUseCase = SaveModelApiConfigUseCase(repository)
        val useCase = TestModelApiConfigUseCase(
            saveModelApiConfigUseCase = saveUseCase,
            modelApiConfigRepository = repository,
            aiChatService = FailureAIChatService()
        )

        val result = useCase(
            ModelApiConfig(
                endpoint = "https://example.com",
                apiKey = "sk-test",
                model = "gpt-4o-mini"
            )
        )

        assertTrue(result is AppResult.Failure)
        val history = repository.historyEntries
        assertTrue(history.isEmpty())
    }

    private class SuccessAIChatService : AIChatService {
        override fun ping(): Flow<AppResult<Unit>> = flowOf(AppResult.Success(Unit))

        override fun streamReply(messages: List<ChatMessage>, systemPrompt: String): Flow<AppResult<String>> {
            return flowOf(AppResult.Success("ok"))
        }

        override fun generateDiary(messages: List<ChatMessage>, systemPrompt: String): Flow<AppResult<String>> {
            return flowOf(AppResult.Success("diary"))
        }

        override fun generateQuickReplies(
            messages: List<ChatMessage>,
            systemPrompt: String
        ): Flow<AppResult<List<String>>> {
            return flowOf(AppResult.Success(listOf("a", "b")))
        }
    }

    private class FailureAIChatService : AIChatService {
        override fun ping(): Flow<AppResult<Unit>> = flowOf(
            AppResult.Failure(icu.merky.mj.core.result.AppError.Network("401"))
        )

        override fun streamReply(messages: List<ChatMessage>, systemPrompt: String): Flow<AppResult<String>> {
            return flowOf(AppResult.Failure(icu.merky.mj.core.result.AppError.Network("401")))
        }

        override fun generateDiary(messages: List<ChatMessage>, systemPrompt: String): Flow<AppResult<String>> {
            return flowOf(AppResult.Failure(icu.merky.mj.core.result.AppError.Network("401")))
        }

        override fun generateQuickReplies(
            messages: List<ChatMessage>,
            systemPrompt: String
        ): Flow<AppResult<List<String>>> {
            return flowOf(AppResult.Failure(icu.merky.mj.core.result.AppError.Network("401")))
        }
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

        val historyEntries: List<ModelApiConfigHistoryEntry>
            get() = history.value

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
