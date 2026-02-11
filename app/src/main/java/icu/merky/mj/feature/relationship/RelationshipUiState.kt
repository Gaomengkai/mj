package icu.merky.mj.feature.relationship

import icu.merky.mj.domain.model.RelationshipMood
import icu.merky.mj.domain.model.RelationshipState

data class RelationshipUiState(
    val state: RelationshipState = RelationshipState(
        affection = 50,
        trust = 50,
        mood = RelationshipMood.NEUTRAL,
        updatedAt = 0L
    )
)
