package icu.merky.mj.compilecheck.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "probe")
data class ProbeEntity(
    @PrimaryKey val id: Int,
    val label: String
)
