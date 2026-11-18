package co.edu.adopcionmascota.domain.usecase.request

import co.edu.adopcionmascota.domain.model.AdoptionApplication
import co.edu.adopcionmascota.domain.model.AvailableTime
import co.edu.adopcionmascota.domain.model.Compatibility
import co.edu.adopcionmascota.domain.model.CompatibilityNote
import co.edu.adopcionmascota.domain.model.HousingType
import co.edu.adopcionmascota.domain.model.OtherPets
import co.edu.adopcionmascota.domain.model.PetExperience
import co.edu.adopcionmascota.domain.model.Publication
import javax.inject.Inject

// Resumen de compatibilidad entre las respuestas del solicitante y la mascota.
// Son reglas simples y transparentes (no IA): así el resumen sale igual aunque la IA
// no esté disponible y el autor puede entender por qué se obtuvo cada puntaje
class EvaluateCompatibilityUseCase @Inject constructor() {

    operator fun invoke(publication: Publication, application: AdoptionApplication): Compatibility {
        var score = 50
        val notes = mutableListOf<CompatibilityNote>()
        val isLarge = publication.size.contains("grande", ignoreCase = true) ||
            publication.size.contains("large", ignoreCase = true)
        val isCat = publication.species.contains("gato", ignoreCase = true) ||
            publication.species.contains("cat", ignoreCase = true)

        when (application.housing) {
            HousingType.HOUSE_WITH_YARD, HousingType.RURAL -> { score += 20; notes += CompatibilityNote.SPACIOUS_HOME }
            HousingType.HOUSE -> score += 10
            HousingType.APARTMENT -> if (isLarge && !isCat) {
                score -= 10; notes += CompatibilityNote.SMALL_SPACE_LARGE_PET
            } else score += 5
        }

        when (application.experience) {
            PetExperience.FIRST_TIME -> notes += CompatibilityNote.FIRST_TIME_OWNER
            PetExperience.SOME -> score += 10
            PetExperience.EXPERIENCED -> { score += 20; notes += CompatibilityNote.EXPERIENCED_OWNER }
        }

        when (application.availableTime) {
            AvailableTime.LOW -> { score -= 15; notes += CompatibilityNote.LOW_TIME }
            AvailableTime.MEDIUM -> score += 5
            AvailableTime.HIGH -> { score += 15; notes += CompatibilityNote.GOOD_TIME }
        }

        when (application.otherPets) {
            OtherPets.NONE -> { score += 5; notes += CompatibilityNote.NO_OTHER_PETS }
            OtherPets.DOGS -> if (isCat) { score -= 5; notes += CompatibilityNote.OTHER_PETS_REVIEW }
            OtherPets.CATS -> if (!isCat) { score -= 5; notes += CompatibilityNote.OTHER_PETS_REVIEW }
            OtherPets.SEVERAL -> { score -= 5; notes += CompatibilityNote.OTHER_PETS_REVIEW }
        }

        return Compatibility(score.coerceIn(0, 100), notes)
    }
}
