package co.edu.adopcionmascota.features.moderator.panel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.edu.adopcionmascota.domain.model.RejectionReason
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


data class RejectPublicationUiState(

    val publicationId: Long? = null,

    val selectedReason: RejectionReason? = null,

    val reasons: List<RejectionReason> = emptyList(),

    val isLoading: Boolean = false,

    val rejectionSent: Boolean = false,

    val errorMessage: String? = null
)

sealed interface RejectPublicationEvent {

    data class SelectReason(
        val reason: RejectionReason
    ) : RejectPublicationEvent

    data object ConfirmRejection : RejectPublicationEvent

    data object ClearError : RejectPublicationEvent
}
class RejectPublicationViewModel : ViewModel() {

    private val _uiState =
        MutableStateFlow(RejectPublicationUiState())

    val uiState: StateFlow<RejectPublicationUiState> =
        _uiState.asStateFlow()

    fun onEvent(event: RejectPublicationEvent) {

        when (event) {

            is RejectPublicationEvent.SelectReason -> {
                _uiState.update {
                    it.copy(
                        selectedReason = event.reason
                    )
                }
            }

            RejectPublicationEvent.ConfirmRejection -> {
                rejectPublication()
            }

            RejectPublicationEvent.ClearError -> {
                _uiState.update {
                    it.copy(errorMessage = null)
                }
            }
        }
    }

    private fun rejectPublication() {

        val reason = _uiState.value.selectedReason

        if (reason == null) {

            _uiState.update {
                it.copy(
                    errorMessage = "Debes seleccionar un motivo."
                )
            }

            return
        }

        viewModelScope.launch {

            _uiState.update {
                it.copy(isLoading = true)
            }

            try {

                // Simula llamada al backend
                delay(1500)

                _uiState.update {
                    it.copy(
                        isLoading = false,
                        rejectionSent = true
                    )
                }

            } catch (e: Exception) {

                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = e.message
                    )
                }
            }
        }
    }
}

val  reasons = listOf(
    RejectionReason(
        1,
        "Venta de animales o aporte económico lucrativo",
        "Cobros en adopción no avalados ni justificados documentalmente."
    ),
RejectionReason(
2,
"Soporte veterinario o carnet ilegible o falso",
"Carné de vacunas adulterado, sin firma profesional o no verificable."
),
RejectionReason(
3,
"Fotos de internet o no auténticas",
"Imágenes de bancos públicos o criaderos sin comprobación real del animal."
),
RejectionReason(
4,
"Datos de contacto o localidad sospechosa",
"Números ficticios o desviación sistemática a canales inseguros."
),
RejectionReason(
5,
"Otra infracción de bienestar animal",
"Indicios de maltrato, destete precoz (< 2 meses)."
)
)