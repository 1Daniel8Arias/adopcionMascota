package co.edu.adopcionmascota.features.adoption.list

import co.edu.adopcionmascota.domain.usecase.preferences.ClearFilterUseCase
import co.edu.adopcionmascota.domain.usecase.preferences.ObserveFilterUseCase
import co.edu.adopcionmascota.domain.usecase.publication.ObserveFeedUseCase
import javax.inject.Inject
import dagger.hilt.android.lifecycle.HiltViewModel
import co.edu.adopcionmascota.domain.model.AdvancedFilter
import androidx.lifecycle.ViewModel
import co.edu.adopcionmascota.domain.model.PetType
import co.edu.adopcionmascota.domain.model.Publication
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import co.edu.adopcionmascota.domain.usecase.favorite.ObserveFavoriteIdsUseCase
import co.edu.adopcionmascota.domain.usecase.favorite.ToggleFavoriteUseCase
import androidx.lifecycle.viewModelScope
import co.edu.adopcionmascota.features.adoption.list.component.ExploreFilter

// Estado completo del feed (búsqueda, filtros rápidos y filtros avanzados)
data class PublicationListUiState(
    val publications: List<Publication> = emptyList(),
    val searchText: String = "",
    val selectedFilter: ExploreFilter = ExploreFilter.TODAS,
    val advancedFilter: AdvancedFilter = AdvancedFilter(),
    val favoriteIds: Set<String> = emptySet()       // Publicaciones con "Me encanta" del usuario
) {
    // La lista visible se calcula aquí para no tener lógica de negocio en la UI
    val visiblePublications: List<Publication>
        get() = publications.filter { publication ->
            val matchesSearch = searchText.isBlank() ||
                    publication.name.contains(searchText, ignoreCase = true) ||
                    publication.description.contains(searchText, ignoreCase = true) ||
                    publication.location.contains(searchText, ignoreCase = true)

            val matchesFilter = when (selectedFilter) {
                ExploreFilter.TODAS -> true
                ExploreFilter.ADOPCION -> publication.type == PetType.ADOPCION
                ExploreFilter.PERDIDO -> publication.type == PetType.PERDIDO
                ExploreFilter.ENCONTRADO -> publication.type == PetType.ENCONTRADO
                ExploreFilter.VETERINARIA -> publication.type == PetType.VETERINARIA
            }

            matchesSearch && matchesFilter && advancedFilter.matches(publication)
        }
}

@HiltViewModel
class PublicationListViewModel @Inject constructor(
    observeFeedUseCase: ObserveFeedUseCase,
    observeFilterUseCase: ObserveFilterUseCase,
    private val clearFilterUseCase: ClearFilterUseCase,
    observeFavoriteIdsUseCase: ObserveFavoriteIdsUseCase,
    private val toggleFavoriteUseCase: ToggleFavoriteUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(PublicationListUiState())
    val uiState: StateFlow<PublicationListUiState> = _uiState.asStateFlow()

    init {
        // Solo las publicaciones aprobadas (Room) con los filtros guardados en Data Store
        viewModelScope.launch {
            combine(observeFeedUseCase(), observeFilterUseCase()) { publications, filter ->
                publications to filter
            }.collect { (publications, filter) ->
                _uiState.update { it.copy(publications = publications, advancedFilter = filter) }
            }
        }
        viewModelScope.launch {
            observeFavoriteIdsUseCase().collect { ids ->
                _uiState.update { it.copy(favoriteIds = ids) }
            }
        }
    }

    fun findById(id: String): Publication? =
        _uiState.value.publications.find { it.id == id }

    // "Me encanta" se guarda en Room y aparece en "Mis favoritos"
    fun onLikeClick(publicationId: String) {
        viewModelScope.launch { runCatching { toggleFavoriteUseCase(publicationId) } }
    }

    fun onSearchChange(text: String) {
        _uiState.update { it.copy(searchText = text) }
    }

    fun onFilterSelected(filter: ExploreFilter) {
        _uiState.update { it.copy(selectedFilter = filter) }
    }

    fun onClearAdvancedFilters() {
        viewModelScope.launch { clearFilterUseCase() }
    }
}
