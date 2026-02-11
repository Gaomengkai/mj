package icu.merky.mj.domain.model

data class ModelApiConfigHistoryEntry(
    val endpoint: String,
    val apiKey: String,
    val model: String,
    val testedAt: Long
)
