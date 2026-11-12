package co.edu.adopcionmascota.domain.usecase.statistics

import co.edu.adopcionmascota.domain.model.AdoptionRequestStatus
import co.edu.adopcionmascota.domain.model.AdoptionRequestType
import co.edu.adopcionmascota.domain.model.PetType
import co.edu.adopcionmascota.domain.model.PublicationStatus
import co.edu.adopcionmascota.domain.model.Statistics
import co.edu.adopcionmascota.domain.repository.AdoptionRequestRepository
import co.edu.adopcionmascota.domain.repository.CommentRepository
import co.edu.adopcionmascota.domain.repository.PublicationRepository
import co.edu.adopcionmascota.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

// Estadísticas de la plataforma calculadas con los datos reales de Room;
// se recalculan solas cada vez que cambia alguna tabla
class ObserveStatisticsUseCase @Inject constructor(
    private val publicationRepository: PublicationRepository,
    private val requestRepository: AdoptionRequestRepository,
    private val commentRepository: CommentRepository,
    private val userRepository: UserRepository
) {
    operator fun invoke(): Flow<Statistics> = combine(
        publicationRepository.publication,
        requestRepository.requests,
        commentRepository.comments,
        userRepository.observeCount()
    ) { publications, requests, comments, users ->
        val reviewTimes = publications.mapNotNull { p -> p.reviewedAt?.let { (it - p.createdAt) / 60_000 } }
        Statistics(
            totalPublications = publications.size,
            byStatus = PublicationStatus.entries.associateWith { status -> publications.count { it.status == status } },
            byType = PetType.entries.associateWith { type -> publications.count { it.type == type } },
            completedAdoptions = maxOf(
                publications.count { it.type == PetType.ADOPCION && it.status == PublicationStatus.RESOLVED },
                requests.count { it.type == AdoptionRequestType.ADOPTION && it.status == AdoptionRequestStatus.ACCEPTED }
            ),
            totalRequests = requests.size,
            pendingRequests = requests.count { it.status == AdoptionRequestStatus.PENDING },
            acceptedRequests = requests.count { it.status == AdoptionRequestStatus.ACCEPTED },
            totalComments = comments.size,
            totalUsers = users,
            averageReviewMinutes = if (reviewTimes.isEmpty()) 0 else reviewTimes.average().toInt()
        )
    }
}
