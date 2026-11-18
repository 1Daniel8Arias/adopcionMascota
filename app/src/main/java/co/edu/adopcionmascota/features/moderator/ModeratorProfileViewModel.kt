package co.edu.adopcionmascota.features.moderator

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.edu.adopcionmascota.domain.model.Statistics
import co.edu.adopcionmascota.domain.model.User
import co.edu.adopcionmascota.domain.usecase.auth.LogoutUseCase
import co.edu.adopcionmascota.domain.usecase.statistics.ObserveStatisticsUseCase
import co.edu.adopcionmascota.domain.usecase.user.ObserveCurrentUserUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ProfileUiState(
    val user: User? = null,
    val statistics: Statistics = Statistics(),
    val poinst: Int = 0
)

@HiltViewModel
class ModeratorProfileViewModel @Inject constructor(
    observeCurrentUserUseCase: ObserveCurrentUserUseCase,
    observeStatisticsUseCase: ObserveStatisticsUseCase,
    private val logotUseCase: LogoutUseCase,
) : ViewModel() {

    // El estado se recalcula cada vez que cambia el usuario o cualquier dato de Room
    val uiState: StateFlow<ProfileUiState> =
        combine(observeCurrentUserUseCase(), observeStatisticsUseCase()) { user, statistics ->
            ProfileUiState(user = user, statistics = statistics, poinst = user?.points ?: 0)
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = ProfileUiState()
        )

    fun logout() {
        viewModelScope.launch {
            runCatching { logotUseCase() }
        }
    }
}
