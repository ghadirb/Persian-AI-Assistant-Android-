package com.example.persianaiapp.di

import android.content.Context
import androidx.room.Room
import com.example.persianaiapp.data.local.AppDatabase
import com.example.persianaiapp.data.local.dao.MemoryDao
import com.example.persianaiapp.domain.repository.MemoryRepository
import com.example.persianaiapp.data.repository.MemoryRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase =
        Room.databaseBuilder(context, AppDatabase::class.java, "memory_db").build()

    @Provides
    fun provideMemoryDao(db: AppDatabase): MemoryDao = db.memoryDao()

    @Provides
    fun provideMemoryRepository(dao: MemoryDao): MemoryRepository =
        MemoryRepositoryImpl(dao)
}
