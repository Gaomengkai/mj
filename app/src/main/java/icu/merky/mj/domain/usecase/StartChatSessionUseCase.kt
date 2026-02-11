package icu.merky.mj.domain.usecase

import icu.merky.mj.core.result.AppResult
import icu.merky.mj.domain.repository.ChatRepository
import javax.inject.Inject

class StartChatSessionUseCase @Inject constructor(
    private val chatRepository: ChatRepository
) {
    suspend operator fun invoke(sessionId: Long): AppResult<Unit> {
        return chatRepository.ensureSession(sessionId)
    }
}
