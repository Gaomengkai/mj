package icu.merky.mj.domain.repository

import icu.merky.mj.domain.model.RelationshipState
import kotlinx.coroutines.flow.Flow

interface RelationshipRepository {
    fun observeRelationshipState(): Flow<RelationshipState>
    suspend fun updateRelationshipState(state: RelationshipState)
}
