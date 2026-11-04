package co.edu.adopcionmascota.features.adoption.list

import co.edu.adopcionmascota.domain.usecase.preferences.ClearFilterUseCase
import co.edu.adopcionmascota.domain.usecase.preferences.ObserveFilterUseCase
import co.edu.adopcionmascota.domain.usecase.publication.ObserveReportsUseCase
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
import androidx.lifecycle.viewModelScope
import co.edu.adopcionmascota.features.adoption.list.component.ExploreFilter
import co.edu.adopcionmascota.features.publication.MyPublicationsStore

// Estado completo del feed (búsqueda, filtros rápidos y filtros avanzados)
data class PublicationListUiState(
    val publications: List<Publication> = emptyList(),
    val searchText: String = "",
    val selectedFilter: ExploreFilter = ExploreFilter.TODAS,
    val advancedFilter: AdvancedFilter = AdvancedFilter(),
    val deletedIds: Set<String> = emptySet()
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
            }

            // Filtros avanzados y publicaciones que el usuario eliminó
            matchesSearch && matchesFilter &&
                    advancedFilter.matches(publication) && publication.id !in deletedIds
        }
}

@HiltViewModel
class PublicationListViewModel @Inject constructor(
    observeReportsUseCase: ObserveReportsUseCase,
    observeFilterUseCase: ObserveFilterUseCase,
    private val clearFilterUseCase: ClearFilterUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(PublicationListUiState())
    val uiState: StateFlow<PublicationListUiState> = _uiState.asStateFlow()

    init {
        // Las publicaciones vienen del repositorio, los filtros de Data Store
        // y las eliminaciones de otras pantallas
        viewModelScope.launch {
            combine(
                observeReportsUseCase(),
                observeFilterUseCase(),
                MyPublicationsStore.deletedFeedIds
            ) { publications, filter, deleted ->
                Triple(publications, filter, deleted)
            }.collect { (publications, filter, deleted) ->
                _uiState.update {
                    it.copy(publications = publications, advancedFilter = filter, deletedIds = deleted)
                }
            }
        }
    }

    fun findById(id: String): Publication? =
        _uiState.value.publications.find { it.id == id }

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
