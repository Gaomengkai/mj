package icu.merky.mj.data.repository

import icu.merky.mj.core.result.AppError
import icu.merky.mj.core.result.AppResult
import icu.merky.mj.data.local.db.dao.HealthCheckDao
import icu.merky.mj.data.local.db.entity.HealthCheckEntity
import icu.merky.mj.domain.repository.HealthRepository
import javax.inject.Inject

class HealthRepositoryImpl @Inject constructor(
    private val healthCheckDao: HealthCheckDao
) : HealthRepository {
    override suspend fun ping(): AppResult<String> = runCatching {
        val marker = "ok"
        healthCheckDao.upsert(HealthCheckEntity(label = marker))
        healthCheckDao.getHealthCheck()?.label ?: marker
    }.fold(
        onSuccess = { AppResult.Success(it) },
        onFailure = { AppResult.Failure(AppError.Data(it.message ?: "Database access failed.")) }
    )
}
