package co.edu.adopcionmascota.domain.usecase.publication

import co.edu.adopcionmascota.domain.model.Publication
import co.edu.adopcionmascota.domain.repository.PublicationRepository
import javax.inject.Inject

class GetReportByIdUseCase @Inject constructor(
    private val publicationRepository: PublicationRepository
){
    suspend operator fun invoke(publicationId: String): Publication? = publicationRepository.findById(publicationId)
}