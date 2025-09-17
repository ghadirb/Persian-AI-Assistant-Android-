package com.example.persianaiapp.voice

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object VoiceModule {

    @Provides
    @Singleton
    fun provideVoiceRecorder(@ApplicationContext context: Context): VoiceRecorder {
        return VoiceRecorder(context)
    }

    @Provides
    @Singleton
    fun provideVoicePlayer(@ApplicationContext context: Context): VoicePlayer {
        return VoicePlayer(context)
    }
}
