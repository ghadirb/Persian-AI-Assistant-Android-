package com.example.persianaiapp.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import androidx.room.TypeConverters

@Entity(tableName = "memories")
@TypeConverters(Memory.Converters::class)
data class Memory(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val content: String,
    val category: MemoryCategory,
    val tags: List<String> = emptyList(),
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val priority: MemoryPriority = MemoryPriority.NORMAL,
    val isArchived: Boolean = false,
    val reminderTime: Long? = null,
    val location: String? = null,
    val relatedPersons: List<String> = emptyList()
) {
    enum class MemoryCategory {
        PERSONAL, MEDICAL, APPOINTMENT, TASK, CONTACT, LOCATION, GENERAL
    }

    enum class MemoryPriority {
        LOW, NORMAL, HIGH, URGENT
    }

    class Converters {
        @TypeConverter
        fun fromStringList(value: List<String>): String = value.joinToString(",")

        @TypeConverter
        fun toStringList(value: String): List<String> = 
            if (value.isBlank()) emptyList() else value.split(",")

        @TypeConverter
        fun fromMemoryCategory(value: MemoryCategory): String = value.name

        @TypeConverter
        fun toMemoryCategory(value: String): MemoryCategory = MemoryCategory.valueOf(value)

        @TypeConverter
        fun fromMemoryPriority(value: MemoryPriority): String = value.name

        @TypeConverter
        fun toMemoryPriority(value: String): MemoryPriority = MemoryPriority.valueOf(value)
    }
}
