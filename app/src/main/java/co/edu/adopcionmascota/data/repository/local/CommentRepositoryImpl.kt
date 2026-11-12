package co.edu.adopcionmascota.data.repository.local

import co.edu.adopcionmascota.data.local.dao.CommentDao
import co.edu.adopcionmascota.data.local.mapper.toDomain
import co.edu.adopcionmascota.data.local.mapper.toEntity
import co.edu.adopcionmascota.domain.model.Comment
import co.edu.adopcionmascota.domain.repository.CommentRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CommentRepositoryImpl @Inject constructor(
    private val commentDao: CommentDao
) : CommentRepository {

    override val comments: Flow<List<Comment>> =
        commentDao.observeAll().map { list -> list.map { it.toDomain() } }

    override fun observeByPublication(publicationId: String): Flow<List<Comment>> =
        commentDao.observeByPublication(publicationId).map { list -> list.map { it.toDomain() } }

    override suspend fun save(comment: Comment) {
        commentDao.insert(comment.toEntity())
    }

    override suspend fun toggleThanks(commentId: String) {
        commentDao.toggleThanks(commentId)
    }
}
