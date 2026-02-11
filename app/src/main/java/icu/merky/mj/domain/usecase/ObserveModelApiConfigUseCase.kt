package icu.merky.mj.domain.usecase

import icu.merky.mj.domain.model.ModelApiConfig
import icu.merky.mj.domain.repository.ModelApiConfigRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveModelApiConfigUseCase @Inject constructor(
    private val modelApiConfigRepository: ModelApiConfigRepository
) {
    operator fun invoke(): Flow<ModelApiConfig> = modelApiConfigRepository.observeCurrentConfig()
}
