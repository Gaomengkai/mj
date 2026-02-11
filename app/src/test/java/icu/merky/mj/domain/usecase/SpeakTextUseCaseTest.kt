package icu.merky.mj.domain.usecase

import icu.merky.mj.domain.repository.TextToSpeechRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class SpeakTextUseCaseTest {
    @Test
    fun `invoke ignores blank text`() = runTest {
        val repository = FakeTextToSpeechRepository()
        val useCase = SpeakTextUseCase(repository)

        useCase("   ")

        assertEquals(0, repository.spokenText.size)
    }

    @Test
    fun `invoke calls repository for non blank text`() = runTest {
        val repository = FakeTextToSpeechRepository()
        val useCase = SpeakTextUseCase(repository)

        useCase("hello")

        assertEquals(listOf("hello"), repository.spokenText)
    }

    private class FakeTextToSpeechRepository : TextToSpeechRepository {
        private val speaking = MutableStateFlow(false)
        val spokenText = mutableListOf<String>()

        override fun observeSpeaking(): Flow<Boolean> = speaking

        override suspend fun speak(text: String) {
            spokenText += text
        }

        override suspend fun stop() {
            speaking.value = false
        }
    }
}
