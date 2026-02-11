package icu.merky.mj.data.local.voice

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FakeTextToSpeechEngine @Inject constructor() {
    private val speaking = MutableStateFlow(false)

    fun observeSpeaking(): Flow<Boolean> = speaking.asStateFlow()

    suspend fun speak(text: String) {
        speaking.emit(true)
        delay(text.length.coerceAtLeast(1) * 10L)
        speaking.emit(false)
    }

    suspend fun stop() {
        speaking.emit(false)
    }
}
