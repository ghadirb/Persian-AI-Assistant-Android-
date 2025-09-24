package com.example.persianaiapp.ui.memories.compose

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.persianaiapp.data.model.Memory
import com.example.persianaiapp.ui.memories.MemoriesViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MemoriesScreen(
    viewModel: MemoriesViewModel,
    onMemoryClick: (Memory) -> Unit,
    onAddMemoryClick: () -> Unit
) {
    val memories by viewModel.memories.collectAsState()
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("خاطرات") },
                actions = {
                    IconButton(onClick = onAddMemoryClick) {
                        Icon(
                            imageVector = androidx.compose.material.icons.Icons.Default.Add,
                            contentDescription = "افزودن خاطره"
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onAddMemoryClick) {
                Icon(
                    imageVector = androidx.compose.material.icons.Icons.Default.Add,
                    contentDescription = "افزودن خاطره"
                )
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (uiState) {
                is MemoriesViewModel.UiState.Loading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
                is MemoriesViewModel.UiState.Success -> {
                    if (memories.isEmpty()) {
                        EmptyMemoriesView()
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(16.dp)
                        ) {
                            items(memories) { memory ->
                                MemoryItem(
                                    memory = memory,
                                    onClick = onMemoryClick
                                )
                            }
                        }
                    }
                }
                is MemoriesViewModel.UiState.Error -> {
                    ErrorView(
                        message = (uiState as MemoriesViewModel.UiState.Error).message,
                        onRetry = { viewModel.loadMemories() }
                    )
                }
            }
        }
    }
}

@Composable
fun MemoryItem(
    memory: Memory,
    onClick: (Memory) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        onClick = { onClick(memory) }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = memory.title,
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = memory.content,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 3
            )
        }
    }
}

@Composable
fun EmptyMemoriesView() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "هیچ خاطره‌ای یافت نشد",
                style = MaterialTheme.typography.bodyLarge
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "برای افزودن خاطره جدید، دکمه + را فشار دهید",
                style = MaterialTheme.typography.bodyMedium,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
        }
    }
}

@Composable
fun ErrorView(
    message: String,
    onRetry: () -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = message,
                style = MaterialTheme.typography.bodyLarge,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = onRetry) {
                Text("تلاش مجدد")
            }
        }
    }
}
