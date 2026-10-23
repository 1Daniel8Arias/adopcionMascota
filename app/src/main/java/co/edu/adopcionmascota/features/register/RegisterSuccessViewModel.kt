package co.edu.adopcionmascota.features.register

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class RegisterSuccessUiState(
    val name: String = "",
    val points: Int = 0,
    val levelName: String = "Nivel 1 · Amigo Animal",
    val enabledFeatures: String = "Habilitada: Adopciones, reportes y comentarios"
) {
    // "¡Bienvenida a la Manada, Camila!" usa solo el primer nombre
    val firstName: String
        get() = name.trim().substringBefore(" ").ifBlank { "amig@" }
}

class RegisterSuccessViewModel(name: String) : ViewModel() {

    private val _uiState = MutableStateFlow(RegisterSuccessUiState(name = name))
    val uiState: StateFlow<RegisterSuccessUiState> = _uiState.asStateFlow()
}
