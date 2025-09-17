package com.example.persianaiapp.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.persianaiapp.ui.theme.PersianBlue
import com.example.persianaiapp.ui.theme.PersianGold
import kotlinx.coroutines.delay

@Composable
fun EnhancedChatBubble(
    message: String,
    isFromUser: Boolean,
    timestamp: String,
    isTyping: Boolean = false,
    modifier: Modifier = Modifier
) {
    val bubbleColor = if (isFromUser) {
        Brush.linearGradient(
            colors = listOf(PersianBlue, PersianBlue.copy(alpha = 0.8f))
        )
    } else {
        Brush.linearGradient(
            colors = listOf(Color.White, Color(0xFFF5F5F5))
        )
    }

    val textColor = if (isFromUser) Color.White else Color.Black

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        horizontalArrangement = if (isFromUser) Arrangement.End else Arrangement.Start
    ) {
        if (!isFromUser) {
            // AI Avatar
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(
                        Brush.radialGradient(
                            colors = listOf(PersianGold, PersianGold.copy(alpha = 0.7f))
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.SmartToy,
                    contentDescription = "AI",
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
        }

        Column(
            modifier = Modifier.widthIn(max = 280.dp),
            horizontalAlignment = if (isFromUser) Alignment.End else Alignment.Start
        ) {
            // Message bubble
            Box(
                modifier = Modifier
                    .shadow(
                        elevation = 4.dp,
                        shape = RoundedCornerShape(
                            topStart = 20.dp,
                            topEnd = 20.dp,
                            bottomStart = if (isFromUser) 20.dp else 4.dp,
                            bottomEnd = if (isFromUser) 4.dp else 20.dp
                        )
                    )
                    .background(
                        brush = bubbleColor,
                        shape = RoundedCornerShape(
                            topStart = 20.dp,
                            topEnd = 20.dp,
                            bottomStart = if (isFromUser) 20.dp else 4.dp,
                            bottomEnd = if (isFromUser) 4.dp else 20.dp
                        )
                    )
                    .padding(12.dp)
            ) {
                if (isTyping) {
                    TypingIndicator()
                } else {
                    Text(
                        text = message,
                        color = textColor,
                        fontSize = 16.sp,
                        lineHeight = 20.sp
                    )
                }
            }

            // Timestamp
            Text(
                text = timestamp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 12.sp,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
            )
        }

        if (isFromUser) {
            Spacer(modifier = Modifier.width(8.dp))
            // User Avatar
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(
                        Brush.radialGradient(
                            colors = listOf(PersianBlue, PersianBlue.copy(alpha = 0.7f))
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "User",
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}

@Composable
fun TypingIndicator() {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            text = "در حال تایپ",
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize = 14.sp
        )
        
        repeat(3) { index ->
            val infiniteTransition = rememberInfiniteTransition(label = "typing")
            val alpha by infiniteTransition.animateFloat(
                initialValue = 0.3f,
                targetValue = 1f,
                animationSpec = infiniteRepeatable(
                    animation = tween(600, delayMillis = index * 200),
                    repeatMode = RepeatMode.Reverse
                ),
                label = "dot_alpha"
            )
            
            Box(
                modifier = Modifier
                    .size(6.dp)
                    .background(
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = alpha),
                        shape = RoundedCornerShape(3.dp)
                    )
            )
        }
    }
}

@Composable
fun AnimatedMessageEntry(
    content: @Composable () -> Unit
) {
    var visible by remember { mutableStateOf(false) }
    
    LaunchedEffect(Unit) {
        delay(100)
        visible = true
    }
    
    AnimatedVisibility(
        visible = visible,
        enter = slideInVertically(
            initialOffsetY = { it / 2 },
            animationSpec = tween(300, easing = EaseOutCubic)
        ) + fadeIn(animationSpec = tween(300))
    ) {
        content()
    }
}

@Composable
fun VoiceMessageBubble(
    duration: String,
    isFromUser: Boolean,
    isPlaying: Boolean,
    onPlayPause: () -> Unit,
    modifier: Modifier = Modifier
) {
    val bubbleColor = if (isFromUser) {
        Brush.linearGradient(
            colors = listOf(PersianBlue, PersianBlue.copy(alpha = 0.8f))
        )
    } else {
        Brush.linearGradient(
            colors = listOf(Color.White, Color(0xFFF5F5F5))
        )
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        horizontalArrangement = if (isFromUser) Arrangement.End else Arrangement.Start
    ) {
        Box(
            modifier = Modifier
                .widthIn(max = 200.dp)
                .shadow(
                    elevation = 4.dp,
                    shape = RoundedCornerShape(20.dp)
                )
                .background(
                    brush = bubbleColor,
                    shape = RoundedCornerShape(20.dp)
                )
                .padding(12.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                IconButton(
                    onClick = onPlayPause,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = if (isPlaying) Icons.Default.SmartToy else Icons.Default.Person,
                        contentDescription = if (isPlaying) "Pause" else "Play",
                        tint = if (isFromUser) Color.White else PersianBlue
                    )
                }
                
                // Voice waveform visualization
                Row(
                    horizontalArrangement = Arrangement.spacedBy(2.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    repeat(5) { index ->
                        val infiniteTransition = rememberInfiniteTransition(label = "waveform")
                        val height by infiniteTransition.animateFloat(
                            initialValue = 4f,
                            targetValue = 16f,
                            animationSpec = if (isPlaying) {
                                infiniteRepeatable(
                                    animation = tween(400, delayMillis = index * 100),
                                    repeatMode = RepeatMode.Reverse
                                )
                            } else {
                                infiniteRepeatable(
                                    animation = tween(0),
                                    repeatMode = RepeatMode.Restart
                                )
                            },
                            label = "wave_height"
                        )
                        
                        Box(
                            modifier = Modifier
                                .width(3.dp)
                                .height(if (isPlaying) height.dp else 4.dp)
                                .background(
                                    color = if (isFromUser) Color.White else PersianGold,
                                    shape = RoundedCornerShape(1.5.dp)
                                )
                        )
                    }
                }
                
                Text(
                    text = duration,
                    color = if (isFromUser) Color.White else Color.Black,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}
