package co.edu.adopcionmascota.data.remote.notification

import co.edu.adopcionmascota.data.local.entity.NotificationEntity
import co.edu.adopcionmascota.data.remote.bool
import co.edu.adopcionmascota.data.remote.long
import co.edu.adopcionmascota.data.remote.str
import co.edu.adopcionmascota.domain.model.Notification
import com.google.firebase.firestore.DocumentSnapshot

// Representación de la colección tal como se guarda en Firestore (guía 19)
// ---------------- notifications/{id} ----------------
data class NotificationDto(
    val id: String = "",
    val userId: String = "",
    val type: String = "",
    val category: String = "",
    val title: String = "",
    val message: String = "",
    val createdAt: Long = 0L,
    val publicationId: String? = null,
    val referenceId: String? = null,
    val actionLabel: String? = null,
    val isRead: Boolean = false
) {
    fun toMap(): Map<String, Any?> = mapOf(
        "id" to id, "userId" to userId, "type" to type, "category" to category, "title" to title,
        "message" to message, "createdAt" to createdAt, "publicationId" to publicationId,
        "referenceId" to referenceId, "actionLabel" to actionLabel, "isRead" to isRead
    )

    fun toEntity() = NotificationEntity(
        id = id, userId = userId, type = type, category = category, title = title,
        message = message, createdAt = createdAt, publicationId = publicationId,
        referenceId = referenceId, actionLabel = actionLabel, isRead = isRead
    )

    companion object {
        fun fromDomain(n: Notification) = NotificationDto(
            id = n.id, userId = n.userId, type = n.type.name, category = n.category,
            title = n.title, message = n.message, createdAt = n.createdAt,
            publicationId = n.publicationId, referenceId = n.referenceId,
            actionLabel = n.actionLabel, isRead = n.isRead
        )

        fun fromSnapshot(doc: DocumentSnapshot) = NotificationDto(
            id = doc.id, userId = doc.str("userId"), type = doc.str("type"),
            category = doc.str("category"), title = doc.str("title"), message = doc.str("message"),
            createdAt = doc.long("createdAt"), publicationId = doc.getString("publicationId"),
            referenceId = doc.getString("referenceId"), actionLabel = doc.getString("actionLabel"),
            isRead = doc.bool("isRead")
        )
    }
}
