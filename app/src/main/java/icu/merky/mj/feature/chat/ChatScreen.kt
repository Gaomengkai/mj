package icu.merky.mj.feature.chat

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import icu.merky.mj.domain.model.ChatRole
import icu.merky.mj.domain.model.ChatStreamState

@Composable
fun ChatScreen(
    uiState: ChatUiState,
    onInputChanged: (String) -> Unit,
    onSend: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "Chat",
            style = MaterialTheme.typography.headlineSmall
        )

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(uiState.messages, key = { it.id }) { message ->
                val prefix = when (message.role) {
                    ChatRole.USER -> "You"
                    ChatRole.ASSISTANT -> "Yuki"
                    ChatRole.SYSTEM -> "System"
                }
                Text(text = "$prefix: ${message.content}")
            }

            val stream = uiState.streamState
            if (stream is ChatStreamState.Streaming) {
                item(key = "streaming_preview") {
                    Text(text = "Yuki: ${stream.content}")
                }
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedTextField(
                value = uiState.input,
                onValueChange = onInputChanged,
                modifier = Modifier.weight(1f),
                label = { Text("Message") }
            )
            Button(onClick = onSend) {
                Text("Send")
            }
        }
    }
}
