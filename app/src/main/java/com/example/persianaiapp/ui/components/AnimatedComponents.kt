package com.example.persianaiapp.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.persianaiapp.ui.theme.PersianBlue
import com.example.persianaiapp.ui.theme.PersianGold
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun PulsatingMicButton(
    isRecording: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "mic_pulse")
    
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = if (isRecording) 1.2f else 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )
    
    val glowRadius by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = if (isRecording) 20f else 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glow"
    )

    Box(
        modifier = modifier.size(80.dp),
        contentAlignment = Alignment.Center
    ) {
        // Glow effect
        if (isRecording) {
            Box(
                modifier = Modifier
                    .size((80 + glowRadius).dp)
                    .background(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                PersianGold.copy(alpha = 0.3f),
                                Color.Transparent
                            )
                        ),
                        shape = CircleShape
                    )
            )
        }
        
        // Main button
        FloatingActionButton(
            onClick = onClick,
            modifier = Modifier
                .size(64.dp)
                .scale(scale),
            containerColor = if (isRecording) PersianGold else PersianBlue,
            contentColor = Color.White
        ) {
            Icon(
                imageVector = Icons.Default.Mic,
                contentDescription = "Record",
                modifier = Modifier.size(32.dp)
            )
        }
    }
}

@Composable
fun LoadingWave(
    modifier: Modifier = Modifier,
    color: Color = PersianBlue
) {
    val infiniteTransition = rememberInfiniteTransition(label = "loading_wave")
    
    val phase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 2f * kotlin.math.PI,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing)
        ),
        label = "phase"
    )

    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(60.dp)
    ) {
        val width = size.width
        val height = size.height
        val centerY = height / 2
        
        for (i in 0 until width.toInt() step 8) {
            val x = i.toFloat()
            val amplitude = 20f
            val frequency = 0.02f
            val y = centerY + amplitude * sin(frequency * x + phase)
            
            drawCircle(
                color = color.copy(alpha = 0.7f),
                radius = 3f,
                center = Offset(x, y)
            )
        }
    }
}

@Composable
fun CircularProgressWithText(
    progress: Float,
    text: String,
    modifier: Modifier = Modifier,
    size: androidx.compose.ui.unit.Dp = 120.dp
) {
    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(1000, easing = EaseOutCubic),
        label = "progress"
    )

    Box(
        modifier = modifier.size(size),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val strokeWidth = 8.dp.toPx()
            val radius = (size.toPx() - strokeWidth) / 2
            val center = Offset(size.toPx() / 2, size.toPx() / 2)
            
            // Background circle
            drawCircle(
                color = Color.Gray.copy(alpha = 0.3f),
                radius = radius,
                center = center,
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
            )
            
            // Progress arc
            drawArc(
                brush = Brush.sweepGradient(
                    colors = listOf(PersianBlue, PersianGold, PersianBlue)
                ),
                startAngle = -90f,
                sweepAngle = 360f * animatedProgress,
                useCenter = false,
                topLeft = Offset(strokeWidth / 2, strokeWidth / 2),
                size = androidx.compose.ui.geometry.Size(
                    size.toPx() - strokeWidth,
                    size.toPx() - strokeWidth
                ),
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
            )
        }
        
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "${(animatedProgress * 100).toInt()}%",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = PersianBlue
            )
            Text(
                text = text,
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun FloatingCard(
    content: @Composable () -> Unit,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "floating")
    
    val offsetY by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 8f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "float"
    )

    Card(
        modifier = modifier.offset(y = offsetY.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        content()
    }
}

@Composable
fun ShimmerEffect(
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "shimmer")
    
    val shimmerTranslateAnim by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmer_translate"
    )

    val brush = Brush.linearGradient(
        colors = listOf(
            Color.Gray.copy(alpha = 0.3f),
            Color.Gray.copy(alpha = 0.5f),
            Color.Gray.copy(alpha = 0.3f)
        ),
        start = Offset(shimmerTranslateAnim - 200f, 0f),
        end = Offset(shimmerTranslateAnim, 0f)
    )

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(brush)
    )
}

@Composable
fun PersianPatternBackground(
    modifier: Modifier = Modifier,
    alpha: Float = 0.05f
) {
    Canvas(modifier = modifier.fillMaxSize()) {
        val patternSize = 60.dp.toPx()
        val cols = (size.width / patternSize).toInt() + 1
        val rows = (size.height / patternSize).toInt() + 1
        
        for (row in 0..rows) {
            for (col in 0..cols) {
                val x = col * patternSize
                val y = row * patternSize
                
                // Draw Persian-inspired geometric pattern
                drawPersianStar(
                    center = Offset(x, y),
                    radius = patternSize / 6,
                    color = PersianGold.copy(alpha = alpha)
                )
            }
        }
    }
}

private fun DrawScope.drawPersianStar(
    center: Offset,
    radius: Float,
    color: Color
) {
    val points = 8
    val angleStep = 2 * kotlin.math.PI / points
    
    for (i in 0 until points) {
        val angle1 = i * angleStep
        val angle2 = (i + 1) * angleStep
        
        val x1 = center.x + radius * cos(angle1).toFloat()
        val y1 = center.y + radius * sin(angle1).toFloat()
        val x2 = center.x + radius * cos(angle2).toFloat()
        val y2 = center.y + radius * sin(angle2).toFloat()
        
        drawLine(
            color = color,
            start = center,
            end = Offset(x1, y1),
            strokeWidth = 1.dp.toPx()
        )
    }
}
