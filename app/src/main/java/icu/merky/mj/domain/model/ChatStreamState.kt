package icu.merky.mj.domain.model

sealed interface ChatStreamState {
    data object Idle : ChatStreamState
    data object Loading : ChatStreamState
    data class Streaming(val content: String) : ChatStreamState
    data class Success(val message: ChatMessage) : ChatStreamState
    data class Error(val reason: String) : ChatStreamState
}
