package com.example.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface AppDao {
    // Users
    @Query("SELECT * FROM users")
    fun getAllUsers(): Flow<List<UserEntity>>

    @Query("SELECT * FROM users WHERE email = :email LIMIT 1")
    suspend fun getUserByEmail(email: String): UserEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity)

    @Delete
    suspend fun deleteUser(user: UserEntity)

    // Events
    @Query("SELECT * FROM events ORDER BY startDate DESC")
    fun getAllEvents(): Flow<List<EventEntity>>

    @Query("SELECT * FROM events WHERE id = :id LIMIT 1")
    suspend fun getEventById(id: Int): EventEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEvent(event: EventEntity): Long

    @Query("DELETE FROM events WHERE id = :eventId")
    suspend fun deleteEventById(eventId: Int)

    // Event Assignments
    @Query("SELECT * FROM event_assignments WHERE eventId = :eventId")
    fun getAssignmentsForEvent(eventId: Int): Flow<List<EventAssignmentEntity>>

    @Query("SELECT * FROM event_assignments")
    fun getAllAssignments(): Flow<List<EventAssignmentEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAssignment(assignment: EventAssignmentEntity)

    @Query("DELETE FROM event_assignments WHERE eventId = :eventId AND teacherEmail = :teacherEmail")
    suspend fun removeAssignment(eventId: Int, teacherEmail: String)

    @Query("DELETE FROM event_assignments WHERE eventId = :eventId")
    suspend fun clearAssignmentsForEvent(eventId: Int)

    // Media Items
    @Query("SELECT * FROM media_items WHERE eventId = :eventId ORDER BY uploadTimestamp DESC")
    fun getMediaForEvent(eventId: Int): Flow<List<MediaItemEntity>>

    @Query("SELECT * FROM media_items ORDER BY uploadTimestamp DESC")
    fun getAllMedia(): Flow<List<MediaItemEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMedia(media: MediaItemEntity)

    @Query("DELETE FROM media_items WHERE id = :id")
    suspend fun deleteMediaById(id: Int)

    @Query("DELETE FROM media_items WHERE eventId = :eventId")
    suspend fun clearMediaForEvent(eventId: Int)
}

@Database(
    entities = [
        UserEntity::class,
        EventEntity::class,
        EventAssignmentEntity::class,
        MediaItemEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun appDao(): AppDao
}
