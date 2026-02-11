package icu.merky.mj.domain.usecase

import icu.merky.mj.domain.model.PlayerProfile
import icu.merky.mj.domain.repository.PlayerProfileRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObservePlayersUseCase @Inject constructor(
    private val playerProfileRepository: PlayerProfileRepository
) {
    operator fun invoke(): Flow<List<PlayerProfile>> = playerProfileRepository.observePlayers()
}
