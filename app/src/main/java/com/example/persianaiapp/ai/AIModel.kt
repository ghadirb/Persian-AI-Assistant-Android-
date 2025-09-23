package com.example.persianaiapp.ai

/**
 * Data class representing an AI model with its properties
 */
data class AIModel(
    val name: String,
    val provider: AIModelManager.ModelProvider,
    val priority: Int,
    val supportsVision: Boolean = false,
    val maxTokens: Int = 4096,
    val isLocal: Boolean = false
) {
    val displayName: String
        get() = when (provider) {
            AIModelManager.ModelProvider.OPENAI -> "GPT: $name"
            AIModelManager.ModelProvider.ANTHROPIC -> "Claude: $name"
            AIModelManager.ModelProvider.OPENROUTER -> name.split("/").last()
            AIModelManager.ModelProvider.AIMLAPI -> "AIML: $name"
            AIModelManager.ModelProvider.LOCAL -> "Local: $name"
        }
}
