package icu.merky.mj.feature.settings

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun SettingsRoute(
    onNavigateBack: () -> Unit,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val uiState = viewModel.uiState.collectAsStateWithLifecycle()
    SettingsScreen(
        uiState = uiState.value,
        onEndpointChanged = viewModel::onEndpointChanged,
        onApiKeyChanged = viewModel::onApiKeyChanged,
        onModelChanged = viewModel::onModelChanged,
        onChat1PromptChanged = viewModel::onChat1PromptChanged,
        onChat2PromptChanged = viewModel::onChat2PromptChanged,
        onChat3PromptChanged = viewModel::onChat3PromptChanged,
        onClearChat1Prompt = viewModel::clearChat1Prompt,
        onClearChat2Prompt = viewModel::clearChat2Prompt,
        onClearChat3Prompt = viewModel::clearChat3Prompt,
        onSaveApiConfig = viewModel::saveApiConfig,
        onTestConnection = viewModel::testConnection,
        onSavePromptConfig = viewModel::savePromptConfig,
        onNewPlayerNameChanged = viewModel::onNewPlayerNameChanged,
        onAddPlayer = viewModel::addPlayer,
        onSelectPlayer = viewModel::onActivePlayerSelected,
        onNavigateBack = onNavigateBack,
        onPickHistory = viewModel::applyHistory
    )
}
