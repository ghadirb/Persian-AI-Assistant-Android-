package com.example.persianaiapp.di

import android.content.Context
import androidx.room.Room
import com.example.persianaiapp.data.local.AppDatabase
import com.example.persianaiapp.data.local.dao.ChatMessageDao
import com.example.persianaiapp.data.local.dao.MemoryDao
import com.example.persianaiapp.data.local.dao.ConversationDao
import com.example.persianaiapp.data.local.dao.MessageDao
import com.example.persianaiapp.data.local.dao.UserSettingsDao
import com.example.persianaiapp.ai.AIModelManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "persian_ai_db"
        )
        .fallbackToDestructiveMigration()
        .build()
    }

    @Provides
    fun provideMemoryDao(database: AppDatabase): MemoryDao {
        return database.memoryDao()
    }

    @Provides
    fun provideChatMessageDao(database: AppDatabase): ChatMessageDao {
        return database.chatMessageDao()
    }

    fun provideUserSettingsDao(database: AppDatabase): UserSettingsDao {
        return database.userSettingsDao()
    }

    @Provides
    @Singleton
    fun provideAIModelManager(@ApplicationContext context: Context, settingsManager: SettingsManager): AIModelManager {
        return AIModelManager(context, settingsManager)
    }

    @Provides
    @Singleton
    fun provideSettingsManager(@ApplicationContext context: Context): SettingsManager {
        return SettingsManager(context)
    }
}
