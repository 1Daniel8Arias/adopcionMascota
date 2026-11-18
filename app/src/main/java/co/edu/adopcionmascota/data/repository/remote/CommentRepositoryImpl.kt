package co.edu.adopcionmascota.data.repository.remote

import co.edu.adopcionmascota.data.local.dao.CommentDao
import co.edu.adopcionmascota.data.local.mapper.toDomain
import co.edu.adopcionmascota.data.remote.Collections
import co.edu.adopcionmascota.data.remote.comment.CommentDto
import co.edu.adopcionmascota.domain.model.Comment
import co.edu.adopcionmascota.domain.repository.CommentRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CommentRepositoryImpl @Inject constructor(
    firestore: FirebaseFirestore,
    private val auth: FirebaseAuth,
    private val commentDao: CommentDao
) : CommentRepository {

    private val collection = firestore.collection(Collections.COMMENTS)

    override val comments: Flow<List<Comment>> =
        commentDao.observeAll().map { list -> list.map { it.toDomain() } }

    override fun observeByPublication(publicationId: String): Flow<List<Comment>> =
        commentDao.observeByPublication(publicationId).map { list -> list.map { it.toDomain() } }

    override suspend fun save(comment: Comment) {
        val uid = auth.currentUser?.uid
        val dto = CommentDto.fromDomain(comment, uid)
        collection.document(dto.id).set(dto.toMap()).await()
        commentDao.insert(dto.toEntity(uid))
    }

    // Agradecer: el uid entra o sale de thankedBy y el contador cambia en la misma escritura
    override suspend fun toggleThanks(commentId: String) {
        val uid = auth.currentUser?.uid ?: return
        val current = commentDao.observeAll().first().firstOrNull { it.id == commentId } ?: return
        val update: Map<String, Any> = if (current.thankedByMe) {
            mapOf("thankedBy" to FieldValue.arrayRemove(uid), "thanks" to FieldValue.increment(-1))
        } else {
            mapOf("thankedBy" to FieldValue.arrayUnion(uid), "thanks" to FieldValue.increment(1))
        }
        collection.document(commentId).update(update).await()
        commentDao.toggleThanks(commentId)
    }
}
