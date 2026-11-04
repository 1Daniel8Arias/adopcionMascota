package co.edu.adopcionmascota.features.register

import co.edu.adopcionmascota.core.util.ResourceProvider
import co.edu.adopcionmascota.R
import co.edu.adopcionmascota.domain.usecase.auth.StartSessionUseCase
import kotlinx.coroutines.launch
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.SavedStateHandle
import javax.inject.Inject
import dagger.hilt.android.lifecycle.HiltViewModel
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class RegisterSuccessUiState(
    val name: String = "",
    val points: Int = 0,
    val levelName: String = "",
    val enabledFeatures: String = ""
) {
    // "¡Bienvenida a la Manada, Camila!" usa solo el primer nombre
    val firstName: String
        get() = name.trim().substringBefore(" ")
}

@HiltViewModel
class RegisterSuccessViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val startSessionUseCase: StartSessionUseCase,
    resources: ResourceProvider
) : ViewModel() {

    private val userId: String = checkNotNull(savedStateHandle["userId"])

    private val _uiState = MutableStateFlow(
        RegisterSuccessUiState(
            name = checkNotNull(savedStateHandle["name"]),
            levelName = resources.getString(R.string.level_1_name),
            enabledFeatures = resources.getString(R.string.enabled_features)
        )
    )
    val uiState: StateFlow<RegisterSuccessUiState> = _uiState.asStateFlow()

    // Al guardarse la sesión, AppNavigation pasa sola a la navegación principal
    fun startSession() {
        viewModelScope.launch { startSessionUseCase(userId) }
    }
}
