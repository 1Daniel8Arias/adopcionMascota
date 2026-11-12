package co.edu.adopcionmascota.features.login

import co.edu.adopcionmascota.core.util.ResourceProvider
import co.edu.adopcionmascota.R
import android.util.Patterns
import androidx.lifecycle.ViewModel
import co.edu.adopcionmascota.core.util.RequestResult
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import co.edu.adopcionmascota.domain.usecase.auth.LoginUserUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import co.edu.adopcionmascota.domain.exception.EmailNotVerifiedException
import kotlinx.coroutines.launch


// Todo el estado de la pantalla en un solo objeto inmutable
data class LoginUiState(
    val email: String = "",
    val password: String = "",
    val emailError: String? = null,
    val passwordError: String? = null,
    val loginResult: RequestResult? = null
) {
    // Propiedad calculada: la pantalla no tiene que repetir esta lógica
    val isFormValid: Boolean
        get() = email.isNotBlank() &&
                password.isNotBlank() &&
                emailError == null &&
                passwordError == null
}

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginUserUseCase: LoginUserUseCase,
    private val resources: ResourceProvider
)   : ViewModel() {

    // Un único flujo con el estado completo de la pantalla
    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    fun onEmailChange(newEmail: String) {
        _uiState.update { state ->
            state.copy(
                email = newEmail,
                emailError = validateEmail(newEmail)
            )
        }
    }

    fun onPasswordChange(newPassword: String) {
        _uiState.update { state ->
            state.copy(
                password = newPassword,
                passwordError = validatePassword(newPassword)
            )
        }
    }

    // Es útil para limpiar el formulario después de un login exitoso
    fun resetForm() {
        _uiState.value = LoginUiState()
    }

    private fun validateEmail(email: String): String? {
        return when {
            email.isBlank() -> resources.getString(R.string.error_email_empty)
            !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> resources.getString(R.string.error_email_invalid)
            else -> null
        }
    }

    private fun validatePassword(password: String): String? {
        return when {
            password.isBlank() -> resources.getString(R.string.error_password_empty)
            password.length < 6 -> resources.getString(R.string.error_password_short)
            else -> null
        }
    }

    fun login() {


        val currentState= _uiState.value
        if(!currentState.isFormValid)return
        // Si el formulario no es válido, no se hace nada
        if (!_uiState.value.isFormValid) return

        // viewModelScope es una corrutina atada al ciclo de vida del ViewModel
        viewModelScope.launch {

            // La solicitud pasa al estado de carga
            _uiState.update { it.copy(loginResult = RequestResult.Loading) }

            // El caso de uso autentica y, si tiene éxito, guarda la sesión (guías 19 y 20)
            val result = runCatching { loginUserUseCase(currentState.email.trim(), currentState.password) }
                .fold(
                    onSuccess = { user ->
                        // Sin excepción no significa credenciales válidas: hay que revisar el usuario
                        if (user != null) {
                            RequestResult.Success(resources.getString(R.string.login_success))
                        } else {
                            RequestResult.Failure(resources.getString(R.string.login_failure))
                        }
                    },
                    onFailure = { error ->
                        RequestResult.Failure(
                            if (error is EmailNotVerifiedException) resources.getString(R.string.login_email_not_verified)
                            else resources.getString(R.string.login_error)
                        )
                    }
                )
            _uiState.update { it.copy(loginResult = result) }
        }
    }

    // Permite limpiar el resultado después de mostrarlo en pantalla
    fun resetLoginResult() {
        _uiState.update { it.copy(loginResult = null) }
    }

    }