package co.edu.adopcionmascota.domain.repository

import co.edu.adopcionmascota.domain.model.AdoptionApplication
import co.edu.adopcionmascota.domain.model.Publication
import co.edu.adopcionmascota.domain.model.User

// Redacta con IA la carta formal de adopción a partir de las respuestas del cuestionario
interface AdoptionLetterRepository {
    // language: "es" o "en". Lanza AiRateLimitException (429) o AiUnavailableException
    suspend fun generateLetter(
        publication: Publication,
        applicant: User,
        application: AdoptionApplication,
        language: String
    ): String
}
