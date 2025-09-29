package com.example.persianaiapp.di

import android.content.Context
import androidx.room.Room
import com.example.persianaiapp.data.local.AppDatabase
import com.example.persianaiapp.data.repository.ChatRepositoryImpl
import com.example.persianaiapp.domain.repository.ChatRepository
import com.example.persianaiapp.domain.service.AIService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideDatabase(appContext: Context): AppDatabase =
        Room.databaseBuilder(appContext, AppDatabase::class.java, "app_db").build()

    @Provides
    fun provideChatDao(db: AppDatabase) = db.chatDao()

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient =
        OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .build()

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit =
        Retrofit.Builder()
            .baseUrl("https://your.api.base.url/") // TODO: آدرس واقعی API
            .addConverterFactory(MoshiConverterFactory.create())
            .client(okHttpClient)
            .build()

    @Provides
    @Singleton
    fun provideAIService(retrofit: Retrofit): AIService =
        retrofit.create(AIService::class.java)

    @Provides
    @Singleton
    fun provideChatRepository(
        dao: com.example.persianaiapp.data.local.ChatDao,
        aiService: AIService
    ): ChatRepository = ChatRepositoryImpl(dao, aiService)
}
