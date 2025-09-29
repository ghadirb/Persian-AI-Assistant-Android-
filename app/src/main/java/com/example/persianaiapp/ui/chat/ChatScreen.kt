package com.example.persianaiapp.ui.chat

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.persianaiapp.R
import com.example.persianaiapp.data.local.entity.ChatMessage
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    viewModel: ChatViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    var messageText by remember { mutableStateOf("") }

    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Chat Header
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = stringResource(R.string.ai_assistant),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                        if (uiState.currentModel.isNotBlank()) {
                            Text(
                                text = uiState.currentModel,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            )

            // Messages List
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(uiState.messages) { message ->
                    MessageBubble(
                        message = message,
                        onPlayVoice = { viewModel.playVoiceMessage(it) },
                        onStopVoice = { viewModel.stopVoicePlayback() },
                        isPlaying = uiState.playingMessageId == message.id
                    )
                }

                if (uiState.isProcessing) {
                    item {
                        TypingIndicator()
                    }
                }
            }

            // Input Area
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.surface,
                shadowElevation = 8.dp
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    // Voice Recording Indicator
                    if (uiState.isRecording) {
                        VoiceRecordingIndicator(
                            duration = uiState.recordingDuration,
                            onStop = { viewModel.stopRecording() },
                            onCancel = { viewModel.cancelRecording() }
                        )
                    } else {
                        // Text Input Row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.Bottom
                        ) {
                            OutlinedTextField(
                                value = messageText,
                                onValueChange = { messageText = it },
                                placeholder = { Text(stringResource(R.string.type_message)) },
                                modifier = Modifier.weight(1f),
                                maxLines = 4,
                                shape = RoundedCornerShape(24.dp)
                            )

                            Spacer(modifier = Modifier.width(8.dp))

                            // Send/Voice Button
                            if (messageText.isNotBlank()) {
                                FloatingActionButton(
                                    onClick = {
                                        viewModel.sendTextMessage(messageText)
                                        messageText = ""
                                    },
                                    modifier = Modifier.size(48.dp),
                                    containerColor = MaterialTheme.colorScheme.primary
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Send,
                                        contentDescription = stringResource(R.string.send_message)
                                    )
                                }
                            } else {
                                FloatingActionButton(
                                    onClick = { viewModel.startRecording() },
                                    modifier = Modifier.size(48.dp),
                                    containerColor = MaterialTheme.colorScheme.primary
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Mic,
                                        contentDescription = stringResource(R.string.record_voice)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Auto-scroll to bottom when new messages arrive
    LaunchedEffect(uiState.messages.size) {
        if (uiState.messages.isNotEmpty()) {
            coroutineScope.launch {
                listState.animateScrollToItem(uiState.messages.size - 1)
            }
        }
    }
}

@Composable
private fun MessageBubble(
    message: ChatMessage,
    onPlayVoice: (Long) -> Unit,
    onStopVoice: () -> Unit,
    isPlaying: Boolean
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (message.isFromUser) Arrangement.End else Arrangement.Start
    ) {
        Card(
            modifier = Modifier.widthIn(max = 280.dp),
            colors = CardDefaults.cardColors(
                containerColor = if (message.isFromUser) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.surfaceVariant
                }
            ),
            shape = RoundedCornerShape(
                topStart = 16.dp,
                topEnd = 16.dp,
                bottomStart = if (message.isFromUser) 16.dp else 4.dp,
                bottomEnd = if (message.isFromUser) 4.dp else 16.dp
            )
        ) {
            Column(
                modifier = Modifier.padding(12.dp)
            ) {
                when (message.messageType) {
                    ChatMessage.MessageType.TEXT -> {
                        Text(
                            text = message.content,
                            color = if (message.isFromUser) {
                                MaterialTheme.colorScheme.onPrimary
                            } else {
                                MaterialTheme.colorScheme.onSurfaceVariant
                            },
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                    ChatMessage.MessageType.VOICE -> {
                        VoiceMessageBubble(
                            message = message,
                            onPlay = { onPlayVoice(message.id) },
                            onStop = onStopVoice,
                            isPlaying = isPlaying
                        )
                    }
                    else -> {
                        Text(
                            text = message.content,
                            color = if (message.isFromUser) {
                                MaterialTheme.colorScheme.onPrimary
                            } else {
                                MaterialTheme.colorScheme.onSurfaceVariant
                            },
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }

                // Timestamp
                Text(
                    text = formatTimestamp(message.timestamp),
                    style = MaterialTheme.typography.bodySmall,
                    color = if (message.isFromUser) {
                        MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.7f)
                    } else {
                        MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                    },
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }
    }
}

@Composable
private fun VoiceMessageBubble(
    message: ChatMessage,
    onPlay: () -> Unit,
    onStop: () -> Unit,
    isPlaying: Boolean
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(4.dp)
    ) {
        IconButton(
            onClick = if (isPlaying) onStop else onPlay,
            modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
                .background(
                    if (message.isFromUser) {
                        MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.2f)
                    } else {
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
                    }
                )
        ) {
            Icon(
                imageVector = if (isPlaying) Icons.Default.Stop else Icons.Default.Mic,
                contentDescription = if (isPlaying) stringResource(R.string.stop_playback) else stringResource(R.string.play_voice),
                modifier = Modifier.size(16.dp),
                tint = if (message.isFromUser) {
                    MaterialTheme.colorScheme.onPrimary
                } else {
                    MaterialTheme.colorScheme.primary
                }
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        Text(
            text = formatDuration(message.audioDuration),
            style = MaterialTheme.typography.bodySmall,
            color = if (message.isFromUser) {
                MaterialTheme.colorScheme.onPrimary
            } else {
                MaterialTheme.colorScheme.onSurfaceVariant
            }
        )
    }
}

@Composable
private fun VoiceRecordingIndicator(
    duration: Long,
    onStop: () -> Unit,
    onCancel: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.errorContainer
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Mic,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onErrorContainer
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = formatDuration(duration),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onErrorContainer
                )
            }

            Row {
                TextButton(onClick = onCancel) {
                    Text(stringResource(R.string.cancel))
                }
                Spacer(modifier = Modifier.width(8.dp))
                Button(onClick = onStop) {
                    Text(stringResource(R.string.send))
                }
            }
        }
    }
}

@Composable
private fun TypingIndicator() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Start
    ) {
        Card(
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            ),
            shape = RoundedCornerShape(16.dp)
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                repeat(3) { index ->
                    val alpha by animateFloatAsState(
                        targetValue = if ((System.currentTimeMillis() / 500) % 3 == index.toLong()) 1f else 0.3f,
                        label = "typing_dot_$index"
                    )
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(
                                MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = alpha)
                            )
                    )
                    if (index < 2) Spacer(modifier = Modifier.width(4.dp))
                }
            }
        }
    }
}

private fun formatTimestamp(timestamp: Long): String {
    val formatter = java.text.SimpleDateFormat("HH:mm", java.util.Locale.getDefault())
    return formatter.format(java.util.Date(timestamp))
}

private fun formatDuration(durationMs: Long): String {
    val seconds = durationMs / 1000
    val minutes = seconds / 60
    val remainingSeconds = seconds % 60
    return String.format("%d:%02d", minutes, remainingSeconds)
}
