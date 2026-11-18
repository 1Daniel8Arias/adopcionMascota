package co.edu.adopcionmascota.features.notification

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.edu.adopcionmascota.R
import co.edu.adopcionmascota.core.util.RequestResult
import co.edu.adopcionmascota.core.util.ResourceProvider
import co.edu.adopcionmascota.domain.model.Comment
import co.edu.adopcionmascota.domain.model.ContributionType
import co.edu.adopcionmascota.domain.model.Notification
import co.edu.adopcionmascota.domain.model.Publication
import co.edu.adopcionmascota.domain.usecase.comment.AddCommentUseCase
import co.edu.adopcionmascota.domain.usecase.comment.ObserveCommentsUseCase
import co.edu.adopcionmascota.domain.usecase.notification.GetNotificationByIdUseCase
import co.edu.adopcionmascota.domain.usecase.notification.MarkNotificationAsReadUseCase
import co.edu.adopcionmascota.domain.usecase.notification.UpdateNotificationUseCase
import co.edu.adopcionmascota.domain.usecase.publication.GetReportByIdUseCase
import co.edu.adopcionmascota.domain.usecase.publication.UpdatePublicationUseCase
import co.edu.adopcionmascota.domain.usecase.user.GetCurrentUserUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class NotificationDetailUiState(
    val notification: Notification? = null,
    val publication: Publication? = null,       // Publicación relacionada (Room)
    val comments: List<Comment> = emptyList(),  // Aportes del caso (nuevo comentario)
    val userName: String = "",
    val infoMessage: String? = null,

    // ---------- Publicación rechazada: formulario de corrección ----------
    val lotNumber: String = "",
    val applicationDay: String = "",
    val applicationMonth: String = "",
    val applicationYear: String = "",
    val rabiesLot: String = "",
    val isEthicsConfirmed: Boolean = false,
    val lotNumberError: String? = null,
    val dateError: String? = null,
    val rabiesLotError: String? = null,
    val showGuidelinesDialog: Boolean = false,
    val resubmitResult: RequestResult? = null,
    val isResubmitted: Boolean = false,

    // ---------- Nuevo comentario: respuesta del autor ----------
    val replyText: String = ""
) {
    val isCorrectionFormValid: Boolean
        get() = lotNumber.isNotBlank() && applicationDay.isNotBlank() &&
                applicationMonth.isNotBlank() && applicationYear.isNotBlank() &&
                rabiesLot.isNotBlank() && isEthicsConfirmed &&
                lotNumberError == null && dateError == null && rabiesLotError == null

    val isResubmitting: Boolean
        get() = resubmitResult is RequestResult.Loading
}

@HiltViewModel
class NotificationDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getNotificationByIdUseCase: GetNotificationByIdUseCase,
    private val markNotificationAsReadUseCase: MarkNotificationAsReadUseCase,
    private val updateNotificationUseCase: UpdateNotificationUseCase,
    private val getReportByIdUseCase: GetReportByIdUseCase,
    private val updatePublicationUseCase: UpdatePublicationUseCase,
    private val observeCommentsUseCase: ObserveCommentsUseCase,
    private val addCommentUseCase: AddCommentUseCase,
    private val getCurrentUserUseCase: GetCurrentUserUseCase,
    private val resources: ResourceProvider
) : ViewModel() {

    private val notificationId: String = checkNotNull(savedStateHandle["notificationId"])

    private val _uiState = MutableStateFlow(NotificationDetailUiState())
    val uiState: StateFlow<NotificationDetailUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            markNotificationAsReadUseCase(notificationId)
            val notification = getNotificationByIdUseCase(notificationId)
            val publication = notification?.publicationId?.let { getReportByIdUseCase(it) }
            _uiState.update {
                it.copy(
                    notification = notification,
                    publication = publication,
                    userName = getCurrentUserUseCase()?.name.orEmpty()
                )
            }
            publication?.let { p ->
                observeCommentsUseCase(p.id).collect { comments ->
                    _uiState.update { it.copy(comments = comments) }
                }
            }
        }
    }

    fun showInfo(message: String) {
        _uiState.update { it.copy(infoMessage = message) }
    }

    fun resetInfoMessage() {
        _uiState.update { it.copy(infoMessage = null) }
    }

    // ---------- Publicación rechazada ----------

    fun onLotNumberChange(value: String) {
        _uiState.update {
            it.copy(lotNumber = value.uppercase(), lotNumberError = validateLot(value))
        }
    }

    fun onRabiesLotChange(value: String) {
        _uiState.update {
            it.copy(rabiesLot = value.uppercase(), rabiesLotError = validateLot(value))
        }
    }

    // Día, mes y año solo aceptan dígitos y se validan juntos
    fun onDayChange(value: String) {
        if (value.length > 2 || value.any { !it.isDigit() }) return
        _uiState.update { it.copy(applicationDay = value) }
        validateDate()
    }

    fun onMonthChange(value: String) {
        if (value.length > 2 || value.any { !it.isDigit() }) return
        _uiState.update { it.copy(applicationMonth = value) }
        validateDate()
    }

    fun onYearChange(value: String) {
        if (value.length > 4 || value.any { !it.isDigit() }) return
        _uiState.update { it.copy(applicationYear = value) }
        validateDate()
    }

    fun onEthicsConfirmedChange(checked: Boolean) {
        _uiState.update { it.copy(isEthicsConfirmed = checked) }
    }

    // La cámara todavía no hace parte del alcance del proyecto
    fun onExamineClick() {
        showInfo(resources.getString(R.string.upload_card_unavailable))
    }

    fun onGuidelinesClick() {
        _uiState.update { it.copy(showGuidelinesDialog = true) }
    }

    fun onDismissGuidelines() {
        _uiState.update { it.copy(showGuidelinesDialog = false) }
    }

    // Reenviar a revisión: la publicación vuelve a la cola del moderador (PENDING)
    fun resubmitForReview() {
        if (!_uiState.value.isCorrectionFormValid || _uiState.value.isResubmitting) return

        viewModelScope.launch {
            _uiState.update { it.copy(resubmitResult = RequestResult.Loading) }

            _uiState.value.publication?.let { publication ->
                updatePublicationUseCase(publication)
                _uiState.update { it.copy(publication = getReportByIdUseCase(publication.id)) }
            }

            _uiState.value.notification?.let { notification ->
                val updated = notification.copy(
                    title = resources.getString(R.string.correction_sent),
                    message = resources.getString(R.string.committee_review),
                    actionLabel = resources.getString(R.string.in_review)
                )
                updateNotificationUseCase(updated)
                _uiState.update { it.copy(notification = updated) }
            }

            _uiState.update {
                it.copy(
                    resubmitResult = RequestResult.Success(resources.getString(R.string.resubmit_success)),
                    isResubmitted = true
                )
            }
        }
    }

    fun resetResubmitResult() {
        _uiState.update { it.copy(resubmitResult = null) }
    }

    // ---------- Nuevo comentario ----------

    fun onReplyChange(value: String) {
        if (value.length <= 300) _uiState.update { it.copy(replyText = value) }
    }

    // La respuesta se publica como un aporte más del caso
    fun sendReply() {
        val text = _uiState.value.replyText.trim()
        val publicationId = _uiState.value.publication?.id
        if (text.isEmpty() || publicationId == null) {
            showInfo(resources.getString(R.string.write_reply_first))
            return
        }
        viewModelScope.launch {
            try {
                addCommentUseCase(publicationId, text, ContributionType.SUPPORT)
                _uiState.update { it.copy(replyText = "", infoMessage = resources.getString(R.string.reply_published)) }
            } catch (e: Exception) {
                showInfo(e.message.orEmpty())
            }
        }
    }

    // ---------- Validaciones ----------

    private fun validateLot(value: String): String? = when {
        value.isBlank() -> resources.getString(R.string.field_required)
        value.trim().length < 4 -> resources.getString(R.string.batch_incomplete)
        else -> null
    }

    private fun validateDate() {
        val state = _uiState.value
        val day = state.applicationDay.toIntOrNull()
        val month = state.applicationMonth.toIntOrNull()
        val year = state.applicationYear.toIntOrNull()
        val error = when {
            day == null || month == null || year == null -> null // Aún incompleta
            day !in 1..31 -> resources.getString(R.string.invalid_day)
            month !in 1..12 -> resources.getString(R.string.invalid_month)
            year !in 2015..2026 -> resources.getString(R.string.invalid_year)
            else -> null
        }
        _uiState.update { it.copy(dateError = error) }
    }
}
