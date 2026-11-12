package co.edu.adopcionmascota.domain.usecase.publication

import co.edu.adopcionmascota.R
import co.edu.adopcionmascota.core.util.ResourceProvider
import co.edu.adopcionmascota.domain.model.NotificationType
import co.edu.adopcionmascota.domain.model.PublicationStatus
import co.edu.adopcionmascota.domain.model.ReputationPoints
import co.edu.adopcionmascota.domain.repository.PublicationRepository
import co.edu.adopcionmascota.domain.repository.UserRepository
import co.edu.adopcionmascota.domain.usecase.notification.SendNotificationUseCase
import javax.inject.Inject

// El moderador aprueba: la publicación pasa al feed y al mapa, el autor gana puntos
// y recibe la notificación "Publicación Aprobada"
class ApprovePublicationUseCase @Inject constructor(
    private val publicationRepository: PublicationRepository,
    private val userRepository: UserRepository,
    private val sendNotificationUseCase: SendNotificationUseCase,
    private val resources: ResourceProvider
) {
    suspend operator fun invoke(publicationId: String) {
        val publication = publicationRepository.findById(publicationId)
            ?: throw IllegalArgumentException("La publicación no existe")

        publicationRepository.updateStatus(
            id = publicationId,
            status = PublicationStatus.VERIFIED,
            rejectionReason = null,
            reviewedAt = System.currentTimeMillis()
        )
        userRepository.addPoints(publication.ownerId, ReputationPoints.PUBLICATION_APPROVED)

        sendNotificationUseCase(
            userId = publication.ownerId,
            type = NotificationType.PUBLICATION_APPROVED,
            category = resources.getString(R.string.notif_approved_category),
            title = resources.getString(R.string.notif_approved_title),
            message = resources.getString(R.string.notif_approved_message, publication.name),
            publicationId = publicationId,
            actionLabel = resources.getString(R.string.notif_action_view_case)
        )
    }
}

// El moderador rechaza con un motivo: la publicación no aparece en el feed
// y el autor recibe la notificación con el motivo para corregirla
class RejectPublicationUseCase @Inject constructor(
    private val publicationRepository: PublicationRepository,
    private val sendNotificationUseCase: SendNotificationUseCase,
    private val resources: ResourceProvider
) {
    suspend operator fun invoke(publicationId: String, reason: String) {
        require(reason.isNotBlank()) { "Debes indicar el motivo del rechazo" }
        val publication = publicationRepository.findById(publicationId)
            ?: throw IllegalArgumentException("La publicación no existe")

        publicationRepository.updateStatus(
            id = publicationId,
            status = PublicationStatus.REJECTED,
            rejectionReason = reason,
            reviewedAt = System.currentTimeMillis()
        )

        sendNotificationUseCase(
            userId = publication.ownerId,
            type = NotificationType.PUBLICATION_PAUSED,
            category = resources.getString(R.string.notif_rejected_category),
            title = resources.getString(R.string.notif_rejected_title),
            message = resources.getString(R.string.notif_rejected_message, publication.name, reason),
            publicationId = publicationId,
            actionLabel = resources.getString(R.string.notif_action_update)
        )
    }
}
