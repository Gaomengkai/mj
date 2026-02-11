package icu.merky.mj.data.local.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import icu.merky.mj.data.local.db.entity.RelationshipStateEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface RelationshipStateDao {
    @Query("SELECT * FROM relationship_state WHERE id = 1 LIMIT 1")
    fun observeRelationshipState(): Flow<RelationshipStateEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(entity: RelationshipStateEntity)
}
