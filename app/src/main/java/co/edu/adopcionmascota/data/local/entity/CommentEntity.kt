package co.edu.adopcionmascota.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "comments", indices = [Index("publicationId")])
data class CommentEntity(
    @PrimaryKey val id: String,
    val publicationId: String,
    val authorId: String,
    val author: String,
    val authorTag: String,
    val text: String,
    val type: String,
    val createdAt: Long,
    val thanks: Int,
    val thankedByMe: Boolean
)
