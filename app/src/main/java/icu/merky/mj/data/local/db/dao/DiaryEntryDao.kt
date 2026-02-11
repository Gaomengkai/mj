package icu.merky.mj.data.local.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import icu.merky.mj.data.local.db.entity.DiaryEntryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface DiaryEntryDao {
    @Query("SELECT * FROM diary_entry WHERE session_id = :sessionId ORDER BY created_at DESC, id DESC LIMIT :limit")
    fun observeRecentEntries(sessionId: Long, limit: Int): Flow<List<DiaryEntryEntity>>

    @Query("SELECT * FROM diary_entry WHERE session_id = :sessionId ORDER BY created_at DESC, id DESC LIMIT :limit")
    suspend fun getRecentEntries(sessionId: Long, limit: Int): List<DiaryEntryEntity>

    @Insert
    suspend fun insert(entity: DiaryEntryEntity)
}
