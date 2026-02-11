package icu.merky.mj.domain.usecase

import icu.merky.mj.domain.repository.DiaryRepository
import icu.merky.mj.domain.repository.PromptConfigRepository
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class BuildChat1SystemPromptUseCase @Inject constructor(
    private val promptConfigRepository: PromptConfigRepository,
    private val diaryRepository: DiaryRepository
) {
    suspend operator fun invoke(sessionId: Long, memoryLimit: Int = 5): String {
        val promptConfig = promptConfigRepository.observePromptConfig().first()
        val diary = diaryRepository.observeRecentEntries(sessionId, memoryLimit).first()
        if (diary.isEmpty()) {
            return promptConfig.chat1SystemPrompt
        }

        val memoryBlock = diary
            .sortedByDescending { it.createdAt }
            .joinToString(separator = "\n") { entry ->
                "- [${entry.createdAt}] ${entry.content}"
            }

        return buildString {
            append(promptConfig.chat1SystemPrompt)
            append("\n\n")
            append("[MEMORY DIARY]\n")
            append(memoryBlock)
        }
    }
}
