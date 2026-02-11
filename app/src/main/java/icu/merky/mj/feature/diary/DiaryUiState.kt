package icu.merky.mj.feature.diary

import icu.merky.mj.domain.model.DiaryEntry

data class DiaryUiState(
    val activePlayerId: Long = 1L,
    val entries: List<DiaryEntry> = emptyList()
)
