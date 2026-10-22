package co.edu.adopcionmascota.features.profile.updated

import androidx.lifecycle.ViewModel
import co.edu.adopcionmascota.features.profile.UserProfileStore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class ProfileUpdatedUiState(
    val photoUrl: String = "",
    val name: String = "",
    val localityName: String = "",
    val neighborhood: String = "",
    val lostPetAlerts: Boolean = false,
    val changes: List<String> = emptyList(),

    // Reputación (por ahora quemada, igual que en el perfil)
    val points: Int = 340,
    val nextLevelPoints: Int = 500
) {
    // Ej: "Usaquén, Bogotá D.C. · Cedritos"
    val locationText: String
        get() {
            val sector = neighborhood.substringBefore(" /")
            return if (sector.isBlank()) "$localityName, Bogotá D.C."
            else "$localityName, Bogotá D.C. · $sector"
        }

    val progress: Float
        get() = if (nextLevelPoints == 0) 0f
        else (points.toFloat() / nextLevelPoints).coerceIn(0f, 1f)

    val missingPoints: Int
        get() = (nextLevelPoints - points).coerceAtLeast(0)
}

class ProfileUpdatedViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUpdatedUiState())
    val uiState: StateFlow<ProfileUpdatedUiState> = _uiState.asStateFlow()

    init {
        // Se toman los datos recién guardados y la lista de cambios
        val profile = UserProfileStore.profile.value
        _uiState.value = ProfileUpdatedUiState(
            photoUrl = profile.photoUrl,
            name = profile.name,
            localityName = profile.localityName,
            neighborhood = profile.neighborhood,
            lostPetAlerts = profile.lostPetAlerts,
            changes = UserProfileStore.lastChanges.value
        )
    }
}
