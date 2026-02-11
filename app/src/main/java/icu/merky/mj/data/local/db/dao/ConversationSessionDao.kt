package icu.merky.mj.data.local.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import icu.merky.mj.data.local.db.entity.ConversationSessionEntity

@Dao
interface ConversationSessionDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(entity: ConversationSessionEntity)

    @Query("UPDATE conversation_session SET updated_at = :updatedAt WHERE id = :sessionId")
    suspend fun updateTimestamp(sessionId: Long, updatedAt: Long)
}
