package icu.merky.mj.data.repository

import icu.merky.mj.core.result.AppResult
import icu.merky.mj.data.local.db.dao.ConversationMessageDao
import icu.merky.mj.data.local.db.dao.ConversationSessionDao
import icu.merky.mj.data.local.db.entity.ConversationMessageEntity
import icu.merky.mj.data.local.db.entity.ConversationSessionEntity
import icu.merky.mj.domain.model.ChatMessage
import icu.merky.mj.domain.model.ChatRole
import icu.merky.mj.domain.repository.AIChatService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ChatRepositoryImplTest {
    @Test
    fun `streamAssistantResponse emits loading streaming success`() = runTest {
        val sessionDao = FakeConversationSessionDao()
        val messageDao = FakeConversationMessageDao()
        messageDao.insert(
            ConversationMessageEntity(
                sessionId = 1L,
                role = ChatRole.USER.name,
                content = "hello",
                createdAt = 1L
            )
        )

        val repository = ChatRepositoryImpl(
            conversationSessionDao = sessionDao,
            conversationMessageDao = messageDao,
            aiChatService = FakeAIChatService()
        )

        val states = repository.streamAssistantResponse(1L).toListForTest()

        assertTrue(states.first() is icu.merky.mj.domain.model.ChatStreamState.Loading)
        assertTrue(states.any { it is icu.merky.mj.domain.model.ChatStreamState.Streaming })
        assertTrue(states.last() is icu.merky.mj.domain.model.ChatStreamState.Success)
    }

    @Test
    fun `observeMessages maps local entities to domain`() = runTest {
        val repository = ChatRepositoryImpl(
            conversationSessionDao = FakeConversationSessionDao(),
            conversationMessageDao = FakeConversationMessageDao().also {
                it.insert(
                    ConversationMessageEntity(
                        sessionId = 1L,
                        role = ChatRole.USER.name,
                        content = "test",
                        createdAt = 1L
                    )
                )
            },
            aiChatService = FakeAIChatService()
        )

        val messages = repository.observeMessages(1L).first()

        assertEquals(1, messages.size)
        assertEquals(ChatRole.USER, messages.first().role)
        assertEquals("test", messages.first().content)
    }

    private class FakeAIChatService : AIChatService {
        override fun ping(): Flow<AppResult<Unit>> {
            return flowOf(AppResult.Success(Unit))
        }

        override fun streamReply(messages: List<ChatMessage>, systemPrompt: String): Flow<AppResult<String>> {
            return flowOf(
                AppResult.Success("Hi"),
                AppResult.Success("Hi there")
            )
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

    private class FakeConversationSessionDao : ConversationSessionDao {
        override suspend fun insert(entity: ConversationSessionEntity) {
        }

        override suspend fun updateTimestamp(sessionId: Long, updatedAt: Long) {
        }
    }

    private class FakeConversationMessageDao : ConversationMessageDao {
        private var nextId = 1L
        private val items = MutableStateFlow<List<ConversationMessageEntity>>(emptyList())

        override fun observeMessages(sessionId: Long): Flow<List<ConversationMessageEntity>> {
            return items.map { list -> list.filter { it.sessionId == sessionId } }
        }

        override suspend fun getMessages(sessionId: Long): List<ConversationMessageEntity> {
            return items.value.filter { it.sessionId == sessionId }
        }

        override suspend fun insert(entity: ConversationMessageEntity): Long {
            val id = if (entity.id == 0L) nextId++ else entity.id
            val persisted = entity.copy(id = id)
            items.value = items.value + persisted
            return id
        }

        override suspend fun deleteBySessionId(sessionId: Long) {
            items.value = items.value.filterNot { it.sessionId == sessionId }
        }
    }
}

private suspend fun <T> Flow<T>.toListForTest(): List<T> {
    val out = mutableListOf<T>()
    collect {
        out += it
    }
    return out
}
