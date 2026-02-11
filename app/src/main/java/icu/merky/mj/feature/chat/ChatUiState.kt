package icu.merky.mj.feature.chat

import icu.merky.mj.domain.model.ChatMessage
import icu.merky.mj.domain.model.ChatStreamState
import icu.merky.mj.domain.model.QuickReplySuggestion

data class ChatUiState(
    val activePlayerId: Long = 1L,
    val messages: List<ChatMessage> = emptyList(),
    val input: String = "",
    val streamState: ChatStreamState = ChatStreamState.Idle,
    val speechPartial: String = "",
    val listening: Boolean = false,
    val speaking: Boolean = false,
    val quickReplies: List<QuickReplySuggestion> = emptyList(),
    val sessionEnded: Boolean = false,
    val exitMessage: String? = null
)
