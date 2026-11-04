package co.edu.adopcionmascota.domain.model


data class Publication(
    val id: String,
    val name: String,
    val description: String,
    val location: String,
    val status: PublicationStatus,
    val type: PetType,
    val image: Int,
    val time: String,
    val distancia: String,
    val likes: String = "",
    val age: String = "",
    val gender: String = "",
    val coordinates: Location = Location(latitude = 4.5389, longitude = -75.6725), // Punto en el mapa
    val ownerId: String = "" // ID del usuario que creó la publicación
) 
