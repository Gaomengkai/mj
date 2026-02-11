package icu.merky.mj.domain.usecase

import icu.merky.mj.domain.model.ChatStreamState
import icu.merky.mj.domain.repository.ChatRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class StreamAssistantResponseUseCase @Inject constructor(
    private val chatRepository: ChatRepository
) {
    operator fun invoke(sessionId: Long): Flow<ChatStreamState> {
        return chatRepository.streamAssistantResponse(sessionId)
    }
}
