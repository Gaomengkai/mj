package icu.merky.mj.domain.repository

import icu.merky.mj.core.result.AppResult
import icu.merky.mj.domain.model.ChatMessage
import kotlinx.coroutines.flow.Flow

interface AIChatService {
    fun ping(): Flow<AppResult<Unit>>
    fun streamReply(messages: List<ChatMessage>, systemPrompt: String = ""): Flow<AppResult<String>>
    fun generateDiary(messages: List<ChatMessage>, systemPrompt: String): Flow<AppResult<String>>
    fun generateQuickReplies(messages: List<ChatMessage>, systemPrompt: String): Flow<AppResult<List<String>>>
}
