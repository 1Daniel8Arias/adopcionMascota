package co.edu.adopcionmascota.features.notification

import co.edu.adopcionmascota.R
import androidx.lifecycle.SavedStateHandle
import javax.inject.Inject
import dagger.hilt.android.lifecycle.HiltViewModel
import co.edu.adopcionmascota.core.util.ResourceProvider
import co.edu.adopcionmascota.domain.model.Notification
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.edu.adopcionmascota.core.util.RequestResult
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ReplyUi(
    val author: String,
    val time: String,
    val text: String
)

data class NotificationDetailUiState(
    val notification: Notification? = null,
    val infoMessage: String? = null,

    // ---------- Publicación pausada: formulario de corrección ----------
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

    // ---------- Nuevo comentario: respuesta del gestor ----------
    val replyText: String = "",
    val replies: List<ReplyUi> = listOf(
        ReplyUi(
            author = "Camila (Hogar de Paso)",
            time = "Hace 12 min",
            text = "¡Doc, qué bendición! Max terminó ayer sus antibióticos y este alimento le caerá de maravilla."
        )
    ),
    val showAllReplies: Boolean = false,
    val isDonationAccepted: Boolean = false,
    val commentLikes: Int = 14,
    val isCommentLiked: Boolean = false
) {
    val isCorrectionFormValid: Boolean
        get() = lotNumber.isNotBlank() && applicationDay.isNotBlank() &&
                applicationMonth.isNotBlank() && applicationYear.isNotBlank() &&
                rabiesLot.isNotBlank() && isEthicsConfirmed &&
                lotNumberError == null && dateError == null && rabiesLotError == null

    val isResubmitting: Boolean
        get() = resubmitResult is RequestResult.Loading

    val visibleReplies: List<ReplyUi>
        get() = if (showAllReplies) replies else replies.takeLast(1)
}

// Sugerencias rápidas para responder al veterinario
val REPLY_SUGGESTIONS = listOf(
    "¡Muchísimas gracias Dr. Vega! ¿A qué hora podemos pasar por la clínica en Usaquén?",
    "Mañana en la tarde nos queda perfecto.",
    "¿Llevamos a Max para la valoración?"
)

@HiltViewModel
class NotificationDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val resources: ResourceProvider
) : ViewModel() {

    private val notificationId: String = checkNotNull(savedStateHandle["notificationId"])


    private val _uiState = MutableStateFlow(NotificationDetailUiState())
    val uiState: StateFlow<NotificationDetailUiState> = _uiState.asStateFlow()

    init {
        NotificationStore.markAsRead(notificationId)
        _uiState.update { it.copy(notification = NotificationStore.findById(notificationId)) }
    }

    fun showInfo(message: String) {
        _uiState.update { it.copy(infoMessage = message) }
    }

    fun resetInfoMessage() {
        _uiState.update { it.copy(infoMessage = null) }
    }

    // ---------- Publicación pausada ----------

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

    fun resubmitForReview() {
        if (!_uiState.value.isCorrectionFormValid || _uiState.value.isResubmitting) return

        viewModelScope.launch {
            _uiState.update { it.copy(resubmitResult = RequestResult.Loading) }
            delay(1500) // Simula el envío al comité ético

            // La notificación queda marcada como "en revisión" también en la lista
            _uiState.value.notification?.let { notification ->
                val updated = notification.copy(
                    category = "REVISIÓN ÉTICA",
                    title = "Publicación en revisión prioritaria",
                    message = "Enviaste los datos corregidos del carnet. El comité ético responderá en unos 45 minutos.",
                    time = resources.getString(R.string.now),
                    actionLabel = "Ver estado"
                )
                NotificationStore.update(updated)
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

    fun onSuggestionClick(suggestion: String) {
        _uiState.update { it.copy(replyText = suggestion) }
    }

    fun sendReply() {
        val text = _uiState.value.replyText.trim()
        if (text.isEmpty()) {
            showInfo(resources.getString(R.string.write_reply_first))
            return
        }
        _uiState.update {
            it.copy(
                replies = it.replies + ReplyUi(author = "Tú (Gestora de Max)", time = resources.getString(R.string.now), text = text),
                replyText = "",
                showAllReplies = true,
                infoMessage = resources.getString(R.string.reply_published)
            )
        }
    }

    fun onToggleAllReplies() {
        _uiState.update { it.copy(showAllReplies = !it.showAllReplies) }
    }

    fun onAcceptDonation() {
        if (_uiState.value.isDonationAccepted) return
        _uiState.update {
            it.copy(
                isDonationAccepted = true,
                infoMessage = resources.getString(R.string.donation_accepted_message)
            )
        }
    }

    fun onToggleLike() {
        _uiState.update {
            it.copy(
                isCommentLiked = !it.isCommentLiked,
                commentLikes = it.commentLikes + if (it.isCommentLiked) -1 else 1
            )
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
