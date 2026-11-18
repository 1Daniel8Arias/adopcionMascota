package co.edu.adopcionmascota.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "adoption_requests", indices = [Index("ownerId"), Index("publicationId")])
data class AdoptionRequestEntity(
    @PrimaryKey val id: String,
    val publicationId: String,
    val publicationName: String,
    val ownerId: String,
    val requesterId: String,
    val requesterName: String,
    val type: String,
    val message: String,
    val status: String,
    val createdAt: Long,
    val housing: String = "",
    val experience: String = "",
    val availableTime: String = "",
    val otherPets: String = "",
    val compatibilityScore: Int? = null,
    val compatibilityNotes: String = "",   // Códigos separados por comas
    val aiGenerated: Boolean = false
)
