package icu.merky.mj.domain.usecase

import icu.merky.mj.core.result.AppError
import icu.merky.mj.core.result.AppResult
import icu.merky.mj.domain.model.ChatMessage
import icu.merky.mj.domain.model.ChatRole
import icu.merky.mj.domain.repository.AIChatService
import icu.merky.mj.domain.repository.ChatRepository
import icu.merky.mj.domain.repository.DiaryRepository
import icu.merky.mj.domain.repository.PromptConfigRepository
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class GenerateDiaryOnChatExitUseCase @Inject constructor(
    private val chatRepository: ChatRepository,
    private val diaryRepository: DiaryRepository,
    private val promptConfigRepository: PromptConfigRepository,
    private val aiChatService: AIChatService
) {
    suspend operator fun invoke(
        sessionId: Long,
        sourceMessages: List<ChatMessage>? = null
    ): AppResult<Unit> {
        val messages = sourceMessages ?: chatRepository.observeMessages(sessionId).first()
        if (messages.isEmpty()) {
            return AppResult.Failure(AppError.Data("No messages to generate diary."))
        }

        val promptConfig = promptConfigRepository.observePromptConfig().first()
        val systemPrompt = buildDiarySystemPrompt(
            basePrompt = promptConfig.chat2DiarySystemPrompt,
            messages = messages
        )

        val diaryResult = aiChatService.generateDiary(
            messages = emptyList(),
            systemPrompt = systemPrompt
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

    private fun buildDiarySystemPrompt(basePrompt: String, messages: List<ChatMessage>): String {
        val dialogueBlock = messages
            .filter { message -> message.role == ChatRole.USER || message.role == ChatRole.ASSISTANT }
            .joinToString(separator = "\n") { message ->
                val rolePrefix = when (message.role) {
                    ChatRole.USER -> "【玩家】"
                    ChatRole.ASSISTANT -> "【Yuki】"
                    ChatRole.SYSTEM -> "System"
                }
                "$rolePrefix: ${message.content}"
            }

        if (dialogueBlock.isBlank()) {
            return basePrompt
        }

        return buildString {
            append(basePrompt)
            append("\n\n")
            append("[DIALOGUE]\n")
            append(dialogueBlock)
        }
    }
}
