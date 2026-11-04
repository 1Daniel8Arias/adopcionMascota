package co.edu.adopcionmascota.domain.model

// Datos del usuario que comparten el perfil y la pantalla de edición
data class UserProfile(
    val photoUrl: String = "https://i.pravatar.cc/300?img=47",
    val name: String = "Camila Morales Restrepo",
    val email: String = "camila.morales@ejemplo.com",
    val isEmailVerified: Boolean = true,
    val phone: String = "+57 310 123 4567",
    val locality: String = "Usaquén (Zona Norte)",
    val neighborhood: String = "Cedritos / Santa Bárbara",
    val bio: String = "Amante de los animales, hogar de paso temporal para cachorros y promotora de esterilización responsable en Bogotá",
    val isTemporaryHome: Boolean = true,
    val lostPetAlerts: Boolean = true,
    val showPublicPhone: Boolean = false
) {

    val localityName: String
        get() = locality.substringBefore(" (")
}
