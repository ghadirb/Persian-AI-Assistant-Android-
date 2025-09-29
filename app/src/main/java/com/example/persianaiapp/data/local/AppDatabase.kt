package com.example.persianaiapp.data.local
<<<<<<< HEAD
import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.TypeConverters
import com.example.persianaiapp.data.local.dao.ChatMessageDao
import com.example.persianaiapp.data.local.dao.MemoryDao
=======

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.persianaiapp.data.local.dao.ChatMessageDao
import com.example.persianaiapp.data.local.dao.MemoryDao
import com.example.persianaiapp.data.local.dao.UserSettingsDao
>>>>>>> 2f16af6ef4a70a76724f242750d19135f262c5e9
import com.example.persianaiapp.data.local.dao.ConversationDao
import com.example.persianaiapp.data.local.dao.MessageDao
import com.example.persianaiapp.data.local.entity.ChatMessage
import com.example.persianaiapp.data.local.entity.Memory
import com.example.persianaiapp.data.local.entity.UserSettings
import com.example.persianaiapp.data.local.entity.Conversation
import com.example.persianaiapp.data.local.entity.Message
<<<<<<< HEAD
=======

@Database(
>>>>>>> 2f16af6ef4a70a76724f242750d19135f262c5e9
    entities = [ChatMessage::class, Memory::class, UserSettings::class, Conversation::class, Message::class],
    version = 1,
    exportSchema = true
)
@TypeConverters(ChatMessage.Converters::class, Memory.Converters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun chatMessageDao(): ChatMessageDao
    abstract fun memoryDao(): MemoryDao
    abstract fun userSettingsDao(): UserSettingsDao
<<<<<<< HEAD
=======
    abstract fun conversationDao(): ConversationDao
>>>>>>> 2f16af6ef4a70a76724f242750d19135f262c5e9
    abstract fun messageDao(): MessageDao

    companion object {
        @Volatile
        private var instance: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return instance ?: synchronized(this) {
                val newInstance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "persian_ai_db"
                )
                .fallbackToDestructiveMigration()
                .build()
                
                instance = newInstance
                newInstance
            }
        }
    }
}
