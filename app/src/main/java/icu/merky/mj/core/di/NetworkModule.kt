package icu.merky.mj.core.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import icu.merky.mj.data.remote.MockAIChatService
import icu.merky.mj.domain.repository.AIChatService
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class NetworkModule {
    @Binds
    @Singleton
    abstract fun bindAIChatService(impl: MockAIChatService): AIChatService
}
