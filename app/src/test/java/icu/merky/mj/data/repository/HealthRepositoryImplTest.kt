package icu.merky.mj.data.repository

import icu.merky.mj.core.result.AppResult
import icu.merky.mj.data.local.db.dao.HealthCheckDao
import icu.merky.mj.data.local.db.entity.HealthCheckEntity
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class HealthRepositoryImplTest {
    @Test
    fun `ping returns success when dao operations succeed`() = runTest {
        val dao = FakeHealthCheckDao()
        val repository = HealthRepositoryImpl(dao)

        val result = repository.ping()

        assertTrue(result is AppResult.Success)
        assertEquals("ok", (result as AppResult.Success).data)
    }

    private class FakeHealthCheckDao : HealthCheckDao {
        private var cached: HealthCheckEntity? = null

        override suspend fun upsert(entity: HealthCheckEntity) {
            cached = entity
        }

        override suspend fun getHealthCheck(): HealthCheckEntity? = cached
    }
}
