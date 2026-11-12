package co.edu.adopcionmascota.features.publication

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.edu.adopcionmascota.R
import co.edu.adopcionmascota.core.util.RequestResult
import co.edu.adopcionmascota.core.util.ResourceProvider
import co.edu.adopcionmascota.domain.model.Publication
import co.edu.adopcionmascota.domain.usecase.publication.GetReportByIdUseCase
import co.edu.adopcionmascota.domain.usecase.publication.UpdatePublicationUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class EditPublicationUiState(
    val original: Publication? = null,
    val category: String = "Adopción",
    val title: String = "",
    val species: String = "Perro",
    val breed: String = "",
    val size: String = "Mediano",
    val isVaccinated: Boolean = true,
    val isDewormed: Boolean = true,
    val isSterilized: Boolean = false,
    val hasMicrochip: Boolean = false,
    val history: String = "",
    val photos: List<String> = emptyList(),
    val saveResult: RequestResult? = null,
    val infoMessage: String? = null
) {
    val isFormValid: Boolean
        get() = title.isNotBlank() && history.isNotBlank() && photos.isNotEmpty()

    val isSaving: Boolean
        get() = saveResult is RequestResult.Loading
}

@HiltViewModel
class EditPublicationViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getReportByIdUseCase: GetReportByIdUseCase,
    private val updatePublicationUseCase: UpdatePublicationUseCase,
    private val resources: ResourceProvider
) : ViewModel() {

    private val publicationId: String = checkNotNull(savedStateHandle["publicationId"])

    private val _uiState = MutableStateFlow(EditPublicationUiState())
    val uiState: StateFlow<EditPublicationUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            getReportByIdUseCase(publicationId)?.let { publication ->
                _uiState.value = EditPublicationUiState(
                    original = publication,
                    category = typeToCategory(publication.type),
                    title = publication.name,
                    species = publication.species,
                    breed = publication.breed,
                    size = publication.size,
                    isVaccinated = publication.isVaccinated,
                    isDewormed = publication.isDewormed,
                    isSterilized = publication.isSterilized,
                    hasMicrochip = publication.hasMicrochip,
                    history = publication.description,
                    photos = listOf(publication.image)
                )
            }
        }
    }

    fun onCategorySelected(category: String) = _uiState.update { it.copy(category = category) }
    fun onTitleChanged(title: String) = _uiState.update { it.copy(title = title) }
    fun onSpeciesSelected(species: String) = _uiState.update { it.copy(species = species) }
    fun onBreedChanged(breed: String) = _uiState.update { it.copy(breed = breed) }
    fun onSizeSelected(size: String) = _uiState.update { it.copy(size = size) }
    fun toggleVaccinated() = _uiState.update { it.copy(isVaccinated = !it.isVaccinated) }
    fun toggleDewormed() = _uiState.update { it.copy(isDewormed = !it.isDewormed) }
    fun toggleSterilized() = _uiState.update { it.copy(isSterilized = !it.isSterilized) }
    fun toggleMicrochip() = _uiState.update { it.copy(hasMicrochip = !it.hasMicrochip) }

    fun onHistoryChanged(history: String) {
        if (history.length <= 500) _uiState.update { it.copy(history = history) }
    }

    // Imagen elegida en la galería
    fun onPhotoSelected(uri: String) = _uiState.update {
        if (it.photos.size < 5) it.copy(photos = it.photos + uri) else it
    }

    fun onRemovePhoto(photo: String) = _uiState.update { it.copy(photos = it.photos - photo) }

    fun onSaveDraft() = _uiState.update { it.copy(infoMessage = resources.getString(R.string.draft_saved_edit)) }

    fun resetInfoMessage() = _uiState.update { it.copy(infoMessage = null) }

    // Si la publicación estaba rechazada, el caso de uso la devuelve a revisión
    fun saveChanges() {
        val state = _uiState.value
        val original = state.original ?: return
        if (!state.isFormValid || state.isSaving) return

        viewModelScope.launch {
            _uiState.update { it.copy(saveResult = RequestResult.Loading) }
            try {
                updatePublicationUseCase(
                    original.copy(
                        name = state.title.trim(),
                        type = categoryToType(state.category),
                        species = state.species,
                        breed = state.breed.trim(),
                        size = state.size,
                        isVaccinated = state.isVaccinated,
                        isDewormed = state.isDewormed,
                        isSterilized = state.isSterilized,
                        hasMicrochip = state.hasMicrochip,
                        description = state.history.trim(),
                        image = state.photos.first()
                    )
                )
                _uiState.update { it.copy(saveResult = RequestResult.Success(resources.getString(R.string.publication_edited_vm))) }
            } catch (e: Exception) {
                _uiState.update { it.copy(saveResult = RequestResult.Failure(e.message.orEmpty())) }
            }
        }
    }
}
