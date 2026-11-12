package co.edu.adopcionmascota.data.remote.publication

import co.edu.adopcionmascota.data.local.entity.PublicationEntity
import co.edu.adopcionmascota.data.remote.bool
import co.edu.adopcionmascota.data.remote.double
import co.edu.adopcionmascota.data.remote.int
import co.edu.adopcionmascota.data.remote.long
import co.edu.adopcionmascota.data.remote.str
import co.edu.adopcionmascota.domain.model.Publication
import com.google.firebase.firestore.DocumentSnapshot

// Representación de la colección tal como se guarda en Firestore (guía 19)
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
