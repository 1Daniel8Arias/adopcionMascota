package co.edu.adopcionmascota.features.comment

import co.edu.adopcionmascota.domain.model.Comment
import co.edu.adopcionmascota.domain.model.ContributionType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

// Por ahora los comentarios viven en memoria; más adelante vendrán del repositorio / Firebase
object CommentStore {

    private val _comments = MutableStateFlow(initialComments())
    val comments: StateFlow<List<Comment>> = _comments.asStateFlow()

    // Borradores por publicación ("Borrador autoguardado")
    private val drafts = mutableMapOf<String, String>()

    fun commentsFor(publicationId: String): List<Comment> =
        _comments.value.filter { it.publicationId == publicationId }

    fun add(comment: Comment) {
        // Los comentarios nuevos van primero
        _comments.update { listOf(comment) + it }
    }

    // "Agradecer": suma o quita el agradecimiento del usuario actual
    fun toggleThanks(commentId: String) {
        _comments.update { list ->
            list.map { comment ->
                if (comment.id != commentId) comment
                else comment.copy(
                    thankedByMe = !comment.thankedByMe,
                    thanks = comment.thanks + if (comment.thankedByMe) -1 else 1
                )
            }
        }
    }

    fun draftFor(publicationId: String): String = drafts[publicationId].orEmpty()

    fun saveDraft(publicationId: String, text: String) {
        if (text.isBlank()) drafts.remove(publicationId) else drafts[publicationId] = text
    }

    fun clearDraft(publicationId: String) {
        drafts.remove(publicationId)
    }

    // Datos quemados con los comentarios que aparecen en el Figma
    private fun initialComments(): List<Comment> = listOf(
        Comment(
            id = "c1",
            publicationId = "1",
            author = "Laura Méndez",
            authorTag = "Hogar Temporal",
            time = "Ayer",
            text = "¡Vi a Max ayer jugando muy feliz en el parque! Es súper noble y dócil con otros perritos más pequeños.",
            type = ContributionType.SIGHTING,
            thanks = 8
        ),
        Comment(
            id = "c2",
            publicationId = "1",
            author = "Dr. Andrés Vega",
            authorTag = "Vet Verificado",
            time = "Hace 5h",
            text = "Puedo aportar un bulto de 15kg de alimento Hills y la primera consulta gratuita para la familia que lo adopte formalmente.",
            type = ContributionType.VET_FOOD,
            thanks = 12
        )
    )
}
