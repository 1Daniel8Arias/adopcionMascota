package co.edu.adopcionmascota.domain.model

data class UserSession(
    val userId: String,
    val role: UserRole
) {

}