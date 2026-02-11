package icu.merky.mj.data.local.voice

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FakeSpeechRecognitionEngine @Inject constructor() {
    private val listening = MutableStateFlow(false)

    fun observeListening(): Flow<Boolean> = listening.asStateFlow()

    fun observePartialResults(): Flow<String> = flow {
        while (true) {
            if (listening.value) {
                emit("voice input")
                delay(350)
            } else {
                delay(200)
            }
        }
    }

    suspend fun start() {
        listening.emit(true)
    }

    suspend fun stop() {
        listening.emit(false)
    }
}
