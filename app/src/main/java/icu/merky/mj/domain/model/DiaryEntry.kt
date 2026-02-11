package icu.merky.mj.domain.model

data class DiaryEntry(
    val id: Long,
    val sessionId: Long,
    val content: String,
    val createdAt: Long
)
