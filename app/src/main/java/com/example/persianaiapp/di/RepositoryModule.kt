package com.example.persianaiapp.di

import com.example.persianaiapp.data.repository.ChatRepositoryImpl
import com.example.persianaiapp.data.repository.MemoryRepositoryImpl
import com.example.persianaiapp.domain.repository.ChatRepository
import com.example.persianaiapp.domain.repository.MemoryRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindMemoryRepository(
        memoryRepositoryImpl: MemoryRepositoryImpl
    ): MemoryRepository

    @Binds
    @Singleton
    abstract fun bindChatRepository(
        chatRepositoryImpl: ChatRepositoryImpl
    ): ChatRepository
<<<<<<< HEAD

=======
>>>>>>> 2f16af6ef4a70a76724f242750d19135f262c5e9
}
