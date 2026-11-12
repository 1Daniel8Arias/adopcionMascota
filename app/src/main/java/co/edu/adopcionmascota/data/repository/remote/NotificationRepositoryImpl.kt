package co.edu.adopcionmascota.data.repository.remote

import co.edu.adopcionmascota.data.local.dao.NotificationDao
import co.edu.adopcionmascota.data.local.mapper.toDomain
import co.edu.adopcionmascota.data.remote.dto.Collections
import co.edu.adopcionmascota.data.remote.dto.NotificationDto
import co.edu.adopcionmascota.domain.model.Notification
import co.edu.adopcionmascota.domain.repository.NotificationRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NotificationRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth,
    private val notificationDao: NotificationDao
) : NotificationRepository {

    private val collection = firestore.collection(Collections.NOTIFICATIONS)

    override fun observeByUser(userId: String): Flow<List<Notification>> =
        notificationDao.observeByUser(userId).map { list -> list.map { it.toDomain() } }

    override suspend fun findById(id: String): Notification? = notificationDao.findById(id)?.toDomain()

    override suspend fun save(notification: Notification) {
        val dto = NotificationDto.fromDomain(notification)
        collection.document(dto.id).set(dto.toMap()).await()
        // Solo las notificaciones propias van a la caché (las demás son de otros usuarios)
        if (notification.userId == auth.currentUser?.uid) notificationDao.insert(dto.toEntity())
    }

    override suspend fun update(notification: Notification) {
        val dto = NotificationDto.fromDomain(notification)
        collection.document(dto.id).set(dto.toMap()).await()
        notificationDao.update(dto.toEntity())
    }

    override suspend fun markAsRead(id: String) {
        collection.document(id).update("isRead", true).await()
        notificationDao.markAsRead(id)
    }

    override suspend fun markAllAsRead(userId: String) {
        val unread = collection.whereEqualTo("userId", userId).whereEqualTo("isRead", false).get().await()
        if (!unread.isEmpty) {
            val batch = firestore.batch()
            unread.documents.forEach { batch.update(it.reference, "isRead", true) }
            batch.commit().await()
        }
        notificationDao.markAllAsRead(userId)
    }
}
