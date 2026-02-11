package icu.merky.mj.domain.usecase

import icu.merky.mj.domain.repository.SpeechRecognitionRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveSpeechListeningUseCase @Inject constructor(
    private val speechRecognitionRepository: SpeechRecognitionRepository
) {
    operator fun invoke(): Flow<Boolean> = speechRecognitionRepository.observeListening()
}
