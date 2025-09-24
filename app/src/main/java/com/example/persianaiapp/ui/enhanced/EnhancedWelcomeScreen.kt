package com.example.persianaiapp.ui.enhanced

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.persianaiapp.R
import com.example.persianaiapp.ui.components.AnimatedMessageEntry
import com.example.persianaiapp.ui.components.FloatingCard
import com.example.persianaiapp.ui.components.PersianPatternBackground
import com.example.persianaiapp.ui.theme.PersianBlue
import com.example.persianaiapp.ui.theme.PersianGold
import kotlinx.coroutines.delay

@Composable
fun EnhancedWelcomeScreen(
    onOnlineModeClick: () -> Unit,
    onOfflineModeClick: () -> Unit
) {
    var showContent by remember { mutableStateOf(false) }
    
    LaunchedEffect(Unit) {
        delay(300)
        showContent = true
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFFF8F9FA),
                        Color(0xFFE3F2FD)
                    )
                )
            )
    ) {
        // Persian pattern background
        PersianPatternBackground(alpha = 0.03f)
        
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            AnimatedVisibility(
                visible = showContent,
                enter = slideInVertically(
                    initialOffsetY = { -it },
                    animationSpec = tween(800, easing = EaseOutCubic)
                ) + fadeIn(animationSpec = tween(800))
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // App Logo with animation
                    FloatingCard {
                        Box(
                            modifier = Modifier
                                .size(120.dp)
                                .background(
                                    Brush.radialGradient(
                                        colors = listOf(PersianBlue, PersianBlue.copy(alpha = 0.8f))
                                    ),
                                    shape = RoundedCornerShape(60.dp)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.SmartToy,
                                contentDescription = "AI Assistant",
                                tint = Color.White,
                                modifier = Modifier.size(60.dp)
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(32.dp))
                    
                    // Welcome text with Persian styling
                    Text(
                        text = stringResource(R.string.welcome_to_persian_ai),
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        color = PersianBlue,
                        lineHeight = 40.sp
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Text(
                        text = "دستیار هوشمند شما برای گفتگو و کمک در امور روزمره",
                        style = MaterialTheme.typography.bodyLarge,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        lineHeight = 24.sp
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(48.dp))
            
            // Mode selection cards
            AnimatedMessageEntry {
                Column(
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Online Mode Card
                    EnhancedModeCard(
                        title = stringResource(R.string.online_mode),
                        description = "دسترسی به قدرتمندترین مدل‌های هوش مصنوعی",
                        icon = Icons.Default.Language,
                        gradient = Brush.linearGradient(
                            colors = listOf(PersianBlue, PersianBlue.copy(alpha = 0.8f))
                        ),
                        onClick = onOnlineModeClick
                    )
                    
                    // Offline Mode Card
                    EnhancedModeCard(
                        title = stringResource(R.string.offline_mode),
                        description = "استفاده بدون نیاز به اینترنت با حفظ حریم خصوصی",
                        icon = Icons.Default.CloudOff,
                        gradient = Brush.linearGradient(
                            colors = listOf(PersianGold, PersianGold.copy(alpha = 0.8f))
                        ),
                        onClick = onOfflineModeClick
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // Features highlight
            AnimatedVisibility(
                visible = showContent,
                enter = slideInVertically(
                    initialOffsetY = { it },
                    animationSpec = tween(1000, delayMillis = 500, easing = EaseOutCubic)
                ) + fadeIn(animationSpec = tween(1000, delayMillis = 500))
            ) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White.copy(alpha = 0.8f)
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp)
                    ) {
                        Text(
                            text = "ویژگی‌های کلیدی",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = PersianBlue
                        )
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        FeatureItem("🎙️", "تشخیص و سنتز گفتار فارسی")
                        FeatureItem("🔒", "امنیت و حفظ حریم خصوصی")
                        FeatureItem("💾", "پشتیبان‌گیری خودکار")
                        FeatureItem("🌐", "یکپارچگی با اپلیکیشن‌ها")
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EnhancedModeCard(
    title: String,
    description: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    gradient: Brush,
    onClick: () -> Unit
) {
    var isPressed by remember { mutableStateOf(false) }
    
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.95f else 1f,
        animationSpec = tween(150),
        label = "card_scale"
    )

    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp)
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            },
        elevation = CardDefaults.cardElevation(defaultElevation = 12.dp),
        shape = RoundedCornerShape(20.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(gradient)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(48.dp)
                )
                
                Spacer(modifier = Modifier.width(16.dp))
                
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    
                    Spacer(modifier = Modifier.height(4.dp))
                    
                    Text(
                        text = description,
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White.copy(alpha = 0.9f),
                        lineHeight = 20.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun FeatureItem(
    emoji: String,
    text: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = emoji,
            fontSize = 20.sp,
            modifier = Modifier.width(32.dp)
        )
        
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}
