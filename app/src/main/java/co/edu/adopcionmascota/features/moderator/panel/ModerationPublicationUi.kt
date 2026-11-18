package co.edu.adopcionmascota.features.moderator.panel

import co.edu.adopcionmascota.domain.model.PetType
import co.edu.adopcionmascota.domain.model.PublicationStatus

// Tarjeta de la cola de moderación; se construye a partir de domain/Publication y su autor
data class ModerationPublicationUi(
    val id: String,
    val userName: String,
    val userLevel: String,
    val publicationAge: String,
    val title: String,
    val animalType: String,
    val animalAge: String,
    val city: String,
    val image: String,
    val type: PetType,
    val status: PublicationStatus,
    val vaccinesComplete: Boolean,
    val sterilized: Boolean,
    val ethicsAlert: Boolean,
    val ethicsMessage: String,
    val rejectionReason: String?,
    val createdAgo: String
)
