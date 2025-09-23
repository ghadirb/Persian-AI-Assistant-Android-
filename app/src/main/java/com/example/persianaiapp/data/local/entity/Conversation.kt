package com.example.persianaiapp.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(
    tableName = "conversations",
    foreignKeys = [
        ForeignKey(
            entity = UserSettings::class,
            parentColumns = ["id"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index("userId"),
        Index("createdAt")
    ]
)
data class Conversation(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    
    val userId: Long = 1, // Default to user ID 1
    
    val title: String,
    
    val createdAt: Long = System.currentTimeMillis(),
    
    val updatedAt: Long = System.currentTimeMillis(),
    
    val isArchived: Boolean = false,
    
    val messageCount: Int = 0,
    
    val lastMessagePreview: String = ""
)
