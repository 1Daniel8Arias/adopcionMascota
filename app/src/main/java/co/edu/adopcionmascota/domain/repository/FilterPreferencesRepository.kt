package co.edu.adopcionmascota.domain.repository

import co.edu.adopcionmascota.domain.model.AdvancedFilter
import kotlinx.coroutines.flow.Flow

interface FilterPreferencesRepository {

    val filter: Flow<AdvancedFilter>

    suspend fun saveFilter(filter: AdvancedFilter)

    suspend fun clearFilter()
}
