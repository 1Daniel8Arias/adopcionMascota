package co.edu.adopcionmascota.features.statistics

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.edu.adopcionmascota.domain.model.Statistics
import co.edu.adopcionmascota.domain.usecase.statistics.ObserveStatisticsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

data class StatisticsUiState(
    val isLoading: Boolean = true,
    val statistics: Statistics = Statistics()
)

@HiltViewModel
class StatisticsViewModel @Inject constructor(
    observeStatisticsUseCase: ObserveStatisticsUseCase
) : ViewModel() {

    // Conteos reales de Room; se recalculan cada vez que cambian los datos
    val uiState: StateFlow<StatisticsUiState> = observeStatisticsUseCase()
        .map { StatisticsUiState(isLoading = false, statistics = it) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = StatisticsUiState()
        )
}
