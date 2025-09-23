package com.example.persianaiapp.ui.dialogs

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.persianaiapp.R

@Composable
fun KeyDecryptionDialog(
    isVisible: Boolean,
    isLoading: Boolean,
    errorMessage: String?,
    onDecrypt: (String) -> Unit,
    onDismiss: () -> Unit,
    onRetry: () -> Unit
) {
    if (isVisible) {
        Dialog(onDismissRequest = onDismiss) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = MaterialTheme.shapes.large
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = stringResource(R.string.decrypt_keys),
                        style = MaterialTheme.typography.headlineSmall,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    if (errorMessage != null) {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 16.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.errorContainer
                            )
                        ) {
                            Text(
                                text = errorMessage,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onErrorContainer,
                                modifier = Modifier.padding(12.dp)
                            )
                        }
                    }

                    var password by remember { mutableStateOf("") }
                    var passwordVisible by remember { mutableStateOf(false) }

                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text(stringResource(R.string.enter_decryption_password)) },
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        trailingIcon = {
                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Icon(
                                    imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                    contentDescription = if (passwordVisible) "پنهان کردن رمز" else "نمایش رمز"
                                )
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp),
                        enabled = !isLoading
                    )

                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.padding(16.dp)
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        if (errorMessage != null) {
                            OutlinedButton(
                                onClick = onRetry,
                                modifier = Modifier.weight(1f),
                                enabled = !isLoading
                            ) {
                                Text(stringResource(R.string.retry_download))
                            }
                        }

                        Button(
                            onClick = { onDecrypt(password) },
                            modifier = Modifier.weight(1f),
                            enabled = password.isNotEmpty() && !isLoading
                        ) {
                            Text(stringResource(R.string.decrypt_keys))
                        }

                        OutlinedButton(
                            onClick = onDismiss,
                            modifier = Modifier.weight(1f),
                            enabled = !isLoading
                        ) {
                            Text(stringResource(R.string.cancel))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ModelSelectionDialog(
    isVisible: Boolean,
    availableModels: List<String>,
    installedModels: List<String>,
    onModelSelect: (String) -> Unit,
    onDownloadModel: (String) -> Unit,
    onSelectFile: () -> Unit,
    onDismiss: () -> Unit
) {
    if (isVisible) {
        Dialog(onDismissRequest = onDismiss) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.8f)
                    .padding(16.dp),
                shape = MaterialTheme.shapes.large
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp)
                ) {
                    Text(
                        text = stringResource(R.string.model_management),
                        style = MaterialTheme.typography.headlineSmall,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    // Installed Models Section
                    if (installedModels.isNotEmpty()) {
                        Text(
                            text = stringResource(R.string.installed_models),
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )

                        installedModels.forEach { model ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                onClick = { onModelSelect(model) }
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = model,
                                        style = MaterialTheme.typography.bodyLarge
                                    )
                                    Icon(
                                        imageVector = Icons.Default.Visibility,
                                        contentDescription = "انتخاب مدل"
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))
                    }

                    // Available Models Section
                    Text(
                        text = stringResource(R.string.available_models),
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    availableModels.forEach { model ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            onClick = { onDownloadModel(model) }
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = model,
                                    style = MaterialTheme.typography.bodyLarge
                                )
                                Icon(
                                    imageVector = Icons.Default.Visibility,
                                    contentDescription = "دانلود مدل"
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Manual File Selection
                    OutlinedButton(
                        onClick = onSelectFile,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(stringResource(R.string.select_model_file))
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = onDismiss,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(stringResource(R.string.close))
                    }
                }
            }
        }
    }
}
