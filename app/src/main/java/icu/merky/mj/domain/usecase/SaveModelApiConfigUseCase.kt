package icu.merky.mj.domain.usecase

import icu.merky.mj.core.result.AppError
import icu.merky.mj.core.result.AppResult
import icu.merky.mj.domain.model.ModelApiConfig
import icu.merky.mj.domain.repository.ModelApiConfigRepository
import javax.inject.Inject

class SaveModelApiConfigUseCase @Inject constructor(
    private val modelApiConfigRepository: ModelApiConfigRepository
) {
    suspend operator fun invoke(config: ModelApiConfig): AppResult<Unit> {
        if (config.endpoint.isBlank()) {
            return AppResult.Failure(AppError.Validation("Endpoint cannot be blank."))
        }
        if (config.apiKey.isBlank()) {
            return AppResult.Failure(AppError.Validation("API key cannot be blank."))
        }
        if (config.model.isBlank()) {
            return AppResult.Failure(AppError.Validation("Model cannot be blank."))
        }

        modelApiConfigRepository.saveCurrentConfig(
            config.copy(
                endpoint = config.endpoint.trim(),
                apiKey = config.apiKey.trim(),
                model = config.model.trim()
            )
        )
        return AppResult.Success(Unit)
    }
}
