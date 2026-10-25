package co.edu.adopcionmascota.features.moderator.panel

data class ModerationPublicationUi(

    val id: Long,

    val userName: String,

    val userLevel: String,

    val publicationAge: String,

    val title: String,

    val animalType: String,

    val animalAge: String,

    val city: String,

    val imageUrl: Int,

    val photosCount: Int,

    val vaccinesComplete: Boolean,

    val sterilized: Boolean,

    val ethicsAlert: Boolean,

    val ethicsMessage: String,

    val createdAgo: String
)