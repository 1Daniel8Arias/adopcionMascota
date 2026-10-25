package co.edu.adopcionmascota.domain.usecase.publication

import co.edu.adopcionmascota.domain.Publication
import co.edu.adopcionmascota.domain.repository.PublicationRepository
import javax.inject.Inject

class CreateReportUseCase @Inject constructor(
    private val publicationRepository: PublicationRepository
) {
    suspend operator fun invoke(publication: Publication){
        publicationRepository.save(publication)
    }
}