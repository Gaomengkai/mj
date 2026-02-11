package icu.merky.mj.domain.usecase

import icu.merky.mj.core.result.AppError
import icu.merky.mj.core.result.AppResult
import icu.merky.mj.domain.repository.SystemSettingsRepository
import javax.inject.Inject

class UpdateApiBaseUrlUseCase @Inject constructor(
    private val systemSettingsRepository: SystemSettingsRepository
) {
    suspend operator fun invoke(url: String): AppResult<Unit> {
        if (url.isBlank()) {
            return AppResult.Failure(AppError.Validation("API base URL cannot be blank."))
        }
        systemSettingsRepository.updateApiBaseUrl(url.trim())
        return AppResult.Success(Unit)
    }
}
