package com.example.persianaiapp.ui.models

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.persianaiapp.R
import com.example.persianaiapp.model.OfflineModelDownloader
import com.example.persianaiapp.ui.components.ModelStatusIndicator

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModelManagementScreen(
    onBackClick: () -> Unit,
    viewModel: ModelManagementViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.loadModels()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.model_management)) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "بازگشت")
                    }
                }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Current Model Status
            item {
                ModelStatusIndicator(
                    modelName = uiState.currentModelName,
                    isModelLoaded = uiState.isModelLoaded,
                    isLoading = uiState.isLoading,
                    onModelSelect = { viewModel.showModelSelection() }
                )
            }

            // Available Models Section
            item {
                Text(
                    text = stringResource(R.string.available_models),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }

            items(uiState.availableModels) { model ->
                AvailableModelCard(
                    model = model,
                    isDownloading = uiState.downloadingModels.contains(model.name),
                    downloadProgress = uiState.downloadProgress[model.name],
                    onDownload = { viewModel.downloadModel(model) }
                )
            }

            // Installed Models Section
            if (uiState.installedModels.isNotEmpty()) {
                item {
                    Text(
                        text = stringResource(R.string.installed_models),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }

                items(uiState.installedModels) { modelFile ->
                    InstalledModelCard(
                        modelFile = modelFile,
                        isSelected = modelFile.name == uiState.currentModelName,
                        onSelect = { viewModel.selectModel(modelFile.name) },
                        onDelete = { viewModel.deleteModel(modelFile.name) }
                    )
                }
            }

            // Manual File Selection
            item {
                OutlinedButton(
                    onClick = { viewModel.selectModelFile() },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.Folder, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(stringResource(R.string.select_model_file))
                }
            }
        }
    }
}

@Composable
private fun AvailableModelCard(
    model: OfflineModelDownloader.AvailableModel,
    isDownloading: Boolean,
    downloadProgress: OfflineModelDownloader.DownloadProgress?,
    onDownload: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = model.displayName,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = model.description,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "${stringResource(R.string.model_size)}: ${formatFileSize(model.sizeBytes)}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                if (isDownloading) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator(
                            progress = (downloadProgress?.percentage ?: 0) / 100f,
                            modifier = Modifier.size(40.dp)
                        )
                        Text(
                            text = "${downloadProgress?.percentage ?: 0}%",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                } else {
                    IconButton(onClick = onDownload) {
                        Icon(Icons.Default.Download, contentDescription = stringResource(R.string.download_model))
                    }
                }
            }

            if (downloadProgress?.error != null) {
                Text(
                    text = downloadProgress.error,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        }
    }
}

@Composable
private fun InstalledModelCard(
    modelFile: java.io.File,
    isSelected: Boolean,
    onSelect: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = if (isSelected) {
            CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
        } else {
            CardDefaults.cardColors()
        }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = modelFile.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "${stringResource(R.string.model_size)}: ${formatFileSize(modelFile.length())}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                if (isSelected) {
                    Text(
                        text = "فعال",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Row {
                if (!isSelected) {
                    TextButton(onClick = onSelect) {
                        Text("انتخاب")
                    }
                }
                IconButton(onClick = onDelete) {
                    Icon(Icons.Default.Delete, contentDescription = stringResource(R.string.delete_model))
                }
            }
        }
    }
}

private fun formatFileSize(bytes: Long): String {
    val units = arrayOf("B", "KB", "MB", "GB", "TB")
    var size = bytes.toDouble()
    var unitIndex = 0
    
    while (size >= 1024 && unitIndex < units.size - 1) {
        size /= 1024
        unitIndex++
    }
    
    return "%.1f %s".format(size, units[unitIndex])
}
