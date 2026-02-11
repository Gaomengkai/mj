package icu.merky.mj.feature.settings

import icu.merky.mj.domain.model.ModelApiConfigHistoryEntry
import icu.merky.mj.domain.model.PlayerProfile

data class SettingsUiState(
    val endpoint: String = "",
    val apiKey: String = "",
    val model: String = "",
    val chat1SystemPrompt: String = "",
    val chat2DiarySystemPrompt: String = "",
    val chat3LazyReplySystemPrompt: String = "",
    val isApiTesting: Boolean = false,
    val apiStatusMessage: String? = null,
    val promptStatusMessage: String? = null,
    val successHistory: List<ModelApiConfigHistoryEntry> = emptyList(),
    val players: List<PlayerProfile> = emptyList(),
    val activePlayerId: Long = 1L,
    val newPlayerName: String = ""
)
