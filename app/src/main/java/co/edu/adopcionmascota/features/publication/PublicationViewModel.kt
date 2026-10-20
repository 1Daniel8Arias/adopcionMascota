package co.edu.adopcionmascota.features.publication


import co.edu.adopcionmascota.core.util.RequestResult
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class PublicationUiState(
    val category: String = "Adopción",
    val photos: List<String> = listOf("principal_photo_mock"),
    val title: String = "",
    val species: String = "Perro",
    val breed: String = "",
    val size: String = "Mediano",
    val isVaccinated: Boolean = true,
    val isDewormed: Boolean = true,
    val isSterilized: Boolean = true,
    val hasMicrochip: Boolean = false,
    val history: String = "",
    val address: String = "Calle 140 # 11-45, Usaquén",
    val cityRegion: String = "Bogotá D.C., Cundinamarca, Colombia",
    val latitude: Double = 4.6956,
    val longitude: Double = -74.0331,
    val publicationResult: RequestResult? = null
) {
    val isFormValid: Boolean
        get() = title.isNotBlank() &&
                photos.isNotEmpty() &&
                species.isNotBlank() &&
                history.isNotBlank() &&
                address.isNotBlank()
}
class PublicationViewModel : ViewModel() {

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
                delay(2000) // Simulación de petición de red
                _uiState.update { it.copy(publicationResult = RequestResult.Success("")) }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(publicationResult = RequestResult.Failure("Error al publicar la mascota"))
                }
            }
        }
    }

    fun resetPublicationResult() {
        _uiState.update { it.copy(publicationResult = null) }
    }

    fun onAddPhoto() {
        val currentPhotos = _uiState.value.photos
        if (currentPhotos.size < 5) {
            val nextNumber = currentPhotos.size + 1
            val newPhotoMock = "mock_photo_$nextNumber"
            _uiState.update { it.copy(photos = currentPhotos + newPhotoMock) }
        }
    }

    fun onRemovePhoto(photoMock: String) {
        _uiState.update { it.copy(photos = it.photos - photoMock) }
    }
}