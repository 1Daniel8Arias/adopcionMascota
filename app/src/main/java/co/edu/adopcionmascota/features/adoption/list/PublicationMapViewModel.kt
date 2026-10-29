package co.edu.adopcionmascota.features.adoption.list

import androidx.lifecycle.ViewModel
import co.edu.adopcionmascota.domain.model.PetType
import co.edu.adopcionmascota.domain.model.Publication
import co.edu.adopcionmascota.features.publication.MyPublicationsStore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

// Posición de cada marcador sobre la imagen del mapa (fracción del ancho y del alto)
data class MapMarker(
    val publication: Publication,
    val x: Float,
    val y: Float
)

data class PublicationMapUiState(
    val markers: List<MapMarker> = emptyList(),
    val typeFilter: PetType? = null,            // null = "Todos"
    val searchText: String = "",
    val selectedId: String? = null,
    val sightings: Map<String, Int> = emptyMap(),
    val infoMessage: String? = null
) {
    val visibleMarkers: List<MapMarker>
        get() = markers.filter { marker ->
            (typeFilter == null || marker.publication.type == typeFilter) &&
                    (searchText.isBlank() || marker.publication.name.contains(searchText, ignoreCase = true) ||
                            marker.publication.location.contains(searchText, ignoreCase = true))
        }

    val selected: Publication?
        get() = visibleMarkers.find { it.publication.id == selectedId }?.publication

    fun countFor(type: PetType?): Int =
        if (type == null) markers.size else markers.count { it.publication.type == type }
}

// Imagen estática: no se usa SDK de mapas ni ubicación real
private val MARKER_POSITIONS = listOf(
    0.28f to 0.32f,
    0.66f to 0.50f,
    0.42f to 0.70f,
    0.75f to 0.25f
)

class PublicationMapViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(PublicationMapUiState())
    val uiState: StateFlow<PublicationMapUiState> = _uiState.asStateFlow()

    init {
        // Mismos datos del feed, sin las publicaciones eliminadas y con los filtros avanzados
        val deleted = MyPublicationsStore.deletedFeedIds.value
        val filter = AdvancedFilterStore.filter.value
        val markers = samplePublications()
            .filter { it.id !in deleted && filter.matches(it) }
            .mapIndexed { index, publication ->
                val (x, y) = MARKER_POSITIONS[index % MARKER_POSITIONS.size]
                MapMarker(publication, x, y)
            }
        _uiState.value = PublicationMapUiState(
            markers = markers,
            selectedId = markers.firstOrNull()?.publication?.id
        )
    }

    fun onMarkerClick(id: String) = _uiState.update { it.copy(selectedId = id) }

    fun onTypeFilterChange(type: PetType?) = _uiState.update { state ->
        val newState = state.copy(typeFilter = type)
        // Si el seleccionado queda oculto, se selecciona el primero visible
        newState.copy(selectedId = newState.visibleMarkers.firstOrNull()?.publication?.id)
    }

    fun onSearchChange(text: String) = _uiState.update { state ->
        val newState = state.copy(searchText = text)
        newState.copy(selectedId = newState.visibleMarkers.firstOrNull()?.publication?.id)
    }

    fun onRecenter() = _uiState.update {
        it.copy(
            typeFilter = null,
            searchText = "",
            selectedId = it.markers.firstOrNull()?.publication?.id,
            infoMessage = "Área re-centrada en tu ubicación"
        )
    }

    fun onCloseCard() = _uiState.update { it.copy(selectedId = null) }

    // "Lo he visto": suma un avistamiento a la mascota seleccionada
    fun onSeenClick() {
        val selected = _uiState.value.selected ?: return
        _uiState.update {
            it.copy(
                sightings = it.sightings + (selected.id to (it.sightings[selected.id] ?: 0) + 1),
                infoMessage = "¡Gracias! Avisamos a la red que viste a ${selected.name}"
            )
        }
    }

    fun resetInfoMessage() = _uiState.update { it.copy(infoMessage = null) }
}
