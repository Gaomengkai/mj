package icu.merky.mj.domain.repository

import icu.merky.mj.domain.model.PlayerProfile
import kotlinx.coroutines.flow.Flow

interface PlayerProfileRepository {
    fun observePlayers(): Flow<List<PlayerProfile>>
    fun observeActivePlayerId(): Flow<Long>
    suspend fun setActivePlayerId(playerId: Long)
    suspend fun createPlayer(name: String): Long
    suspend fun ensureDefaultPlayer(): Long
}
