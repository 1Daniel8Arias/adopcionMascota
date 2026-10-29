package co.edu.adopcionmascota.domain.model

import java.time.LocalDateTime

data class Notification(
    val id: String,
    val type: String,
    val title: String,
    val message: String,
    val date: LocalDateTime,
    val read: Boolean = false, // Indica si el usuario ya abrió la notificación
    val reportId: String? = null // Reporte relacionado con la notificación (si lo hay)
)
