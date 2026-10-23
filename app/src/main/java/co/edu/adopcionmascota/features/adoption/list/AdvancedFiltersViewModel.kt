package co.edu.adopcionmascota.features.adoption.list

import androidx.lifecycle.ViewModel
import co.edu.adopcionmascota.domain.PetType
import co.edu.adopcionmascota.domain.Publication
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

// Distancias disponibles en el selector
val DISTANCE_OPTIONS = listOf(1, 5, 10, 20)

data class AdvancedFiltersUiState(
    val types: Set<PetType> = emptySet(),
    val sex: String? = null,
    val maxDistanceKm: Int = 20,

    // Preferencias que todavía no tienen datos en las publicaciones; se guardan solo en pantalla
    val species: Set<String> = emptySet(),
    val sizes: Set<String> = emptySet(),
    val lifeStages: Set<String> = emptySet(),
    val seals: Set<String> = emptySet(),
    val notifyLostNearby: Boolean = true,

    val publications: List<Publication> = emptyList(),
    val infoMessage: String? = null
) {
    val filter: AdvancedFilter
        get() = AdvancedFilter(types = types, sex = sex, maxDistanceKm = maxDistanceKm)

    // El conteo se recalcula con cada cambio: "Mostrar N Resultados"
    val resultsCount: Int
        get() = publications.count { filter.matches(it) }

    val highlighted: List<Publication>
        get() = publications.filter { filter.matches(it) }.take(2)
}

class AdvancedFiltersViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(AdvancedFiltersUiState())
    val uiState: StateFlow<AdvancedFiltersUiState> = _uiState.asStateFlow()

    init {
        // Se parte de los filtros que el feed ya tiene aplicados
        val current = AdvancedFilterStore.filter.value
        _uiState.value = AdvancedFiltersUiState(
            types = current.types,
            sex = current.sex,
            maxDistanceKm = current.maxDistanceKm,
            publications = samplePublications()
        )
    }

    fun onTypeToggle(type: PetType) = _uiState.update {
        it.copy(types = if (type in it.types) it.types - type else it.types + type)
    }

    fun onSexSelected(sex: String?) = _uiState.update { it.copy(sex = sex) }

    fun onDistanceSelected(km: Int) = _uiState.update { it.copy(maxDistanceKm = km) }

    fun onSpeciesToggle(value: String) = _uiState.update { it.copy(species = it.species.toggle(value)) }
    fun onSizeToggle(value: String) = _uiState.update { it.copy(sizes = it.sizes.toggle(value)) }
    fun onLifeStageToggle(value: String) = _uiState.update { it.copy(lifeStages = it.lifeStages.toggle(value)) }
    fun onSealToggle(value: String) = _uiState.update { it.copy(seals = it.seals.toggle(value)) }

    fun onNotifyLostChange(enabled: Boolean) = _uiState.update { it.copy(notifyLostNearby = enabled) }

    fun onReset() {
        _uiState.update {
            AdvancedFiltersUiState(publications = it.publications, infoMessage = "Filtros restablecidos")
        }
    }

    // "Mostrar N Resultados": el feed aplica los filtros al volver
    fun onApply() {
        AdvancedFilterStore.apply(_uiState.value.filter)
    }

    fun onSaveFavorite() = _uiState.update {
        it.copy(infoMessage = "Búsqueda guardada como favorita")
    }

    fun resetInfoMessage() = _uiState.update { it.copy(infoMessage = null) }

    private fun Set<String>.toggle(value: String): Set<String> =
        if (value in this) this - value else this + value
}
