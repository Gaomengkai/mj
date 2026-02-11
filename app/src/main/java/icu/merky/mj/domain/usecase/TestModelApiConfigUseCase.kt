package icu.merky.mj.domain.usecase

import icu.merky.mj.core.result.AppResult
import icu.merky.mj.domain.model.ModelApiConfig
import icu.merky.mj.domain.model.ModelApiConfigHistoryEntry
import icu.merky.mj.domain.repository.AIChatService
import icu.merky.mj.domain.repository.ModelApiConfigRepository
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class TestModelApiConfigUseCase @Inject constructor(
    private val saveModelApiConfigUseCase: SaveModelApiConfigUseCase,
    private val modelApiConfigRepository: ModelApiConfigRepository,
    private val aiChatService: AIChatService
) {
    suspend operator fun invoke(config: ModelApiConfig): AppResult<Unit> {
        val saved = saveModelApiConfigUseCase(config)
        if (saved is AppResult.Failure) {
            return saved
        }

        val ping = aiChatService.ping().first()
        if (ping is AppResult.Success) {
            modelApiConfigRepository.appendSuccessHistory(
                ModelApiConfigHistoryEntry(
                    endpoint = config.endpoint.trim(),
                    apiKey = config.apiKey.trim(),
                    model = config.model.trim(),
                    testedAt = System.currentTimeMillis()
                )
            )
        }
        return ping
    }
}
