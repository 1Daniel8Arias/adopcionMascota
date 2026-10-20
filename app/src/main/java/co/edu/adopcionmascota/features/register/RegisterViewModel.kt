package co.edu.adopcionmascota.features.register
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
    val phoneError: String? = null,
    val passwordError: String? = null,
    val confirmPasswordError: String? = null,
    val showConfirmDialog: Boolean = false,
    val showExitDialog: Boolean = false,
    val registerResult: RequestResult? = null,
    val phone: String = "",
    val acceptTerms: Boolean = false,
    val receiveNotifications: Boolean = false,


    ) {
    val isFormValid: Boolean
        get() = name.isNotBlank() &&
                city.isNotBlank() &&
                address.isNotBlank() &&
                email.isNotBlank() &&
                password.isNotBlank() &&
                confirmPassword.isNotBlank() &&
                acceptTerms &&
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

    fun onCityChange(newCity: String) {
        _uiState.update { state ->
            state.copy(
                city = newCity,
                cityError = validateCity(newCity)
            )
        }
    }

    fun onAddressChange(newAddress: String) {
        _uiState.update { state ->
            state.copy(
                address = newAddress,
                addressError = validateAddress(newAddress)
            )
        }
    }

    fun onEmailChange(newEmail: String){
        _uiState.update{ state ->
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
                passwordError = validatePassword(newPassword),

                confirmPasswordError = if (state.confirmPassword.isEmpty()) state.confirmPasswordError
                else validateConfirmPassword(newPassword,state.confirmPassword)
            )

        }
    }

    fun onConfirmPasswordChange(newConfirmPassword: String){
        _uiState.update { state ->
            state.copy(
                confirmPassword = newConfirmPassword,
                confirmPasswordError = validateConfirmPassword(state.password,newConfirmPassword)
            )
        }
    }

    fun onPhoneChange(newPhone: String) {

        _uiState.update { state ->

            state.copy(
                phone = newPhone,
                phoneError = validatePhone(newPhone)

            )
        }
    }

    fun onReceiveNotificationsChange(
        receive: Boolean
    ) {

        _uiState.update { state ->

            state.copy(
                receiveNotifications = receive
            )
        }
    }
    fun onAcceptTermsChange(
        accepted: Boolean
    ) {

        _uiState.update { state ->

            state.copy(
                acceptTerms = accepted
            )
        }
    }



    fun onRegisterClick(){
        if(!_uiState.value.isFormValid)return
        _uiState.update {
            it.copy(showConfirmDialog = true)
        }
    }

    fun onConfirmRegister(){
        _uiState.update { it.copy(showConfirmDialog = false) }
        register()
    }

    fun onDismissConfirmDialog(){
        _uiState.update { it.copy(showConfirmDialog = false) }
    }

    fun onBackClick(){
        _uiState.update { it.copy(showExitDialog  = true) }
    }

    fun onConfirmExit(){
        _uiState.value= RegisterUiState()
    }

    fun onDismissExitDialog(){
        _uiState.update { it.copy(showExitDialog = false) }
    }

    fun resetRegisterResult(){
        _uiState.update { it.copy(registerResult = null) }
    }


    private fun register(){
        if(!_uiState.value.isFormValid)return

        viewModelScope.launch {
            _uiState.update { it.copy(registerResult = RequestResult.Loading) }
            delay(2000)
            _uiState.update { it.copy(registerResult = RequestResult.Success("Registro exitoso")) }
        }
    }
    private fun validateName(name: String): String?{
        return when{
            name.isBlank()-> "El nombre es obligatorio"
            name.length< 3-> "El nombre debe tener al menos 3 caracteres"
            else->null
        }
    }

    private fun validateEmail(email: String): String?{
        return when{
            email.isBlank()-> "El email es obligatorio"
            !Patterns.EMAIL_ADDRESS.matcher(email).matches()->"Ingrese un email valido"
            else->null
        }
    }

    private fun validatePhone(phone: String): String?{
        return when {
            phone.isBlank()-> "El telefono es obligatorio"
            phone.length !in 10..10 -> "Numero de telefono no valido"
            else->null
        }

    }

    private fun validatePassword(password: String): String?{
        return when{
            password.isBlank()->"La contraseña es obligatoria"
            password.length<6 ->"La contraseña debe tener al menos 6 caracteres"
            password.length>15 ->"La contraseña debe ser menor a 15 caracteres"
            else->null
        }
    }

    private fun validateConfirmPassword(password: String,confirmPassword: String): String?{
        return when{
            confirmPassword.isBlank()->"Confirmar contraseña"
            confirmPassword!=password ->"Las contraseñas no coinciden"
            else->null
        }
    }
    private fun validateCity(city: String): String? {
        return if (city.isBlank()) "Selecciona una ciudad" else null
    }

    private fun validateAddress(address: String): String? {
        return if (address.isBlank()) "La dirección es obligatoria" else null
    }


}