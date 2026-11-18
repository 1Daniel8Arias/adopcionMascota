package co.edu.adopcionmascota.domain.usecase.request

import co.edu.adopcionmascota.domain.model.AdoptionApplication
import co.edu.adopcionmascota.domain.repository.AdoptionLetterRepository
import co.edu.adopcionmascota.domain.repository.PublicationRepository
import co.edu.adopcionmascota.domain.usecase.user.GetCurrentUserUseCase
import javax.inject.Inject

// Pide a la IA el borrador de la carta formal; el usuario la revisa y la edita antes de enviarla
class GenerateAdoptionLetterUseCase @Inject constructor(
    private val letterRepository: AdoptionLetterRepository,
    private val publicationRepository: PublicationRepository,
    private val getCurrentUserUseCase: GetCurrentUserUseCase
) {
    suspend operator fun invoke(publicationId: String, application: AdoptionApplication, language: String): String {
        val applicant = getCurrentUserUseCase() ?: throw IllegalStateException("No hay una sesión activa")
        val publication = publicationRepository.findById(publicationId)
            ?: throw IllegalArgumentException("La publicación no existe")
        return letterRepository.generateLetter(publication, applicant, application, language).trim()
    }
}
