package icu.merky.mj.feature.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import icu.merky.mj.core.result.AppResult
import icu.merky.mj.domain.model.ChatStreamState
import icu.merky.mj.domain.usecase.ObserveConversationMessagesUseCase
import icu.merky.mj.domain.usecase.SendChatMessageUseCase
import icu.merky.mj.domain.usecase.StreamAssistantResponseUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    observeConversationMessagesUseCase: ObserveConversationMessagesUseCase,
    private val sendChatMessageUseCase: SendChatMessageUseCase,
    private val streamAssistantResponseUseCase: StreamAssistantResponseUseCase
) : ViewModel() {
    private val _uiState = MutableStateFlow(ChatUiState())
    val uiState: StateFlow<ChatUiState> = _uiState.asStateFlow()

    private val sessionId = DEFAULT_SESSION_ID

    init {
        viewModelScope.launch {
            observeConversationMessagesUseCase(sessionId).collect { messages ->
                _uiState.update { current -> current.copy(messages = messages) }
            }
        }
    }

    fun onInputChanged(value: String) {
        _uiState.update { current -> current.copy(input = value) }
    }

    fun send() {
        val content = uiState.value.input
        viewModelScope.launch {
            when (val result = sendChatMessageUseCase(sessionId, content)) {
                is AppResult.Success -> {
                    _uiState.update { current -> current.copy(input = "") }
                    streamAssistantResponseUseCase(sessionId).collect { state ->
                        _uiState.update { current -> current.copy(streamState = state) }
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

    private companion object {
        const val DEFAULT_SESSION_ID = 1L
    }
}
