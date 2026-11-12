package co.edu.adopcionmascota.domain.usecase.publication

import co.edu.adopcionmascota.domain.model.Publication
import co.edu.adopcionmascota.domain.model.PublicationStatus
import co.edu.adopcionmascota.domain.repository.PublicationRepository
import javax.inject.Inject

// Editar una publicación rechazada la devuelve a la cola del moderador
class UpdatePublicationUseCase @Inject constructor(
    private val publicationRepository: PublicationRepository
) {
    suspend operator fun invoke(publication: Publication) {
        val updated = if (publication.status == PublicationStatus.REJECTED) {
            publication.copy(status = PublicationStatus.PENDING, rejectionReason = null)
        } else publication
        publicationRepository.update(updated)
    }
}

// Acciones del autor sobre su publicación: marcar resuelta, pausar o eliminar
class ChangePublicationStatusUseCase @Inject constructor(
    private val publicationRepository: PublicationRepository
) {
    suspend operator fun invoke(id: String, status: PublicationStatus) {
        val publication = publicationRepository.findById(id) ?: return
        publicationRepository.updateStatus(id, status, publication.rejectionReason)
    }
}
