package co.edu.adopcionmascota.domain.usecase.notification

import co.edu.adopcionmascota.domain.model.Notification
import co.edu.adopcionmascota.domain.model.NotificationType
import co.edu.adopcionmascota.domain.repository.NotificationRepository
import java.util.UUID
import javax.inject.Inject

// Crea una notificación para un usuario. La usan los demás casos de uso
// (aprobar, rechazar, comentar, solicitar adopción y responder la solicitud)
class SendNotificationUseCase @Inject constructor(
    private val notificationRepository: NotificationRepository
) {
    suspend operator fun invoke(
        userId: String,
        type: NotificationType,
        category: String,
        title: String,
        message: String,
        publicationId: String? = null,
        actionLabel: String? = null
    ) {
        notificationRepository.save(
            Notification(
                id = UUID.randomUUID().toString(),
                userId = userId,
                type = type,
                category = category,
                title = title,
                message = message,
                publicationId = publicationId,
                referenceId = publicationId?.let { "ID: ${it.take(8)}" },
                actionLabel = actionLabel
            )
        )
    }
}
