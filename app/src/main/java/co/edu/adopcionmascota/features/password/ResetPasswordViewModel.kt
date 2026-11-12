package co.edu.adopcionmascota.features.password

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.edu.adopcionmascota.R
import co.edu.adopcionmascota.core.util.RequestResult
import co.edu.adopcionmascota.core.util.ResourceProvider
import co.edu.adopcionmascota.domain.exception.InvalidResetCodeException
import co.edu.adopcionmascota.domain.usecase.auth.ConfirmPasswordResetUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ResetPasswordUiState(
    val password: String = "",
    val confirmPassword: String = "",
    val passwordError: String? = null,
    val confirmPasswordError: String? = null,
    val result: RequestResult? = null
) {
    val isFormValid: Boolean
        get() = password.isNotBlank() && confirmPassword.isNotBlank() &&
            passwordError == null && confirmPasswordError == null
}

// Guía 20, actividad 2: nueva contraseña con el código (oobCode) del enlace del correo
@HiltViewModel
class ResetPasswordViewModel @Inject constructor(
    private val confirmPasswordResetUseCase: ConfirmPasswordResetUseCase,
    private val resources: ResourceProvider
) : ViewModel() {

    private val _uiState = MutableStateFlow(ResetPasswordUiState())
    val uiState: StateFlow<ResetPasswordUiState> = _uiState.asStateFlow()

    fun onPasswordChange(value: String) = _uiState.update {
        it.copy(
            password = value,
            passwordError = validatePassword(value),
            confirmPasswordError = if (it.confirmPassword.isEmpty()) null else validateConfirm(value, it.confirmPassword)
        )
    }

    fun onConfirmPasswordChange(value: String) = _uiState.update {
        it.copy(confirmPassword = value, confirmPasswordError = validateConfirm(it.password, value))
    }

    private fun validatePassword(password: String): String? = when {
        password.isBlank() -> resources.getString(R.string.error_password_empty)
        password.length < 6 -> resources.getString(R.string.error_password_short)
        password.length > 15 -> resources.getString(R.string.error_password_long)
        else -> null
    }

    private fun validateConfirm(password: String, confirm: String): String? =
        if (confirm != password) resources.getString(R.string.error_password_mismatch) else null

    fun confirm(code: String) {
        val state = _uiState.value
        if (!state.isFormValid) return
        viewModelScope.launch {
            _uiState.update { it.copy(result = RequestResult.Loading) }
            val result = runCatching { confirmPasswordResetUseCase(code, state.password) }.fold(
                onSuccess = { RequestResult.Success(resources.getString(R.string.reset_password_success)) },
                onFailure = { error ->
                    RequestResult.Failure(
                        if (error is InvalidResetCodeException) resources.getString(R.string.reset_password_invalid_code)
                        else resources.getString(R.string.network_error)
                    )
                }
            )
            _uiState.update { it.copy(result = result) }
        }
    }
}
