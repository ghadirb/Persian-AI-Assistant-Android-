package com.example.persianaiapp.ai

import android.content.Context
import com.example.persianaiapp.data.local.SettingsManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AIModelManager @Inject constructor(
    private val context: Context,
    private val settingsManager: SettingsManager
) {
    
    enum class ModelProvider {
        OPENAI,
        OPENROUTER,
        ANTHROPIC,
        AIMLAPI,
        LOCAL
    }

    private val _availableModels = MutableStateFlow<List<AIModel>>(emptyList())
    val availableModels: StateFlow<List<AIModel>> = _availableModels.asStateFlow()

    private val _currentModel = MutableStateFlow<AIModel?>(null)
    val currentModel: StateFlow<AIModel?> = _currentModel.asStateFlow()

    private val models = listOf(
        // GPT-4 models
        AIModel("gpt-4o", ModelProvider.OPENAI, 100, supportsVision = true, maxTokens = 128000),
        AIModel("gpt-4-turbo-preview", ModelProvider.OPENAI, 99, supportsVision = true, maxTokens = 128000),
        
        // Claude 3 models
        AIModel("claude-3-opus-20240229", ModelProvider.ANTHROPIC, 98, supportsVision = true, maxTokens = 200000),
        AIModel("claude-3-sonnet-20240229", ModelProvider.ANTHROPIC, 97, supportsVision = true, maxTokens = 200000),
        
        // GPT-3.5 models
        AIModel("gpt-3.5-turbo-0125", ModelProvider.OPENAI, 90, maxTokens = 16385),
        
        // OpenRouter models
        AIModel("anthropic/claude-2", ModelProvider.OPENROUTER, 85, maxTokens = 100000),
        AIModel("google/gemini-pro", ModelProvider.OPENROUTER, 80, maxTokens = 30720),
        
        // Local models
        AIModel("persian-llama-7b", ModelProvider.LOCAL, 50, isLocal = true, maxTokens = 2048)
    )

    init {
        updateAvailableModels()
    }

    private fun updateAvailableModels() {
        val available = models.filter { isModelAvailable(it) }
        _availableModels.value = available
        
        // Select the best available model if current is not set or not available
        if (_currentModel.value == null || available.none { it.name == _currentModel.value?.name }) {
            _currentModel.value = available.maxByOrNull { it.priority }
        }
    }

    private fun isModelAvailable(model: AIModel): Boolean {
        return when (model.provider) {
            ModelProvider.OPENAI -> settingsManager.getOpenAIApiKey().isNotBlank()
            ModelProvider.ANTHROPIC -> settingsManager.getAnthropicApiKey().isNotBlank()
            ModelProvider.OPENROUTER -> settingsManager.getOpenRouterApiKey().isNotBlank()
            ModelProvider.AIMLAPI -> settingsManager.getAimlApiKey().isNotBlank()
            ModelProvider.LOCAL -> checkLocalModelAvailability(model.name)
        }
    }

    private fun checkLocalModelAvailability(modelName: String): Boolean {
        // TODO: Implement actual check for local model files
        return false
    }

    fun setCurrentModel(modelName: String) {
        _currentModel.value = models.find { it.name == modelName }
    }

    fun getApiKeyForCurrentModel(): String? {
        return when (currentModel.value?.provider) {
            ModelProvider.OPENAI -> settingsManager.getOpenAIApiKey()
            ModelProvider.ANTHROPIC -> settingsManager.getAnthropicApiKey()
            ModelProvider.OPENROUTER -> settingsManager.getOpenRouterApiKey()
            ModelProvider.AIMLAPI -> settingsManager.getAimlApiKey()
            else -> null
        }.takeIf { !it.isNullOrBlank() }
    }
}
