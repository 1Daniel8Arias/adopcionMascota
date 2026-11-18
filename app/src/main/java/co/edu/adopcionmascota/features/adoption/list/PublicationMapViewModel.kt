package co.edu.adopcionmascota.features.adoption.list

import co.edu.adopcionmascota.core.util.ResourceProvider
import co.edu.adopcionmascota.R
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.edu.adopcionmascota.domain.model.PetType
import co.edu.adopcionmascota.domain.model.Publication
import co.edu.adopcionmascota.domain.usecase.preferences.ObserveFilterUseCase
import co.edu.adopcionmascota.domain.usecase.publication.ObserveFeedUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import co.edu.adopcionmascota.domain.usecase.favorite.ObserveFavoriteIdsUseCase
import co.edu.adopcionmascota.domain.usecase.favorite.ToggleFavoriteUseCase

data class PublicationMapUiState(
    val publications: List<Publication> = emptyList(),
    val typeFilter: PetType? = null,            // null = "Todos"
    val searchText: String = "",
    val selectedId: String? = null,
    val sightings: Map<String, Int> = emptyMap(),
    val favoriteIds: Set<String> = emptySet(),
    val infoMessage: String? = null
) {
    val visiblePublications: List<Publication>
        get() = publications.filter { publication ->
            (typeFilter == null || publication.type == typeFilter) &&
                    (searchText.isBlank() || publication.name.contains(searchText, ignoreCase = true) ||
                            publication.location.contains(searchText, ignoreCase = true))
        }

    val selected: Publication?
        get() = visiblePublications.find { it.id == selectedId }

    fun countFor(type: PetType?): Int =
        if (type == null) publications.size else publications.count { it.type == type }
}

@HiltViewModel
class PublicationMapViewModel @Inject constructor(
    observeFeedUseCase: ObserveFeedUseCase,
    observeFilterUseCase: ObserveFilterUseCase,
    private val resources: ResourceProvider,
    observeFavoriteIdsUseCase: ObserveFavoriteIdsUseCase,
    private val toggleFavoriteUseCase: ToggleFavoriteUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(PublicationMapUiState())
    val uiState: StateFlow<PublicationMapUiState> = _uiState.asStateFlow()

    init {
        // Mismos datos del feed (publicaciones aprobadas) con los filtros avanzados
        viewModelScope.launch {
            combine(observeFeedUseCase(), observeFilterUseCase()) { publications, filter ->
                publications.filter { filter.matches(it) }
            }.collect { publications ->
                _uiState.update { it.copy(publications = publications) }
            }
        }
    }

    init {
        viewModelScope.launch {
            observeFavoriteIdsUseCase().collect { ids -> _uiState.update { it.copy(favoriteIds = ids) } }
        }
    }

    fun onLikeClick(publicationId: String) {
        viewModelScope.launch { toggleFavoriteUseCase(publicationId) }
    }

    fun onMarkerClick(id: String) = _uiState.update { it.copy(selectedId = id) }

    fun onTypeFilterChange(type: PetType?) = _uiState.update { it.copy(typeFilter = type) }

    fun onSearchChange(text: String) = _uiState.update { it.copy(searchText = text) }

    // Tocar una zona vacía del mapa oculta la tarjeta
    fun onCloseCard() = _uiState.update { it.copy(selectedId = null) }

    // "Lo he visto": suma un avistamiento a la mascota seleccionada
    fun onSeenClick() {
        val selected = _uiState.value.selected ?: return
        _uiState.update {
            it.copy(
                sightings = it.sightings + (selected.id to (it.sightings[selected.id] ?: 0) + 1),
                infoMessage = resources.getString(R.string.map_thanks, selected.name)
            )
        }
    }

    fun resetInfoMessage() = _uiState.update { it.copy(infoMessage = null) }
}
