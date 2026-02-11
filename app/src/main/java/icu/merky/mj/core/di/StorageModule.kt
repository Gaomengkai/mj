package icu.merky.mj.core.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStoreFile
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import icu.merky.mj.data.local.datastore.SettingsDataStoreSource
import icu.merky.mj.data.local.db.AppDatabase
import icu.merky.mj.data.local.db.dao.ConversationMessageDao
import icu.merky.mj.data.local.db.dao.ConversationSessionDao
import icu.merky.mj.data.local.db.dao.DiaryEntryDao
import icu.merky.mj.data.local.db.dao.HealthCheckDao
import icu.merky.mj.data.local.db.dao.PlayerProfileDao
import icu.merky.mj.data.local.db.dao.RelationshipStateDao
import icu.merky.mj.data.local.db.Migrations
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object StorageModule {
    private const val DATABASE_NAME = "mj.db"
    private const val SETTINGS_STORE_NAME = "system_settings.preferences_pb"

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(context, AppDatabase::class.java, DATABASE_NAME)
            .addMigrations(Migrations.MIGRATION_1_2)
            .addMigrations(Migrations.MIGRATION_2_3)
            .addMigrations(Migrations.MIGRATION_3_4)
            .addMigrations(Migrations.MIGRATION_4_5)
            .build()
    }

    @Provides
    fun provideHealthCheckDao(appDatabase: AppDatabase): HealthCheckDao = appDatabase.healthCheckDao()

    @Provides
    fun provideRelationshipStateDao(appDatabase: AppDatabase): RelationshipStateDao {
        return appDatabase.relationshipStateDao()
    }

    @Provides
    fun provideConversationSessionDao(appDatabase: AppDatabase): ConversationSessionDao {
        return appDatabase.conversationSessionDao()
    }

    @Provides
    fun provideConversationMessageDao(appDatabase: AppDatabase): ConversationMessageDao {
        return appDatabase.conversationMessageDao()
    }

    @Provides
    fun provideDiaryEntryDao(appDatabase: AppDatabase): DiaryEntryDao {
        return appDatabase.diaryEntryDao()
    }

    @Provides
    fun providePlayerProfileDao(appDatabase: AppDatabase): PlayerProfileDao {
        return appDatabase.playerProfileDao()
    }

    @Provides
    @Singleton
    fun providePreferencesDataStore(@ApplicationContext context: Context): DataStore<Preferences> {
        return PreferenceDataStoreFactory.create(
            produceFile = { context.preferencesDataStoreFile(SETTINGS_STORE_NAME) }
        )
    }

    @Provides
    @Singleton
    fun provideSettingsDataStoreSource(dataStore: DataStore<Preferences>): SettingsDataStoreSource {
        return SettingsDataStoreSource(dataStore)
    }
}
