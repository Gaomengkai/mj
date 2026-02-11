package icu.merky.mj.domain.usecase

import icu.merky.mj.domain.model.ChatMessage
import icu.merky.mj.domain.repository.ChatRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveConversationMessagesUseCase @Inject constructor(
    private val chatRepository: ChatRepository
) {
    operator fun invoke(sessionId: Long): Flow<List<ChatMessage>> {
        return chatRepository.observeMessages(sessionId)
    }
}
