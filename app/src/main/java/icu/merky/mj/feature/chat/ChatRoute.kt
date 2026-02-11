package icu.merky.mj.feature.chat

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun ChatRoute(
    viewModel: ChatViewModel = hiltViewModel()
) {
    val uiState = viewModel.uiState.collectAsStateWithLifecycle()
    ChatScreen(
        uiState = uiState.value,
        onInputChanged = viewModel::onInputChanged,
        onSend = viewModel::send
    )
}
