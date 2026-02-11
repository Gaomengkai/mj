package icu.merky.mj.domain.usecase

import icu.merky.mj.domain.repository.PlayerProfileRepository
import javax.inject.Inject

class CreatePlayerUseCase @Inject constructor(
    private val playerProfileRepository: PlayerProfileRepository
) {
    suspend operator fun invoke(name: String): Long {
        return playerProfileRepository.createPlayer(name)
    }
}
