package co.edu.adopcionmascota.features.adoption.list

import co.edu.adopcionmascota.domain.PetType
import co.edu.adopcionmascota.domain.Publication
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

// Filtros de la pantalla "Filtros y Búsqueda" que el feed aplica
data class AdvancedFilter(
    val types: Set<PetType> = emptySet(),       // Vacío = todos los tipos
    val sex: String? = null,                    // "Macho", "Hembra" o null (cualquiera)
    val maxDistanceKm: Int = 20
) {
    val isActive: Boolean
        get() = types.isNotEmpty() || sex != null || maxDistanceKm < 20

    val activeCount: Int
        get() = listOf(types.isNotEmpty(), sex != null, maxDistanceKm < 20).count { it }

    fun matches(publication: Publication): Boolean {
        val matchesType = types.isEmpty() || publication.type in types
        val matchesSex = sex == null || publication.gender.equals(sex, ignoreCase = true)
        val matchesDistance = publication.distanceKm() <= maxDistanceKm
        return matchesType && matchesSex && matchesDistance
    }
}

// Convierte "1.2 km" u "850 m" en kilómetros
fun Publication.distanceKm(): Double {
    val value = distancia.filter { it.isDigit() || it == '.' }.toDoubleOrNull() ?: return 0.0
    return if (distancia.trim().endsWith("km")) value else value / 1000
}

object AdvancedFilterStore {

    private val _filter = MutableStateFlow(AdvancedFilter())
    val filter: StateFlow<AdvancedFilter> = _filter.asStateFlow()

    fun apply(filter: AdvancedFilter) {
        _filter.value = filter
    }

    fun clear() {
        _filter.value = AdvancedFilter()
    }
}
