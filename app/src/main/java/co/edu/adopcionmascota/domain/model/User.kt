package co.edu.adopcionmascota.domain.model

data class User(
    val id: String,
    val name: String,
    val city: String,
    val address: String,
    val email: String,
    val password: String,
    val phoneNumber: String = "",
    val profilePictureUrl: String = "",
    val role: UserRole = UserRole.USER,
    val bio: String = "",
    val isTemporaryHome: Boolean = false,
    val lostPetAlerts: Boolean = true,
    val showPublicPhone: Boolean = false,
    val points: Int = 0,                    // Reputación acumulada por sus aportes
    val createdAt: Long = System.currentTimeMillis()
) {
    // Nivel de reputación según los puntos (enunciado del proyecto)
    val level: ReputationLevel
        get() = ReputationLevel.forPoints(points)
}
