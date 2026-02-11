package icu.merky.mj.data.repository

import icu.merky.mj.data.local.voice.FakeSpeechRecognitionEngine
import icu.merky.mj.domain.repository.SpeechRecognitionRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SpeechRecognitionRepositoryImpl @Inject constructor(
    private val fakeSpeechRecognitionEngine: FakeSpeechRecognitionEngine
) : SpeechRecognitionRepository {
    override fun observePartialResults(): Flow<String> {
        return fakeSpeechRecognitionEngine.observePartialResults()
    }

    override fun observeListening(): Flow<Boolean> {
        return fakeSpeechRecognitionEngine.observeListening()
    }

    override suspend fun startListening() {
        fakeSpeechRecognitionEngine.start()
    }

    override suspend fun stopListening() {
        fakeSpeechRecognitionEngine.stop()
    }
}
