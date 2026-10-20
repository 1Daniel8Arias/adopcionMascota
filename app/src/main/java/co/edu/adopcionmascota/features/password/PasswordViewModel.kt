package co.edu.adopcionmascota.features.password

import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.edu.adopcionmascota.core.util.RequestResult
import kotlinx.coroutines.delay
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

class PasswordViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(PasswordUiState())
    val uiState: StateFlow<PasswordUiState> = _uiState.asStateFlow()

    private val registeredEmails = listOf(
        "daniel@email.com",
        "camila.morales@ejemplo.com",
        "usuario@bogota.gov.co"
    )


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
            email.isBlank() -> "El correo electrónico no puede estar vacío"
            !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> "Ingresa un correo electrónico válido"
            else -> null
        }
    }

    fun sendRecoveryEmail() {
        if (!_uiState.value.isFormValid) return

        viewModelScope.launch {
            // 1. Cambia el estado a Carga (Loading)
            _uiState.update { it.copy(recoveryResult = RequestResult.Loading) }

            // 2. Simula el tiempo que tarda la consulta a la base de datos o servidor
            delay(1500)

            val inputEmail = _uiState.value.email.trim()


            val emailExists = registeredEmails.any { it.equals(inputEmail, ignoreCase = true) }

            val result = if (emailExists) {
                RequestResult.Success("¡Enlace enviado! Revisa tu bandeja de entrada.")
            } else {
                RequestResult.Failure("El correo no se encuentra registrado en nuestro sistema.")
            }

            // 3. Se emite el resultado final hacia la UI[cite: 1, 3]
            _uiState.update { it.copy(recoveryResult = result) }
        }
    }

    fun resetResult() {
        _uiState.update { it.copy(recoveryResult = null) }
    }




}



