package co.edu.adopcionmascota.features.publication

import co.edu.adopcionmascota.R
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

data class EditPublicationUiState(
    val original: MyPublication? = null,
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
    val photos: List<String> = listOf("principal_photo_mock"),
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
    private val resources: ResourceProvider
) : ViewModel() {

    private val publicationId: String = checkNotNull(savedStateHandle["publicationId"])


    private val _uiState = MutableStateFlow(EditPublicationUiState())
    val uiState: StateFlow<EditPublicationUiState> = _uiState.asStateFlow()

    init {
        MyPublicationsStore.findById(publicationId)?.let { publication ->
            _uiState.value = EditPublicationUiState(
                original = publication,
                category = publication.category,
                title = publication.name,
                species = publication.species,
                breed = publication.breed,
                size = publication.size,
                isVaccinated = publication.isVaccinated,
                isDewormed = publication.isDewormed,
                isSterilized = publication.isSterilized,
                hasMicrochip = publication.hasMicrochip,
                history = publication.description
            )
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

    fun saveChanges() {
        val state = _uiState.value
        val original = state.original ?: return
        if (!state.isFormValid || state.isSaving) return

        viewModelScope.launch {
            _uiState.update { it.copy(saveResult = RequestResult.Loading) }
            delay(1500) // Simula el envío a revalidación

            // Una publicación con observaciones vuelve a revisión al corregirla
            val newStatus = if (original.status == MyPublicationStatus.OBSERVATION) {
                MyPublicationStatus.IN_REVIEW
            } else original.status

            MyPublicationsStore.update(
                original.copy(
                    name = state.title.trim(),
                    category = state.category,
                    species = state.species,
                    breed = state.breed.trim(),
                    size = state.size,
                    isVaccinated = state.isVaccinated,
                    isDewormed = state.isDewormed,
                    isSterilized = state.isSterilized,
                    hasMicrochip = state.hasMicrochip,
                    description = state.history.trim(),
                    status = newStatus,
                    moderatorNote = if (newStatus == MyPublicationStatus.IN_REVIEW) null else original.moderatorNote
                )
            )
            _uiState.update { it.copy(saveResult = RequestResult.Success(resources.getString(R.string.publication_edited_vm))) }
        }
    }
}
