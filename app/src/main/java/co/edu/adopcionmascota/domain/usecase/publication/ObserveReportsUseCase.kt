package co.edu.adopcionmascota.domain.usecase.publication

import co.edu.adopcionmascota.domain.model.Publication
import co.edu.adopcionmascota.domain.model.PublicationStatus
import co.edu.adopcionmascota.domain.repository.PublicationRepository
import co.edu.adopcionmascota.domain.repository.SessionRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject

// Todas las publicaciones (cualquier estado excepto eliminadas)
class ObserveReportsUseCase @Inject constructor(
    private val publicationRepository: PublicationRepository
) {
    operator fun invoke(): Flow<List<Publication>> = publicationRepository.publication
}

// Feed público y mapa: solo las publicaciones aprobadas por un moderador
class ObserveFeedUseCase @Inject constructor(
    private val publicationRepository: PublicationRepository
) {
    operator fun invoke(): Flow<List<Publication>> =
        publicationRepository.observeByStatus(PublicationStatus.VERIFIED)
}

// Cola del moderador: publicaciones en revisión
class ObservePendingPublicationsUseCase @Inject constructor(
    private val publicationRepository: PublicationRepository
) {
    operator fun invoke(): Flow<List<Publication>> =
        publicationRepository.observeByStatus(PublicationStatus.PENDING)
}

// "Mis Publicaciones": las del usuario de la sesión, en cualquier estado
class ObserveMyPublicationsUseCase @Inject constructor(
    private val sessionRepository: SessionRepository,
    private val publicationRepository: PublicationRepository
) {
    @OptIn(ExperimentalCoroutinesApi::class)
    operator fun invoke(): Flow<List<Publication>> = sessionRepository.session.flatMapLatest { session ->
        if (session == null) flowOf(emptyList()) else publicationRepository.observeByOwner(session.userId)
    }
}

class ObservePublicationUseCase @Inject constructor(
    private val publicationRepository: PublicationRepository
) {
    operator fun invoke(id: String): Flow<Publication?> = publicationRepository.observeById(id)
}
