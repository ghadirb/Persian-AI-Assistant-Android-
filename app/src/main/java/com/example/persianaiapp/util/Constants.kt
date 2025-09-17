package com.example.persianaiapp.util

object Constants {
    
    // API Configuration
    const val OPENAI_BASE_URL = "https://api.openai.com/v1/"
    const val CLAUDE_BASE_URL = "https://api.anthropic.com/v1/"
    const val GEMINI_BASE_URL = "https://generativelanguage.googleapis.com/v1beta/"
    
    // Model Names
    object Models {
        // OpenAI Models
        const val GPT_4O = "gpt-4o"
        const val GPT_4O_MINI = "gpt-4o-mini"
        const val GPT_4_TURBO = "gpt-4-turbo"
        const val GPT_3_5_TURBO = "gpt-3.5-turbo"
        
        // Claude Models
        const val CLAUDE_3_OPUS = "claude-3-opus-20240229"
        const val CLAUDE_3_SONNET = "claude-3-sonnet-20240229"
        const val CLAUDE_3_HAIKU = "claude-3-haiku-20240307"
        
        // Gemini Models
        const val GEMINI_1_5_PRO = "gemini-1.5-pro"
        const val GEMINI_1_5_FLASH = "gemini-1.5-flash"
        const val GEMINI_PRO = "gemini-pro"
    }
    
    // Local Model Configuration
    object LocalModels {
        const val VOSK_PERSIAN_MODEL_URL = "https://alphacephei.com/vosk/models/vosk-model-small-fa-0.5.zip"
        const val VOSK_MODEL_DIR = "vosk-model-small-fa-0.5"
        const val MAX_MODEL_SIZE_MB = 2048L
        
        val SUPPORTED_EXTENSIONS = listOf(".gguf", ".onnx", ".tflite", ".bin", ".safetensors")
    }
    
    // Database Configuration
    const val DATABASE_NAME = "persian_ai_database"
    const val DATABASE_VERSION = 1
    
    // Backup Configuration
    const val BACKUP_FILE_PREFIX = "persian_ai_backup"
    const val ENCRYPTED_BACKUP_EXTENSION = ".enc"
    const val JSON_BACKUP_EXTENSION = ".json"
    
    // Voice Configuration
    const val SAMPLE_RATE = 16000
    const val AUDIO_FORMAT = "wav"
    const val MAX_RECORDING_DURATION_MS = 300000L // 5 minutes
    
    // Network Configuration
    const val NETWORK_TIMEOUT_SECONDS = 30L
    const val MAX_RETRIES = 3
    
    // UI Configuration
    const val ANIMATION_DURATION_MS = 300
    const val DEBOUNCE_DELAY_MS = 500L
    
    // Persian Language Configuration
    const val PERSIAN_LOCALE = "fa_IR"
    const val RTL_DIRECTION = true
    
    // Notification IDs
    object NotificationIds {
        const val AI_RESPONSE = 1001
        const val REMINDER = 1002
        const val VOICE_ACTIVATION = 1003
        const val BACKUP = 1004
        const val MODEL_UPDATE = 1005
    }
    
    // Intent Extras
    object IntentExtras {
        const val CONVERSATION_ID = "conversation_id"
        const val REMINDER_ID = "reminder_id"
        const val MODEL_PATH = "model_path"
        const val BACKUP_PATH = "backup_path"
    }
    
    // Shared Preferences Keys
    object PrefsKeys {
        const val FIRST_LAUNCH = "first_launch"
        const val SELECTED_MODE = "selected_mode"
        const val CURRENT_MODEL = "current_model"
        const val VOICE_ACTIVATION = "voice_activation"
        const val AUTO_BACKUP = "auto_backup"
        const val THEME = "theme"
        const val LANGUAGE = "language"
    }
}
