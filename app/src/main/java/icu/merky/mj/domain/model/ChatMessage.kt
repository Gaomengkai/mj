package icu.merky.mj.domain.model

data class ChatMessage(
    val id: Long,
    val sessionId: Long,
    val role: ChatRole,
    val content: String,
    val createdAt: Long
)
