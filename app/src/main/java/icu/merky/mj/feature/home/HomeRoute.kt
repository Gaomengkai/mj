package icu.merky.mj.feature.home

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun HomeRoute(
    onOpenDiary: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState = viewModel.uiState.collectAsStateWithLifecycle()
    HomeScreen(
        uiState = uiState.value,
        onApiBaseUrlChanged = viewModel::onApiBaseUrlChanged,
        onOpenDiary = onOpenDiary
    )
}
