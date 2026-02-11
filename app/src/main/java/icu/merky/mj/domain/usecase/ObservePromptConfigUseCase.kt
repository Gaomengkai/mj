package icu.merky.mj.domain.usecase

import icu.merky.mj.domain.model.PromptConfig
import icu.merky.mj.domain.repository.PromptConfigRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObservePromptConfigUseCase @Inject constructor(
    private val promptConfigRepository: PromptConfigRepository
) {
    operator fun invoke(): Flow<PromptConfig> = promptConfigRepository.observePromptConfig()
}
