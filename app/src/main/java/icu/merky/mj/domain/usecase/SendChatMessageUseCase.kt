package icu.merky.mj.domain.usecase

import icu.merky.mj.core.result.AppError
import icu.merky.mj.core.result.AppResult
import icu.merky.mj.domain.repository.ChatRepository
import javax.inject.Inject

class SendChatMessageUseCase @Inject constructor(
    private val chatRepository: ChatRepository
) {
    suspend operator fun invoke(sessionId: Long, content: String): AppResult<Unit> {
        if (content.isBlank()) {
            return AppResult.Failure(AppError.Validation("Message cannot be blank."))
        }

        val ensured = chatRepository.ensureSession(sessionId)
        if (ensured is AppResult.Failure) {
            return ensured
        }

        return chatRepository.sendUserMessage(sessionId = sessionId, content = content.trim())
    }
}
