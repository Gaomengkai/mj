package icu.merky.mj.feature.home

import icu.merky.mj.domain.model.SystemSettings

data class HomeUiState(
    val settings: SystemSettings = SystemSettings(
        apiBaseUrl = "",
        streamingEnabled = true
    )
)
