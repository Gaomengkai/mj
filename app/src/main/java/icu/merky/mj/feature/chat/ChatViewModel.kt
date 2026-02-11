package icu.merky.mj.feature.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import icu.merky.mj.core.result.AppResult
import icu.merky.mj.domain.model.ChatStreamState
import icu.merky.mj.domain.model.ChatRole
import icu.merky.mj.domain.usecase.BuildChat1SystemPromptUseCase
import icu.merky.mj.domain.usecase.ClearChatSessionUseCase
import icu.merky.mj.domain.usecase.GenerateDiaryOnChatExitUseCase
import icu.merky.mj.domain.usecase.GenerateQuickRepliesUseCase
import icu.merky.mj.domain.usecase.ObserveConversationMessagesUseCase
import icu.merky.mj.domain.usecase.ObserveActivePlayerIdUseCase
import icu.merky.mj.domain.usecase.ObserveQuickReplySuggestionsUseCase
import icu.merky.mj.domain.usecase.ObserveSpeechListeningUseCase
import icu.merky.mj.domain.usecase.ObserveSpeakingUseCase
import icu.merky.mj.domain.usecase.ObserveSpeechPartialResultsUseCase
import icu.merky.mj.domain.usecase.SendChatMessageUseCase
import icu.merky.mj.domain.usecase.SpeakTextUseCase
import icu.merky.mj.domain.usecase.StartChatSessionUseCase
import icu.merky.mj.domain.usecase.StreamAssistantResponseUseCase
import icu.merky.mj.domain.usecase.ToggleSpeechListeningUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    observeConversationMessagesUseCase: ObserveConversationMessagesUseCase,
    observeActivePlayerIdUseCase: ObserveActivePlayerIdUseCase,
    observeQuickReplySuggestionsUseCase: ObserveQuickReplySuggestionsUseCase,
    private val sendChatMessageUseCase: SendChatMessageUseCase,
    private val streamAssistantResponseUseCase: StreamAssistantResponseUseCase,
    private val buildChat1SystemPromptUseCase: BuildChat1SystemPromptUseCase,
    private val clearChatSessionUseCase: ClearChatSessionUseCase,
    private val startChatSessionUseCase: StartChatSessionUseCase,
    private val generateQuickRepliesUseCase: GenerateQuickRepliesUseCase,
    private val generateDiaryOnChatExitUseCase: GenerateDiaryOnChatExitUseCase,
    observeSpeechPartialResultsUseCase: ObserveSpeechPartialResultsUseCase,
    observeSpeechListeningUseCase: ObserveSpeechListeningUseCase,
    private val toggleSpeechListeningUseCase: ToggleSpeechListeningUseCase,
    observeSpeakingUseCase: ObserveSpeakingUseCase,
    private val speakTextUseCase: SpeakTextUseCase
) : ViewModel() {
    private val _uiState = MutableStateFlow(ChatUiState())
    val uiState: StateFlow<ChatUiState> = _uiState.asStateFlow()

    private val activePlayerId = MutableStateFlow(DEFAULT_PLAYER_ID)

    init {
        viewModelScope.launch {
            observeActivePlayerIdUseCase().collect { playerId ->
                activePlayerId.value = playerId
            }
        }

        viewModelScope.launch {
            activePlayerId.collectLatest { playerId ->
                observeConversationMessagesUseCase(playerId).collect { messages ->
                    _uiState.update { current ->
                        current.copy(
                            messages = messages,
                            sessionEnded = messages.isEmpty() && current.sessionEnded
                        )
                    }
                }
            }
        }

        viewModelScope.launch {
            activePlayerId.collectLatest { playerId ->
                observeQuickReplySuggestionsUseCase(playerId).collect { suggestions ->
                    _uiState.update { current -> current.copy(quickReplies = suggestions) }
                }
            }
        }

        viewModelScope.launch {
            activePlayerId.collect { playerId ->
                _uiState.update { current ->
                    current.copy(
                        sessionEnded = current.sessionEnded && current.messages.isEmpty(),
                        activePlayerId = playerId
                    )
                }
            }
        }

        viewModelScope.launch {
            observeSpeechPartialResultsUseCase().collect { partial ->
                _uiState.update { current ->
                    if (!current.listening) {
                        current
                    } else {
                        current.copy(speechPartial = partial, input = partial)
                    }
                }
            }
        }

        viewModelScope.launch {
            observeSpeechListeningUseCase().collect { listening ->
                _uiState.update { current ->
                    current.copy(
                        listening = listening,
                        speechPartial = if (listening) current.speechPartial else ""
                    )
                }
            }
        }

        viewModelScope.launch {
            observeSpeakingUseCase().collect { speaking ->
                _uiState.update { current -> current.copy(speaking = speaking) }
            }
        }
    }

    fun onInputChanged(value: String) {
        _uiState.update { current -> current.copy(input = value) }
    }

    fun send() {
        val content = uiState.value.input
        viewModelScope.launch {
            val sessionId = activePlayerId.value
            when (val result = sendChatMessageUseCase(sessionId, content)) {
                is AppResult.Success -> {
                    _uiState.update { current ->
                        current.copy(
                            input = "",
                            speechPartial = "",
                            sessionEnded = false
                        )
                    }
                    val systemPrompt = buildChat1SystemPromptUseCase(sessionId = sessionId)
                    streamAssistantResponseUseCase(sessionId, systemPrompt).collect { state ->
                        _uiState.update { current -> current.copy(streamState = state) }
                        if (state is ChatStreamState.Success) {
                            speakTextUseCase(state.message.content)
                            if (state.message.role == ChatRole.ASSISTANT) {
                                generateQuickRepliesUseCase(sessionId)
                            }
                        }
                    }
                }

                is AppResult.Failure -> {
                    _uiState.update {
                        it.copy(streamState = ChatStreamState.Error(result.error.toString()))
                    }
                }
            }
        }
    }

    fun applyQuickReply(content: String) {
        _uiState.update { current -> current.copy(input = content) }
    }

    fun onExitChat() {
        viewModelScope.launch {
            val sessionId = activePlayerId.value
            val hadMessages = uiState.value.messages.isNotEmpty()
            val diaryResult = generateDiaryOnChatExitUseCase(sessionId)
            clearChatSessionUseCase(sessionId)
            _uiState.update { current ->
                current.copy(
                    input = "",
                    speechPartial = "",
                    streamState = ChatStreamState.Idle,
                    sessionEnded = true,
                    exitMessage = if (diaryResult is AppResult.Success && hadMessages) {
                        EXIT_DIARY_MESSAGE
                    } else {
                        null
                    }
                )
            }
        }
    }

    fun consumeExitMessage() {
        _uiState.update { current -> current.copy(exitMessage = null) }
    }

    fun startChat() {
        viewModelScope.launch {
            val sessionId = activePlayerId.value
            val starterPrompt = buildChat1SystemPromptUseCase(sessionId = sessionId)
            when (startChatSessionUseCase(sessionId)) {
                is AppResult.Success -> {
                    _uiState.update { current -> current.copy(sessionEnded = false, exitMessage = null) }
                    streamAssistantResponseUseCase(sessionId, starterPrompt).collect { state ->
                        _uiState.update { current -> current.copy(streamState = state) }
                        if (state is ChatStreamState.Success && state.message.role == ChatRole.ASSISTANT) {
                            speakTextUseCase(state.message.content)
                            generateQuickRepliesUseCase(sessionId)
                        }
                    }
                }

                is AppResult.Failure -> {
                    _uiState.update { current ->
                        current.copy(streamState = ChatStreamState.Error("Failed to start chat."))
                    }
                }
            }
        }
    }

    fun toggleListening() {
        viewModelScope.launch {
            toggleSpeechListeningUseCase()
        }
    }

    private companion object {
        const val DEFAULT_PLAYER_ID = 1L
        const val EXIT_DIARY_MESSAGE = "Yuki新写了一篇日记，快来看看吧"
    }
}
