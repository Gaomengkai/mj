package icu.merky.mj.feature.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import icu.merky.mj.core.result.AppResult
import icu.merky.mj.domain.model.ModelApiConfig
import icu.merky.mj.domain.model.ModelApiConfigHistoryEntry
import icu.merky.mj.domain.model.PromptConfig
import icu.merky.mj.domain.usecase.CreatePlayerUseCase
import icu.merky.mj.domain.usecase.EnsureDefaultPlayerUseCase
import icu.merky.mj.domain.usecase.ObserveActivePlayerIdUseCase
import icu.merky.mj.domain.usecase.ObserveModelApiConfigHistoryUseCase
import icu.merky.mj.domain.usecase.ObserveModelApiConfigUseCase
import icu.merky.mj.domain.usecase.ObservePlayersUseCase
import icu.merky.mj.domain.usecase.ObservePromptConfigUseCase
import icu.merky.mj.domain.usecase.SaveModelApiConfigUseCase
import icu.merky.mj.domain.usecase.SavePromptConfigUseCase
import icu.merky.mj.domain.usecase.SetActivePlayerUseCase
import icu.merky.mj.domain.usecase.TestModelApiConfigUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val ensureDefaultPlayerUseCase: EnsureDefaultPlayerUseCase,
    observePlayersUseCase: ObservePlayersUseCase,
    observeActivePlayerIdUseCase: ObserveActivePlayerIdUseCase,
    observeModelApiConfigUseCase: ObserveModelApiConfigUseCase,
    observeModelApiConfigHistoryUseCase: ObserveModelApiConfigHistoryUseCase,
    observePromptConfigUseCase: ObservePromptConfigUseCase,
    private val saveModelApiConfigUseCase: SaveModelApiConfigUseCase,
    private val savePromptConfigUseCase: SavePromptConfigUseCase,
    private val testModelApiConfigUseCase: TestModelApiConfigUseCase,
    private val setActivePlayerUseCase: SetActivePlayerUseCase,
    private val createPlayerUseCase: CreatePlayerUseCase
) : ViewModel() {
    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            ensureDefaultPlayerUseCase()
        }

        viewModelScope.launch {
            observePlayersUseCase().collect { players ->
                _uiState.update { current ->
                    current.copy(players = players)
                }
            }
        }

        viewModelScope.launch {
            observeActivePlayerIdUseCase().collect { playerId ->
                _uiState.update { current ->
                    current.copy(activePlayerId = playerId)
                }
            }
        }

        viewModelScope.launch {
            observeModelApiConfigUseCase().collect { config ->
                _uiState.update { current ->
                    current.copy(
                        endpoint = config.endpoint,
                        apiKey = config.apiKey,
                        model = config.model
                    )
                }
            }
        }

        viewModelScope.launch {
            observeModelApiConfigHistoryUseCase().collect { history ->
                _uiState.update { current ->
                    current.copy(successHistory = history)
                }
            }
        }

        viewModelScope.launch {
            observePromptConfigUseCase().collect { prompt ->
                _uiState.update { current ->
                    current.copy(
                        chat1SystemPrompt = prompt.chat1SystemPrompt,
                        chat2DiarySystemPrompt = prompt.chat2DiarySystemPrompt,
                        chat3LazyReplySystemPrompt = prompt.chat3LazyReplySystemPrompt
                    )
                }
            }
        }
    }

    fun onEndpointChanged(value: String) {
        _uiState.update { current ->
            current.copy(
                endpoint = value,
                apiStatusMessage = null
            )
        }
    }

    fun onApiKeyChanged(value: String) {
        _uiState.update { current ->
            current.copy(
                apiKey = value,
                apiStatusMessage = null
            )
        }
    }

    fun onModelChanged(value: String) {
        _uiState.update { current ->
            current.copy(
                model = value,
                apiStatusMessage = null
            )
        }
    }

    fun onChat1PromptChanged(value: String) {
        _uiState.update { current ->
            current.copy(
                chat1SystemPrompt = value,
                promptStatusMessage = null
            )
        }
    }

    fun clearChat1Prompt() {
        onChat1PromptChanged("")
    }

    fun onChat2PromptChanged(value: String) {
        _uiState.update { current ->
            current.copy(
                chat2DiarySystemPrompt = value,
                promptStatusMessage = null
            )
        }
    }

    fun clearChat2Prompt() {
        onChat2PromptChanged("")
    }

    fun onChat3PromptChanged(value: String) {
        _uiState.update { current ->
            current.copy(
                chat3LazyReplySystemPrompt = value,
                promptStatusMessage = null
            )
        }
    }

    fun clearChat3Prompt() {
        onChat3PromptChanged("")
    }

    fun onNewPlayerNameChanged(value: String) {
        _uiState.update { current ->
            current.copy(newPlayerName = value)
        }
    }

    fun onActivePlayerSelected(playerId: Long) {
        viewModelScope.launch {
            setActivePlayerUseCase(playerId)
        }
    }

    fun addPlayer() {
        viewModelScope.launch {
            val inputName = uiState.value.newPlayerName
            createPlayerUseCase(inputName)
            _uiState.update { current -> current.copy(newPlayerName = "") }
        }
    }

    fun applyHistory(entry: ModelApiConfigHistoryEntry) {
        _uiState.update { current ->
            current.copy(
                endpoint = entry.endpoint,
                apiKey = entry.apiKey,
                model = entry.model,
                apiStatusMessage = null
            )
        }
    }

    fun saveApiConfig() {
        viewModelScope.launch {
            val apiResult = saveModelApiConfigUseCase(currentConfig())
            _uiState.update { current ->
                current.copy(
                    apiStatusMessage = when (apiResult) {
                        is AppResult.Success -> "API configuration saved."
                        is AppResult.Failure -> "Save failed: ${apiResult.error}"
                    }
                )
            }
        }
    }

    fun savePromptConfig() {
        viewModelScope.launch {
            val promptResult = savePromptConfigUseCase(currentPromptConfig())
            _uiState.update { current ->
                current.copy(
                    promptStatusMessage = when (promptResult) {
                        is AppResult.Success -> "Prompt configuration saved."
                        is AppResult.Failure -> "Save failed: ${promptResult.error}"
                    }
                )
            }
        }
    }

    fun testConnection() {
        viewModelScope.launch {
            _uiState.update { current ->
                current.copy(isApiTesting = true, apiStatusMessage = "Testing...")
            }

            val result = testModelApiConfigUseCase(currentConfig())
            when (result) {
                is AppResult.Success -> {
                    _uiState.update { current ->
                        current.copy(
                            isApiTesting = false,
                            apiStatusMessage = "Connection successful."
                        )
                    }
                }

                is AppResult.Failure -> {
                    _uiState.update { current ->
                        current.copy(
                            isApiTesting = false,
                            apiStatusMessage = "Connection failed: ${result.error}"
                        )
                    }
                }
            }
        }
    }

    private fun currentConfig(): ModelApiConfig {
        val current = uiState.value
        return ModelApiConfig(
            endpoint = current.endpoint,
            apiKey = current.apiKey,
            model = current.model
        )
    }

    private fun currentPromptConfig(): PromptConfig {
        val current = uiState.value
        return PromptConfig(
            chat1SystemPrompt = current.chat1SystemPrompt,
            chat2DiarySystemPrompt = current.chat2DiarySystemPrompt,
            chat3LazyReplySystemPrompt = current.chat3LazyReplySystemPrompt
        )
    }
}
