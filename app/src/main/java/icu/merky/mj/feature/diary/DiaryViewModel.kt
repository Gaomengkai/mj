package icu.merky.mj.feature.diary

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import icu.merky.mj.domain.usecase.ObserveActivePlayerIdUseCase
import icu.merky.mj.domain.usecase.ObserveRecentDiaryEntriesUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DiaryViewModel @Inject constructor(
    observeActivePlayerIdUseCase: ObserveActivePlayerIdUseCase,
    observeRecentDiaryEntriesUseCase: ObserveRecentDiaryEntriesUseCase
) : ViewModel() {
    private val _uiState = MutableStateFlow(DiaryUiState())
    val uiState: StateFlow<DiaryUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            observeActivePlayerIdUseCase().collectLatest { playerId ->
                _uiState.update { current -> current.copy(activePlayerId = playerId) }
                observeRecentDiaryEntriesUseCase(sessionId = playerId, limit = 50).collect { entries ->
                    _uiState.update { current -> current.copy(entries = entries) }
                }
            }
        }
    }
}
