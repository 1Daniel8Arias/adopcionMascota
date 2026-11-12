package co.edu.adopcionmascota.domain.model

// Solicitud de adopción u hogar de paso que un usuario envía desde el detalle de una publicación
data class AdoptionRequest(
    val id: String,
    val publicationId: String,
    val publicationName: String,
    val ownerId: String,            // Autor de la publicación (quien la recibe)
    val requesterId: String,
    val requesterName: String,
    val type: AdoptionRequestType,
    val message: String = "",
    val status: AdoptionRequestStatus = AdoptionRequestStatus.PENDING,
    val createdAt: Long = System.currentTimeMillis(),
    // Solicitud formal (adopción): respuestas del cuestionario y resumen de compatibilidad
    val housing: String = "",              // HousingType.name
    val experience: String = "",           // PetExperience.name
    val availableTime: String = "",        // AvailableTime.name
    val otherPets: String = "",            // OtherPets.name
    val compatibilityScore: Int? = null,   // 0..100, null si no hubo cuestionario
    val compatibilityNotes: List<String> = emptyList(), // Códigos de CompatibilityNote
    val aiGenerated: Boolean = false       // La carta la redactó la IA (y el usuario la revisó)
)

enum class AdoptionRequestType { ADOPTION, TEMPORARY_HOME }

enum class AdoptionRequestStatus { PENDING, ACCEPTED, REJECTED }
