package co.edu.adopcionmascota.domain.repository

import co.edu.adopcionmascota.domain.model.Notification
import kotlinx.coroutines.flow.Flow

interface NotificationRepository {

    fun observeByUser(userId: String): Flow<List<Notification>>

    suspend fun findById(id: String): Notification?

    suspend fun save(notification: Notification)

    suspend fun update(notification: Notification)

    suspend fun markAsRead(id: String)

    suspend fun markAllAsRead(userId: String)
}
