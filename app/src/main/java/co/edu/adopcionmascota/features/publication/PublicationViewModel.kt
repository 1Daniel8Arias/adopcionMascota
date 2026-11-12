package co.edu.adopcionmascota.features.publication

import javax.inject.Inject
import dagger.hilt.android.lifecycle.HiltViewModel
import co.edu.adopcionmascota.core.util.ResourceProvider
import co.edu.adopcionmascota.domain.model.Location
import co.edu.adopcionmascota.domain.model.Publication
import co.edu.adopcionmascota.domain.model.PublicationStatus
import co.edu.adopcionmascota.domain.usecase.publication.CreateReportUseCase
import java.util.UUID

import co.edu.adopcionmascota.R
import co.edu.adopcionmascota.core.util.RequestResult
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class PublicationUiState(
    val category: String = "Adopción",
    val photos: List<String> = emptyList(),
    val title: String = "",
    val species: String = "Perro",
    val breed: String = "",
    val size: String = "Mediano",
    val isVaccinated: Boolean = true,
    val isDewormed: Boolean = true,
    val isSterilized: Boolean = true,
    val hasMicrochip: Boolean = false,
    val history: String = "",
    val address: String = "Armenia, Quindío",
    val cityRegion: String = "Quindío, Colombia",
    val latitude: Double = 4.5389,
    val longitude: Double = -75.6725,
    val publicationResult: RequestResult? = null,
    val infoMessage: String? = null
) {
    val isFormValid: Boolean
        get() = title.isNotBlank() &&
                photos.isNotEmpty() &&
                species.isNotBlank() &&
                history.isNotBlank() &&
                address.isNotBlank()
}
@HiltViewModel
class PublicationViewModel @Inject constructor(
    private val createReportUseCase: CreateReportUseCase,
    private val resources: ResourceProvider
) : ViewModel() {

    private val _uiState = MutableStateFlow(PublicationUiState())
    val uiState: StateFlow<PublicationUiState> = _uiState.asStateFlow()

    fun onCategorySelected(category: String) {
        _uiState.update { it.copy(category = category) }
    }

    fun onTitleChanged(newTitle: String) {
        _uiState.update { it.copy(title = newTitle) }
    }

    fun onSpeciesSelected(species: String) {
        _uiState.update { it.copy(species = species) }
    }

    fun onBreedChanged(newBreed: String) {
        _uiState.update { it.copy(breed = newBreed) }
    }

    fun onSizeSelected(size: String) {
        _uiState.update { it.copy(size = size) }
    }

    fun toggleVaccinated() {
        _uiState.update { it.copy(isVaccinated = !it.isVaccinated) }
    }

    fun toggleDewormed() {
        _uiState.update { it.copy(isDewormed = !it.isDewormed) }
    }

    fun toggleSterilized() {
        _uiState.update { it.copy(isSterilized = !it.isSterilized) }
    }

    fun toggleMicrochip() {
        _uiState.update { it.copy(hasMicrochip = !it.hasMicrochip) }
    }

    fun onHistoryChanged(newHistory: String) {
        if (newHistory.length <= 500) {
            _uiState.update { it.copy(history = newHistory) }
        }
    }

    fun publishPet() {
        if (!_uiState.value.isFormValid) return

        viewModelScope.launch {
            _uiState.update { it.copy(publicationResult = RequestResult.Loading) }
            try {
                // Se guarda en Room "En revisión": aparece en "Mis Publicaciones"
                // y en la cola del moderador, pero no en el feed hasta que la aprueben
                val state = _uiState.value
                createReportUseCase(
                    Publication(
                        id = UUID.randomUUID().toString(),
                        name = state.title.trim(),
                        type = categoryToType(state.category),
                        species = state.species,
                        breed = state.breed.trim(),
                        size = state.size,
                        description = state.history.trim(),
                        location = state.address,
                        coordinates = Location(state.latitude, state.longitude),
                        image = state.photos.firstOrNull() ?: "icono",
                        status = PublicationStatus.PENDING,
                        isVaccinated = state.isVaccinated,
                        isDewormed = state.isDewormed,
                        isSterilized = state.isSterilized,
                        hasMicrochip = state.hasMicrochip
                    )
                )
                _uiState.update { it.copy(publicationResult = RequestResult.Success("")) }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(publicationResult = RequestResult.Failure(resources.getString(R.string.publish_error)))
                }
            }
        }
    }

    // "Guardar borrador": el formulario se conserva mientras la pantalla siga abierta
    fun onSaveDraft() {
        _uiState.update { it.copy(infoMessage = resources.getString(R.string.draft_saved)) }
    }

    fun resetInfoMessage() {
        _uiState.update { it.copy(infoMessage = null) }
    }

    fun resetPublicationResult() {
        _uiState.update { it.copy(publicationResult = null) }
    }

    // Punto elegido en el mapa
    fun onLocationSelected(location: Location) {
        _uiState.update { it.copy(latitude = location.latitude, longitude = location.longitude) }
    }

    // Imagen elegida en la galería
    fun onPhotoSelected(uri: String) {
        _uiState.update { if (it.photos.size < 5) it.copy(photos = it.photos + uri) else it }
    }

    fun onRemovePhoto(photo: String) {
        _uiState.update { it.copy(photos = it.photos - photo) }
    }
}