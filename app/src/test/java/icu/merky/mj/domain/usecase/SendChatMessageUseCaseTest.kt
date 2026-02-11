package icu.merky.mj.domain.usecase

import icu.merky.mj.core.result.AppError
import icu.merky.mj.core.result.AppResult
import icu.merky.mj.domain.model.ChatMessage
import icu.merky.mj.domain.model.ChatRole
import icu.merky.mj.domain.model.ChatStreamState
import icu.merky.mj.domain.repository.ChatRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertTrue
import org.junit.Test

class SendChatMessageUseCaseTest {
    @Test
    fun `invoke returns validation failure when content blank`() = runTest {
        val useCase = SendChatMessageUseCase(FakeChatRepository())

        val result = useCase(sessionId = 1L, content = "   ")

        assertTrue(result is AppResult.Failure)
        assertTrue((result as AppResult.Failure).error is AppError.Validation)
    }

    private class FakeChatRepository : ChatRepository {
        private val messages = MutableStateFlow<List<ChatMessage>>(emptyList())

        override fun observeMessages(sessionId: Long): Flow<List<ChatMessage>> = messages

        override suspend fun ensureSession(sessionId: Long): AppResult<Unit> = AppResult.Success(Unit)

        override suspend fun sendUserMessage(sessionId: Long, content: String): AppResult<Unit> {
            return AppResult.Success(Unit)
        }

        override fun streamAssistantResponse(sessionId: Long): Flow<ChatStreamState> = emptyFlow()
    }
}
