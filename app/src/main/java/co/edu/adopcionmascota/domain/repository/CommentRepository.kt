package co.edu.adopcionmascota.domain.repository

import co.edu.adopcionmascota.domain.model.Comment
import kotlinx.coroutines.flow.Flow

interface CommentRepository {

    val comments: Flow<List<Comment>>

    fun observeByPublication(publicationId: String): Flow<List<Comment>>

    suspend fun save(comment: Comment)

    // "Agradecer": suma o quita el agradecimiento del usuario actual
    suspend fun toggleThanks(commentId: String)
}
