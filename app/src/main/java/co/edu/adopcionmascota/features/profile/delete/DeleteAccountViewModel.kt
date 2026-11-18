package co.edu.adopcionmascota.features.profile.delete

import co.edu.adopcionmascota.R
import javax.inject.Inject
import dagger.hilt.android.lifecycle.HiltViewModel
import co.edu.adopcionmascota.core.util.ResourceProvider
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.edu.adopcionmascota.core.util.RequestResult
import co.edu.adopcionmascota.domain.usecase.user.DeleteAccountUseCase
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

@HiltViewModel
class DeleteAccountViewModel @Inject constructor(
    private val deleteAccountUseCase: DeleteAccountUseCase,
    private val resources: ResourceProvider
) : ViewModel() {

    private val _uiState = MutableStateFlow(DeleteAccountUiState())
    val uiState: StateFlow<DeleteAccountUiState> = _uiState.asStateFlow()

    fun onConfirmedChange(checked: Boolean) {
        _uiState.update { it.copy(isConfirmed = checked) }
    }

    fun deleteAccount() {
        if (!_uiState.value.canDelete) return

        viewModelScope.launch {
            _uiState.update { it.copy(deleteResult = RequestResult.Loading) }
            // El usuario se borra de Room; la sesión se cierra en la pantalla de despedida
            val result = runCatching { deleteAccountUseCase() }.fold(
                onSuccess = { RequestResult.Success(resources.getString(R.string.account_deleted_ok)) },
                onFailure = { RequestResult.Failure(resources.getString(R.string.network_error)) }
            )
            _uiState.update { it.copy(deleteResult = result) }
        }
    }

    fun resetDeleteResult() {
        _uiState.update { it.copy(deleteResult = null) }
    }
}
