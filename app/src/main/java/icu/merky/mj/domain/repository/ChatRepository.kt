package icu.merky.mj.domain.repository

import icu.merky.mj.core.result.AppResult
import icu.merky.mj.domain.model.ChatMessage
import icu.merky.mj.domain.model.ChatStreamState
import kotlinx.coroutines.flow.Flow

interface ChatRepository {
    fun observeMessages(sessionId: Long): Flow<List<ChatMessage>>
    suspend fun ensureSession(sessionId: Long): AppResult<Unit>
    suspend fun sendUserMessage(sessionId: Long, content: String): AppResult<Unit>
    fun streamAssistantResponse(sessionId: Long): Flow<ChatStreamState>
}
