package co.edu.adopcionmascota.features.publication

import androidx.annotation.StringRes
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.edu.adopcionmascota.R
import co.edu.adopcionmascota.core.util.RequestResult
import co.edu.adopcionmascota.core.util.ResourceProvider
import co.edu.adopcionmascota.domain.model.Publication
import co.edu.adopcionmascota.domain.model.PublicationStatus
import co.edu.adopcionmascota.domain.usecase.publication.ChangePublicationStatusUseCase
import co.edu.adopcionmascota.domain.usecase.publication.GetReportByIdUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

// Motivos de la baja del caso
enum class DeleteReason(@StringRes val title: Int, @StringRes val description: Int) {
    ADOPTED_ELSEWHERE(R.string.reason_adopted, R.string.reason_adopted_detail),
    RETURNED_TO_OWNER(R.string.reason_returned, R.string.reason_returned_detail),
    REDO_PUBLICATION(R.string.reason_redo, R.string.reason_redo_detail),
    OTHER(R.string.reason_other, R.string.reason_other_detail)
}

data class DeletePublicationUiState(
    val publication: Publication? = null,
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
    private val getReportByIdUseCase: GetReportByIdUseCase,
    private val changePublicationStatusUseCase: ChangePublicationStatusUseCase,
    private val resources: ResourceProvider
) : ViewModel() {

    private val publicationId: String = checkNotNull(savedStateHandle["publicationId"])

    private val _uiState = MutableStateFlow(DeletePublicationUiState())
    val uiState: StateFlow<DeletePublicationUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            _uiState.update { it.copy(publication = getReportByIdUseCase(publicationId)) }
        }
    }

    fun onReasonSelected(reason: DeleteReason) = _uiState.update { it.copy(reason = reason) }

    fun onConfirmedChange(checked: Boolean) = _uiState.update { it.copy(isConfirmed = checked) }

    // Opciones recomendadas antes de borrar
    fun onMarkAdopted() {
        viewModelScope.launch {
            changePublicationStatusUseCase(publicationId, PublicationStatus.RESOLVED)
            _uiState.update { it.copy(leaveScreen = true) }
        }
    }

    fun onPause() {
        viewModelScope.launch {
            changePublicationStatusUseCase(publicationId, PublicationStatus.PAUSED)
            _uiState.update {
                it.copy(
                    publication = getReportByIdUseCase(publicationId),
                    infoMessage = resources.getString(R.string.publication_paused_message)
                )
            }
        }
    }

    fun resetInfoMessage() = _uiState.update { it.copy(infoMessage = null) }

    // Borrado lógico: la publicación queda con estado DELETED y sale del feed y de la lista
    fun deletePublication() {
        val state = _uiState.value
        if (!state.canDelete) return

        viewModelScope.launch {
            _uiState.update { it.copy(deleteResult = RequestResult.Loading) }
            changePublicationStatusUseCase(publicationId, PublicationStatus.DELETED)
            _uiState.update {
                it.copy(deleteResult = RequestResult.Success(resources.getString(R.string.publication_deleted_vm)))
            }
        }
    }
}
