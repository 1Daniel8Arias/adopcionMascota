package co.edu.adopcionmascota.features.publication

import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.edu.adopcionmascota.R
import co.edu.adopcionmascota.core.util.ResourceProvider
import co.edu.adopcionmascota.domain.model.AdoptionRequestStatus
import co.edu.adopcionmascota.domain.model.Publication
import co.edu.adopcionmascota.domain.model.PublicationStatus
import co.edu.adopcionmascota.domain.usecase.publication.ChangePublicationStatusUseCase
import co.edu.adopcionmascota.domain.usecase.publication.ObserveMyPublicationsUseCase
import co.edu.adopcionmascota.domain.usecase.request.ObserveReceivedRequestsUseCase
import co.edu.adopcionmascota.domain.usecase.comment.ObserveAllCommentsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

// Pestañas de "Mis Publicaciones"
enum class MyPublicationsTab(@StringRes val label: Int) {
    ALL(R.string.all),
    ACTIVE(R.string.tab_active),
    IN_REVIEW(R.string.tab_review),
    RESOLVED(R.string.tab_resolved),
    OBSERVATION(R.string.tab_observations)
}

data class MyPublicationsUiState(
    val publications: List<Publication> = emptyList(),
    val commentCounts: Map<String, Int> = emptyMap(),
    val requestCounts: Map<String, Int> = emptyMap(),
    val pendingRequests: Int = 0,
    val tab: MyPublicationsTab = MyPublicationsTab.ALL,
    val searchText: String = "",
    val hiddenNotices: Set<String> = emptySet(),          // Avisos de moderación descartados
    val publicationToCancel: Publication? = null,         // Diálogo "Cancelar Solicitud"
    val showGuideDialog: Boolean = false,
    val infoMessage: String? = null
) {
    fun countFor(tab: MyPublicationsTab): Int = publications.count { it.matches(tab) }

    val activeCount: Int
        get() = publications.count { it.status != PublicationStatus.RESOLVED }

    val resolvedCount: Int
        get() = publications.count { it.status == PublicationStatus.RESOLVED }

    val visiblePublications: List<Publication>
        get() = publications
            .filter { it.matches(tab) }
            .filter {
                searchText.isBlank() ||
                        it.name.contains(searchText, ignoreCase = true) ||
                        it.breed.contains(searchText, ignoreCase = true) ||
                        it.location.contains(searchText, ignoreCase = true)
            }

    // Se usa en la tarjeta "Red de Apoyo Local": reacciones + aportes recibidos
    val totalReach: Int
        get() = publications.sumOf { it.likes } + commentCounts.values.sum()
}

private fun Publication.matches(tab: MyPublicationsTab): Boolean = when (tab) {
    MyPublicationsTab.ALL -> true
    MyPublicationsTab.ACTIVE -> status == PublicationStatus.VERIFIED || status == PublicationStatus.PAUSED
    MyPublicationsTab.IN_REVIEW -> status == PublicationStatus.PENDING
    MyPublicationsTab.RESOLVED -> status == PublicationStatus.RESOLVED
    MyPublicationsTab.OBSERVATION -> status == PublicationStatus.REJECTED
}

@HiltViewModel
class MyPublicationsViewModel @Inject constructor(
    observeMyPublicationsUseCase: ObserveMyPublicationsUseCase,
    observeReceivedRequestsUseCase: ObserveReceivedRequestsUseCase,
    observeAllCommentsUseCase: ObserveAllCommentsUseCase,
    private val changePublicationStatusUseCase: ChangePublicationStatusUseCase,
    private val resources: ResourceProvider
) : ViewModel() {

    private val _uiState = MutableStateFlow(MyPublicationsUiState())
    val uiState: StateFlow<MyPublicationsUiState> = _uiState.asStateFlow()

    init {
        // Crear, editar, moderar o eliminar en otras pantallas actualiza esta lista (Room)
        viewModelScope.launch {
            combine(
                observeMyPublicationsUseCase(),
                observeReceivedRequestsUseCase(),
                observeAllCommentsUseCase()
            ) { publications, requests, comments ->
                Triple(publications, requests, comments)
            }.collect { (publications, requests, comments) ->
                _uiState.update {
                    it.copy(
                        publications = publications,
                        commentCounts = comments.groupingBy { c -> c.publicationId }.eachCount(),
                        requestCounts = requests.groupingBy { r -> r.publicationId }.eachCount(),
                        pendingRequests = requests.count { r -> r.status == AdoptionRequestStatus.PENDING }
                    )
                }
            }
        }
    }

    fun onTabChange(tab: MyPublicationsTab) {
        _uiState.update { it.copy(tab = tab) }
    }

    fun onSearchChange(text: String) {
        _uiState.update { it.copy(searchText = text) }
    }

    fun onMarkResolved(id: String) {
        viewModelScope.launch {
            changePublicationStatusUseCase(id, PublicationStatus.RESOLVED)
            _uiState.update { it.copy(infoMessage = resources.getString(R.string.case_resolved)) }
        }
    }

    // ---------- Cancelar solicitud en revisión ----------

    fun onCancelRequestClick(publication: Publication) {
        _uiState.update { it.copy(publicationToCancel = publication) }
    }

    fun onConfirmCancelRequest() {
        val publication = _uiState.value.publicationToCancel ?: return
        viewModelScope.launch {
            changePublicationStatusUseCase(publication.id, PublicationStatus.DELETED)
            _uiState.update {
                it.copy(
                    publicationToCancel = null,
                    infoMessage = resources.getString(R.string.request_cancelled, publication.name)
                )
            }
        }
    }

    fun onDismissCancelRequest() {
        _uiState.update { it.copy(publicationToCancel = null) }
    }

    // ---------- Avisos y ayuda ----------

    fun onDiscardNotice(id: String) {
        _uiState.update { it.copy(hiddenNotices = it.hiddenNotices + id) }
    }

    fun onGuideClick() {
        _uiState.update { it.copy(showGuideDialog = true) }
    }

    fun onDismissGuide() {
        _uiState.update { it.copy(showGuideDialog = false) }
    }

    fun resetInfoMessage() {
        _uiState.update { it.copy(infoMessage = null) }
    }
}
