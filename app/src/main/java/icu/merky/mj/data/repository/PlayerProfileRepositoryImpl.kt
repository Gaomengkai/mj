package icu.merky.mj.data.repository

import icu.merky.mj.data.local.datastore.SettingsDataStoreSource
import icu.merky.mj.data.local.db.dao.PlayerProfileDao
import icu.merky.mj.data.local.db.entity.PlayerProfileEntity
import icu.merky.mj.domain.model.PlayerProfile
import icu.merky.mj.domain.repository.PlayerProfileRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class PlayerProfileRepositoryImpl @Inject constructor(
    private val playerProfileDao: PlayerProfileDao,
    private val settingsDataStoreSource: SettingsDataStoreSource
) : PlayerProfileRepository {
    override fun observePlayers(): Flow<List<PlayerProfile>> {
        return playerProfileDao.observePlayers().map { entities ->
            entities.map { entity ->
                PlayerProfile(
                    id = entity.id,
                    name = entity.name,
                    createdAt = entity.createdAt
                )
            }
        }
    }

    override fun observeActivePlayerId(): Flow<Long> {
        return settingsDataStoreSource.observeActivePlayerId()
    }

    override suspend fun setActivePlayerId(playerId: Long) {
        val target = playerProfileDao.getById(playerId) ?: return
        settingsDataStoreSource.setActivePlayerId(target.id)
    }

    override suspend fun createPlayer(name: String): Long {
        val normalized = name.trim()
        val finalName = if (normalized.isBlank()) {
            "Player ${System.currentTimeMillis() % 10000}"
        } else {
            normalized
        }
        val playerId = playerProfileDao.insert(
            PlayerProfileEntity(
                name = finalName,
                createdAt = System.currentTimeMillis()
            )
        )
        settingsDataStoreSource.setActivePlayerId(playerId)
        return playerId
    }

    override suspend fun ensureDefaultPlayer(): Long {
        val defaultPlayerId = ensurePlayerExists()
        val activeId = settingsDataStoreSource.getActivePlayerIdOrNull()
        val validActive = activeId?.let { playerProfileDao.getById(it) }

        val resolvedId = when {
            validActive != null -> validActive.id
            else -> defaultPlayerId
        }

        settingsDataStoreSource.setActivePlayerId(resolvedId)
        return resolvedId
    }

    private suspend fun ensurePlayerExists(): Long {
        val first = playerProfileDao.getFirstPlayer()
        if (first != null) {
            return first.id
        }
        return playerProfileDao.insert(
            PlayerProfileEntity(
                name = DEFAULT_PLAYER_NAME,
                createdAt = System.currentTimeMillis()
            )
        )
    }

    private companion object {
        const val DEFAULT_PLAYER_NAME = "Player 1"
    }
}
