package co.edu.adopcionmascota.features.adoption.list

import androidx.lifecycle.ViewModel
import co.edu.adopcionmascota.R
import co.edu.adopcionmascota.domain.PetType
import co.edu.adopcionmascota.domain.PublicationStatus
import co.edu.adopcionmascota.domain.Publication
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

class PublicationListViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(PublicationListUiState(publications = samplePublications()))
    val uiState: StateFlow<PublicationListUiState> = _uiState.asStateFlow()

    init {
        // Los filtros avanzados y las eliminaciones vienen de otras pantallas
        viewModelScope.launch {
            combine(AdvancedFilterStore.filter, MyPublicationsStore.deletedFeedIds) { filter, deleted ->
                filter to deleted
            }.collect { (filter, deleted) ->
                _uiState.update { it.copy(advancedFilter = filter, deletedIds = deleted) }
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
        AdvancedFilterStore.clear()
    }
}

// Datos quemados del feed; también los usan la vista mapa y los filtros avanzados
fun samplePublications(): List<Publication> {
        val publications =listOf(
            Publication(
                id = "1",
            name = "Max",
            description = "Perro amigable y juguetón en busca de un hogar.",
            image = R.drawable.imagen_max,
            status = PublicationStatus.VERIFIED,
            type = PetType.ADOPCION,
            location = "Filandia",
            time = "Hace 2 horas",
            distancia = "1.2 km",
            likes = "45",
            age = "3 meses",
            gender = "Macho"
        ),

         Publication(
            id = "2",
            name = "Luna",
            description = "Gatita tranquila y cariñosa, vacunada y esterilizada.",
            image = R.drawable.imagen_luna,
            status = PublicationStatus.RESOLVED,
            type = PetType.PERDIDO,
            location = "Circasia",
            time = "Hace 1 día",
            distancia = "3.8 km",
            likes = "78",
            age = "2 años",
            gender = "Hembra"
        ),

        Publication(
            id = "3",
            name = "Rocky",
            description = "Perro rescatado que necesita una familia responsable.",
            image = R.drawable.imagen_rocky,
            status = PublicationStatus.PENDING,
            type = PetType.ENCONTRADO,
            location = "Armenia",
            time = "Hace 5 horas",
            distancia = "850 m",
            likes = "32",
            age = "5 años",
            gender = "Macho"
        )

            )
        return publications
}