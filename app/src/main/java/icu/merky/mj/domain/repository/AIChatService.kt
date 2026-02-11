package icu.merky.mj.domain.repository

import icu.merky.mj.core.result.AppResult
import icu.merky.mj.domain.model.ChatMessage
import kotlinx.coroutines.flow.Flow

interface AIChatService {
    fun streamReply(messages: List<ChatMessage>): Flow<AppResult<String>>
}
