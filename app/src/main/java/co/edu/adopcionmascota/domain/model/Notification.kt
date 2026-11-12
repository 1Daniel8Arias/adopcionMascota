package co.edu.adopcionmascota.domain.model

data class Notification(
    val id: String,
    val userId: String,                 // Usuario que recibe la notificación
    val type: NotificationType,
    val category: String,               // Ej: "APROBACIÓN ÉTICA"
    val title: String,
    val message: String,
    val createdAt: Long = System.currentTimeMillis(),
    val publicationId: String? = null,  // Publicación relacionada
    val referenceId: String? = null,    // Ej: "ID: max_1"
    val actionLabel: String? = null,    // Botón de acción rápida en la lista
    val isRead: Boolean = false
) {
    val isAchievement: Boolean
        get() = type == NotificationType.LEVEL_UP || type == NotificationType.BADGE_EARNED
}
