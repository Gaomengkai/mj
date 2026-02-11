package icu.merky.mj.domain.usecase

import icu.merky.mj.domain.model.RelationshipMood
import icu.merky.mj.domain.model.RelationshipState
import icu.merky.mj.domain.repository.RelationshipRepository
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class AdjustRelationshipStateUseCase @Inject constructor(
    private val relationshipRepository: RelationshipRepository
) {
    suspend operator fun invoke(
        affectionDelta: Int,
        trustDelta: Int,
        updatedAt: Long
    ) {
        val current = relationshipRepository.observeRelationshipState().first()
        val nextAffection = (current.affection + affectionDelta).coerceIn(MIN_SCORE, MAX_SCORE)
        val nextTrust = (current.trust + trustDelta).coerceIn(MIN_SCORE, MAX_SCORE)
        val nextMood = resolveMood(nextAffection, nextTrust)

        relationshipRepository.updateRelationshipState(
            RelationshipState(
                affection = nextAffection,
                trust = nextTrust,
                mood = nextMood,
                updatedAt = updatedAt
            )
        )
    }

    internal fun resolveMood(affection: Int, trust: Int): RelationshipMood {
        val average = (affection + trust) / 2
        return when {
            average >= 70 -> RelationshipMood.AFFECTIONATE
            average <= 30 -> RelationshipMood.DISTANT
            else -> RelationshipMood.NEUTRAL
        }
    }

    companion object {
        const val MIN_SCORE = 0
        const val MAX_SCORE = 100
    }
}
