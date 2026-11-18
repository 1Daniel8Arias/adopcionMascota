package co.edu.adopcionmascota.domain.usecase.publication

import co.edu.adopcionmascota.domain.model.Publication
import co.edu.adopcionmascota.domain.model.PublicationStatus
import co.edu.adopcionmascota.domain.repository.PublicationRepository
import co.edu.adopcionmascota.domain.usecase.user.GetCurrentUserUseCase
import javax.inject.Inject

// Toda publicación nueva queda "En revisión" hasta que un moderador la apruebe
class CreateReportUseCase @Inject constructor(
    private val publicationRepository: PublicationRepository,
    private val getCurrentUserUseCase: GetCurrentUserUseCase
) {
    suspend operator fun invoke(publication: Publication) {
        val user = getCurrentUserUseCase() ?: throw IllegalStateException("No hay una sesión activa")
        require(publication.name.isNotBlank()) { "El nombre de la mascota es obligatorio" }
        publicationRepository.save(
            publication.copy(
                ownerId = user.id,
                status = PublicationStatus.PENDING,
                createdAt = System.currentTimeMillis(),
                rejectionReason = null,
                reviewedAt = null
            )
        )
    }
}
