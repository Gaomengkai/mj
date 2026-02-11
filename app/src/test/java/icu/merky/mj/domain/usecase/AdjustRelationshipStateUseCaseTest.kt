package icu.merky.mj.domain.usecase

import icu.merky.mj.domain.model.RelationshipMood
import icu.merky.mj.domain.model.RelationshipState
import icu.merky.mj.domain.repository.RelationshipRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class AdjustRelationshipStateUseCaseTest {
    @Test
    fun `invoke clamps scores within boundaries`() = runTest {
        val repository = FakeRelationshipRepository(
            RelationshipState(
                affection = 98,
                trust = 2,
                mood = RelationshipMood.NEUTRAL,
                updatedAt = 1L
            )
        )
        val useCase = AdjustRelationshipStateUseCase(repository)

        useCase(affectionDelta = 20, trustDelta = -20, updatedAt = 2L)

        val state = repository.state.value
        assertEquals(100, state.affection)
        assertEquals(0, state.trust)
    }

    @Test
    fun `invoke resolves mood from average score`() = runTest {
        val repository = FakeRelationshipRepository(
            RelationshipState(
                affection = 60,
                trust = 60,
                mood = RelationshipMood.NEUTRAL,
                updatedAt = 1L
            )
        )
        val useCase = AdjustRelationshipStateUseCase(repository)

        useCase(affectionDelta = 20, trustDelta = 20, updatedAt = 2L)
        assertEquals(RelationshipMood.AFFECTIONATE, repository.state.value.mood)

        useCase(affectionDelta = -90, trustDelta = -90, updatedAt = 3L)
        assertEquals(RelationshipMood.DISTANT, repository.state.value.mood)
    }

    private class FakeRelationshipRepository(
        initial: RelationshipState
    ) : RelationshipRepository {
        val state = MutableStateFlow(initial)

        override fun observeRelationshipState(): Flow<RelationshipState> = state

        override suspend fun updateRelationshipState(state: RelationshipState) {
            this.state.value = state
        }
    }
}
