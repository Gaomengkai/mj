package icu.merky.mj.domain.usecase

import icu.merky.mj.domain.repository.PlayerProfileRepository
import javax.inject.Inject

class EnsureDefaultPlayerUseCase @Inject constructor(
    private val playerProfileRepository: PlayerProfileRepository
) {
    suspend operator fun invoke(): Long = playerProfileRepository.ensureDefaultPlayer()
}
