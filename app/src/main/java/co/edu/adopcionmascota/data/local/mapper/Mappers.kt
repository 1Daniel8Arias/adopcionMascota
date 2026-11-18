package co.edu.adopcionmascota.data.local.mapper

import co.edu.adopcionmascota.data.local.entity.AdoptionRequestEntity
import co.edu.adopcionmascota.data.local.entity.CommentEntity
import co.edu.adopcionmascota.data.local.entity.NotificationEntity
import co.edu.adopcionmascota.data.local.entity.PublicationEntity
import co.edu.adopcionmascota.data.local.entity.UserEntity
import co.edu.adopcionmascota.domain.model.AdoptionRequest
import co.edu.adopcionmascota.domain.model.AdoptionRequestStatus
import co.edu.adopcionmascota.domain.model.AdoptionRequestType
import co.edu.adopcionmascota.domain.model.Comment
import co.edu.adopcionmascota.domain.model.ContributionType
import co.edu.adopcionmascota.domain.model.Location
import co.edu.adopcionmascota.domain.model.Notification
import co.edu.adopcionmascota.domain.model.NotificationType
import co.edu.adopcionmascota.domain.model.PetType
import co.edu.adopcionmascota.domain.model.Publication
import co.edu.adopcionmascota.domain.model.PublicationStatus
import co.edu.adopcionmascota.domain.model.User
import co.edu.adopcionmascota.domain.model.UserRole

// Conversión entre las tablas de Room y los modelos del dominio.
// La capa de dominio nunca conoce las entidades de Room.

fun UserEntity.toDomain() = User(
    id = id, name = name, city = city, address = address, email = email, password = password,
    phoneNumber = phoneNumber, profilePictureUrl = profilePictureUrl, role = UserRole.entries.find { it.name == role } ?: if (role == "ADMIN") UserRole.MODERATOR else UserRole.USER,
    bio = bio, isTemporaryHome = isTemporaryHome, lostPetAlerts = lostPetAlerts,
    showPublicPhone = showPublicPhone, points = points, createdAt = createdAt
)

fun User.toEntity() = UserEntity(
    id = id, name = name, city = city, address = address, email = email, password = password,
    phoneNumber = phoneNumber, profilePictureUrl = profilePictureUrl, role = role.name,
    bio = bio, isTemporaryHome = isTemporaryHome, lostPetAlerts = lostPetAlerts,
    showPublicPhone = showPublicPhone, points = points, createdAt = createdAt
)

fun PublicationEntity.toDomain() = Publication(
    id = id, name = name, description = description, location = location,
    status = PublicationStatus.valueOf(status), type = PetType.valueOf(type), image = image,
    createdAt = createdAt, distancia = distancia, likes = likes, age = age, gender = gender,
    species = species, breed = breed, size = size, isVaccinated = isVaccinated,
    isDewormed = isDewormed, isSterilized = isSterilized, hasMicrochip = hasMicrochip,
    coordinates = Location(latitude, longitude), ownerId = ownerId,
    rejectionReason = rejectionReason, reviewedAt = reviewedAt
)

fun Publication.toEntity() = PublicationEntity(
    id = id, name = name, description = description, location = location,
    status = status.name, type = type.name, image = image, createdAt = createdAt,
    distancia = distancia, likes = likes, age = age, gender = gender, species = species,
    breed = breed, size = size, isVaccinated = isVaccinated, isDewormed = isDewormed,
    isSterilized = isSterilized, hasMicrochip = hasMicrochip,
    latitude = coordinates.latitude, longitude = coordinates.longitude, ownerId = ownerId,
    rejectionReason = rejectionReason, reviewedAt = reviewedAt
)

fun CommentEntity.toDomain() = Comment(
    id = id, publicationId = publicationId, authorId = authorId, author = author,
    authorTag = authorTag, text = text, type = ContributionType.valueOf(type),
    createdAt = createdAt, thanks = thanks, thankedByMe = thankedByMe
)

fun Comment.toEntity() = CommentEntity(
    id = id, publicationId = publicationId, authorId = authorId, author = author,
    authorTag = authorTag, text = text, type = type.name, createdAt = createdAt,
    thanks = thanks, thankedByMe = thankedByMe
)

fun NotificationEntity.toDomain() = Notification(
    id = id, userId = userId, type = NotificationType.valueOf(type), category = category,
    title = title, message = message, createdAt = createdAt, publicationId = publicationId,
    referenceId = referenceId, actionLabel = actionLabel, isRead = isRead
)

fun Notification.toEntity() = NotificationEntity(
    id = id, userId = userId, type = type.name, category = category, title = title,
    message = message, createdAt = createdAt, publicationId = publicationId,
    referenceId = referenceId, actionLabel = actionLabel, isRead = isRead
)

fun AdoptionRequestEntity.toDomain() = AdoptionRequest(
    id = id, publicationId = publicationId, publicationName = publicationName, ownerId = ownerId,
    requesterId = requesterId, requesterName = requesterName,
    type = AdoptionRequestType.valueOf(type), message = message,
    status = AdoptionRequestStatus.valueOf(status), createdAt = createdAt,
    housing = housing, experience = experience, availableTime = availableTime, otherPets = otherPets,
    compatibilityScore = compatibilityScore,
    compatibilityNotes = compatibilityNotes.split(",").filter { it.isNotBlank() },
    aiGenerated = aiGenerated
)

fun AdoptionRequest.toEntity() = AdoptionRequestEntity(
    id = id, publicationId = publicationId, publicationName = publicationName, ownerId = ownerId,
    requesterId = requesterId, requesterName = requesterName, type = type.name,
    message = message, status = status.name, createdAt = createdAt,
    housing = housing, experience = experience, availableTime = availableTime, otherPets = otherPets,
    compatibilityScore = compatibilityScore, compatibilityNotes = compatibilityNotes.joinToString(","),
    aiGenerated = aiGenerated
)
