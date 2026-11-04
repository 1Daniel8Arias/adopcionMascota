package co.edu.adopcionmascota.domain.usecase.preferences

import co.edu.adopcionmascota.domain.model.AdvancedFilter
import co.edu.adopcionmascota.domain.repository.FilterPreferencesRepository
import javax.inject.Inject

class SaveFilterUseCase @Inject constructor(
    private val filterPreferencesRepository: FilterPreferencesRepository
) {
    suspend operator fun invoke(filter: AdvancedFilter) {
        filterPreferencesRepository.saveFilter(filter)
    }
}
