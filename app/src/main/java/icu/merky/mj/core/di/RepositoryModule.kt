package icu.merky.mj.core.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import icu.merky.mj.data.repository.HealthRepositoryImpl
import icu.merky.mj.data.repository.ModelApiConfigRepositoryImpl
import icu.merky.mj.data.repository.DiaryRepositoryImpl
import icu.merky.mj.data.repository.PlayerProfileRepositoryImpl
import icu.merky.mj.data.repository.PromptConfigRepositoryImpl
import icu.merky.mj.data.repository.QuickReplyRepositoryImpl
import icu.merky.mj.data.repository.RelationshipRepositoryImpl
import icu.merky.mj.data.repository.SpeechRecognitionRepositoryImpl
import icu.merky.mj.data.repository.SystemSettingsRepositoryImpl
import icu.merky.mj.data.repository.ChatRepositoryImpl
import icu.merky.mj.data.repository.TextToSpeechRepositoryImpl
import icu.merky.mj.domain.repository.DiaryRepository
import icu.merky.mj.domain.repository.HealthRepository
import icu.merky.mj.domain.repository.ModelApiConfigRepository
import icu.merky.mj.domain.repository.PlayerProfileRepository
import icu.merky.mj.domain.repository.PromptConfigRepository
import icu.merky.mj.domain.repository.QuickReplyRepository
import icu.merky.mj.domain.repository.RelationshipRepository
import icu.merky.mj.domain.repository.SpeechRecognitionRepository
import icu.merky.mj.domain.repository.SystemSettingsRepository
import icu.merky.mj.domain.repository.ChatRepository
import icu.merky.mj.domain.repository.TextToSpeechRepository
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

    @Binds
    @Singleton
    abstract fun bindSpeechRecognitionRepository(
        impl: SpeechRecognitionRepositoryImpl
    ): SpeechRecognitionRepository

    @Binds
    @Singleton
    abstract fun bindTextToSpeechRepository(impl: TextToSpeechRepositoryImpl): TextToSpeechRepository

    @Binds
    @Singleton
    abstract fun bindModelApiConfigRepository(
        impl: ModelApiConfigRepositoryImpl
    ): ModelApiConfigRepository

    @Binds
    @Singleton
    abstract fun bindDiaryRepository(impl: DiaryRepositoryImpl): DiaryRepository

    @Binds
    @Singleton
    abstract fun bindQuickReplyRepository(impl: QuickReplyRepositoryImpl): QuickReplyRepository

    @Binds
    @Singleton
    abstract fun bindPromptConfigRepository(
        impl: PromptConfigRepositoryImpl
    ): PromptConfigRepository

    @Binds
    @Singleton
    abstract fun bindPlayerProfileRepository(
        impl: PlayerProfileRepositoryImpl
    ): PlayerProfileRepository
}
