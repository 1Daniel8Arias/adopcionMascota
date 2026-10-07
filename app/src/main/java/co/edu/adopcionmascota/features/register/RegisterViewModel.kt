package co.edu.adopcionmascota.features.register
import androidx.lifecycle.ViewModel
import co.edu.adopcionmascota.core.util.RequestResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update


data class RegisterUiState(
    val name: String = "",
    val city: String = "",
    val address: String = "",
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val nameError: String? = null,
    val cityError: String? = null,
    val addressError: String? = null,
    val emailError: String? = null,
    val passwordError: String? = null,
    val confirmPasswordError: String? = null,
    val showConfirmDialog: Boolean = false,
    val showExitDialog: Boolean = false,
    val registerResult: RequestResult? = null
) {
    val isFormValid: Boolean
        get() = name.isNotBlank() &&
                city.isNotBlank() &&
                address.isNotBlank() &&
                email.isNotBlank() &&
                password.isNotBlank() &&
                confirmPassword.isNotBlank() &&
                nameError == null &&
                cityError == null &&
                addressError == null &&
                emailError == null &&
                passwordError == null &&
                confirmPasswordError == null
}

class RegisterViewModel: ViewModel(){
    private val _uiState= MutableStateFlow(value = RegisterUiState())

    val uiState: StateFlow<RegisterUiState> = _uiState.asStateFlow()

    val cities = listOf("Circasia","Armenia","Filandia")

    fun onNameChange(newName: String){
        _uiState.update { state ->
            state.copy(
                name = newName,
                nameError = validateName(newName)
            )
        }
    }

    private fun validateNamr(name: String): String?{
        return when{
            name.isBlank()-> "El nombre es obligatorio"
            name.length< 3-> ""
        }
    }
}