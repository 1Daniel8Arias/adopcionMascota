package co.edu.adopcionmascota.domain.model

// Entidad principal del dominio. Contiene todos los datos de la mascota
// (antes repartidos en MyPublication y en datos quemados de la capa de presentación)
data class Publication(
    val id: String,
    val name: String,
    val description: String,
    val location: String,
    val status: PublicationStatus,
    val type: PetType,
    val image: String,                  // Nombre del recurso drawable (datos de ejemplo) o URI de la galería
    val createdAt: Long = System.currentTimeMillis(),
    val distancia: String = "1 km",
    val likes: Int = 0,
    val age: String = "",
    val gender: String = "",
    val species: String = "Perro",
    val breed: String = "",
    val size: String = "Mediano",
    val isVaccinated: Boolean = false,
    val isDewormed: Boolean = false,
    val isSterilized: Boolean = false,
    val hasMicrochip: Boolean = false,
    val coordinates: Location = Location(latitude = 4.5389, longitude = -75.6725),
    val ownerId: String = "",
    val rejectionReason: String? = null,    // Motivo que escribe el moderador al rechazar
    val reviewedAt: Long? = null            // Cuándo la revisó el moderador
) {
    // Señal para la revisión ética: el texto menciona dinero o venta
    val mentionsMoney: Boolean
        get() = MONEY_WORDS.any { description.contains(it, ignoreCase = true) || name.contains(it, ignoreCase = true) }

    companion object {
        private val MONEY_WORDS = listOf("$", "precio", "venta", "vendo", "aporte económico", "pedigrí")
    }
}
