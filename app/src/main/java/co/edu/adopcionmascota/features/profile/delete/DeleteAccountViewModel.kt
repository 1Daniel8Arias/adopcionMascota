package co.edu.adopcionmascota.features.profile.delete

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.edu.adopcionmascota.core.util.RequestResult
import co.edu.adopcionmascota.features.profile.UserProfileStore
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class DeleteAccountUiState(
    // El usuario debe marcar la casilla "Comprendo que perderé..." para poder eliminar
    val isConfirmed: Boolean = false,
    val deleteResult: RequestResult? = null
) {
    val canDelete: Boolean
        get() = isConfirmed && deleteResult !is RequestResult.Loading
}

class DeleteAccountViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(DeleteAccountUiState())
    val uiState: StateFlow<DeleteAccountUiState> = _uiState.asStateFlow()

    fun onConfirmedChange(checked: Boolean) {
        _uiState.update { it.copy(isConfirmed = checked) }
    }

    fun deleteAccount() {
        if (!_uiState.value.canDelete) return

        viewModelScope.launch {
            _uiState.update { it.copy(deleteResult = RequestResult.Loading) }
            delay(1500) // Simula la petición al servidor
            UserProfileStore.clear()
            _uiState.update {
                it.copy(deleteResult = RequestResult.Success("Cuenta eliminada correctamente"))
            }
        }
    }

    fun resetDeleteResult() {
        _uiState.update { it.copy(deleteResult = null) }
    }
}
