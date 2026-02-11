package icu.merky.mj.data.repository

import icu.merky.mj.data.local.db.dao.RelationshipStateDao
import icu.merky.mj.data.local.db.entity.RelationshipStateEntity
import icu.merky.mj.domain.model.RelationshipMood
import icu.merky.mj.domain.model.RelationshipState
import icu.merky.mj.domain.repository.RelationshipRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class RelationshipRepositoryImpl @Inject constructor(
    private val relationshipStateDao: RelationshipStateDao
) : RelationshipRepository {
    override fun observeRelationshipState(): Flow<RelationshipState> {
        return relationshipStateDao.observeRelationshipState().map { entity ->
            entity?.toDomain() ?: defaultState()
        }
    }

    override suspend fun updateRelationshipState(state: RelationshipState) {
        relationshipStateDao.upsert(state.toEntity())
    }

    private fun RelationshipStateEntity.toDomain(): RelationshipState {
        return RelationshipState(
            affection = affection,
            trust = trust,
            mood = RelationshipMood.valueOf(mood),
            updatedAt = updatedAt
        )
    }

    private fun RelationshipState.toEntity(): RelationshipStateEntity {
        return RelationshipStateEntity(
            affection = affection,
            trust = trust,
            mood = mood.name,
            updatedAt = updatedAt
        )
    }

    private fun defaultState(): RelationshipState {
        return RelationshipState(
            affection = 50,
            trust = 50,
            mood = RelationshipMood.NEUTRAL,
            updatedAt = 0L
        )
    }
}
