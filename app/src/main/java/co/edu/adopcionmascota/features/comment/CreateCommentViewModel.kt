package co.edu.adopcionmascota.features.comment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.edu.adopcionmascota.core.util.RequestResult
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// Longitudes permitidas para el aporte
const val COMMENT_MAX_LENGTH = 500
const val COMMENT_MIN_LENGTH = 10

// Puntos de reputación que se otorgan por cada aporte
const val COMMENT_POINTS = 5

data class CreateCommentUiState(
    // Resumen del caso (por ahora quemado, igual que el detalle de la publicación)
    val petName: String = "",
    val petDescription: String = "",
    val petLocation: String = "",

    // Autor (usuario actual)
    val authorName: String = "",
    val authorLevel: String = "",

    // Formulario
    val type: ContributionType = ContributionType.SUPPORT,
    val text: String = "",
    val textError: String? = null,
    val notifyReplies: Boolean = true,
    val isDraftSaved: Boolean = false,

    // Comentarios recientes del caso
    val recentComments: List<Comment> = emptyList(),
    val totalComments: Int = 0,

    // Diálogos y mensajes
    val showDiscardDialog: Boolean = false,
    val infoMessage: String? = null,

    // Resultado de publicar; el comentario publicado se muestra en la hoja de éxito
    val publishResult: RequestResult? = null,
    val publishedComment: Comment? = null
) {
    val textLength: Int
        get() = text.length

    val isFormValid: Boolean
        get() = text.trim().length >= COMMENT_MIN_LENGTH && textError == null

    val isPublishing: Boolean
        get() = publishResult is RequestResult.Loading
}

class CreateCommentViewModel(
    private val publicationId: String
) : ViewModel() {

    private val _uiState = MutableStateFlow(CreateCommentUiState())
    val uiState: StateFlow<CreateCommentUiState> = _uiState.asStateFlow()

    init {
        loadCase()
        observeComments()
    }

    private fun loadCase() {
        val draft = CommentStore.draftFor(publicationId)
        _uiState.value = CreateCommentUiState(
            petName = "Max",
            petDescription = "Golden Mestizo · 3 meses",
            petLocation = "Armenia, Quindío · Caso #$publicationId",
            authorName = "Camila Morales",
            authorLevel = "Voluntaria Nivel 2 · Protector",
            text = draft,
            isDraftSaved = draft.isNotBlank()
        )
    }

    // La lista de aportes recientes se actualiza al publicar o agradecer
    private fun observeComments() {
        viewModelScope.launch {
            CommentStore.comments.collect { all ->
                val comments = all.filter { it.publicationId == publicationId }
                _uiState.update {
                    it.copy(
                        recentComments = comments.take(2),
                        totalComments = comments.size
                    )
                }
            }
        }
    }

    // ---------- Formulario ----------

    fun onTypeChange(type: ContributionType) {
        _uiState.update { it.copy(type = type) }
    }

    fun onTextChange(newText: String) {
        // No se aceptan más caracteres que el máximo permitido
        if (newText.length > COMMENT_MAX_LENGTH) return
        CommentStore.saveDraft(publicationId, newText)
        _uiState.update {
            it.copy(
                text = newText,
                // El error solo se actualiza si ya se mostró al intentar publicar
                textError = if (it.textError != null) validateText(newText) else null,
                isDraftSaved = newText.isNotBlank()
            )
        }
    }

    fun onNotifyRepliesChange(enabled: Boolean) {
        _uiState.update { it.copy(notifyReplies = enabled) }
    }

    // "Mencionar voluntario": agrega una @ al final del texto
    fun onMentionClick() {
        val text = _uiState.value.text
        val separator = if (text.isEmpty() || text.endsWith(" ")) "" else " "
        onTextChange("$text$separator@")
    }

    // La cámara y el mapa todavía no hacen parte del alcance del proyecto
    fun onAttachPhotoClick() {
        _uiState.update {
            it.copy(infoMessage = "Adjuntar fotos estará disponible cuando se integre la cámara")
        }
    }

    fun onShareLocationClick() {
        _uiState.update {
            it.copy(infoMessage = "Compartir ubicación estará disponible cuando se integre el mapa")
        }
    }

    fun onHelpClick() {
        _uiState.update {
            it.copy(infoMessage = "Elige el tipo de aporte, escribe tu mensaje y presiona \"Publicar Comentario\"")
        }
    }

    fun resetInfoMessage() {
        _uiState.update { it.copy(infoMessage = null) }
    }

    fun onThanksClick(commentId: String) {
        CommentStore.toggleThanks(commentId)
    }

    // ---------- Publicar ----------

    fun publishComment() {
        val state = _uiState.value
        val error = validateText(state.text)
        if (error != null) {
            _uiState.update { it.copy(textError = error) }
            return
        }
        if (state.isPublishing) return

        viewModelScope.launch {
            _uiState.update { it.copy(publishResult = RequestResult.Loading) }
            delay(1200) // Simula el envío al servidor

            val comment = Comment(
                id = "c${System.currentTimeMillis()}",
                publicationId = publicationId,
                author = state.authorName,
                authorTag = "Nivel 2",
                time = "Ahora",
                text = state.text.trim(),
                type = state.type
            )
            CommentStore.add(comment)
            CommentStore.clearDraft(publicationId)

            _uiState.update {
                it.copy(
                    publishResult = RequestResult.Success("Aporte agregado con éxito"),
                    publishedComment = comment,
                    text = "",
                    isDraftSaved = false
                )
            }
        }
    }

    // ---------- Descartar ----------

    // Si hay texto se pide confirmación; si no, se puede salir directamente
    fun onDiscardClick(): Boolean {
        if (_uiState.value.text.isBlank()) return true
        _uiState.update { it.copy(showDiscardDialog = true) }
        return false
    }

    fun onConfirmDiscard() {
        CommentStore.clearDraft(publicationId)
        _uiState.update { it.copy(showDiscardDialog = false, text = "", isDraftSaved = false) }
    }

    fun onDismissDiscardDialog() {
        _uiState.update { it.copy(showDiscardDialog = false) }
    }

    // ---------- Validaciones ----------

    private fun validateText(text: String): String? {
        val length = text.trim().length
        return when {
            length == 0 -> "Escribe tu aporte antes de publicar"
            length < COMMENT_MIN_LENGTH -> "El aporte debe tener al menos $COMMENT_MIN_LENGTH caracteres"
            else -> null
        }
    }
}
