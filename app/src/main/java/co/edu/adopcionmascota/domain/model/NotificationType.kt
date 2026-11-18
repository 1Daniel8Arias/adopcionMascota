package co.edu.adopcionmascota.domain.model

enum class NotificationType {
    PUBLICATION_APPROVED,   // MODERACION_APROBADA
    LEVEL_UP,               // NUEVO_LOGRO (nivel)
    PUBLICATION_PAUSED,     // MODERACION_RECHAZADA
    NEW_COMMENT,            // NUEVO_COMENTARIO
    BADGE_EARNED,           // NUEVO_LOGRO (insignia)
    ADOPTION_REQUEST,       // SOLICITUD_RECIBIDA (para el autor de la publicación)
    REQUEST_ANSWERED        // SOLICITUD_RESPONDIDA (para quien envió la solicitud)
}
