package icu.merky.mj.domain.model

data class RelationshipState(
    val affection: Int,
    val trust: Int,
    val mood: RelationshipMood,
    val updatedAt: Long
)
