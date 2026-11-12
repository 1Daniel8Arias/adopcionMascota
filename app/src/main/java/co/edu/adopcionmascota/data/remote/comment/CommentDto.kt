package co.edu.adopcionmascota.data.remote.comment

import co.edu.adopcionmascota.data.local.entity.CommentEntity
import co.edu.adopcionmascota.data.remote.int
import co.edu.adopcionmascota.data.remote.long
import co.edu.adopcionmascota.data.remote.str
import co.edu.adopcionmascota.domain.model.Comment
import co.edu.adopcionmascota.domain.model.User
import com.google.firebase.firestore.DocumentSnapshot

// Representación de la colección tal como se guarda en Firestore (guía 19)
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
