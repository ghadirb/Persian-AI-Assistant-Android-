package com.example.persianaiapp.ui.enhanced

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.persianaiapp.R
import com.example.persianaiapp.ui.components.FloatingCard
import com.example.persianaiapp.ui.components.PersianPatternBackground
import com.example.persianaiapp.ui.theme.PersianBlue
import com.example.persianaiapp.ui.theme.PersianGold
import com.example.persianaiapp.ui.theme.PersianTeal

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EnhancedSettingsScreen(
    onBackClick: () -> Unit,
    onApiKeysClick: () -> Unit,
    onModelsClick: () -> Unit,
    onBackupClick: () -> Unit,
    onThemeClick: () -> Unit
) {
    var showContent by remember { mutableStateOf(false) }
    
    LaunchedEffect(Unit) {
        showContent = true
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.settings),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "بازگشت")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                )
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Background pattern
            PersianPatternBackground(alpha = 0.02f)
            
            AnimatedVisibility(
                visible = showContent,
                enter = slideInVertically(
                    initialOffsetY = { it },
                    animationSpec = tween(600, easing = EaseOutCubic)
                ) + fadeIn(animationSpec = tween(600))
            ) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(vertical = 16.dp)
                ) {
                    item {
                        SettingsSection(
                            title = "پیکربندی اصلی",
                            items = listOf(
                                SettingsItem(
                                    title = stringResource(R.string.api_keys),
                                    subtitle = "مدیریت کلیدهای API",
                                    icon = Icons.Default.Key,
                                    gradient = Brush.linearGradient(
                                        colors = listOf(PersianBlue, PersianBlue.copy(alpha = 0.8f))
                                    ),
                                    onClick = onApiKeysClick
                                ),
                                SettingsItem(
                                    title = stringResource(R.string.offline_models),
                                    subtitle = "دانلود و مدیریت مدل‌های آفلاین",
                                    icon = Icons.Default.CloudDownload,
                                    gradient = Brush.linearGradient(
                                        colors = listOf(PersianGold, PersianGold.copy(alpha = 0.8f))
                                    ),
                                    onClick = onModelsClick
                                )
                            )
                        )
                    }
                    
                    item {
                        SettingsSection(
                            title = "داده‌ها و پشتیبان‌گیری",
                            items = listOf(
                                SettingsItem(
                                    title = stringResource(R.string.backup_restore),
                                    subtitle = "پشتیبان‌گیری و بازیابی گفتگوها",
                                    icon = Icons.Default.Backup,
                                    gradient = Brush.linearGradient(
                                        colors = listOf(PersianTeal, PersianTeal.copy(alpha = 0.8f))
                                    ),
                                    onClick = onBackupClick
                                )
                            )
                        )
                    }
                    
                    item {
                        SettingsSection(
                            title = "ظاهر و تنظیمات",
                            items = listOf(
                                SettingsItem(
                                    title = "تم و ظاهر",
                                    subtitle = "تغییر رنگ‌ها و حالت تاریک",
                                    icon = Icons.Default.Palette,
                                    gradient = Brush.linearGradient(
                                        colors = listOf(
                                            Color(0xFF9C27B0),
                                            Color(0xFF9C27B0).copy(alpha = 0.8f)
                                        )
                                    ),
                                    onClick = onThemeClick
                                ),
                                SettingsItem(
                                    title = "زبان و منطقه",
                                    subtitle = "تنظیمات زبان و قالب‌بندی",
                                    icon = Icons.Default.Language,
                                    gradient = Brush.linearGradient(
                                        colors = listOf(
                                            Color(0xFF607D8B),
                                            Color(0xFF607D8B).copy(alpha = 0.8f)
                                        )
                                    ),
                                    onClick = { /* Handle language settings */ }
                                )
                            )
                        )
                    }
                    
                    item {
                        SettingsSection(
                            title = "اطلاعات و پشتیبانی",
                            items = listOf(
                                SettingsItem(
                                    title = "درباره برنامه",
                                    subtitle = "نسخه و اطلاعات توسعه‌دهنده",
                                    icon = Icons.Default.Info,
                                    gradient = Brush.linearGradient(
                                        colors = listOf(
                                            Color(0xFF795548),
                                            Color(0xFF795548).copy(alpha = 0.8f)
                                        )
                                    ),
                                    onClick = { /* Handle about */ }
                                ),
                                SettingsItem(
                                    title = "پشتیبانی و بازخورد",
                                    subtitle = "ارسال بازخورد و دریافت کمک",
                                    icon = Icons.Default.Support,
                                    gradient = Brush.linearGradient(
                                        colors = listOf(
                                            Color(0xFF4CAF50),
                                            Color(0xFF4CAF50).copy(alpha = 0.8f)
                                        )
                                    ),
                                    onClick = { /* Handle support */ }
                                )
                            )
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SettingsSection(
    title: String,
    items: List<SettingsItem>
) {
    Column {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = PersianBlue,
            modifier = Modifier.padding(horizontal = 4.dp, vertical = 8.dp)
        )
        
        items.forEachIndexed { index, item ->
            EnhancedSettingsCard(
                item = item,
                isLast = index == items.size - 1
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EnhancedSettingsCard(
    item: SettingsItem,
    isLast: Boolean = false
) {
    var isPressed by remember { mutableStateOf(false) }
    
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.98f else 1f,
        animationSpec = tween(150),
        label = "settings_card_scale"
    )

    FloatingCard(
        modifier = Modifier
            .fillMaxWidth()
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
    ) {
        Card(
            onClick = {
                isPressed = true
                item.onClick()
            },
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.White
            )
        ) {
            Box(
                modifier = Modifier.fillMaxWidth()
            ) {
                // Gradient overlay
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(80.dp)
                        .background(
                            item.gradient.copy(alpha = 0.1f),
                            shape = RoundedCornerShape(16.dp)
                        )
                )
                
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Icon with gradient background
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(item.gradient),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = item.icon,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    
                    Spacer(modifier = Modifier.width(16.dp))
                    
                    // Text content
                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = item.title,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        
                        Spacer(modifier = Modifier.height(2.dp))
                        
                        Text(
                            text = item.subtitle,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            lineHeight = 18.sp
                        )
                    }
                    
                    // Arrow icon
                    Icon(
                        imageVector = Icons.Default.ChevronRight,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
    
    if (!isLast) {
        Spacer(modifier = Modifier.height(8.dp))
    }
}

private data class SettingsItem(
    val title: String,
    val subtitle: String,
    val icon: ImageVector,
    val gradient: Brush,
    val onClick: () -> Unit
)
