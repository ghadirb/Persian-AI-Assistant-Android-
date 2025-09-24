package com.example.persianaiapp.di

import android.content.Context
import com.example.persianaiapp.data.local.dao.ChatMessageDao
import com.example.persianaiapp.data.local.dao.MemoryDao
import com.example.persianaiapp.data.local.dao.UserSettingsDao
import com.example.persianaiapp.data.local.dao.ConversationDao
import com.example.persianaiapp.data.local.dao.MessageDao
import com.example.persianaiapp.ai.AIServiceManager
import com.example.persianaiapp.ai.ClaudeService
import com.example.persianaiapp.ai.GeminiService
import com.example.persianaiapp.ai.OpenAIService
import com.example.persianaiapp.data.backup.ConversationMemoryManager
import com.example.persianaiapp.notification.AppNotificationManager
import com.example.persianaiapp.security.EncryptionManager
import com.example.persianaiapp.security.EncryptedKeysManager
import com.example.persianaiapp.security.PasswordManager
import com.example.persianaiapp.security.BiometricAuthManager
import com.example.persianaiapp.util.NetworkUtils
import okhttp3.OkHttpClient
import com.example.persianaiapp.data.model.ModelManager
import com.google.gson.Gson
import com.example.persianaiapp.data.repository.ISettingsRepository
import com.example.persianaiapp.data.repository.SettingsRepositoryImpl
import com.example.persianaiapp.data.remote.AIService
import com.example.persianaiapp.data.remote.GoogleDriveService
import com.example.persianaiapp.model.LocalModelManager
import com.example.persianaiapp.model.OfflineModelDownloader
import com.example.persianaiapp.model.OfflineModelInference
import com.example.persianaiapp.integration.FilePickerManager
import com.example.persianaiapp.backup.BackupManager
import com.example.persianaiapp.util.ThemeManager
import com.example.persianaiapp.util.TextToSpeechHelper
import com.example.persianaiapp.util.TextProcessingUtils
import com.example.persianaiapp.util.SpeechRecognitionHelper
import com.example.persianaiapp.util.PermissionManager
import com.example.persianaiapp.util.NetworkManager
import com.example.persianaiapp.util.EncryptionUtils
import com.example.persianaiapp.voice.PersianTTSEngine
import com.example.persianaiapp.voice.VoskSpeechRecognizer
import com.example.persianaiapp.integration.AppIntegrationManager
import com.example.persianaiapp.data.local.SettingsManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {



    @Provides
    @Singleton
    fun provideSettingsRepository(userSettingsDao: UserSettingsDao): ISettingsRepository {
        return SettingsRepositoryImpl(userSettingsDao)
    }

    @Provides
    @Singleton
    fun provideGoogleDriveService(
        @ApplicationContext context: Context,
        okHttpClient: OkHttpClient
    ): GoogleDriveService {
        return GoogleDriveService(context, okHttpClient)
    }

    @Provides
    @Singleton
    fun provideOpenAIService(okHttpClient: OkHttpClient): OpenAIService {
        return OpenAIService(okHttpClient)
    }

    @Provides
    @Singleton
    fun provideClaudeService(okHttpClient: OkHttpClient): ClaudeService {
        return ClaudeService(okHttpClient)
    }

    @Provides
    @Singleton
    fun provideGeminiService(okHttpClient: OkHttpClient): GeminiService {
        return GeminiService(okHttpClient)
    }

    @Provides
    @Singleton
    fun provideAIServiceManager(
        openAIService: OpenAIService,
        claudeService: ClaudeService,
        geminiService: GeminiService
    ): AIServiceManager {
        return AIServiceManager(openAIService, claudeService, geminiService)
    }




    @Provides
    @Singleton
    fun provideEncryptionManager(
        @ApplicationContext context: Context
    ): EncryptionManager {
        return EncryptionManager(context)
    }


    @Provides
    @Singleton
    fun provideGson(): Gson {
        return Gson()
    }

    @Provides
    @Singleton
    fun provideConversationMemoryManager(
        @ApplicationContext context: Context,
        conversationDao: ConversationDao,
        messageDao: MessageDao,
        encryptionManager: EncryptionManager,
        gson: Gson
    ): ConversationMemoryManager {
        return ConversationMemoryManager(context, conversationDao, messageDao, encryptionManager, gson)
    }

    @Provides
    @Singleton
    fun provideAppNotificationManager(
        @ApplicationContext context: Context
    ): AppNotificationManager {
        return AppNotificationManager(context)
    }

    @Provides
    @Singleton
    fun provideNetworkUtils(
        @ApplicationContext context: Context
    ): NetworkUtils {
        return NetworkUtils(context)
    }

    @Provides
    @Singleton
    fun provideLocalModelManager(
        @ApplicationContext context: Context,
        okHttpClient: OkHttpClient
    ): LocalModelManager {
        return LocalModelManager(context, okHttpClient)
    }

    @Provides
    @Singleton
    fun provideBackupManager(
        @ApplicationContext context: Context,
        chatRepository: com.example.persianaiapp.domain.repository.ChatRepository,
        memoryRepository: com.example.persianaiapp.domain.repository.MemoryRepository,
        settingsRepository: ISettingsRepository
    ): BackupManager {
        return BackupManager(context, chatRepository, memoryRepository, settingsRepository)
    }

    @Provides
    @Singleton
    fun provideModelManager(
        @ApplicationContext context: Context,
        okHttpClient: OkHttpClient
    ): ModelManager {
        return ModelManager(context, okHttpClient)
    }

    @Provides
    @Singleton
    fun provideEncryptedKeysManager(
        @ApplicationContext context: Context,
        okHttpClient: OkHttpClient
    ): EncryptedKeysManager {
        return EncryptedKeysManager(context, okHttpClient)
    }

    @Provides
    @Singleton
    fun providePasswordManager(
        @ApplicationContext context: Context
    ): PasswordManager {
        return PasswordManager(context)
    }

    @Provides
    @Singleton
    fun provideBiometricAuthManager(
        @ApplicationContext context: Context
    ): BiometricAuthManager {
        return BiometricAuthManager(context)
    }

    @Provides
    @Singleton
    fun provideOfflineModelDownloader(
        @ApplicationContext context: Context,
        okHttpClient: OkHttpClient
    ): OfflineModelDownloader {
        return OfflineModelDownloader(context, okHttpClient)
    }

    @Provides
    @Singleton
    fun provideOfflineModelInference(
        @ApplicationContext context: Context
    ): OfflineModelInference {
        return OfflineModelInference(context)
    }

    @Provides
    @Singleton
    fun provideFilePickerManager(): FilePickerManager {
        return FilePickerManager()
    }

    @Provides
    @Singleton
    fun provideThemeManager(
        @ApplicationContext context: Context,
        settingsManager: SettingsManager
    ): ThemeManager {
        return ThemeManager(context, settingsManager)
    }

    @Provides
    @Singleton
    fun provideTextToSpeechHelper(
        @ApplicationContext context: Context,
        settingsManager: SettingsManager
    ): TextToSpeechHelper {
        return TextToSpeechHelper(context, settingsManager)
    }

    @Provides
    @Singleton
    fun provideTextProcessingUtils(): TextProcessingUtils {
        return TextProcessingUtils()
    }

    @Provides
    @Singleton
    fun provideSpeechRecognitionHelper(
        @ApplicationContext context: Context,
        settingsManager: SettingsManager
    ): SpeechRecognitionHelper {
        return SpeechRecognitionHelper(context, settingsManager)
    }

    @Provides
    @Singleton
    fun providePermissionManager(): PermissionManager {
        return PermissionManager()
    }

    @Provides
    @Singleton
    fun provideNetworkManager(
        @ApplicationContext context: Context,
        settingsManager: SettingsManager
    ): NetworkManager {
        return NetworkManager(context, settingsManager)
    }

    @Provides
    @Singleton
    fun provideEncryptionUtils(@ApplicationContext context: Context): EncryptionUtils {
        return EncryptionUtils(context)
    }


    @Provides
    @Singleton
    fun providePersianTTSEngine(@ApplicationContext context: Context): PersianTTSEngine {
        return PersianTTSEngine(context)
    }

    @Provides
    @Singleton
    fun provideVoskSpeechRecognizer(@ApplicationContext context: Context): VoskSpeechRecognizer {
        return VoskSpeechRecognizer(context)
    }

    @Provides
    @Singleton
    fun provideAppIntegrationManager(@ApplicationContext context: Context): AppIntegrationManager {
        return AppIntegrationManager(context)
    }
}