package icu.merky.mj.domain.usecase

import icu.merky.mj.domain.repository.TextToSpeechRepository
import javax.inject.Inject

class SpeakTextUseCase @Inject constructor(
    private val textToSpeechRepository: TextToSpeechRepository
) {
    suspend operator fun invoke(text: String) {
        if (text.isBlank()) {
            return
        }
        textToSpeechRepository.speak(text)
    }
}
