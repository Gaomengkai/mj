package icu.merky.mj.domain.usecase

import icu.merky.mj.domain.repository.SpeechRecognitionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ToggleSpeechListeningUseCaseTest {
    @Test
    fun `invoke starts listening when currently stopped`() = runTest {
        val repository = FakeSpeechRecognitionRepository(initialListening = false)
        val useCase = ToggleSpeechListeningUseCase(repository)

        useCase()

        assertTrue(repository.listening.value)
    }

    @Test
    fun `invoke stops listening when currently started`() = runTest {
        val repository = FakeSpeechRecognitionRepository(initialListening = true)
        val useCase = ToggleSpeechListeningUseCase(repository)

        useCase()

        assertFalse(repository.listening.value)
    }

    private class FakeSpeechRecognitionRepository(
        initialListening: Boolean
    ) : SpeechRecognitionRepository {
        val listening = MutableStateFlow(initialListening)

        override fun observePartialResults(): Flow<String> = emptyFlow()

        override fun observeListening(): Flow<Boolean> = listening

        override suspend fun startListening() {
            listening.value = true
        }

        override suspend fun stopListening() {
            listening.value = false
        }
    }
}
