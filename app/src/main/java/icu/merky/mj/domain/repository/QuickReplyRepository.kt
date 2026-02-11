package icu.merky.mj.domain.repository

import icu.merky.mj.domain.model.QuickReplySuggestion
import kotlinx.coroutines.flow.Flow

interface QuickReplyRepository {
    fun observeSuggestions(sessionId: Long): Flow<List<QuickReplySuggestion>>
    suspend fun replaceSuggestions(sessionId: Long, suggestions: List<String>, createdAt: Long)
    suspend fun clearSuggestions(sessionId: Long)
}
