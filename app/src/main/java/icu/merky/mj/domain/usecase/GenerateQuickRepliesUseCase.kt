package icu.merky.mj.domain.usecase

import icu.merky.mj.core.result.AppResult
import icu.merky.mj.domain.model.ChatMessage
import icu.merky.mj.domain.model.ChatRole
import icu.merky.mj.domain.repository.AIChatService
import icu.merky.mj.domain.repository.ChatRepository
import icu.merky.mj.domain.repository.PromptConfigRepository
import icu.merky.mj.domain.repository.QuickReplyRepository
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class GenerateQuickRepliesUseCase @Inject constructor(
    private val chatRepository: ChatRepository,
    private val quickReplyRepository: QuickReplyRepository,
    private val promptConfigRepository: PromptConfigRepository,
    private val aiChatService: AIChatService
) {
    suspend operator fun invoke(sessionId: Long): AppResult<Unit> {
        val allMessages = chatRepository.observeMessages(sessionId).first()
        if (allMessages.isEmpty()) {
            return AppResult.Success(Unit)
        }

        val recentMessages = allMessages.takeLast(4)
        val promptConfig = promptConfigRepository.observePromptConfig().first()
        val chat3Prompt = replaceLastResponsePlaceholder(
            basePrompt = promptConfig.chat3LazyReplySystemPrompt,
            allMessages = recentMessages
        )
        val response = aiChatService.generateQuickReplies(
            messages = emptyList(),
            systemPrompt = chat3Prompt
        ).first()

        return when (response) {
            is AppResult.Success -> {
                quickReplyRepository.replaceSuggestions(
                    sessionId = sessionId,
                    suggestions = response.data.take(2),
                    createdAt = System.currentTimeMillis()
                )
                AppResult.Success(Unit)
            }

            is AppResult.Failure -> response
        }
    }

    private fun replaceLastResponsePlaceholder(
        basePrompt: String,
        allMessages: List<ChatMessage>
    ): String {
        if (!basePrompt.contains(LAST_RESPONSE_PLACEHOLDER)) {
            return basePrompt
        }

        val recentDialogue = allMessages
            .filter { message -> message.role == ChatRole.USER || message.role == ChatRole.ASSISTANT }
            .takeLast(4)
            .joinToString(separator = "\n") { message ->
                val rolePrefix = when (message.role) {
                    ChatRole.USER -> "【玩家】"
                    ChatRole.ASSISTANT -> "【Yuki的台词】"
                    ChatRole.SYSTEM -> "System"
                }
                "$rolePrefix: ${message.content}"
            }

        return basePrompt.replace(LAST_RESPONSE_PLACEHOLDER, recentDialogue)
    }

    private companion object {
        const val LAST_RESPONSE_PLACEHOLDER = "\${LASTRESPONSE}"
    }
}
