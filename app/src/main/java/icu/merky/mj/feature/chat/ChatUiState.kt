package icu.merky.mj.feature.chat

import icu.merky.mj.domain.model.ChatMessage
import icu.merky.mj.domain.model.ChatStreamState

data class ChatUiState(
    val messages: List<ChatMessage> = emptyList(),
    val input: String = "",
    val streamState: ChatStreamState = ChatStreamState.Idle,
    val speechPartial: String = "",
    val listening: Boolean = false,
    val speaking: Boolean = false
)
