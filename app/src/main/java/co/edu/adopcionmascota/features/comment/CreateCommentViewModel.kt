package co.edu.adopcionmascota.features.comment

import co.edu.adopcionmascota.R
import androidx.lifecycle.SavedStateHandle
import javax.inject.Inject
import dagger.hilt.android.lifecycle.HiltViewModel
import co.edu.adopcionmascota.core.util.ResourceProvider
import co.edu.adopcionmascota.domain.model.Comment
import co.edu.adopcionmascota.domain.model.ContributionType
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.edu.adopcionmascota.core.util.RequestResult
import co.edu.adopcionmascota.domain.usecase.comment.AddCommentUseCase
import co.edu.adopcionmascota.domain.usecase.comment.ClearCommentDraftUseCase
import co.edu.adopcionmascota.domain.usecase.comment.ObserveCommentDraftUseCase
import co.edu.adopcionmascota.domain.usecase.comment.ObserveCommentsUseCase
import co.edu.adopcionmascota.domain.usecase.comment.SaveCommentDraftUseCase
import co.edu.adopcionmascota.domain.usecase.comment.ToggleThanksUseCase
import co.edu.adopcionmascota.domain.usecase.publication.GetReportByIdUseCase
import co.edu.adopcionmascota.domain.usecase.user.GetCurrentUserUseCase
import kotlinx.coroutines.flow.first
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
    // Resumen del caso (se carga de Room)
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

@HiltViewModel
class CreateCommentViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getReportByIdUseCase: GetReportByIdUseCase,
    private val getCurrentUserUseCase: GetCurrentUserUseCase,
    private val observeCommentsUseCase: ObserveCommentsUseCase,
    private val addCommentUseCase: AddCommentUseCase,
    private val toggleThanksUseCase: ToggleThanksUseCase,
    private val observeCommentDraftUseCase: ObserveCommentDraftUseCase,
    private val saveCommentDraftUseCase: SaveCommentDraftUseCase,
    private val clearCommentDraftUseCase: ClearCommentDraftUseCase,
    private val resources: ResourceProvider
) : ViewModel() {

    private val publicationId: String = checkNotNull(savedStateHandle["publicationId"])


    private val _uiState = MutableStateFlow(CreateCommentUiState())
    val uiState: StateFlow<CreateCommentUiState> = _uiState.asStateFlow()

    init {
        loadCase()
        observeComments()
    }

    // El caso, el autor (usuario de la sesión) y el borrador vienen de Room y Data Store
    private fun loadCase() {
        viewModelScope.launch {
            val publication = getReportByIdUseCase(publicationId)
            val user = getCurrentUserUseCase()
            val draft = observeCommentDraftUseCase(publicationId).first()
            _uiState.update {
                it.copy(
                    petName = publication?.name.orEmpty(),
                    petDescription = listOf(publication?.breed, publication?.age)
                        .filter { part -> !part.isNullOrBlank() }.joinToString(" · "),
                    petLocation = resources.getString(R.string.case_location, publication?.location.orEmpty(), publicationId.take(8)),
                    authorName = user?.name.orEmpty(),
                    authorLevel = user?.let { u -> resources.getString(R.string.level_tag, u.level.number) + " · " + u.level.displayName }.orEmpty(),
                    text = draft,
                    isDraftSaved = draft.isNotBlank()
                )
            }
        }
    }

    // La lista de aportes recientes se actualiza al publicar o agradecer
    private fun observeComments() {
        viewModelScope.launch {
            observeCommentsUseCase(publicationId).collect { comments ->
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
        viewModelScope.launch { saveCommentDraftUseCase(publicationId, newText) }
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
            it.copy(infoMessage = resources.getString(R.string.attach_unavailable))
        }
    }

    fun onShareLocationClick() {
        _uiState.update {
            it.copy(infoMessage = resources.getString(R.string.share_location_unavailable))
        }
    }

    fun onHelpClick() {
        _uiState.update {
            it.copy(infoMessage = resources.getString(R.string.comment_help))
        }
    }

    fun resetInfoMessage() {
        _uiState.update { it.copy(infoMessage = null) }
    }

    fun onThanksClick(commentId: String) {
        viewModelScope.launch { toggleThanksUseCase(commentId) }
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
            // El caso de uso guarda el comentario, suma puntos y notifica al autor de la publicación
            val comment = try {
                addCommentUseCase(publicationId, state.text, state.type)
            } catch (e: Exception) {
                _uiState.update { it.copy(publishResult = RequestResult.Failure(e.message.orEmpty())) }
                return@launch
            }
            clearCommentDraftUseCase(publicationId)

            _uiState.update {
                it.copy(
                    publishResult = RequestResult.Success(resources.getString(R.string.contribution_success)),
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
        viewModelScope.launch { clearCommentDraftUseCase(publicationId) }
        _uiState.update { it.copy(showDiscardDialog = false, text = "", isDraftSaved = false) }
    }

    fun onDismissDiscardDialog() {
        _uiState.update { it.copy(showDiscardDialog = false) }
    }

    // ---------- Validaciones ----------

    private fun validateText(text: String): String? {
        val length = text.trim().length
        return when {
            length == 0 -> resources.getString(R.string.comment_empty)
            length < COMMENT_MIN_LENGTH -> resources.getString(R.string.comment_short, COMMENT_MIN_LENGTH)
            else -> null
        }
    }
}
