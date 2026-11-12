package co.edu.adopcionmascota.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "notifications", indices = [Index("userId")])
data class NotificationEntity(
    @PrimaryKey val id: String,
    val userId: String,
    val type: String,
    val category: String,
    val title: String,
    val message: String,
    val createdAt: Long,
    val publicationId: String?,
    val referenceId: String?,
    val actionLabel: String?,
    val isRead: Boolean
)
