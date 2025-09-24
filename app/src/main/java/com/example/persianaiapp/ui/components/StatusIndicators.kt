package com.example.persianaiapp.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.persianaiapp.R

@Composable
fun ApiKeyStatusIndicator(
    isValid: Boolean,
    isLoading: Boolean,
    onRefresh: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
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
                StatusIcon(
                    isValid = isValid,
                    isLoading = isLoading
                )
                
                Spacer(modifier = Modifier.width(12.dp))
                
                Column {
                    Text(
                        text = stringResource(R.string.api_keys_status),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = when {
                            isLoading -> stringResource(R.string.checking_keys)
                            isValid -> stringResource(R.string.keys_valid)
                            else -> stringResource(R.string.keys_invalid)
                        },
                        style = MaterialTheme.typography.bodyMedium,
                        color = when {
                            isLoading -> MaterialTheme.colorScheme.onSurfaceVariant
                            isValid -> Color(0xFF4CAF50)
                            else -> MaterialTheme.colorScheme.error
                        }
                    )
                }
            }
            
            IconButton(
                onClick = onRefresh,
                enabled = !isLoading
            ) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = stringResource(R.string.refresh_keys),
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Composable
fun ModelStatusIndicator(
    modelName: String?,
    isModelLoaded: Boolean,
    isLoading: Boolean,
    onModelSelect: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clickable { onModelSelect() },
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
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
                StatusIcon(
                    isValid = isModelLoaded,
                    isLoading = isLoading
                )
                
                Spacer(modifier = Modifier.width(12.dp))
                
                Column {
                    Text(
                        text = stringResource(R.string.active_model),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = when {
                            isLoading -> stringResource(R.string.loading_model)
                            modelName != null -> modelName
                            else -> stringResource(R.string.no_model_selected)
                        },
                        style = MaterialTheme.typography.bodyMedium,
                        color = when {
                            isLoading -> MaterialTheme.colorScheme.onSurfaceVariant
                            isModelLoaded -> Color(0xFF4CAF50)
                            else -> MaterialTheme.colorScheme.error
                        }
                    )
                }
            }
            
            Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = null,
                tint = if (isModelLoaded) Color(0xFF4CAF50) else MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun StatusIcon(
    isValid: Boolean,
    isLoading: Boolean
) {
    Box(
        modifier = Modifier
            .size(40.dp)
            .clip(CircleShape)
            .background(
                when {
                    isLoading -> MaterialTheme.colorScheme.surfaceVariant
                    isValid -> Color(0xFF4CAF50).copy(alpha = 0.1f)
                    else -> MaterialTheme.colorScheme.errorContainer
                }
            ),
        contentAlignment = Alignment.Center
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.size(24.dp),
                strokeWidth = 2.dp,
                color = MaterialTheme.colorScheme.primary
            )
        } else {
            Icon(
                imageVector = if (isValid) Icons.Default.CheckCircle else Icons.Default.Error,
                contentDescription = null,
                tint = if (isValid) Color(0xFF4CAF50) else MaterialTheme.colorScheme.error,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

@Composable
fun ConnectionStatusBar(
    isOnline: Boolean,
    keysValid: Boolean,
    modelLoaded: Boolean,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = when {
            isOnline && keysValid && modelLoaded -> Color(0xFF4CAF50)
            isOnline && keysValid -> Color(0xFFFF9800)
            isOnline -> Color(0xFFF44336)
            else -> Color(0xFF9E9E9E)
        },
        contentColor = Color.White
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = when {
                    isOnline && keysValid && modelLoaded -> Icons.Default.CheckCircle
                    isOnline && keysValid -> Icons.Default.Warning
                    else -> Icons.Default.Error
                },
                contentDescription = null,
                modifier = Modifier.size(16.dp)
            )
            
            Spacer(modifier = Modifier.width(8.dp))
            
            Text(
                text = when {
                    !isOnline -> stringResource(R.string.offline_mode)
                    !keysValid -> stringResource(R.string.keys_not_configured)
                    !modelLoaded -> stringResource(R.string.model_not_loaded)
                    else -> stringResource(R.string.ready_to_chat)
                },
                style = MaterialTheme.typography.bodySmall,
                fontSize = 12.sp
            )
        }
    }
}
