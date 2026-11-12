package co.edu.adopcionmascota.domain.usecase.request

import co.edu.adopcionmascota.R
import co.edu.adopcionmascota.core.util.ResourceProvider
import co.edu.adopcionmascota.domain.model.AdoptionApplication
import co.edu.adopcionmascota.domain.model.AdoptionRequest
import co.edu.adopcionmascota.domain.model.AdoptionRequestStatus
import co.edu.adopcionmascota.domain.model.AdoptionRequestType
import co.edu.adopcionmascota.domain.model.NotificationType
import co.edu.adopcionmascota.domain.model.PublicationStatus
import co.edu.adopcionmascota.domain.model.ReputationPoints
import co.edu.adopcionmascota.domain.repository.AdoptionRequestRepository
import co.edu.adopcionmascota.domain.repository.PublicationRepository
import co.edu.adopcionmascota.domain.repository.SessionRepository
import co.edu.adopcionmascota.domain.repository.UserRepository
import co.edu.adopcionmascota.domain.usecase.notification.SendNotificationUseCase
import co.edu.adopcionmascota.domain.usecase.user.GetCurrentUserUseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import java.util.UUID
import javax.inject.Inject

// Envía una solicitud de adopción u hogar de paso y notifica al autor de la publicación
class SendAdoptionRequestUseCase @Inject constructor(
    private val requestRepository: AdoptionRequestRepository,
    private val publicationRepository: PublicationRepository,
    private val getCurrentUserUseCase: GetCurrentUserUseCase,
    private val sendNotificationUseCase: SendNotificationUseCase,
    private val resources: ResourceProvider,
    private val evaluateCompatibility: EvaluateCompatibilityUseCase = EvaluateCompatibilityUseCase()
) {
    // application: respuestas del cuestionario de la solicitud formal (solo adopción)
    // aiGenerated: la carta (message) la redactó la IA y el usuario la revisó
    suspend operator fun invoke(
        publicationId: String,
        type: AdoptionRequestType,
        message: String = "",
        application: AdoptionApplication? = null,
        aiGenerated: Boolean = false
    ): AdoptionRequest {
        val requester = getCurrentUserUseCase() ?: throw IllegalStateException("No hay una sesión activa")
        val publication = publicationRepository.findById(publicationId)
            ?: throw IllegalArgumentException("La publicación no existe")
        require(publication.ownerId != requester.id) { "No puedes enviar una solicitud a tu propia publicación" }

        val alreadySent = requestRepository.observeByRequester(requester.id, publicationId).first()
            .any { it.type == type && it.status == AdoptionRequestStatus.PENDING }
        require(!alreadySent) { "Ya enviaste esta solicitud" }

        val request = AdoptionRequest(
            id = UUID.randomUUID().toString(),
            publicationId = publicationId,
            publicationName = publication.name,
            ownerId = publication.ownerId,
            requesterId = requester.id,
            requesterName = requester.name,
            type = type,
            message = message.trim()
        ).let { base ->
            if (application == null) base
            else {
                // El autor ve el resumen de compatibilidad en "Solicitudes recibidas"
                val compatibility = evaluateCompatibility(publication, application)
                base.copy(
                    housing = application.housing.name,
                    experience = application.experience.name,
                    availableTime = application.availableTime.name,
                    otherPets = application.otherPets.name,
                    compatibilityScore = compatibility.score,
                    compatibilityNotes = compatibility.notes.map { it.name },
                    aiGenerated = aiGenerated
                )
            }
        }
        requestRepository.save(request)

        val text = when (type) {
            AdoptionRequestType.ADOPTION -> R.string.notif_request_adopt_message
            AdoptionRequestType.TEMPORARY_HOME -> R.string.notif_request_foster_message
        }
        sendNotificationUseCase(
            userId = publication.ownerId,
            type = NotificationType.ADOPTION_REQUEST,
            category = resources.getString(R.string.notif_request_category),
            title = resources.getString(R.string.notif_request_title),
            message = resources.getString(text, requester.name, publication.name),
            publicationId = publicationId,
            actionLabel = resources.getString(R.string.notif_action_view_requests)
        )
        return request
    }
}

// Solicitudes recibidas por el usuario de la sesión (autor de las publicaciones)
class ObserveReceivedRequestsUseCase @Inject constructor(
    private val sessionRepository: SessionRepository,
    private val requestRepository: AdoptionRequestRepository
) {
    @OptIn(ExperimentalCoroutinesApi::class)
    operator fun invoke(): Flow<List<AdoptionRequest>> = sessionRepository.session.flatMapLatest { session ->
        if (session == null) flowOf(emptyList()) else requestRepository.observeByOwner(session.userId)
    }
}

// Solicitudes que el usuario de la sesión ya envió para una publicación
class ObserveMyRequestsUseCase @Inject constructor(
    private val sessionRepository: SessionRepository,
    private val requestRepository: AdoptionRequestRepository
) {
    @OptIn(ExperimentalCoroutinesApi::class)
    operator fun invoke(publicationId: String): Flow<List<AdoptionRequest>> = sessionRepository.session.flatMapLatest { session ->
        if (session == null) flowOf(emptyList()) else requestRepository.observeByRequester(session.userId, publicationId)
    }
}

// El autor acepta o rechaza la solicitud. Aceptar una adopción cierra el caso (RESOLVED)
class RespondAdoptionRequestUseCase @Inject constructor(
    private val requestRepository: AdoptionRequestRepository,
    private val publicationRepository: PublicationRepository,
    private val userRepository: UserRepository,
    private val sendNotificationUseCase: SendNotificationUseCase,
    private val resources: ResourceProvider
) {
    suspend operator fun invoke(requestId: String, accept: Boolean) {
        val request = requestRepository.findById(requestId)
            ?: throw IllegalArgumentException("La solicitud no existe")
        val status = if (accept) AdoptionRequestStatus.ACCEPTED else AdoptionRequestStatus.REJECTED
        requestRepository.updateStatus(requestId, status)

        if (accept && request.type == AdoptionRequestType.ADOPTION) {
            publicationRepository.updateStatus(request.publicationId, PublicationStatus.RESOLVED)
            userRepository.addPoints(request.ownerId, ReputationPoints.ADOPTION_COMPLETED)
        }

        val message = if (accept) R.string.notif_answer_accepted else R.string.notif_answer_rejected
        sendNotificationUseCase(
            userId = request.requesterId,
            type = NotificationType.REQUEST_ANSWERED,
            category = resources.getString(R.string.notif_answer_category),
            title = resources.getString(if (accept) R.string.notif_answer_accepted_title else R.string.notif_answer_rejected_title),
            message = resources.getString(message, request.publicationName),
            publicationId = request.publicationId,
            actionLabel = resources.getString(R.string.notif_action_view_case)
        )
    }
}
