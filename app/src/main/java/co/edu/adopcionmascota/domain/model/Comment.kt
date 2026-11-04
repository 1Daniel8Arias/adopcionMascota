package co.edu.adopcionmascota.domain.model

// Aporte de un usuario en una publicación
data class Comment(
    val id: String,
    val publicationId: String,
    val author: String,
    val authorTag: String,          // Ej: "Vet Verificado", "Hogar Temporal", "Nivel 2"
    val time: String,
    val text: String,
    val type: ContributionType,
    val thanks: Int = 0,
    val thankedByMe: Boolean = false
) {
    // Iniciales para el avatar: "Laura Méndez" -> "LM"
    val initials: String
        get() = author.split(" ")
            .filter { it.isNotBlank() && it.first().isLetter() }
            .take(2)
            .joinToString("") { it.first().uppercase() }
}
