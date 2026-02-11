package icu.merky.mj.data.local.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import icu.merky.mj.data.local.db.entity.HealthCheckEntity

@Dao
interface HealthCheckDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(entity: HealthCheckEntity)

    @Query("SELECT * FROM health_check WHERE id = 1 LIMIT 1")
    suspend fun getHealthCheck(): HealthCheckEntity?
}
