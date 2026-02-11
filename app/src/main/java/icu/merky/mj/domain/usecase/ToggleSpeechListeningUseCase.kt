package icu.merky.mj.domain.usecase

import icu.merky.mj.domain.repository.SpeechRecognitionRepository
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class ToggleSpeechListeningUseCase @Inject constructor(
    private val speechRecognitionRepository: SpeechRecognitionRepository
) {
    suspend operator fun invoke() {
        if (speechRecognitionRepository.observeListening().first()) {
            speechRecognitionRepository.stopListening()
        } else {
            speechRecognitionRepository.startListening()
        }
    }
}
