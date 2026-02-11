package icu.merky.mj.feature.chat

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Button
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import icu.merky.mj.R
import icu.merky.mj.domain.model.ChatRole
import icu.merky.mj.domain.model.ChatStreamState
import androidx.compose.material3.Icon
import androidx.compose.ui.res.painterResource

@Composable
fun ChatScreen(
    uiState: ChatUiState,
    onInputChanged: (String) -> Unit,
    onSend: () -> Unit,
    onToggleListening: () -> Unit,
    onApplyQuickReply: (String) -> Unit,
    onExitChat: () -> Unit,
    onConsumeExitMessage: () -> Unit,
    onStartChat: () -> Unit,
    onOpenSettings: () -> Unit,
    onOpenDiary: () -> Unit,
    modifier: Modifier = Modifier
) {
    val listState = rememberLazyListState()
    val imeBottom = WindowInsets.ime.getBottom(LocalDensity.current)

    LaunchedEffect(
        uiState.messages.size,
        (uiState.streamState as? ChatStreamState.Streaming)?.content,
        imeBottom,
        uiState.sessionEnded
    ) {
        if (uiState.sessionEnded) {
            return@LaunchedEffect
        }
        val hasStreaming = uiState.streamState is ChatStreamState.Streaming
        if (uiState.messages.isEmpty() && !hasStreaming) {
            return@LaunchedEffect
        }

        val targetIndex = if (uiState.messages.isEmpty()) {
            0
        } else {
            uiState.messages.lastIndex + if (hasStreaming) 1 else 0
        }
        listState.animateScrollToItem(targetIndex)
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .imePadding()
    ) {
        Text(
            text = "Chat",
            style = MaterialTheme.typography.headlineSmall
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(onClick = onOpenDiary) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_diary),
                    contentDescription = "Open Diary"
                )
                Text("打开日记")
            }
            Button(onClick = onOpenSettings) {
                Text("Model Settings")
            }
            Button(onClick = onExitChat) {
                Text("Exit Chat")
            }
        }

        Text(
            text = "Listening: ${uiState.listening} | Speaking: ${uiState.speaking}",
            modifier = Modifier.padding(top = 8.dp)
        )
        if (uiState.speechPartial.isNotBlank()) {
            Text(text = "Speech partial: ${uiState.speechPartial}")
        }

        if (uiState.sessionEnded) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Button(onClick = onStartChat, modifier = Modifier.fillMaxWidth()) {
                    Text("开始聊天")
                }
            }
            return@Column
        }

        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(top = 12.dp),
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

        if (uiState.quickReplies.isNotEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "懒人回复",
                    style = MaterialTheme.typography.titleMedium
                )
                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(uiState.quickReplies, key = { it.id }) { suggestion ->
                        Button(onClick = { onApplyQuickReply(suggestion.content) }) {
                            Text(suggestion.content)
                        }
                    }
                }
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(onClick = onToggleListening) {
                Text(if (uiState.listening) "Stop Mic" else "Start Mic")
            }
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

    uiState.exitMessage?.let { message ->
        AlertDialog(
            onDismissRequest = onConsumeExitMessage,
            confirmButton = {
                TextButton(onClick = onConsumeExitMessage) {
                    Text("知道了")
                }
            },
            text = {
                Text(message)
            }
        )
    }
}
