package icu.merky.mj.data.repository

import icu.merky.mj.domain.model.QuickReplySuggestion
import icu.merky.mj.domain.repository.QuickReplyRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class QuickReplyRepositoryImpl @Inject constructor() : QuickReplyRepository {
    private val state = MutableStateFlow<Map<Long, List<QuickReplySuggestion>>>(emptyMap())
    private var nextId = 1L

    override fun observeSuggestions(sessionId: Long): Flow<List<QuickReplySuggestion>> {
        return state.map { map -> map[sessionId].orEmpty() }
    }

    override suspend fun replaceSuggestions(sessionId: Long, suggestions: List<String>, createdAt: Long) {
        val mapped = suggestions
            .map { it.trim() }
            .filter { it.isNotBlank() }
            .take(2)
            .map { content ->
                QuickReplySuggestion(
                    id = nextId++,
                    sessionId = sessionId,
                    content = content,
                    createdAt = createdAt
                )
            }
        state.value = state.value.toMutableMap().apply {
            put(sessionId, mapped)
        }
    }

    override suspend fun clearSuggestions(sessionId: Long) {
        state.value = state.value.toMutableMap().apply {
            remove(sessionId)
        }
    }
}
