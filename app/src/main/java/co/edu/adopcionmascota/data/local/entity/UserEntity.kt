package co.edu.adopcionmascota.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "users", indices = [Index(value = ["email"], unique = true)])
data class UserEntity(
    @PrimaryKey val id: String,
    val name: String,
    val city: String,
    val address: String,
    val email: String,
    val password: String,
    val phoneNumber: String,
    val profilePictureUrl: String,
    val role: String,
    val bio: String,
    val isTemporaryHome: Boolean,
    val lostPetAlerts: Boolean,
    val showPublicPhone: Boolean,
    val points: Int,
    val createdAt: Long
)
