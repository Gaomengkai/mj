package icu.merky.mj.domain.usecase

import icu.merky.mj.domain.model.QuickReplySuggestion
import icu.merky.mj.domain.repository.QuickReplyRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveQuickReplySuggestionsUseCase @Inject constructor(
    private val quickReplyRepository: QuickReplyRepository
) {
    operator fun invoke(sessionId: Long): Flow<List<QuickReplySuggestion>> {
        return quickReplyRepository.observeSuggestions(sessionId)
    }
}
