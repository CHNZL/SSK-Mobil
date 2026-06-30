package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey val email: String,
    val name: String,
    val role: String // "ADMIN" or "TEACHER"
)

@Entity(tableName = "events")
data class EventEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val description: String,
    val startDate: Long, // timestamp
    val endDate: Long // timestamp
)

@Entity(tableName = "event_assignments")
data class EventAssignmentEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val eventId: Int,
    val teacherEmail: String
)

@Entity(tableName = "media_items")
data class MediaItemEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val eventId: Int,
    val filePath: String, // Predefined asset name, local URI, or Base64 string
    val isDocument: Boolean, // True for document, false for image
    val fileName: String,
    val uploadedByEmail: String,
    val uploadedByName: String,
    val uploadTimestamp: Long
)
