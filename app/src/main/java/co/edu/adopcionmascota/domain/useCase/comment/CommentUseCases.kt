package co.edu.adopcionmascota.domain.usecase.comment

import co.edu.adopcionmascota.R
import co.edu.adopcionmascota.core.util.ResourceProvider
import co.edu.adopcionmascota.domain.model.Comment
import co.edu.adopcionmascota.domain.model.ContributionType
import co.edu.adopcionmascota.domain.model.NotificationType
import co.edu.adopcionmascota.domain.model.ReputationPoints
import co.edu.adopcionmascota.domain.repository.CommentRepository
import co.edu.adopcionmascota.domain.repository.DraftRepository
import co.edu.adopcionmascota.domain.repository.PublicationRepository
import co.edu.adopcionmascota.domain.repository.UserRepository
import co.edu.adopcionmascota.domain.usecase.notification.SendNotificationUseCase
import co.edu.adopcionmascota.domain.usecase.user.GetCurrentUserUseCase
import kotlinx.coroutines.flow.Flow
import java.util.UUID
import javax.inject.Inject

class ObserveCommentsUseCase @Inject constructor(
    private val commentRepository: CommentRepository
) {
    operator fun invoke(publicationId: String): Flow<List<Comment>> =
        commentRepository.observeByPublication(publicationId)
}

class ObserveAllCommentsUseCase @Inject constructor(
    private val commentRepository: CommentRepository
) {
    operator fun invoke(): Flow<List<Comment>> = commentRepository.comments
}

// Publica el aporte del usuario de la sesión, le suma puntos y notifica al autor de la publicación
class AddCommentUseCase @Inject constructor(
    private val commentRepository: CommentRepository,
    private val publicationRepository: PublicationRepository,
    private val userRepository: UserRepository,
    private val getCurrentUserUseCase: GetCurrentUserUseCase,
    private val sendNotificationUseCase: SendNotificationUseCase,
    private val resources: ResourceProvider
) {
    suspend operator fun invoke(publicationId: String, text: String, type: ContributionType): Comment {
        val author = getCurrentUserUseCase() ?: throw IllegalStateException("No hay una sesión activa")
        val publication = publicationRepository.findById(publicationId)
            ?: throw IllegalArgumentException("La publicación no existe")
        require(text.isNotBlank()) { "El comentario está vacío" }

        val comment = Comment(
            id = UUID.randomUUID().toString(),
            publicationId = publicationId,
            authorId = author.id,
            author = author.name,
            authorTag = resources.getString(R.string.level_tag, author.level.number),
            text = text.trim(),
            type = type
        )
        commentRepository.save(comment)
        userRepository.addPoints(author.id, ReputationPoints.COMMENT)

        // El autor no se notifica a sí mismo
        if (publication.ownerId != author.id) {
            sendNotificationUseCase(
                userId = publication.ownerId,
                type = NotificationType.NEW_COMMENT,
                category = resources.getString(R.string.notif_comment_category),
                title = author.name,
                message = resources.getString(R.string.notif_comment_message, publication.name, comment.text.take(120)),
                publicationId = publicationId,
                actionLabel = resources.getString(R.string.notif_action_reply)
            )
        }
        return comment
    }
}

class ToggleThanksUseCase @Inject constructor(
    private val commentRepository: CommentRepository
) {
    suspend operator fun invoke(commentId: String) = commentRepository.toggleThanks(commentId)
}

class ObserveCommentDraftUseCase @Inject constructor(
    private val draftRepository: DraftRepository
) {
    operator fun invoke(publicationId: String): Flow<String> = draftRepository.observeDraft(publicationId)
}

class SaveCommentDraftUseCase @Inject constructor(
    private val draftRepository: DraftRepository
) {
    suspend operator fun invoke(publicationId: String, text: String) = draftRepository.saveDraft(publicationId, text)
}

class ClearCommentDraftUseCase @Inject constructor(
    private val draftRepository: DraftRepository
) {
    suspend operator fun invoke(publicationId: String) = draftRepository.clearDraft(publicationId)
}
