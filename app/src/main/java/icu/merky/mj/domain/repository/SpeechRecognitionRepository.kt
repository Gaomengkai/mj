package icu.merky.mj.domain.repository

import kotlinx.coroutines.flow.Flow

interface SpeechRecognitionRepository {
    fun observePartialResults(): Flow<String>
    fun observeListening(): Flow<Boolean>
    suspend fun startListening()
    suspend fun stopListening()
}
