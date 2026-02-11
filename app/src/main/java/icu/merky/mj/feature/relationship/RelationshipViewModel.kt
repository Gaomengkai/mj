package icu.merky.mj.feature.relationship

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import icu.merky.mj.domain.usecase.AdjustRelationshipStateUseCase
import icu.merky.mj.domain.usecase.ObserveRelationshipStateUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RelationshipViewModel @Inject constructor(
    observeRelationshipStateUseCase: ObserveRelationshipStateUseCase,
    private val adjustRelationshipStateUseCase: AdjustRelationshipStateUseCase
) : ViewModel() {
    private val _uiState = MutableStateFlow(RelationshipUiState())
    val uiState: StateFlow<RelationshipUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            observeRelationshipStateUseCase().collect { state ->
                _uiState.update { current -> current.copy(state = state) }
            }
        }
    }

    fun increaseAffection() {
        adjust(affectionDelta = 5, trustDelta = 0)
    }

    fun decreaseAffection() {
        adjust(affectionDelta = -5, trustDelta = 0)
    }

    fun increaseTrust() {
        adjust(affectionDelta = 0, trustDelta = 5)
    }

    fun decreaseTrust() {
        adjust(affectionDelta = 0, trustDelta = -5)
    }

    private fun adjust(affectionDelta: Int, trustDelta: Int) {
        viewModelScope.launch {
            adjustRelationshipStateUseCase(
                affectionDelta = affectionDelta,
                trustDelta = trustDelta,
                updatedAt = System.currentTimeMillis()
            )
        }
    }
}
