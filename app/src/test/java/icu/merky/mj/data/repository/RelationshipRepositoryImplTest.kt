package icu.merky.mj.data.repository

import icu.merky.mj.data.local.db.dao.RelationshipStateDao
import icu.merky.mj.data.local.db.entity.RelationshipStateEntity
import icu.merky.mj.domain.model.RelationshipMood
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class RelationshipRepositoryImplTest {
    @Test
    fun `observeRelationshipState returns default when dao is empty`() = runTest {
        val dao = FakeRelationshipStateDao(null)
        val repository = RelationshipRepositoryImpl(dao)

        val state = repository.observeRelationshipState().first()

        assertEquals(50, state.affection)
        assertEquals(50, state.trust)
        assertEquals(RelationshipMood.NEUTRAL, state.mood)
    }

    @Test
    fun `updateRelationshipState persists mapped entity`() = runTest {
        val dao = FakeRelationshipStateDao(null)
        val repository = RelationshipRepositoryImpl(dao)

        repository.updateRelationshipState(
            icu.merky.mj.domain.model.RelationshipState(
                affection = 72,
                trust = 65,
                mood = RelationshipMood.AFFECTIONATE,
                updatedAt = 1234L
            )
        )

        val entity = dao.lastUpserted
        assertEquals(72, entity?.affection)
        assertEquals(65, entity?.trust)
        assertEquals("AFFECTIONATE", entity?.mood)
        assertEquals(1234L, entity?.updatedAt)
    }

    private class FakeRelationshipStateDao(
        initial: RelationshipStateEntity?
    ) : RelationshipStateDao {
        private val state = MutableStateFlow(initial)
        var lastUpserted: RelationshipStateEntity? = null

        override fun observeRelationshipState(): Flow<RelationshipStateEntity?> = state

        override suspend fun upsert(entity: RelationshipStateEntity) {
            lastUpserted = entity
            state.value = entity
        }
    }
}
