package icu.merky.mj.domain.usecase

import icu.merky.mj.domain.repository.PlayerProfileRepository
import javax.inject.Inject

class SetActivePlayerUseCase @Inject constructor(
    private val playerProfileRepository: PlayerProfileRepository
) {
    suspend operator fun invoke(playerId: Long) {
        playerProfileRepository.setActivePlayerId(playerId)
    }
}
