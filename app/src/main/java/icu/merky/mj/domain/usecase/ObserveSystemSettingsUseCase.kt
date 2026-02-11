package icu.merky.mj.domain.usecase

import icu.merky.mj.domain.model.SystemSettings
import icu.merky.mj.domain.repository.SystemSettingsRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveSystemSettingsUseCase @Inject constructor(
    private val systemSettingsRepository: SystemSettingsRepository
) {
    operator fun invoke(): Flow<SystemSettings> = systemSettingsRepository.observeSettings()
}
