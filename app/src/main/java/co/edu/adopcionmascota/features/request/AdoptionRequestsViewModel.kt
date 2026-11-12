package co.edu.adopcionmascota.features.request

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.edu.adopcionmascota.R
import co.edu.adopcionmascota.core.util.ResourceProvider
import co.edu.adopcionmascota.domain.model.AdoptionRequest
import co.edu.adopcionmascota.domain.model.AdoptionRequestStatus
import co.edu.adopcionmascota.domain.usecase.request.ObserveReceivedRequestsUseCase
import co.edu.adopcionmascota.domain.usecase.request.RespondAdoptionRequestUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AdoptionRequestsUiState(
    val requests: List<AdoptionRequest> = emptyList(),
    val showAnswered: Boolean = false,
    val isLoading: Boolean = true,
    val infoMessage: String? = null
) {
    val pending: List<AdoptionRequest>
        get() = requests.filter { it.status == AdoptionRequestStatus.PENDING }

    val visibleRequests: List<AdoptionRequest>
        get() = if (showAnswered) requests.filter { it.status != AdoptionRequestStatus.PENDING } else pending
}

// Solicitudes de adopción y hogar de paso que recibe el autor de las publicaciones
@HiltViewModel
class AdoptionRequestsViewModel @Inject constructor(
    observeReceivedRequestsUseCase: ObserveReceivedRequestsUseCase,
    private val respondAdoptionRequestUseCase: RespondAdoptionRequestUseCase,
    private val resources: ResourceProvider
) : ViewModel() {

    private val _uiState = MutableStateFlow(AdoptionRequestsUiState())
    val uiState: StateFlow<AdoptionRequestsUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            observeReceivedRequestsUseCase().collect { requests ->
                _uiState.update { it.copy(requests = requests, isLoading = false) }
            }
        }
    }

    fun onTabChange(showAnswered: Boolean) = _uiState.update { it.copy(showAnswered = showAnswered) }

    // Aceptar una adopción cierra el caso; en ambos casos se notifica a quien la envió
    fun onRespond(request: AdoptionRequest, accept: Boolean) {
        viewModelScope.launch {
            try {
                respondAdoptionRequestUseCase(request.id, accept)
                val message = if (accept) R.string.request_accepted_toast else R.string.request_rejected_toast
                _uiState.update { it.copy(infoMessage = resources.getString(message, request.requesterName)) }
            } catch (e: Exception) {
                _uiState.update { it.copy(infoMessage = e.message) }
            }
        }
    }

    fun resetInfoMessage() = _uiState.update { it.copy(infoMessage = null) }
}
