package co.edu.adopcionmascota.data.repository.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import co.edu.adopcionmascota.domain.model.AdvancedFilter
import co.edu.adopcionmascota.domain.model.PetType
import co.edu.adopcionmascota.domain.repository.FilterPreferencesRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.filterDataStore: DataStore<Preferences> by preferencesDataStore(name = "filter_preferences")

// Los filtros del feed se conservan aunque se cierre la aplicación
@Singleton
class FilterPreferencesRepositoryImpl @Inject constructor(
    @param:ApplicationContext private val context: Context
) : FilterPreferencesRepository {

    private object Keys {
        val TYPES = stringSetPreferencesKey("types")
        val SEX = stringPreferencesKey("sex")
        val MAX_DISTANCE_KM = intPreferencesKey("max_distance_km")
    }

    override val filter: Flow<AdvancedFilter> = context.filterDataStore.data.map { prefs ->
        val default = AdvancedFilter()
        AdvancedFilter(
            types = prefs[Keys.TYPES].orEmpty()
                .mapNotNull { name -> PetType.entries.find { it.name == name } }
                .toSet(),
            sex = prefs[Keys.SEX],
            maxDistanceKm = prefs[Keys.MAX_DISTANCE_KM] ?: default.maxDistanceKm
        )
    }

    override suspend fun saveFilter(filter: AdvancedFilter) {
        context.filterDataStore.edit { prefs ->
            prefs[Keys.TYPES] = filter.types.map { it.name }.toSet()
            prefs[Keys.MAX_DISTANCE_KM] = filter.maxDistanceKm
            val sex = filter.sex
            if (sex == null) prefs.remove(Keys.SEX) else prefs[Keys.SEX] = sex
        }
    }

    override suspend fun clearFilter() {
        context.filterDataStore.edit { it.clear() }
    }
}
