package icu.merky.mj.feature.relationship

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun RelationshipRoute(
    viewModel: RelationshipViewModel = hiltViewModel()
) {
    val uiState = viewModel.uiState.collectAsStateWithLifecycle()
    RelationshipScreen(
        uiState = uiState.value,
        onIncreaseAffection = viewModel::increaseAffection,
        onDecreaseAffection = viewModel::decreaseAffection,
        onIncreaseTrust = viewModel::increaseTrust,
        onDecreaseTrust = viewModel::decreaseTrust
    )
}
