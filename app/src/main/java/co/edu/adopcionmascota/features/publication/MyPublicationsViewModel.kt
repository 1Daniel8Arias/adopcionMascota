package co.edu.adopcionmascota.features.publication

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// Pestañas de "Mis Publicaciones"
enum class MyPublicationsTab(val label: String) {
    ALL("Todas"),
    ACTIVE("Activas"),
    IN_REVIEW("En Revisión"),
    RESOLVED("Resueltas"),
    OBSERVATION("Observaciones")
}

data class MyPublicationsUiState(
    val publications: List<MyPublication> = emptyList(),
    val tab: MyPublicationsTab = MyPublicationsTab.ALL,
    val searchText: String = "",
    val hiddenNotices: Set<String> = emptySet(),          // Avisos de moderación descartados
    val publicationToCancel: MyPublication? = null,       // Diálogo "Cancelar Solicitud"
    val showGuideDialog: Boolean = false,
    val infoMessage: String? = null
) {
    fun countFor(tab: MyPublicationsTab): Int = publications.count { it.matches(tab) }

    val activeCount: Int
        get() = publications.count {
            it.status == MyPublicationStatus.ACTIVE || it.status == MyPublicationStatus.IN_REVIEW ||
                    it.status == MyPublicationStatus.OBSERVATION || it.status == MyPublicationStatus.PAUSED
        }

    val resolvedCount: Int
        get() = publications.count { it.status == MyPublicationStatus.RESOLVED }

    val visiblePublications: List<MyPublication>
        get() = publications
            .filter { it.matches(tab) }
            .filter {
                searchText.isBlank() ||
                        it.name.contains(searchText, ignoreCase = true) ||
                        it.breed.contains(searchText, ignoreCase = true) ||
                        it.location.contains(searchText, ignoreCase = true)
            }

    // Se usa en la tarjeta "Red de Apoyo Local"
    val totalReach: Int
        get() = publications.sumOf { it.followers + it.shares * 2 }.coerceAtLeast(publications.size)
}

private fun MyPublication.matches(tab: MyPublicationsTab): Boolean = when (tab) {
    MyPublicationsTab.ALL -> true
    MyPublicationsTab.ACTIVE -> status == MyPublicationStatus.ACTIVE || status == MyPublicationStatus.PAUSED
    MyPublicationsTab.IN_REVIEW -> status == MyPublicationStatus.IN_REVIEW
    MyPublicationsTab.RESOLVED -> status == MyPublicationStatus.RESOLVED
    MyPublicationsTab.OBSERVATION -> status == MyPublicationStatus.OBSERVATION
}

class MyPublicationsViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(MyPublicationsUiState())
    val uiState: StateFlow<MyPublicationsUiState> = _uiState.asStateFlow()

    init {
        // Crear, editar o eliminar en otras pantallas actualiza esta lista
        viewModelScope.launch {
            MyPublicationsStore.publications.collect { list ->
                _uiState.update { it.copy(publications = list) }
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
        MyPublicationsStore.changeStatus(id, MyPublicationStatus.RESOLVED)
        _uiState.update { it.copy(infoMessage = "¡Caso marcado como resuelto! Se notificó a sus seguidores") }
    }

    // ---------- Cancelar solicitud en revisión ----------

    fun onCancelRequestClick(publication: MyPublication) {
        _uiState.update { it.copy(publicationToCancel = publication) }
    }

    fun onConfirmCancelRequest() {
        val publication = _uiState.value.publicationToCancel ?: return
        MyPublicationsStore.delete(publication.id, "Solicitud cancelada por el usuario")
        _uiState.update {
            it.copy(
                publicationToCancel = null,
                infoMessage = "Se canceló la solicitud de ${publication.name}"
            )
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
