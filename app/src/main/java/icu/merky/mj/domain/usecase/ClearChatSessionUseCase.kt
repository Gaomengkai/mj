package icu.merky.mj.domain.usecase

import icu.merky.mj.core.result.AppResult
import icu.merky.mj.domain.repository.ChatRepository
import icu.merky.mj.domain.repository.QuickReplyRepository
import javax.inject.Inject

class ClearChatSessionUseCase @Inject constructor(
    private val chatRepository: ChatRepository,
    private val quickReplyRepository: QuickReplyRepository
) {
    suspend operator fun invoke(sessionId: Long): AppResult<Unit> {
        val result = chatRepository.clearSessionMessages(sessionId)
        quickReplyRepository.clearSuggestions(sessionId)
        return result
    }
}
