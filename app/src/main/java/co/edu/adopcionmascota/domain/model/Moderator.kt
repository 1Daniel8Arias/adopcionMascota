package co.edu.adopcionmascota.domain.model

data class Moderator(
    val id: String,
    val name: String,
    val city:String,
    val address: String,
    val email: String,
    val password: String,
    val phoneNumber: String="",
    val profilePictureUrl: String="",
    val role: UserRole= UserRole.ADMIN
) {
}