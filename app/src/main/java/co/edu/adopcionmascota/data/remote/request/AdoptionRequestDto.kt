package co.edu.adopcionmascota.data.remote.request

import co.edu.adopcionmascota.data.local.entity.AdoptionRequestEntity
import co.edu.adopcionmascota.data.remote.bool
import co.edu.adopcionmascota.data.remote.long
import co.edu.adopcionmascota.data.remote.str
import co.edu.adopcionmascota.domain.model.AdoptionRequest
import com.google.firebase.firestore.DocumentSnapshot

// Representación de la colección tal como se guarda en Firestore (guía 19)
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
