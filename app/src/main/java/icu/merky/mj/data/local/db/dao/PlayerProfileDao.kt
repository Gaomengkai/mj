package icu.merky.mj.data.local.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import icu.merky.mj.data.local.db.entity.PlayerProfileEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PlayerProfileDao {
    @Query("SELECT * FROM player_profile ORDER BY created_at ASC, id ASC")
    fun observePlayers(): Flow<List<PlayerProfileEntity>>

    @Query("SELECT COUNT(*) FROM player_profile")
    suspend fun countPlayers(): Int

    @Query("SELECT * FROM player_profile WHERE id = :id LIMIT 1")
    suspend fun getById(id: Long): PlayerProfileEntity?

    @Query("SELECT * FROM player_profile ORDER BY created_at ASC, id ASC LIMIT 1")
    suspend fun getFirstPlayer(): PlayerProfileEntity?

    @Insert
    suspend fun insert(entity: PlayerProfileEntity): Long
}
