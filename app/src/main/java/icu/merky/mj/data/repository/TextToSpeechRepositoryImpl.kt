package icu.merky.mj.data.repository

import icu.merky.mj.data.local.voice.FakeTextToSpeechEngine
import icu.merky.mj.domain.repository.TextToSpeechRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class TextToSpeechRepositoryImpl @Inject constructor(
    private val fakeTextToSpeechEngine: FakeTextToSpeechEngine
) : TextToSpeechRepository {
    override fun observeSpeaking(): Flow<Boolean> {
        return fakeTextToSpeechEngine.observeSpeaking()
    }

    override suspend fun speak(text: String) {
        fakeTextToSpeechEngine.speak(text)
    }

    override suspend fun stop() {
        fakeTextToSpeechEngine.stop()
    }
}
