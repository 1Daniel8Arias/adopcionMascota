package co.edu.adopcionmascota.domain.usecase.publication

import co.edu.adopcionmascota.domain.Publication
import co.edu.adopcionmascota.domain.repository.PublicationRepository
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

class ObserveReportsUseCase @Inject constructor(
    private val publicationRepository: PublicationRepository
){
    operator fun invoke(): StateFlow<List<Publication>> = publicationRepository.publication
}