package icu.merky.mj.data.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import icu.merky.mj.data.local.db.dao.ConversationMessageDao
import icu.merky.mj.data.local.db.dao.ConversationSessionDao
import icu.merky.mj.data.local.db.dao.DiaryEntryDao
import icu.merky.mj.data.local.db.dao.HealthCheckDao
import icu.merky.mj.data.local.db.dao.PlayerProfileDao
import icu.merky.mj.data.local.db.dao.RelationshipStateDao
import icu.merky.mj.data.local.db.entity.ConversationMessageEntity
import icu.merky.mj.data.local.db.entity.ConversationSessionEntity
import icu.merky.mj.data.local.db.entity.DiaryEntryEntity
import icu.merky.mj.data.local.db.entity.HealthCheckEntity
import icu.merky.mj.data.local.db.entity.PlayerProfileEntity
import icu.merky.mj.data.local.db.entity.RelationshipStateEntity

@Database(
    entities = [
        HealthCheckEntity::class,
        RelationshipStateEntity::class,
        ConversationSessionEntity::class,
        ConversationMessageEntity::class,
        DiaryEntryEntity::class,
        PlayerProfileEntity::class
    ],
    version = 5,
    exportSchema = true
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun healthCheckDao(): HealthCheckDao
    abstract fun relationshipStateDao(): RelationshipStateDao
    abstract fun conversationSessionDao(): ConversationSessionDao
    abstract fun conversationMessageDao(): ConversationMessageDao
    abstract fun diaryEntryDao(): DiaryEntryDao
    abstract fun playerProfileDao(): PlayerProfileDao
}
