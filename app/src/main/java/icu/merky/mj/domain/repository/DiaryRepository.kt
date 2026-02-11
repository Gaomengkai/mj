package icu.merky.mj.domain.repository

import icu.merky.mj.domain.model.DiaryEntry
import kotlinx.coroutines.flow.Flow

interface DiaryRepository {
    fun observeRecentEntries(sessionId: Long, limit: Int): Flow<List<DiaryEntry>>
    suspend fun getRecentEntries(sessionId: Long, limit: Int): List<DiaryEntry>
    suspend fun addEntry(sessionId: Long, content: String, createdAt: Long)
}
