package co.edu.adopcionmascota.features.moderator.panel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.edu.adopcionmascota.R
import co.edu.adopcionmascota.core.util.ResourceProvider
import co.edu.adopcionmascota.domain.model.PetType
import co.edu.adopcionmascota.domain.model.Publication
import co.edu.adopcionmascota.domain.model.PublicationStatus
import co.edu.adopcionmascota.domain.model.User
import co.edu.adopcionmascota.domain.usecase.publication.ApprovePublicationUseCase
import co.edu.adopcionmascota.domain.usecase.publication.ObserveReportsUseCase
import co.edu.adopcionmascota.domain.usecase.publication.RejectPublicationUseCase
import co.edu.adopcionmascota.domain.usecase.user.GetUserByIdUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject

// Pestañas: Pendientes, Revisadas (aprobadas) y Rechazadas
enum class ModerationTab { PENDING, REVIEWED, REJECTED }

// Filtros rápidos de la cola
enum class ModerationFilter { ALL, POSSIBLE_SALE, ADOPTION, URGENT }

data class ModerationUiState(
    val isLoading: Boolean = true,
    val all: List<ModerationPublicationUi> = emptyList(),
    val tab: ModerationTab = ModerationTab.PENDING,
    val selectedFilter: ModerationFilter = ModerationFilter.ALL,
    val approvedToday: Int = 0,
    val averageReviewMinutes: Int = 0,
    val publicationToReject: ModerationPublicationUi? = null,   // Hoja de rechazo abierta
    val infoMessage: String? = null
) {
    val pendingCount: Int get() = all.count { it.status == PublicationStatus.PENDING }
    val rejectedCount: Int get() = all.count { it.status == PublicationStatus.REJECTED }
    val approvedCount: Int get() = all.count { it.status == PublicationStatus.VERIFIED }
    val alertsCount: Int get() = all.count { it.status == PublicationStatus.PENDING && it.ethicsAlert }

    val publications: List<ModerationPublicationUi>
        get() = all
            .filter {
                when (tab) {
                    ModerationTab.PENDING -> it.status == PublicationStatus.PENDING
                    ModerationTab.REVIEWED -> it.status == PublicationStatus.VERIFIED
                    ModerationTab.REJECTED -> it.status == PublicationStatus.REJECTED
                }
            }
            .filter {
                when (selectedFilter) {
                    ModerationFilter.ALL -> true
                    ModerationFilter.POSSIBLE_SALE -> it.ethicsAlert
                    ModerationFilter.ADOPTION -> it.type == PetType.ADOPCION
                    ModerationFilter.URGENT -> it.type == PetType.PERDIDO || it.type == PetType.VETERINARIA
                }
            }
}

sealed interface ModerationEvent {
    data class ChangeTab(val tab: ModerationTab) : ModerationEvent
    data class ChangeFilter(val filter: ModerationFilter) : ModerationEvent
    data class ApprovePublication(val publicationId: String) : ModerationEvent
    data class OpenReject(val publication: ModerationPublicationUi) : ModerationEvent
    data class ConfirmReject(val reason: String) : ModerationEvent
    data object DismissReject : ModerationEvent
    data object ClearMessage : ModerationEvent
}

@HiltViewModel
class ModerationDashboardViewModel @Inject constructor(
    observeReportsUseCase: ObserveReportsUseCase,
    private val getUserByIdUseCase: GetUserByIdUseCase,
    private val approvePublicationUseCase: ApprovePublicationUseCase,
    private val rejectPublicationUseCase: RejectPublicationUseCase,
    private val resources: ResourceProvider
) : ViewModel() {

    private val _uiState = MutableStateFlow(ModerationUiState())
    val uiState: StateFlow<ModerationUiState> = _uiState.asStateFlow()

    // Los autores se consultan una vez y se reutilizan
    private val authors = mutableMapOf<String, User?>()

    init {
        // La cola sale de Room: cualquier publicación nueva aparece aquí al instante
        viewModelScope.launch {
            observeReportsUseCase().collect { publications ->
                val startOfDay = startOfToday()
                val reviewed = publications.filter { it.reviewedAt != null }
                _uiState.update { state ->
                    state.copy(
                        isLoading = false,
                        all = publications.map { it.toUi() },
                        approvedToday = publications.count {
                            it.status == PublicationStatus.VERIFIED && (it.reviewedAt ?: 0) >= startOfDay
                        },
                        averageReviewMinutes = if (reviewed.isEmpty()) 0
                        else reviewed.map { ((it.reviewedAt ?: 0) - it.createdAt) / 60_000 }.average().toInt()
                    )
                }
            }
        }
    }

    fun onEvent(event: ModerationEvent) {
        when (event) {
            is ModerationEvent.ChangeTab -> _uiState.update { it.copy(tab = event.tab) }
            is ModerationEvent.ChangeFilter -> _uiState.update { it.copy(selectedFilter = event.filter) }
            is ModerationEvent.ApprovePublication -> approve(event.publicationId)
            is ModerationEvent.OpenReject -> _uiState.update { it.copy(publicationToReject = event.publication) }
            is ModerationEvent.ConfirmReject -> reject(event.reason)
            ModerationEvent.DismissReject -> _uiState.update { it.copy(publicationToReject = null) }
            ModerationEvent.ClearMessage -> _uiState.update { it.copy(infoMessage = null) }
        }
    }

    // Aprobar: pasa al feed y se notifica al autor (ApprovePublicationUseCase)
    private fun approve(publicationId: String) {
        viewModelScope.launch {
            try {
                approvePublicationUseCase(publicationId)
                _uiState.update { it.copy(infoMessage = resources.getString(R.string.moderation_approved_msg)) }
            } catch (e: Exception) {
                _uiState.update { it.copy(infoMessage = e.message) }
            }
        }
    }

    // Rechazar con motivo: se notifica al autor (RejectPublicationUseCase)
    private fun reject(reason: String) {
        val publication = _uiState.value.publicationToReject ?: return
        viewModelScope.launch {
            try {
                rejectPublicationUseCase(publication.id, reason)
                _uiState.update {
                    it.copy(publicationToReject = null, infoMessage = resources.getString(R.string.moderation_rejected_msg))
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(infoMessage = e.message) }
            }
        }
    }

    private suspend fun Publication.toUi(): ModerationPublicationUi {
        val author = authors.getOrPut(ownerId) { getUserByIdUseCase(ownerId) }
        val minutes = ((System.currentTimeMillis() - createdAt) / 60_000).coerceAtLeast(0)
        return ModerationPublicationUi(
            id = id,
            userName = author?.name ?: resources.getString(R.string.unknown_user),
            userLevel = author?.let { resources.getString(R.string.level_tag, it.level.number) + ": " + it.level.displayName }.orEmpty(),
            publicationAge = resources.getString(R.string.created_ago, formatAge(minutes)),
            title = name,
            animalType = breed.ifBlank { species },
            animalAge = age,
            city = location,
            image = image,
            type = type,
            status = status,
            vaccinesComplete = isVaccinated,
            sterilized = isSterilized,
            ethicsAlert = mentionsMoney,
            ethicsMessage = if (mentionsMoney) resources.getString(R.string.possible_infraction) else "",
            rejectionReason = rejectionReason,
            createdAgo = formatAge(minutes)
        )
    }

    private fun formatAge(minutes: Long): String = when {
        minutes < 60 -> "${minutes}m"
        minutes < 60 * 24 -> "${minutes / 60}h"
        else -> "${minutes / (60 * 24)}d"
    }

    private fun startOfToday(): Long = Calendar.getInstance().apply {
        set(Calendar.HOUR_OF_DAY, 0); set(Calendar.MINUTE, 0); set(Calendar.SECOND, 0); set(Calendar.MILLISECOND, 0)
    }.timeInMillis
}
