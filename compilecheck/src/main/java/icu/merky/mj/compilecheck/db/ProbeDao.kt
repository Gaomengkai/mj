package icu.merky.mj.compilecheck.db

import androidx.room.Dao
import androidx.room.Query

@Dao
interface ProbeDao {
    @Query("SELECT * FROM probe LIMIT 1")
    suspend fun firstOrNull(): ProbeEntity?
}
