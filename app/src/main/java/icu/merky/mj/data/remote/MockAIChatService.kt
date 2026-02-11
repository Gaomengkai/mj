package icu.merky.mj.data.remote

import icu.merky.mj.core.result.AppResult
import icu.merky.mj.domain.model.ChatMessage
import icu.merky.mj.domain.model.ChatRole
import icu.merky.mj.domain.repository.AIChatService
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class MockAIChatService @Inject constructor() : AIChatService {
    override fun ping(): Flow<AppResult<Unit>> = flow {
        emit(AppResult.Success(Unit))
    }

    override fun streamReply(messages: List<ChatMessage>, systemPrompt: String): Flow<AppResult<String>> = flow {
        val latestUser = messages.lastOrNull { it.role == ChatRole.USER }?.content.orEmpty()
        val target = if (latestUser.isBlank()) {
            "Hi, I am Yuki."
        } else {
            "I heard you: $latestUser"
        }

        val tokens = target.split(" ")
        var accumulator = ""
        for (token in tokens) {
            accumulator = if (accumulator.isEmpty()) token else "$accumulator $token"
            emit(AppResult.Success(accumulator))
            delay(30)
        }
    }

    override fun generateDiary(messages: List<ChatMessage>, systemPrompt: String): Flow<AppResult<String>> = flow {
        val latestUser = messages.lastOrNull { it.role == ChatRole.USER }?.content.orEmpty()
        val output = if (latestUser.isBlank()) {
            "Today was calm."
        } else {
            "Diary: We talked about '$latestUser'."
        }
        emit(AppResult.Success(output))
    }

    override fun generateQuickReplies(
        messages: List<ChatMessage>,
        systemPrompt: String
    ): Flow<AppResult<List<String>>> = flow {
        emit(
            AppResult.Success(
                listOf(
                    "Got it, tell me more.",
                    "That sounds nice, Yuki."
                )
            )
        )
    }
}
