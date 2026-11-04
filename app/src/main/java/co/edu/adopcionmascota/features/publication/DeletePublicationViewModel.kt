package co.edu.adopcionmascota.features.publication

import co.edu.adopcionmascota.R
import androidx.annotation.StringRes
import androidx.lifecycle.SavedStateHandle
import javax.inject.Inject
import dagger.hilt.android.lifecycle.HiltViewModel
import co.edu.adopcionmascota.core.util.ResourceProvider
import co.edu.adopcionmascota.domain.model.MyPublication
import co.edu.adopcionmascota.domain.model.MyPublicationStatus
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
enum class DeleteReason(@StringRes val title: Int, @StringRes val description: Int) {
    ADOPTED_ELSEWHERE(R.string.reason_adopted, R.string.reason_adopted_detail),
    RETURNED_TO_OWNER(R.string.reason_returned, R.string.reason_returned_detail),
    REDO_PUBLICATION(R.string.reason_redo, R.string.reason_redo_detail),
    OTHER(R.string.reason_other, R.string.reason_other_detail)
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

@HiltViewModel
class DeletePublicationViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val resources: ResourceProvider
) : ViewModel() {

    private val publicationId: String = checkNotNull(savedStateHandle["publicationId"])


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
                infoMessage = resources.getString(R.string.publication_paused_message)
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
            MyPublicationsStore.delete(publicationId, resources.getString(reason.title))
            _uiState.update {
                it.copy(deleteResult = RequestResult.Success(resources.getString(R.string.publication_deleted_vm)))
            }
        }
    }
}
