package co.edu.adopcionmascota.domain.usecase.preferences

import co.edu.adopcionmascota.domain.model.AdvancedFilter
import co.edu.adopcionmascota.domain.repository.FilterPreferencesRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveFilterUseCase @Inject constructor(
    private val filterPreferencesRepository: FilterPreferencesRepository
) {
    operator fun invoke(): Flow<AdvancedFilter> = filterPreferencesRepository.filter
}
