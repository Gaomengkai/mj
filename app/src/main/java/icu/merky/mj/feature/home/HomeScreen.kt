package icu.merky.mj.feature.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun HomeScreen(
    uiState: HomeUiState,
    onApiBaseUrlChanged: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "Yuki Companion",
            style = MaterialTheme.typography.headlineSmall
        )
        Text(
            text = "Milestone A: Foundation shell is ready.",
            style = MaterialTheme.typography.bodyMedium
        )
        OutlinedTextField(
            value = uiState.settings.apiBaseUrl,
            onValueChange = onApiBaseUrlChanged,
            modifier = Modifier.fillMaxWidth(),
            label = { Text("AI API Base URL") }
        )
        Text(
            text = "Streaming: ${uiState.settings.streamingEnabled}",
            style = MaterialTheme.typography.bodySmall
        )
    }
}
