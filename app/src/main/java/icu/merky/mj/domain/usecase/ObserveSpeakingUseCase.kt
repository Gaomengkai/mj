package icu.merky.mj.domain.usecase

import icu.merky.mj.domain.repository.TextToSpeechRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveSpeakingUseCase @Inject constructor(
    private val textToSpeechRepository: TextToSpeechRepository
) {
    operator fun invoke(): Flow<Boolean> = textToSpeechRepository.observeSpeaking()
}
