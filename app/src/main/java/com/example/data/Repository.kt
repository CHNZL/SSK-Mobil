package com.example.data

import kotlinx.coroutines.flow.Flow

class AppRepository(private val appDao: AppDao) {
    // Users
    val allUsers: Flow<List<UserEntity>> = appDao.getAllUsers()

    suspend fun getUserByEmail(email: String): UserEntity? = appDao.getUserByEmail(email)

    suspend fun insertUser(user: UserEntity) = appDao.insertUser(user)

    suspend fun deleteUser(user: UserEntity) = appDao.deleteUser(user)

    // Events
    val allEvents: Flow<List<EventEntity>> = appDao.getAllEvents()

    suspend fun getEventById(id: Int): EventEntity? = appDao.getEventById(id)

    suspend fun insertEvent(event: EventEntity): Long = appDao.insertEvent(event)

    suspend fun deleteEvent(eventId: Int) {
        appDao.deleteEventById(eventId)
        appDao.clearAssignmentsForEvent(eventId)
        appDao.clearMediaForEvent(eventId)
    }

    // Assignments
    val allAssignments: Flow<List<EventAssignmentEntity>> = appDao.getAllAssignments()

    fun getAssignmentsForEvent(eventId: Int): Flow<List<EventAssignmentEntity>> =
        appDao.getAssignmentsForEvent(eventId)

    suspend fun assignTeacherToEvent(eventId: Int, teacherEmail: String) {
        appDao.insertAssignment(EventAssignmentEntity(eventId = eventId, teacherEmail = teacherEmail))
    }

    suspend fun removeTeacherFromEvent(eventId: Int, teacherEmail: String) {
        appDao.removeAssignment(eventId, teacherEmail)
    }

    // Media
    val allMedia: Flow<List<MediaItemEntity>> = appDao.getAllMedia()

    fun getMediaForEvent(eventId: Int): Flow<List<MediaItemEntity>> = appDao.getMediaForEvent(eventId)

    suspend fun insertMedia(media: MediaItemEntity) = appDao.insertMedia(media)

    suspend fun deleteMedia(id: Int) = appDao.deleteMediaById(id)
}
