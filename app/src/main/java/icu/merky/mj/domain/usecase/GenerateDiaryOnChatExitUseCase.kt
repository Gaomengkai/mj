package icu.merky.mj.domain.usecase

import icu.merky.mj.core.result.AppError
import icu.merky.mj.core.result.AppResult
import icu.merky.mj.domain.repository.AIChatService
import icu.merky.mj.domain.repository.ChatRepository
import icu.merky.mj.domain.repository.DiaryRepository
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class GenerateDiaryOnChatExitUseCase @Inject constructor(
    private val chatRepository: ChatRepository,
    private val diaryRepository: DiaryRepository,
    private val aiChatService: AIChatService
) {
    suspend operator fun invoke(sessionId: Long): AppResult<Unit> {
        val messages = chatRepository.observeMessages(sessionId).first()
        if (messages.isEmpty()) {
            return AppResult.Success(Unit)
        }

        val diaryResult = aiChatService.generateDiary(
            messages = messages,
            systemPrompt = ""
        ).first()

        return when (diaryResult) {
            is AppResult.Success -> {
                val content = diaryResult.data.trim()
                if (content.isBlank()) {
                    AppResult.Failure(AppError.Data("Diary generation returned empty content."))
                } else {
                    diaryRepository.addEntry(
                        sessionId = sessionId,
                        content = content,
                        createdAt = System.currentTimeMillis()
                    )
                    AppResult.Success(Unit)
                }
            }

            is AppResult.Failure -> diaryResult
        }
    }
}
