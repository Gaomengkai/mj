package icu.merky.mj.compilecheck.db

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [ProbeEntity::class],
    version = 1,
    exportSchema = true
)
abstract class ProbeDatabase : RoomDatabase() {
    abstract fun probeDao(): ProbeDao
}
