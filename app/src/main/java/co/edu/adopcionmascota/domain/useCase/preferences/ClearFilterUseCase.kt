package co.edu.adopcionmascota.domain.usecase.preferences

import co.edu.adopcionmascota.domain.repository.FilterPreferencesRepository
import javax.inject.Inject

class ClearFilterUseCase @Inject constructor(
    private val filterPreferencesRepository: FilterPreferencesRepository
) {
    suspend operator fun invoke() {
        filterPreferencesRepository.clearFilter()
    }
}
