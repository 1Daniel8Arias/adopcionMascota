package co.edu.adopcionmascota.domain.model

data class Notification(
    val id: String,
    val type: NotificationType,
    val category: String,       // Ej: "APROBACIÓN ÉTICA"
    val title: String,
    val message: String,
    val time: String,
    val referenceId: String? = null,    // Ej: "ID: max_1"
    val actionLabel: String? = null,    // Botón de acción rápida en la lista
    val isRead: Boolean = false
) {
    val isAchievement: Boolean
        get() = type == NotificationType.LEVEL_UP || type == NotificationType.BADGE_EARNED
}
