package co.edu.adopcionmascota.data.remote.user

import co.edu.adopcionmascota.data.local.entity.UserEntity
import co.edu.adopcionmascota.data.remote.bool
import co.edu.adopcionmascota.data.remote.int
import co.edu.adopcionmascota.data.remote.long
import co.edu.adopcionmascota.data.remote.str
import co.edu.adopcionmascota.domain.model.User
import co.edu.adopcionmascota.domain.model.UserRole
import com.google.firebase.firestore.DocumentSnapshot

// Representación de la colección tal como se guarda en Firestore (guía 19)
// ---------------- users/{uid} ----------------
// La contraseña NO se guarda: la maneja Firebase Authentication (guía 20)
data class UserDto(
    val id: String = "",
    val name: String = "",
    val city: String = "",
    val address: String = "",
    val email: String = "",
    val phoneNumber: String = "",
    val profilePictureUrl: String = "",
    val role: String = UserRole.USER.name,   // "USER" o "MODERATOR"
    val bio: String = "",
    val isTemporaryHome: Boolean = false,
    val lostPetAlerts: Boolean = true,
    val showPublicPhone: Boolean = false,
    val points: Int = 0,
    val createdAt: Long = 0L
) {
    fun toMap(): Map<String, Any?> = mapOf(
        "id" to id, "name" to name, "city" to city, "address" to address, "email" to email,
        "phoneNumber" to phoneNumber, "profilePictureUrl" to profilePictureUrl, "role" to role,
        "bio" to bio, "isTemporaryHome" to isTemporaryHome, "lostPetAlerts" to lostPetAlerts,
        "showPublicPhone" to showPublicPhone, "points" to points, "createdAt" to createdAt
    )

    fun toEntity() = UserEntity(
        id = id, name = name, city = city, address = address, email = email, password = "",
        phoneNumber = phoneNumber, profilePictureUrl = profilePictureUrl, role = role, bio = bio,
        isTemporaryHome = isTemporaryHome, lostPetAlerts = lostPetAlerts,
        showPublicPhone = showPublicPhone, points = points, createdAt = createdAt
    )

    fun toDomain(): User = User(
        id = id, name = name, city = city, address = address, email = email, password = "",
        phoneNumber = phoneNumber, profilePictureUrl = profilePictureUrl,
        // Si llega un rol desconocido se usa USER en lugar de romper la aplicación
        role = roleOf(role), bio = bio, isTemporaryHome = isTemporaryHome,
        lostPetAlerts = lostPetAlerts, showPublicPhone = showPublicPhone, points = points,
        createdAt = createdAt
    )

    companion object {
        fun roleOf(value: String): UserRole =
            UserRole.entries.find { it.name == value } ?: if (value == "ADMIN") UserRole.MODERATOR else UserRole.USER

        fun fromDomain(user: User) = UserDto(
            id = user.id, name = user.name, city = user.city, address = user.address,
            email = user.email, phoneNumber = user.phoneNumber,
            profilePictureUrl = user.profilePictureUrl, role = user.role.name, bio = user.bio,
            isTemporaryHome = user.isTemporaryHome, lostPetAlerts = user.lostPetAlerts,
            showPublicPhone = user.showPublicPhone, points = user.points, createdAt = user.createdAt
        )

        fun fromSnapshot(doc: DocumentSnapshot) = UserDto(
            id = doc.id, name = doc.str("name"), city = doc.str("city"), address = doc.str("address"),
            email = doc.str("email"), phoneNumber = doc.str("phoneNumber"),
            profilePictureUrl = doc.str("profilePictureUrl"),
            role = doc.getString("role") ?: UserRole.USER.name, bio = doc.str("bio"),
            isTemporaryHome = doc.bool("isTemporaryHome"),
            lostPetAlerts = doc.getBoolean("lostPetAlerts") ?: true,
            showPublicPhone = doc.bool("showPublicPhone"), points = doc.int("points"),
            createdAt = doc.long("createdAt")
        )
    }
}
