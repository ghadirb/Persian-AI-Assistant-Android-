package com.example.persianaiapp.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

@Entity(tableName = "chat_messages")
@TypeConverters(ChatMessage.Converters::class)
data class ChatMessage(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val content: String,
    val isFromUser: Boolean,
    val timestamp: Long = System.currentTimeMillis(),
    val messageType: MessageType = MessageType.TEXT,
    val audioFilePath: String? = null,
    val audioDuration: Long = 0,
    val isProcessed: Boolean = true,
    val modelUsed: String? = null,
    val conversationId: String = "default",
    val metadata: Map<String, String> = emptyMap()
) {
    enum class MessageType {
        TEXT, VOICE, IMAGE, SYSTEM
    }

    class Converters {
        @TypeConverter
        fun fromMessageType(value: MessageType): String = value.name

        @TypeConverter
        fun toMessageType(value: String): MessageType = MessageType.valueOf(value)

        @TypeConverter
        fun fromStringMap(value: Map<String, String>): String = Gson().toJson(value)

        @TypeConverter
        fun toStringMap(value: String): Map<String, String> {
            val mapType = object : TypeToken<Map<String, String>>() {}.type
            return Gson().fromJson(value, mapType) ?: emptyMap()
        }
    }
}
