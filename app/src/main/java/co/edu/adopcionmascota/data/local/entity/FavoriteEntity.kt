package co.edu.adopcionmascota.data.local.entity

import androidx.room.Entity
import androidx.room.Index

// "Me encanta": relación entre un usuario y una publicación
@Entity(
    tableName = "favorites",
    primaryKeys = ["userId", "publicationId"],
    indices = [Index("userId")]
)
data class FavoriteEntity(
    val userId: String,
    val publicationId: String,
    val createdAt: Long
)
