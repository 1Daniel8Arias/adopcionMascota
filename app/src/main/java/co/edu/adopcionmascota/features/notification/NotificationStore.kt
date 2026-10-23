package co.edu.adopcionmascota.features.notification

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

// Tipos de notificación que aparecen en el Figma (página "notificacion")
enum class NotificationType {
    PUBLICATION_APPROVED,   // MODERACION_APROBADA
    LEVEL_UP,               // NUEVO_LOGRO (nivel)
    PUBLICATION_PAUSED,     // MODERACION_RECHAZADA
    NEW_COMMENT,            // NUEVO_COMENTARIO
    BADGE_EARNED            // NUEVO_LOGRO (insignia)
}

data class AppNotification(
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

// Por ahora las notificaciones viven en memoria; más adelante vendrán del repositorio / Firebase
object NotificationStore {

    private val _notifications = MutableStateFlow(initialNotifications())
    val notifications: StateFlow<List<AppNotification>> = _notifications.asStateFlow()

    fun findById(id: String): AppNotification? =
        _notifications.value.find { it.id == id }

    fun markAsRead(id: String) {
        _notifications.update { list ->
            list.map { if (it.id == id) it.copy(isRead = true) else it }
        }
    }

    fun markAllAsRead() {
        _notifications.update { list -> list.map { it.copy(isRead = true) } }
    }

    // Se usa cuando la publicación pausada se reenvía a revisión
    fun update(notification: AppNotification) {
        _notifications.update { list ->
            list.map { if (it.id == notification.id) notification else it }
        }
    }

    // Datos quemados con las notificaciones del Figma
    private fun initialNotifications(): List<AppNotification> = listOf(
        AppNotification(
            id = "n1",
            type = NotificationType.PUBLICATION_APPROVED,
            category = "APROBACIÓN ÉTICA",
            title = "¡Publicación Aprobada!",
            message = "Tu caso Max (Golden Mestizo) fue verificado éticamente y ya está en el feed público para adopción responsable.",
            time = "Hace 15 min",
            referenceId = "ID: max_1",
            actionLabel = "Ver Caso"
        ),
        AppNotification(
            id = "n2",
            type = NotificationType.LEVEL_UP,
            category = "COMUNIDAD & IMPACTO",
            title = "¡Subiste a Nivel Guardián!",
            message = "Desbloqueaste la insignia \"10 Publicaciones Verificadas\". Has sumado +50 pts de reputación en la red.",
            time = "Hace 1h",
            actionLabel = "Insignia Nivel Guardián (+50 pts)"
        ),
        AppNotification(
            id = "n3",
            type = NotificationType.PUBLICATION_PAUSED,
            category = "REVISIÓN ÉTICA",
            title = "Publicación Pausada",
            message = "Motivo: Fotos poco nítidas del carnet de vacunas. Corrige los datos para reactivar la visibilidad del rescate.",
            time = "Hace 2h",
            referenceId = "ID: luna_2",
            actionLabel = "Actualizar Datos"
        ),
        AppNotification(
            id = "n4",
            type = NotificationType.NEW_COMMENT,
            category = "COMENTARIO EN CASO",
            title = "Dr. Andrés Vega (Veterinario)",
            message = "Comentó en Max: \"Puedo aportar un bulto de alimento medicado y desparasitación gratuita en mi clínica de Usaquén...\"",
            time = "Hace 4h",
            referenceId = "targetPostId: max_1",
            actionLabel = "Responder",
            isRead = true
        ),
        AppNotification(
            id = "n5",
            type = NotificationType.BADGE_EARNED,
            category = "LOGRO DE RESCATE",
            title = "Insignia Primera Huella",
            message = "Obtuviste la insignia por haber registrado y verificado tu primer caso con respaldo veterinario en la plataforma.",
            time = "Ayer",
            isRead = true
        )
    )
}
