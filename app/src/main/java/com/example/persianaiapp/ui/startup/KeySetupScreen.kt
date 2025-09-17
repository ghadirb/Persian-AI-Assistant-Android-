package com.example.persianaiapp.ui.startup

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.persianaiapp.R
import com.example.persianaiapp.ui.components.ApiKeyStatusIndicator
import com.example.persianaiapp.ui.dialogs.KeyDecryptionDialog
import kotlinx.coroutines.delay

@Composable
fun KeySetupScreen(
    onKeysConfigured: () -> Unit,
    viewModel: KeySetupViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    // Add timeout to prevent infinite loading
    LaunchedEffect(uiState.isLoading) {
        if (uiState.isLoading) {
            kotlinx.coroutines.delay(10000) // 10 second timeout
            if (uiState.isLoading) {
                // Still loading after timeout, show error
                viewModel.showError("خطا در بارگذاری کلیدها - زمان مجاز به پایان رسید")
            }
        }
    }

    LaunchedEffect(uiState.keysValid) {
        if (uiState.keysValid) {
            onKeysConfigured()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(32.dp))

        Icon(
            imageVector = Icons.Default.Key,
            contentDescription = null,
            modifier = Modifier.size(80.dp),
            tint = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "تنظیم کلیدهای API",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "برای استفاده از برنامه، نیاز به تنظیم کلیدهای API دارید",
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        when {
            uiState.isLoading -> {
                Spacer(modifier = Modifier.height(32.dp))
                CircularProgressIndicator()
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "در حال بارگذاری کلیدها...",
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = { 
                        viewModel.clearError()
                        viewModel.checkExistingKeys()
                    }
                ) {
                    Text("لغو")
                }
            }
            uiState.errorMessage != null -> {
                Spacer(modifier = Modifier.height(32.dp))
                Icon(
                    imageVector = Icons.Default.Warning,
                    contentDescription = null,
                    modifier = Modifier.size(48.dp),
                    tint = MaterialTheme.colorScheme.error
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = uiState.errorMessage ?: "خطای ناشناخته",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.error,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(24.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = { 
                            viewModel.clearError()
                            viewModel.downloadAndDecryptKeys()
                        }
                    ) {
                        Text("تلاش مجدد")
                    }
                    Button(
                        onClick = onKeysConfigured
                    ) {
                        Text("ادامه بدون کلید")
                    }
                }
            }
            else -> {
                // Normal UI content
                Spacer(modifier = Modifier.height(32.dp))
                
                ApiKeyStatusIndicator(
                    isValid = uiState.keysValid,
                    isLoading = uiState.isLoading,
                    onRefresh = { viewModel.downloadAndDecryptKeys() }
                )

                Spacer(modifier = Modifier.height(24.dp))

                if (!uiState.keysValid && !uiState.isLoading) {
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
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Warning,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onErrorContainer
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = "تنظیم کلیدهای API الزامی است",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onErrorContainer
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                }

                Button(
                    onClick = { viewModel.downloadAndDecryptKeys() },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !uiState.isLoading
                ) {
                    Text(
                        text = if (uiState.keysValid) {
                            "به‌روزرسانی کلیدها"
                        } else {
                            "تنظیم کلیدهای API"
                        }
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedButton(
                    onClick = onKeysConfigured,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("ادامه بدون تنظیم")
                }

                Spacer(modifier = Modifier.height(32.dp))

                Text(
                    text = "می‌توانید بدون کلیدهای API از حالت آفلاین استفاده کنید",
                    style = MaterialTheme.typography.bodySmall,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }

    KeyDecryptionDialog(
        showDialog = uiState.showDecryptionDialog,
        onDismiss = { viewModel.hideDecryptionDialog() },
        onConfirm = { password ->
            viewModel.decryptKeysWithPassword(password)
        }
    )
}
