package co.edu.adopcionmascota.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "publications", indices = [Index("ownerId"), Index("status")])
data class PublicationEntity(
    @PrimaryKey val id: String,
    val name: String,
    val description: String,
    val location: String,
    val status: String,
    val type: String,
    val image: String,
    val createdAt: Long,
    val distancia: String,
    val likes: Int,
    val age: String,
    val gender: String,
    val species: String,
    val breed: String,
    val size: String,
    val isVaccinated: Boolean,
    val isDewormed: Boolean,
    val isSterilized: Boolean,
    val hasMicrochip: Boolean,
    val latitude: Double,
    val longitude: Double,
    val ownerId: String,
    val rejectionReason: String?,
    val reviewedAt: Long?
)
