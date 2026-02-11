package icu.merky.mj.data.local.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import icu.merky.mj.data.local.db.entity.ConversationMessageEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ConversationMessageDao {
    @Query("SELECT * FROM conversation_message WHERE session_id = :sessionId ORDER BY created_at ASC, id ASC")
    fun observeMessages(sessionId: Long): Flow<List<ConversationMessageEntity>>

    @Query("SELECT * FROM conversation_message WHERE session_id = :sessionId ORDER BY created_at ASC, id ASC")
    suspend fun getMessages(sessionId: Long): List<ConversationMessageEntity>

    @Insert
    suspend fun insert(entity: ConversationMessageEntity): Long

    @Query("DELETE FROM conversation_message WHERE session_id = :sessionId")
    suspend fun deleteBySessionId(sessionId: Long)
}
