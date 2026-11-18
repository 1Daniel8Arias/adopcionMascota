package co.edu.adopcionmascota.data.repository.local

import co.edu.adopcionmascota.data.local.dao.NotificationDao
import co.edu.adopcionmascota.data.local.mapper.toDomain
import co.edu.adopcionmascota.data.local.mapper.toEntity
import co.edu.adopcionmascota.domain.model.Notification
import co.edu.adopcionmascota.domain.repository.NotificationRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NotificationRepositoryImpl @Inject constructor(
    private val notificationDao: NotificationDao
) : NotificationRepository {

    override fun observeByUser(userId: String): Flow<List<Notification>> =
        notificationDao.observeByUser(userId).map { list -> list.map { it.toDomain() } }

    override suspend fun findById(id: String): Notification? = notificationDao.findById(id)?.toDomain()

    override suspend fun save(notification: Notification) {
        notificationDao.insert(notification.toEntity())
    }

    override suspend fun update(notification: Notification) {
        notificationDao.update(notification.toEntity())
    }

    override suspend fun markAsRead(id: String) {
        notificationDao.markAsRead(id)
    }

    override suspend fun markAllAsRead(userId: String) {
        notificationDao.markAllAsRead(userId)
    }
}
