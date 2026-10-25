package co.edu.adopcionmascota.features.moderator.panel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.edu.adopcionmascota.R
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch



data class ModerationUiState(

    val isLoading: Boolean = false,

    val pendingCount: Int = 0,
    val approvedToday: Int = 0,
    val averageReviewTime: String = "0 min",
    val criticalReports: Int = 0,

    val selectedFilter: ModerationFilter = ModerationFilter.ALL,

    val publications: List<ModerationPublicationUi> = emptyList(),

    val error: String? = null
)
enum class ModerationFilter {
    ALL,
    POSSIBLE_SALE,
    ADOPTION,
    REJECTED,
    APPROVED
}
@HiltViewModel
class ModerationDashboardViewModel @Inject constructor() : ViewModel() {

    private val _uiState =
        MutableStateFlow(
            ModerationUiState(
                publications = fakePublications()
            )
        )

    val uiState: StateFlow<ModerationUiState> =
        _uiState.asStateFlow()

    fun onEvent(event: ModerationEvent) {

        when (event) {

            is ModerationEvent.ChangeFilter -> {
                _uiState.update {
                    it.copy(
                        selectedFilter = event.filter
                    )
                }
            }

            is ModerationEvent.ApprovePublication -> {
                approvePublication(event.publicationId)
            }

            else -> {}
        }
    }

    private fun approvePublication(publicationId: Long) {

        _uiState.update { state ->

            state.copy(
                publications = state.publications.filter {
                    it.id != publicationId
                },
                pendingCount = state.pendingCount - 1,
                approvedToday = state.approvedToday + 1
            )
        }
    }

    private fun rejectPublication(publicationId: Long) {

        _uiState.update { state ->

            state.copy(
                publications = state.publications.filter {
                    it.id != publicationId
                },
                pendingCount = state.pendingCount - 1
            )
        }
    }

    private fun inspectPublication(publicationId: Long) {

        println("Inspeccionando publicación: $publicationId")
    }
}

sealed interface ModerationEvent {

    data class ChangeFilter(
        val filter: ModerationFilter
    ) : ModerationEvent

    data class ApprovePublication(
        val publicationId: Long
    ) : ModerationEvent

    data class RejectPublication(
        val publicationId: Long
    ) : ModerationEvent

    data class InspectPublication(
        val publicationId: Long
    ) : ModerationEvent
}
fun fakePublications(): List<ModerationPublicationUi> {
    return listOf(
        ModerationPublicationUi(
            id = 1,
            userName = "Carlos M.",
            userLevel = "Nivel 1: Amigo Animal",
            publicationAge = "Creada hace 2 d",
            title = "Cachorros Bulldog Francés con pedigrí en adopción con aporte económico",
            animalType = "Bulldog Francés",
            animalAge = "2 meses",
            city = "Chapinero, Bogotá",
            imageUrl = R.drawable.imagen_max,
            photosCount = 3,
            vaccinesComplete = false,
            sterilized = false,
            ethicsAlert = true,
            ethicsMessage = "Detección de cobro: menciona aporte voluntario de \$500.000",
            createdAgo = "18m"
        ),

        ModerationPublicationUi(
            id = 2,
            userName = "Sofía G.",
            userLevel = "Nivel 3: Guardián",
            publicationAge = "Creada hace 5 h",
            title = "Kira - Perrita mestiza rescatada en Parque El Virrey",
            animalType = "Mestiza",
            animalAge = "1.5 años",
            city = "Usaquén, Bogotá",
            imageUrl = R.drawable.imagen_rocky,
            photosCount = 4,
            vaccinesComplete = true,
            sterilized = true,
            ethicsAlert = false,
            ethicsMessage = "",
            createdAgo = "34m"
        )
    )
}