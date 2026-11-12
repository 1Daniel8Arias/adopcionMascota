package co.edu.adopcionmascota.features.profile.updated

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.edu.adopcionmascota.domain.usecase.user.ObserveCurrentUserUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ProfileUpdatedUiState(
    val photoUrl: String = "",
    val name: String = "",
    val localityName: String = "",
    val neighborhood: String = "",
    val lostPetAlerts: Boolean = false,
    val changes: List<String> = emptyList(),

    // Reputación del usuario (Room)
    val points: Int = 0,
    val nextLevelPoints: Int = 100
) {
    // Ej: "Armenia · Barrio Centro"
    val locationText: String
        get() {
            val sector = neighborhood.substringBefore(",")
            return if (sector.isBlank()) localityName else "$localityName · $sector"
        }

    val progress: Float
        get() = if (nextLevelPoints == 0) 0f
        else (points.toFloat() / nextLevelPoints).coerceIn(0f, 1f)

    val missingPoints: Int
        get() = (nextLevelPoints - points).coerceAtLeast(0)
}

@HiltViewModel
class ProfileUpdatedViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    observeCurrentUserUseCase: ObserveCurrentUserUseCase
) : ViewModel() {

    // La lista de cambios llega como argumento de la ruta desde "Editar Perfil"
    private val changes: List<String> = savedStateHandle.get<List<String>>("changes").orEmpty()

    private val _uiState = MutableStateFlow(ProfileUpdatedUiState(changes = changes))
    val uiState: StateFlow<ProfileUpdatedUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            observeCurrentUserUseCase().collect { user ->
                if (user == null) return@collect
                _uiState.update {
                    it.copy(
                        photoUrl = user.profilePictureUrl,
                        name = user.name,
                        localityName = user.city,
                        neighborhood = user.address,
                        lostPetAlerts = user.lostPetAlerts,
                        points = user.points,
                        nextLevelPoints = user.level.next?.minPoints ?: user.points
                    )
                }
            }
        }
    }
}
