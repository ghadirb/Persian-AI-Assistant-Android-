package com.example.persianaiapp.ui.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.persianaiapp.data.local.entity.ChatMessage
import com.example.persianaiapp.domain.repository.ChatRepository
import com.example.persianaiapp.domain.repository.MemoryRepository
import com.example.persianaiapp.data.repository.ISettingsRepository
import com.example.persianaiapp.ai.AIModelManager
import com.example.persianaiapp.ai.AIServiceManager
import com.example.persianaiapp.model.LocalModelManager
import com.example.persianaiapp.voice.VoicePlayer
import com.example.persianaiapp.voice.VoiceRecorder
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val chatRepository: ChatRepository,
    private val memoryRepository: MemoryRepository,
    private val settingsRepository: ISettingsRepository,
    private val aiModelManager: AIModelManager,
    private val aiServiceManager: AIServiceManager,
    private val localModelManager: LocalModelManager,
    private val voiceRecorder: VoiceRecorder,
    private val voicePlayer: VoicePlayer
) : ViewModel() {

    private val _uiState = MutableStateFlow(ChatUiState())
    val uiState: StateFlow<ChatUiState> = _uiState.asStateFlow()

    private var recordingStartTime = 0L

    init {
        loadChatHistory()
        observeCurrentModel()
    }

    private fun loadChatHistory() {
        viewModelScope.launch {
            chatRepository.getAllMessages()
                .collect { messages ->
                    _uiState.update { it.copy(messages = messages) }
                }
        }
    }

    private fun observeCurrentModel() {
        viewModelScope.launch {
            aiModelManager.currentModel.collect { model ->
                _uiState.update { it.copy(currentModel = model?.displayName ?: "") }
            }
        }
    }

    fun sendTextMessage(content: String) {
        if (content.isBlank()) return

        viewModelScope.launch {
            val userMessage = ChatMessage(
                content = content,
                isFromUser = true,
                messageType = ChatMessage.MessageType.TEXT
            )

            val messageId = chatRepository.insertMessage(userMessage)
            processAIResponse(content)
        }
    }

    fun startRecording() {
        viewModelScope.launch {
            try {
                voiceRecorder.startRecording()
                recordingStartTime = System.currentTimeMillis()
                _uiState.update { it.copy(isRecording = true, recordingDuration = 0) }
                
                // Update recording duration
                while (_uiState.value.isRecording) {
                    delay(100)
                    val duration = System.currentTimeMillis() - recordingStartTime
                    _uiState.update { it.copy(recordingDuration = duration) }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isRecording = false, error = "خطا در شروع ضبط: ${e.message}") }
            }
        }
    }

    fun stopRecording() {
        viewModelScope.launch {
            try {
                val audioFile = voiceRecorder.stopRecording()
                val duration = System.currentTimeMillis() - recordingStartTime
                
                _uiState.update { it.copy(isRecording = false, recordingDuration = 0) }

                if (audioFile != null) {
                    val voiceMessage = ChatMessage(
                        content = "پیام صوتی",
                        isFromUser = true,
                        messageType = ChatMessage.MessageType.VOICE,
                        audioFilePath = audioFile.absolutePath,
                        audioDuration = duration
                    )

                    chatRepository.insertMessage(voiceMessage)
                    
                    // Convert voice to text and process
                    val transcription = voiceRecorder.transcribeAudio(audioFile)
                    if (transcription.isNotBlank()) {
                        processAIResponse(transcription)
                    }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isRecording = false, error = "خطا در توقف ضبط: ${e.message}") }
            }
        }
    }

    fun cancelRecording() {
        viewModelScope.launch {
            voiceRecorder.cancelRecording()
            _uiState.update { it.copy(isRecording = false, recordingDuration = 0) }
        }
    }

    fun playVoiceMessage(messageId: Long) {
        viewModelScope.launch {
            val message = _uiState.value.messages.find { it.id == messageId }
            if (message?.audioFilePath != null) {
                try {
                    _uiState.update { it.copy(playingMessageId = messageId) }
                    voicePlayer.playAudio(message.audioFilePath)
                    _uiState.update { it.copy(playingMessageId = null) }
                } catch (e: Exception) {
                    _uiState.update { it.copy(playingMessageId = null, error = "خطا در پخش صدا: ${e.message}") }
                }
            }
        }
    }

    fun stopVoicePlayback() {
        voicePlayer.stopPlayback()
        _uiState.update { it.copy(playingMessageId = null) }
    }

    private suspend fun processAIResponse(userInput: String) {
        _uiState.update { it.copy(isProcessing = true) }

        try {
            // Get AI response based on current mode (online/offline)
            val settings = settingsRepository.getUserSettingsSync()
            val response = if (settings?.isOnlineMode == true) {
                getOnlineAIResponse(userInput)
            } else {
                getOfflineAIResponse(userInput)
            }

            val aiMessage = ChatMessage(
                content = response,
                isFromUser = false,
                messageType = ChatMessage.MessageType.TEXT,
                modelUsed = _uiState.value.currentModel
            )

            chatRepository.insertMessage(aiMessage)
            
        } catch (e: Exception) {
            val errorMessage = ChatMessage(
                content = "متأسفم، خطایی رخ داده است: ${e.message}",
                isFromUser = false,
                messageType = ChatMessage.MessageType.SYSTEM
            )
            chatRepository.insertMessage(errorMessage)
        } finally {
            _uiState.update { it.copy(isProcessing = false) }
        }
    }

    private suspend fun getOnlineAIResponse(input: String): String {
        return try {
            val currentModel = aiModelManager.getCurrentModel()
            val apiKeys = aiModelManager.getApiKeys()
            
            if (currentModel == null || apiKeys.isEmpty()) {
                return "خطا: مدل یا کلید API تنظیم نشده است"
            }
            
            val provider = currentModel.provider
            val apiKey = apiKeys[provider] ?: return "خطا: کلید API برای $provider یافت نشد"
            
            // Get conversation history
            val recentMessages = chatRepository.getRecentMessages(10).first()
            val messages = mutableListOf<com.example.persianaiapp.data.remote.dto.Message>()
            
            // Add system message for Persian context
            messages.add(
                com.example.persianaiapp.data.remote.dto.Message(
                    role = "system",
                    content = "شما یک دستیار هوش مصنوعی فارسی‌زبان هستید. لطفاً به زبان فارسی پاسخ دهید و در پاسخ‌هایتان مفید، دقیق و مودبانه باشید."
                )
            )
            
            // Add conversation history
            recentMessages.reversed().forEach { msg ->
                messages.add(
                    com.example.persianaiapp.data.remote.dto.Message(
                        role = if (msg.isFromUser) "user" else "assistant",
                        content = msg.textContent ?: ""
                    )
                )
            }
            
            // Add current user message
            messages.add(
                com.example.persianaiapp.data.remote.dto.Message(
                    role = "user",
                    content = input
                )
            )
            
            val result = aiServiceManager.sendMessage(
                provider = provider,
                apiKey = apiKey,
                messages = messages,
                model = currentModel.name,
                maxTokens = 1000,
                temperature = 0.7f
            )
            
            result.fold(
                onSuccess = { response -> response.content },
                onFailure = { error -> "خطا در دریافت پاسخ: ${error.message}" }
            )
            
        } catch (e: Exception) {
            "خطا در پردازش آنلاین: ${e.message}"
        }
    }

    private suspend fun getOfflineAIResponse(input: String): String {
        return try {
            val availableModels = localModelManager.getInstalledModels()
            if (availableModels.isEmpty()) {
                return "هیچ مدل محلی نصب نشده است. لطفاً ابتدا یک مدل دانلود کنید."
            }
            
            // Use the first available model
            val model = availableModels.first()
            
            // TODO: Implement actual local model inference
            // This would require integrating with libraries like:
            // - llama.cpp for GGUF models
            // - ONNX Runtime for ONNX models
            // - TensorFlow Lite for TFLite models
            
            delay(2000) // Simulate processing time
            
            // For now, return a Persian response with some intelligence
            generatePersianResponse(input)
            
        } catch (e: Exception) {
            "خطا در پردازش آفلاین: ${e.message}"
        }
    }
    
    private fun generatePersianResponse(userMessage: String): String {
        // Simple rule-based responses for common Persian queries
        val lowerMessage = userMessage.lowercase()
        
        return when {
            lowerMessage.contains("سلام") || lowerMessage.contains("درود") -> 
                "سلام! چطور می‌تونم کمکتون کنم؟"
            
            lowerMessage.contains("چطوری") || lowerMessage.contains("حالت") ->
                "ممنون، من یک دستیار هوش مصنوعی هستم و همیشه آماده کمک به شما هستم!"
            
            lowerMessage.contains("وقت") || lowerMessage.contains("ساعت") ->
                "متأسفانه من به ساعت دسترسی ندارم، اما می‌تونید از ساعت دستگاهتون استفاده کنید."
            
            lowerMessage.contains("نام") || lowerMessage.contains("اسم") ->
                "من دستیار هوش مصنوعی فارسی شما هستم. می‌تونید هر اسمی که دوست دارید برام انتخاب کنید!"
            
            lowerMessage.contains("کمک") ->
                "البته! من می‌تونم در زمینه‌های مختلفی مثل پاسخ به سوالات، ترجمه، نوشتن متن و بسیاری موارد دیگر کمکتون کنم."
            
            lowerMessage.contains("خداحافظ") || lowerMessage.contains("بای") ->
                "خداحافظ! امیدوارم تونسته باشم کمکتون کنم. موفق باشید!"
            
            else -> {
                val responses = listOf(
                    "این سوال جالبی است. متأسفانه در حالت آفلاین امکانات محدودی دارم.",
                    "درباره '$userMessage' می‌تونم بگم که این موضوع قابل بررسی است.",
                    "پیام شما رو دریافت کردم. در حالت آنلاین می‌تونم پاسخ کاملتری بدم.",
                    "ممنون از پیامتون. برای پاسخ دقیق‌تر، حالت آنلاین رو امتحان کنید."
                )
                responses.random()
            }
        }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }

    data class ChatUiState(
        val messages: List<ChatMessage> = emptyList(),
        val currentModel: String = "",
        val isProcessing: Boolean = false,
        val isRecording: Boolean = false,
        val recordingDuration: Long = 0,
        val playingMessageId: Long? = null,
        val error: String? = null
    )
}
