package icu.merky.mj.domain.repository

import kotlinx.coroutines.flow.Flow

interface TextToSpeechRepository {
    fun observeSpeaking(): Flow<Boolean>
    suspend fun speak(text: String)
    suspend fun stop()
}
