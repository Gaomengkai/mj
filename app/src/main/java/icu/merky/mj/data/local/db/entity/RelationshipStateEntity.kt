package icu.merky.mj.data.local.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "relationship_state")
data class RelationshipStateEntity(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: Int = 1,
    @ColumnInfo(name = "affection")
    val affection: Int,
    @ColumnInfo(name = "trust")
    val trust: Int,
    @ColumnInfo(name = "mood")
    val mood: String,
    @ColumnInfo(name = "updated_at")
    val updatedAt: Long
)
