package co.edu.adopcionmascota.features.password

import javax.inject.Inject
import co.edu.adopcionmascota.domain.exception.UserNotFoundException
import co.edu.adopcionmascota.domain.usecase.auth.SendPasswordResetUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import co.edu.adopcionmascota.core.util.ResourceProvider
import co.edu.adopcionmascota.R
import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.edu.adopcionmascota.core.util.RequestResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class PasswordUiState(
    val email: String = "",
    val emailError: String? = null,
    val recoveryResult: RequestResult? = null
) {

    val isFormValid: Boolean
        get() = email.isNotBlank() && emailError == null
}

@HiltViewModel
class PasswordViewModel @Inject constructor(
    private val sendPasswordResetUseCase: SendPasswordResetUseCase,
    private val resources: ResourceProvider
) : ViewModel() {

    private val _uiState = MutableStateFlow(PasswordUiState())
    val uiState: StateFlow<PasswordUiState> = _uiState.asStateFlow()



    fun onEmailChange(newEmail: String) {
        _uiState.update { state ->
            state.copy(
                email = newEmail,
                emailError = validateEmail(newEmail)
            )
        }
    }


    private fun validateEmail(email: String): String? {
        return when {
            email.isBlank() -> resources.getString(R.string.error_recovery_email_empty)
            !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> resources.getString(R.string.error_recovery_email_invalid)
            else -> null
        }
    }

    fun sendRecoveryEmail() {
        if (!_uiState.value.isFormValid) return

        viewModelScope.launch {
            // 1. Cambia el estado a Carga (Loading)
            _uiState.update { it.copy(recoveryResult = RequestResult.Loading) }

            // 2. Firebase Authentication envía el correo real de recuperación
            val result = try {
                sendPasswordResetUseCase(_uiState.value.email)
                RequestResult.Success(resources.getString(R.string.recovery_sent))
            } catch (e: UserNotFoundException) {
                RequestResult.Failure(resources.getString(R.string.recovery_not_found))
            } catch (e: Exception) {
                RequestResult.Failure(resources.getString(R.string.network_error))
            }
            // 3. Se emite el resultado final hacia la UI[cite: 1, 3]
            _uiState.update { it.copy(recoveryResult = result) }
        }
    }

    fun resetResult() {
        _uiState.update { it.copy(recoveryResult = null) }
    }




}



