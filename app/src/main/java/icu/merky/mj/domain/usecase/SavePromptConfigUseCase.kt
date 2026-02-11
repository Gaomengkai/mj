package icu.merky.mj.domain.usecase

import icu.merky.mj.core.result.AppResult
import icu.merky.mj.domain.model.PromptConfig
import icu.merky.mj.domain.repository.PromptConfigRepository
import javax.inject.Inject

class SavePromptConfigUseCase @Inject constructor(
    private val promptConfigRepository: PromptConfigRepository
) {
    suspend operator fun invoke(config: PromptConfig): AppResult<Unit> {
        promptConfigRepository.savePromptConfig(config)
        return AppResult.Success(Unit)
    }
}
