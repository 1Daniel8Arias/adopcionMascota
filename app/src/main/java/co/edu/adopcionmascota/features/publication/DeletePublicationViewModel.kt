package co.edu.adopcionmascota.features.publication

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.edu.adopcionmascota.core.util.RequestResult
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// Motivos de la baja del caso
enum class DeleteReason(val title: String, val description: String) {
    ADOPTED_ELSEWHERE("Ya fue adoptado felizmente por otro medio", "Adoptado fuera de la plataforma o con conocidos"),
    RETURNED_TO_OWNER("El animalito regresó con su tutor original", "Se trataba de un extravío y ya fue reconocido"),
    REDO_PUBLICATION("Necesito rehacer la publicación", "Para actualizar fotos principales, edad o historia"),
    OTHER("Otro motivo logístico o personal", "Cambio de refugio o gestión administrativa")
}

data class DeletePublicationUiState(
    val publication: MyPublication? = null,
    val reason: DeleteReason? = null,
    val isConfirmed: Boolean = false,
    val deleteResult: RequestResult? = null,
    val infoMessage: String? = null,
    val leaveScreen: Boolean = false        // Tras "Marcar como Adoptado" se regresa a la lista
) {
    val canDelete: Boolean
        get() = reason != null && isConfirmed && deleteResult !is RequestResult.Loading
}

class DeletePublicationViewModel(
    private val publicationId: String
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        DeletePublicationUiState(publication = MyPublicationsStore.findById(publicationId))
    )
    val uiState: StateFlow<DeletePublicationUiState> = _uiState.asStateFlow()

    fun onReasonSelected(reason: DeleteReason) = _uiState.update { it.copy(reason = reason) }

    fun onConfirmedChange(checked: Boolean) = _uiState.update { it.copy(isConfirmed = checked) }

    // Opciones recomendadas antes de borrar
    fun onMarkAdopted() {
        MyPublicationsStore.changeStatus(publicationId, MyPublicationStatus.RESOLVED)
        _uiState.update { it.copy(leaveScreen = true) }
    }

    fun onPause() {
        MyPublicationsStore.changeStatus(publicationId, MyPublicationStatus.PAUSED)
        _uiState.update {
            it.copy(
                publication = MyPublicationsStore.findById(publicationId),
                infoMessage = "Publicación pausada: ya no se muestra en el mapa"
            )
        }
    }

    fun resetInfoMessage() = _uiState.update { it.copy(infoMessage = null) }

    fun deletePublication() {
        val state = _uiState.value
        val reason = state.reason ?: return
        if (!state.canDelete) return

        viewModelScope.launch {
            _uiState.update { it.copy(deleteResult = RequestResult.Loading) }
            delay(1500) // Simula la petición al servidor
            MyPublicationsStore.delete(publicationId, reason.title)
            _uiState.update {
                it.copy(deleteResult = RequestResult.Success("¡Publicación Eliminada con Éxito!"))
            }
        }
    }
}
