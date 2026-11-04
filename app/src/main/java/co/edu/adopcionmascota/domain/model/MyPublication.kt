package co.edu.adopcionmascota.domain.model

// Publicación vista por su autor, con su estado de revisión y sus métricas
data class MyPublication(
    val id: String,
    val feedId: String?,            // Id de la publicación en el feed (null si aún no está publicada)
    val name: String,
    val category: String,           // Adopción, Perdido, Encontrado...
    val species: String = "Perro",
    val breed: String = "",
    val size: String = "Mediano",
    val description: String,
    val location: String,
    val coordinates: Location? = null,  // Punto elegido en el mapa al publicar
    val image: Int,
    val imageUri: String? = null,       // Foto elegida en la galería
    val status: MyPublicationStatus,
    val isVaccinated: Boolean = true,
    val isDewormed: Boolean = true,
    val isSterilized: Boolean = false,
    val hasMicrochip: Boolean = false,
    val followers: Int = 0,
    val comments: Int = 0,
    val shares: Int = 0,
    val moderatorNote: String? = null
)
