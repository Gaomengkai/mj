package icu.merky.mj.data.repository

import icu.merky.mj.core.result.AppError
import icu.merky.mj.core.result.AppResult
import icu.merky.mj.data.local.db.dao.ConversationMessageDao
import icu.merky.mj.data.local.db.dao.ConversationSessionDao
import icu.merky.mj.data.local.db.entity.ConversationMessageEntity
import icu.merky.mj.data.local.db.entity.ConversationSessionEntity
import icu.merky.mj.domain.model.ChatMessage
import icu.merky.mj.domain.model.ChatRole
import icu.merky.mj.domain.model.ChatStreamState
import icu.merky.mj.domain.repository.AIChatService
import icu.merky.mj.domain.repository.ChatRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class ChatRepositoryImpl @Inject constructor(
    private val conversationSessionDao: ConversationSessionDao,
    private val conversationMessageDao: ConversationMessageDao,
    private val aiChatService: AIChatService
) : ChatRepository {
    override fun observeMessages(sessionId: Long): Flow<List<ChatMessage>> {
        return conversationMessageDao.observeMessages(sessionId).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun ensureSession(sessionId: Long): AppResult<Unit> = runCatching {
        val now = System.currentTimeMillis()
        conversationSessionDao.insert(
            ConversationSessionEntity(
                id = sessionId,
                title = "Session $sessionId",
                createdAt = now,
                updatedAt = now
            )
        )
    }.fold(
        onSuccess = { AppResult.Success(Unit) },
        onFailure = { AppResult.Failure(AppError.Data(it.message ?: "Failed to create session.")) }
    )

    override suspend fun sendUserMessage(sessionId: Long, content: String): AppResult<Unit> = runCatching {
        val now = System.currentTimeMillis()
        conversationMessageDao.insert(
            ConversationMessageEntity(
                sessionId = sessionId,
                role = ChatRole.USER.name,
                content = content,
                createdAt = now
            )
        )
        conversationSessionDao.updateTimestamp(sessionId = sessionId, updatedAt = now)
    }.fold(
        onSuccess = { AppResult.Success(Unit) },
        onFailure = { AppResult.Failure(AppError.Data(it.message ?: "Failed to store user message.")) }
    )

    override fun streamAssistantResponse(sessionId: Long): Flow<ChatStreamState> = flow {
        emit(ChatStreamState.Loading)
        val contextMessages = conversationMessageDao.getMessages(sessionId).map { it.toDomain() }
        var latest = ""
        var failureReason: String? = null

        aiChatService.streamReply(contextMessages).collect { chunk ->
            when (chunk) {
                is AppResult.Success -> {
                    latest = chunk.data
                    emit(ChatStreamState.Streaming(content = latest))
                }

                is AppResult.Failure -> {
                    failureReason = chunk.error.toString()
                }
            }
        }

        failureReason?.let { reason ->
            emit(ChatStreamState.Error(reason = reason))
            return@flow
        }

        val createdAt = System.currentTimeMillis()
        val insertedId = conversationMessageDao.insert(
            ConversationMessageEntity(
                sessionId = sessionId,
                role = ChatRole.ASSISTANT.name,
                content = latest,
                createdAt = createdAt
            )
        )
        conversationSessionDao.updateTimestamp(sessionId, createdAt)

        val persisted = conversationMessageDao.observeMessages(sessionId).first()
            .first { it.id == insertedId }

        emit(ChatStreamState.Success(message = persisted.toDomain()))
    }

    private fun ConversationMessageEntity.toDomain(): ChatMessage {
        return ChatMessage(
            id = id,
            sessionId = sessionId,
            role = ChatRole.valueOf(role),
            content = content,
            createdAt = createdAt
        )
    }
}
