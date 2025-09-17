package com.example.persianaiapp.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(
    tableName = "messages",
    foreignKeys = [
        ForeignKey(
            entity = Conversation::class,
            parentColumns = ["id"],
            childColumns = ["conversationId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index("conversationId"),
        Index("timestamp"),
        Index("isFromUser")
    ]
)
data class Message(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    
    val conversationId: Long,
    
    val content: String,
    
    val timestamp: Long = System.currentTimeMillis(),
    
    val isFromUser: Boolean = true,
    
    val messageType: String = "text", // text, image, audio, etc.
    
    val isProcessed: Boolean = false,
    
    val metadata: String? = null // JSON string for additional data
)
