package com.example.persianaiapp.ui.settings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.persianaiapp.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onBackClick: () -> Unit,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        TopAppBar(
            title = { Text(stringResource(R.string.settings)) },
            navigationIcon = {
                IconButton(onClick = onBackClick) {
                    Icon(Icons.Default.ArrowBack, contentDescription = stringResource(R.string.back))
                }
            }
        )

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // General Settings
            item {
                SettingsSection(title = stringResource(R.string.general_settings)) {
                    SettingsItem(
                        title = stringResource(R.string.language),
                        subtitle = uiState.selectedLanguage,
                        icon = Icons.Default.Language,
                        onClick = { viewModel.showLanguageDialog() }
                    )
                    
                    SettingsItem(
                        title = stringResource(R.string.theme),
                        subtitle = uiState.selectedTheme,
                        icon = Icons.Default.Palette,
                        onClick = { viewModel.showThemeDialog() }
                    )
                }
            }

            // Voice Settings
            item {
                SettingsSection(title = stringResource(R.string.voice_settings)) {
                    SettingsSwitchItem(
                        title = stringResource(R.string.voice_activation),
                        subtitle = stringResource(R.string.voice_activation_desc),
                        icon = Icons.Default.Mic,
                        checked = uiState.voiceActivationEnabled,
                        onCheckedChange = { viewModel.setVoiceActivation(it) }
                    )
                    
                    SettingsSwitchItem(
                        title = stringResource(R.string.voice_feedback),
                        subtitle = stringResource(R.string.voice_feedback_desc),
                        icon = Icons.Default.VolumeUp,
                        checked = uiState.voiceFeedbackEnabled,
                        onCheckedChange = { viewModel.setVoiceFeedback(it) }
                    )
                }
            }

            // Security Settings
            item {
                SettingsSection(title = stringResource(R.string.security)) {
                    SettingsSwitchItem(
                        title = stringResource(R.string.biometric_auth),
                        subtitle = stringResource(R.string.biometric_auth_desc),
                        icon = Icons.Default.Fingerprint,
                        checked = uiState.biometricEnabled,
                        onCheckedChange = { viewModel.setBiometricAuth(it) }
                    )
                    
                    SettingsItem(
                        title = stringResource(R.string.password_protection),
                        subtitle = if (uiState.passwordSet) stringResource(R.string.password_set) else stringResource(R.string.no_password),
                        icon = Icons.Default.Lock,
                        onClick = { viewModel.showPasswordDialog() }
                    )
                }
            }

            // Backup Settings
            item {
                SettingsSection(title = stringResource(R.string.backup_restore)) {
                    SettingsSwitchItem(
                        title = stringResource(R.string.auto_backup),
                        subtitle = stringResource(R.string.auto_backup_desc),
                        icon = Icons.Default.Backup,
                        checked = uiState.autoBackupEnabled,
                        onCheckedChange = { viewModel.setAutoBackup(it) }
                    )
                    
                    SettingsItem(
                        title = stringResource(R.string.create_backup),
                        subtitle = stringResource(R.string.create_backup_desc),
                        icon = Icons.Default.Save,
                        onClick = { viewModel.createBackup() }
                    )
                    
                    SettingsItem(
                        title = stringResource(R.string.restore_from_backup),
                        subtitle = stringResource(R.string.restore_backup_desc),
                        icon = Icons.Default.Restore,
                        onClick = { viewModel.showRestoreDialog() }
                    )
                }
            }

            // Model Settings
            item {
                SettingsSection(title = stringResource(R.string.model_manager)) {
                    SettingsItem(
                        title = stringResource(R.string.installed_models),
                        subtitle = "${uiState.installedModelsCount} ${stringResource(R.string.models_installed)}",
                        icon = Icons.Default.Storage,
                        onClick = { viewModel.showModelManager() }
                    )
                    
                    SettingsSwitchItem(
                        title = stringResource(R.string.auto_model_updates),
                        subtitle = stringResource(R.string.auto_model_updates_desc),
                        icon = Icons.Default.Update,
                        checked = uiState.autoModelUpdateEnabled,
                        onCheckedChange = { viewModel.setAutoModelUpdate(it) }
                    )
                }
            }

            // About
            item {
                SettingsSection(title = stringResource(R.string.about)) {
                    SettingsItem(
                        title = stringResource(R.string.app_name),
                        subtitle = stringResource(R.string.version, uiState.appVersion),
                        icon = Icons.Default.Info,
                        onClick = { viewModel.showAboutDialog() }
                    )
                    
                    SettingsItem(
                        title = stringResource(R.string.privacy_policy),
                        subtitle = stringResource(R.string.privacy_policy_desc),
                        icon = Icons.Default.PrivacyTip,
                        onClick = { viewModel.openPrivacyPolicy() }
                    )
                }
            }
        }
    }

    // Dialogs
    if (uiState.showLanguageDialog) {
        LanguageSelectionDialog(
            currentLanguage = uiState.selectedLanguage,
            onLanguageSelected = { viewModel.setLanguage(it) },
            onDismiss = { viewModel.hideLanguageDialog() }
        )
    }

    if (uiState.showThemeDialog) {
        ThemeSelectionDialog(
            currentTheme = uiState.selectedTheme,
            onThemeSelected = { viewModel.setTheme(it) },
            onDismiss = { viewModel.hideThemeDialog() }
        )
    }

    if (uiState.showPasswordDialog) {
        PasswordDialog(
            isPasswordSet = uiState.passwordSet,
            onPasswordSet = { viewModel.setPassword(it) },
            onPasswordRemoved = { viewModel.removePassword() },
            onDismiss = { viewModel.hidePasswordDialog() }
        )
    }
}

@Composable
fun SettingsSection(
    title: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            content()
        }
    }
}

@Composable
fun SettingsItem(
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(24.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
        
        Spacer(modifier = Modifier.width(16.dp))
        
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        
        IconButton(onClick = onClick) {
            Icon(
                Icons.Default.ChevronRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun SettingsSwitchItem(
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(24.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
        
        Spacer(modifier = Modifier.width(16.dp))
        
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange
        )
    }
}
