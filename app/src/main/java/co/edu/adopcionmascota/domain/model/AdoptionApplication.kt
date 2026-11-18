package co.edu.adopcionmascota.domain.model

// Respuestas del cuestionario de la solicitud formal de adopción

enum class HousingType { HOUSE_WITH_YARD, HOUSE, APARTMENT, RURAL }

enum class PetExperience { FIRST_TIME, SOME, EXPERIENCED }

enum class AvailableTime { LOW, MEDIUM, HIGH }   // < 2 h, 2-5 h, > 5 h al día

enum class OtherPets { NONE, DOGS, CATS, SEVERAL }

data class AdoptionApplication(
    val housing: HousingType,
    val experience: PetExperience,
    val availableTime: AvailableTime,
    val otherPets: OtherPets,
    val extraInfo: String = ""      // "¿Algo más que quieras contar?"
)

// Observaciones del resumen de compatibilidad (la pantalla las traduce con strings.xml)
enum class CompatibilityNote {
    SPACIOUS_HOME,          // Casa con patio o finca
    SMALL_SPACE_LARGE_PET,  // Apartamento y mascota grande
    FIRST_TIME_OWNER,
    EXPERIENCED_OWNER,
    LOW_TIME,
    GOOD_TIME,
    OTHER_PETS_REVIEW,      // Convivencia con otras especies o varias mascotas
    NO_OTHER_PETS
}

enum class CompatibilityLevel { HIGH, MEDIUM, LOW }

data class Compatibility(val score: Int, val notes: List<CompatibilityNote>) {
    val level: CompatibilityLevel
        get() = levelFor(score)

    companion object {
        fun levelFor(score: Int) = when {
            score >= 75 -> CompatibilityLevel.HIGH
            score >= 50 -> CompatibilityLevel.MEDIUM
            else -> CompatibilityLevel.LOW
        }
    }
}
