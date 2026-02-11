package icu.merky.mj.domain.repository

import icu.merky.mj.core.result.AppResult

interface HealthRepository {
    suspend fun ping(): AppResult<String>
}
