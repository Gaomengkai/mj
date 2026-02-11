package icu.merky.mj.feature.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.AssistChip
import androidx.compose.material3.TextButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import icu.merky.mj.domain.model.ModelApiConfigHistoryEntry
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun SettingsScreen(
    uiState: SettingsUiState,
    onEndpointChanged: (String) -> Unit,
    onApiKeyChanged: (String) -> Unit,
    onModelChanged: (String) -> Unit,
    onChat1PromptChanged: (String) -> Unit,
    onChat2PromptChanged: (String) -> Unit,
    onChat3PromptChanged: (String) -> Unit,
    onClearChat1Prompt: () -> Unit,
    onClearChat2Prompt: () -> Unit,
    onClearChat3Prompt: () -> Unit,
    onSaveApiConfig: () -> Unit,
    onTestConnection: () -> Unit,
    onSavePromptConfig: () -> Unit,
    onNewPlayerNameChanged: (String) -> Unit,
    onAddPlayer: () -> Unit,
    onSelectPlayer: (Long) -> Unit,
    onNavigateBack: () -> Unit,
    onPickHistory: (ModelApiConfigHistoryEntry) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                TextButton(onClick = onNavigateBack) {
                    Text("Back")
                }
                Text(
                    text = "Model API Settings",
                    style = MaterialTheme.typography.headlineSmall
                )
            }
        }

        item {
            Text(text = "API Settings", style = MaterialTheme.typography.titleMedium)
        }

        item {
            Text(text = "Player Save Slots", style = MaterialTheme.typography.titleMedium)
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = uiState.newPlayerName,
                    onValueChange = onNewPlayerNameChanged,
                    modifier = Modifier.weight(1f),
                    label = { Text("New player name") },
                    singleLine = true
                )
                Button(onClick = onAddPlayer) {
                    Text("Add")
                }
            }
        }

        item {
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(uiState.players, key = { it.id }) { player ->
                    AssistChip(
                        onClick = { onSelectPlayer(player.id) },
                        label = {
                            val marker = if (uiState.activePlayerId == player.id) "*" else ""
                            Text("$marker${player.name}")
                        }
                    )
                }
            }
        }

        item {
            HorizontalDivider()
        }

        item {
            OutlinedTextField(
                value = uiState.endpoint,
                onValueChange = onEndpointChanged,
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Endpoint (OpenAI-compatible)") },
                singleLine = true
            )
        }
        item {
            OutlinedTextField(
                value = uiState.apiKey,
                onValueChange = onApiKeyChanged,
                modifier = Modifier.fillMaxWidth(),
                label = { Text("API Key") },
                singleLine = true
            )
        }
        item {
            OutlinedTextField(
                value = uiState.model,
                onValueChange = onModelChanged,
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Model") },
                singleLine = true
            )
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(onClick = onSaveApiConfig, enabled = !uiState.isApiTesting) {
                    Text("Save API")
                }
                Button(onClick = onTestConnection, enabled = !uiState.isApiTesting) {
                    Text(if (uiState.isApiTesting) "Testing..." else "Test API")
                }
            }
        }

        uiState.apiStatusMessage?.let { message ->
            item {
                Text(
                    text = message,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }

        item {
            HorizontalDivider()
        }

        item {
            Text(text = "Prompt Settings", style = MaterialTheme.typography.titleMedium)
        }

        item {
            PromptFieldHeader(
                title = "Chat1 System Prompt",
                onClear = onClearChat1Prompt
            )
            OutlinedTextField(
                value = uiState.chat1SystemPrompt,
                onValueChange = onChat1PromptChanged,
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 120.dp, max = 200.dp),
                label = { Text("Chat1 System Prompt") }
            )
        }
        item {
            PromptFieldHeader(
                title = "Chat2 Diary Prompt",
                onClear = onClearChat2Prompt
            )
            OutlinedTextField(
                value = uiState.chat2DiarySystemPrompt,
                onValueChange = onChat2PromptChanged,
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 120.dp, max = 200.dp),
                label = { Text("Chat2 Diary Prompt") }
            )
        }
        item {
            PromptFieldHeader(
                title = "Chat3 Lazy Reply Prompt",
                onClear = onClearChat3Prompt
            )
            OutlinedTextField(
                value = uiState.chat3LazyReplySystemPrompt,
                onValueChange = onChat3PromptChanged,
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 120.dp, max = 200.dp),
                label = { Text("Chat3 Lazy Reply Prompt") }
            )
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(onClick = onSavePromptConfig) {
                    Text("Save Prompt")
                }
            }
        }

        uiState.promptStatusMessage?.let { message ->
            item {
                Text(
                    text = message,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }

        item {
            Text(
                text = "Recent successful endpoints",
                style = MaterialTheme.typography.titleMedium
            )
        }

        items(uiState.successHistory, key = { historyKey(it) }) { entry ->
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onPickHistory(entry) }
                        .padding(vertical = 8.dp)
                ) {
                    Text(
                        text = entry.endpoint,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        text = "Model: ${entry.model}",
                        style = MaterialTheme.typography.bodySmall
                    )
                    Text(
                        text = "Key: ${maskKey(entry.apiKey)}",
                        style = MaterialTheme.typography.bodySmall
                    )
                    Text(
                        text = "Tested: ${formatTimestamp(entry.testedAt)}",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
        }
    }
}

private fun historyKey(entry: ModelApiConfigHistoryEntry): String {
    return "${entry.endpoint}|${entry.model}|${entry.testedAt}"
}

private fun maskKey(raw: String): String {
    if (raw.length <= 8) {
        return "****"
    }
    return "${raw.take(4)}****${raw.takeLast(4)}"
}

private fun formatTimestamp(value: Long): String {
    val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.US)
    return formatter.format(Date(value))
}

@Composable
private fun PromptFieldHeader(
    title: String,
    onClear: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold
        )
        TextButton(onClick = onClear) {
            Text("一键清空")
        }
    }
}
