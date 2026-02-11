package icu.merky.mj.feature.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import icu.merky.mj.domain.usecase.ObserveSystemSettingsUseCase
import icu.merky.mj.domain.usecase.UpdateApiBaseUrlUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    observeSystemSettingsUseCase: ObserveSystemSettingsUseCase,
    private val updateApiBaseUrlUseCase: UpdateApiBaseUrlUseCase
) : ViewModel() {
    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            observeSystemSettingsUseCase().collect { settings ->
                _uiState.update { current -> current.copy(settings = settings) }
            }
        }
    }

    fun onApiBaseUrlChanged(url: String) {
        viewModelScope.launch {
            updateApiBaseUrlUseCase(url)
        }
    }
}
