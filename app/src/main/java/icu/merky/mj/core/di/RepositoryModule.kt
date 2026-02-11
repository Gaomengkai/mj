package icu.merky.mj.core.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import icu.merky.mj.data.repository.HealthRepositoryImpl
import icu.merky.mj.data.repository.RelationshipRepositoryImpl
import icu.merky.mj.data.repository.SystemSettingsRepositoryImpl
import icu.merky.mj.data.repository.ChatRepositoryImpl
import icu.merky.mj.domain.repository.HealthRepository
import icu.merky.mj.domain.repository.RelationshipRepository
import icu.merky.mj.domain.repository.SystemSettingsRepository
import icu.merky.mj.domain.repository.ChatRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds
    @Singleton
    abstract fun bindHealthRepository(impl: HealthRepositoryImpl): HealthRepository

    @Binds
    @Singleton
    abstract fun bindSystemSettingsRepository(impl: SystemSettingsRepositoryImpl): SystemSettingsRepository

    @Binds
    @Singleton
    abstract fun bindRelationshipRepository(impl: RelationshipRepositoryImpl): RelationshipRepository

    @Binds
    @Singleton
    abstract fun bindChatRepository(impl: ChatRepositoryImpl): ChatRepository
}
