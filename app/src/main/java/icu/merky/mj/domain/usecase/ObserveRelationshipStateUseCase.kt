package icu.merky.mj.domain.usecase

import icu.merky.mj.domain.model.RelationshipState
import icu.merky.mj.domain.repository.RelationshipRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveRelationshipStateUseCase @Inject constructor(
    private val relationshipRepository: RelationshipRepository
) {
    operator fun invoke(): Flow<RelationshipState> = relationshipRepository.observeRelationshipState()
}
