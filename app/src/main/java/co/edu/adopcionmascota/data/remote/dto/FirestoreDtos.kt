package co.edu.adopcionmascota.data.remote.dto

import co.edu.adopcionmascota.data.local.entity.AdoptionRequestEntity
import co.edu.adopcionmascota.data.local.entity.CommentEntity
import co.edu.adopcionmascota.data.local.entity.NotificationEntity
import co.edu.adopcionmascota.data.local.entity.PublicationEntity
import co.edu.adopcionmascota.data.local.entity.UserEntity
import co.edu.adopcionmascota.domain.model.AdoptionRequest
import co.edu.adopcionmascota.domain.model.Comment
import co.edu.adopcionmascota.domain.model.Notification
import co.edu.adopcionmascota.domain.model.Publication
import co.edu.adopcionmascota.domain.model.User
import co.edu.adopcionmascota.domain.model.UserRole
import com.google.firebase.firestore.DocumentSnapshot

// DTOs: representación de cada modelo tal como se guarda en Firestore (guía 19).
// Se leen campo por campo desde el DocumentSnapshot (en lugar de toObject) porque las
// propiedades "isAlgo" de Kotlin se guardarían con otro nombre y los val no tienen setter.
// Cada DTO sabe convertirse al Map que se escribe y a la entidad de Room que sirve de caché.

private fun DocumentSnapshot.str(field: String) = getString(field).orEmpty()
private fun DocumentSnapshot.long(field: String) = getLong(field) ?: 0L
private fun DocumentSnapshot.int(field: String) = getLong(field)?.toInt() ?: 0
private fun DocumentSnapshot.bool(field: String) = getBoolean(field) ?: false
private fun DocumentSnapshot.double(field: String) = getDouble(field) ?: 0.0

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

// ---------------- publications/{id} ----------------
data class PublicationDto(
    val id: String = "",
    val name: String = "",
    val description: String = "",
    val location: String = "",
    val status: String = "",
    val type: String = "",
    val image: String = "",
    val createdAt: Long = 0L,
    val distancia: String = "",
    val likes: Int = 0,
    val age: String = "",
    val gender: String = "",
    val species: String = "",
    val breed: String = "",
    val size: String = "",
    val isVaccinated: Boolean = false,
    val isDewormed: Boolean = false,
    val isSterilized: Boolean = false,
    val hasMicrochip: Boolean = false,
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val ownerId: String = "",
    val rejectionReason: String? = null,
    val reviewedAt: Long? = null
) {
    fun toMap(): Map<String, Any?> = mapOf(
        "id" to id, "name" to name, "description" to description, "location" to location,
        "status" to status, "type" to type, "image" to image, "createdAt" to createdAt,
        "distancia" to distancia, "likes" to likes, "age" to age, "gender" to gender,
        "species" to species, "breed" to breed, "size" to size, "isVaccinated" to isVaccinated,
        "isDewormed" to isDewormed, "isSterilized" to isSterilized, "hasMicrochip" to hasMicrochip,
        "latitude" to latitude, "longitude" to longitude, "ownerId" to ownerId,
        "rejectionReason" to rejectionReason, "reviewedAt" to reviewedAt
    )

    fun toEntity() = PublicationEntity(
        id = id, name = name, description = description, location = location, status = status,
        type = type, image = image, createdAt = createdAt, distancia = distancia, likes = likes,
        age = age, gender = gender, species = species, breed = breed, size = size,
        isVaccinated = isVaccinated, isDewormed = isDewormed, isSterilized = isSterilized,
        hasMicrochip = hasMicrochip, latitude = latitude, longitude = longitude, ownerId = ownerId,
        rejectionReason = rejectionReason, reviewedAt = reviewedAt
    )

    companion object {
        fun fromDomain(p: Publication) = PublicationDto(
            id = p.id, name = p.name, description = p.description, location = p.location,
            status = p.status.name, type = p.type.name, image = p.image, createdAt = p.createdAt,
            distancia = p.distancia, likes = p.likes, age = p.age, gender = p.gender,
            species = p.species, breed = p.breed, size = p.size, isVaccinated = p.isVaccinated,
            isDewormed = p.isDewormed, isSterilized = p.isSterilized, hasMicrochip = p.hasMicrochip,
            latitude = p.coordinates.latitude, longitude = p.coordinates.longitude,
            ownerId = p.ownerId, rejectionReason = p.rejectionReason, reviewedAt = p.reviewedAt
        )

        fun fromSnapshot(doc: DocumentSnapshot) = PublicationDto(
            id = doc.id, name = doc.str("name"), description = doc.str("description"),
            location = doc.str("location"), status = doc.getString("status") ?: "PENDING",
            type = doc.getString("type") ?: "ADOPCION", image = doc.str("image"),
            createdAt = doc.long("createdAt"), distancia = doc.str("distancia"),
            likes = doc.int("likes"), age = doc.str("age"), gender = doc.str("gender"),
            species = doc.str("species"), breed = doc.str("breed"), size = doc.str("size"),
            isVaccinated = doc.bool("isVaccinated"), isDewormed = doc.bool("isDewormed"),
            isSterilized = doc.bool("isSterilized"), hasMicrochip = doc.bool("hasMicrochip"),
            latitude = doc.double("latitude"), longitude = doc.double("longitude"),
            ownerId = doc.str("ownerId"), rejectionReason = doc.getString("rejectionReason"),
            reviewedAt = doc.getLong("reviewedAt")
        )
    }
}

// ---------------- comments/{id} ----------------
// "thankedBy" guarda los uid que agradecieron; "thankedByMe" se calcula para el usuario actual
data class CommentDto(
    val id: String = "",
    val publicationId: String = "",
    val authorId: String = "",
    val author: String = "",
    val authorTag: String = "",
    val text: String = "",
    val type: String = "",
    val createdAt: Long = 0L,
    val thanks: Int = 0,
    val thankedBy: List<String> = emptyList()
) {
    fun toMap(): Map<String, Any?> = mapOf(
        "id" to id, "publicationId" to publicationId, "authorId" to authorId, "author" to author,
        "authorTag" to authorTag, "text" to text, "type" to type, "createdAt" to createdAt,
        "thanks" to thanks, "thankedBy" to thankedBy
    )

    fun toEntity(currentUserId: String?) = CommentEntity(
        id = id, publicationId = publicationId, authorId = authorId, author = author,
        authorTag = authorTag, text = text, type = type, createdAt = createdAt, thanks = thanks,
        thankedByMe = currentUserId != null && currentUserId in thankedBy
    )

    companion object {
        fun fromDomain(c: Comment, currentUserId: String?) = CommentDto(
            id = c.id, publicationId = c.publicationId, authorId = c.authorId, author = c.author,
            authorTag = c.authorTag, text = c.text, type = c.type.name, createdAt = c.createdAt,
            thanks = c.thanks,
            thankedBy = if (c.thankedByMe && currentUserId != null) listOf(currentUserId) else emptyList()
        )

        @Suppress("UNCHECKED_CAST")
        fun fromSnapshot(doc: DocumentSnapshot) = CommentDto(
            id = doc.id, publicationId = doc.str("publicationId"), authorId = doc.str("authorId"),
            author = doc.str("author"), authorTag = doc.str("authorTag"), text = doc.str("text"),
            type = doc.getString("type") ?: "COMMENT", createdAt = doc.long("createdAt"),
            thanks = doc.int("thanks"),
            thankedBy = (doc.get("thankedBy") as? List<String>).orEmpty()
        )
    }
}

// ---------------- notifications/{id} ----------------
data class NotificationDto(
    val id: String = "",
    val userId: String = "",
    val type: String = "",
    val category: String = "",
    val title: String = "",
    val message: String = "",
    val createdAt: Long = 0L,
    val publicationId: String? = null,
    val referenceId: String? = null,
    val actionLabel: String? = null,
    val isRead: Boolean = false
) {
    fun toMap(): Map<String, Any?> = mapOf(
        "id" to id, "userId" to userId, "type" to type, "category" to category, "title" to title,
        "message" to message, "createdAt" to createdAt, "publicationId" to publicationId,
        "referenceId" to referenceId, "actionLabel" to actionLabel, "isRead" to isRead
    )

    fun toEntity() = NotificationEntity(
        id = id, userId = userId, type = type, category = category, title = title,
        message = message, createdAt = createdAt, publicationId = publicationId,
        referenceId = referenceId, actionLabel = actionLabel, isRead = isRead
    )

    companion object {
        fun fromDomain(n: Notification) = NotificationDto(
            id = n.id, userId = n.userId, type = n.type.name, category = n.category,
            title = n.title, message = n.message, createdAt = n.createdAt,
            publicationId = n.publicationId, referenceId = n.referenceId,
            actionLabel = n.actionLabel, isRead = n.isRead
        )

        fun fromSnapshot(doc: DocumentSnapshot) = NotificationDto(
            id = doc.id, userId = doc.str("userId"), type = doc.str("type"),
            category = doc.str("category"), title = doc.str("title"), message = doc.str("message"),
            createdAt = doc.long("createdAt"), publicationId = doc.getString("publicationId"),
            referenceId = doc.getString("referenceId"), actionLabel = doc.getString("actionLabel"),
            isRead = doc.bool("isRead")
        )
    }
}

// ---------------- adoption_requests/{id} ----------------
data class AdoptionRequestDto(
    val id: String = "",
    val publicationId: String = "",
    val publicationName: String = "",
    val ownerId: String = "",
    val requesterId: String = "",
    val requesterName: String = "",
    val type: String = "",
    val message: String = "",
    val status: String = "",
    val createdAt: Long = 0L,
    val housing: String = "",
    val experience: String = "",
    val availableTime: String = "",
    val otherPets: String = "",
    val compatibilityScore: Int? = null,
    val compatibilityNotes: List<String> = emptyList(),
    val aiGenerated: Boolean = false
) {
    fun toMap(): Map<String, Any?> = mapOf(
        "id" to id, "publicationId" to publicationId, "publicationName" to publicationName,
        "ownerId" to ownerId, "requesterId" to requesterId, "requesterName" to requesterName,
        "type" to type, "message" to message, "status" to status, "createdAt" to createdAt,
        "housing" to housing, "experience" to experience, "availableTime" to availableTime,
        "otherPets" to otherPets, "compatibilityScore" to compatibilityScore,
        "compatibilityNotes" to compatibilityNotes, "aiGenerated" to aiGenerated
    )

    fun toEntity() = AdoptionRequestEntity(
        id = id, publicationId = publicationId, publicationName = publicationName,
        ownerId = ownerId, requesterId = requesterId, requesterName = requesterName, type = type,
        message = message, status = status, createdAt = createdAt, housing = housing,
        experience = experience, availableTime = availableTime, otherPets = otherPets,
        compatibilityScore = compatibilityScore,
        compatibilityNotes = compatibilityNotes.joinToString(","), aiGenerated = aiGenerated
    )

    companion object {
        fun fromDomain(r: AdoptionRequest) = AdoptionRequestDto(
            id = r.id, publicationId = r.publicationId, publicationName = r.publicationName,
            ownerId = r.ownerId, requesterId = r.requesterId, requesterName = r.requesterName,
            type = r.type.name, message = r.message, status = r.status.name,
            createdAt = r.createdAt, housing = r.housing, experience = r.experience,
            availableTime = r.availableTime, otherPets = r.otherPets,
            compatibilityScore = r.compatibilityScore, compatibilityNotes = r.compatibilityNotes,
            aiGenerated = r.aiGenerated
        )

        @Suppress("UNCHECKED_CAST")
        fun fromSnapshot(doc: DocumentSnapshot) = AdoptionRequestDto(
            id = doc.id, publicationId = doc.str("publicationId"),
            publicationName = doc.str("publicationName"), ownerId = doc.str("ownerId"),
            requesterId = doc.str("requesterId"), requesterName = doc.str("requesterName"),
            type = doc.getString("type") ?: "ADOPTION", message = doc.str("message"),
            status = doc.getString("status") ?: "PENDING", createdAt = doc.long("createdAt"),
            housing = doc.str("housing"), experience = doc.str("experience"),
            availableTime = doc.str("availableTime"), otherPets = doc.str("otherPets"),
            compatibilityScore = doc.getLong("compatibilityScore")?.toInt(),
            compatibilityNotes = (doc.get("compatibilityNotes") as? List<String>).orEmpty(),
            aiGenerated = doc.bool("aiGenerated")
        )
    }
}

// Nombres de las colecciones en Firestore
object Collections {
    const val USERS = "users"
    const val PUBLICATIONS = "publications"
    const val COMMENTS = "comments"
    const val NOTIFICATIONS = "notifications"
    const val ADOPTION_REQUESTS = "adoption_requests"
    const val FAVORITES = "favorites"   // Subcolección: users/{uid}/favorites/{publicationId}
    const val META = "meta"
}
