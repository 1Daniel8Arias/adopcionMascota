package co.edu.adopcionmascota.features.register
import co.edu.adopcionmascota.core.util.ResourceProvider
import co.edu.adopcionmascota.R
import co.edu.adopcionmascota.domain.usecase.user.RegisterUserUseCase
import co.edu.adopcionmascota.domain.model.User
import java.util.UUID
import javax.inject.Inject
import dagger.hilt.android.lifecycle.HiltViewModel
import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.edu.adopcionmascota.core.util.RequestResult
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
    val showHelpDialog: Boolean = false,
    val registeredUserId: String = "",
    val photoUri: String = "",


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

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val registerUserUseCase: RegisterUserUseCase,
    private val resources: ResourceProvider
) : ViewModel() {
    private val _uiState= MutableStateFlow(value = RegisterUiState())

    val uiState: StateFlow<RegisterUiState> = _uiState.asStateFlow()

    val cities = listOf("Circasia","Armenia","Filandia")

    // Imagen elegida en la galería
    fun onPhotoSelected(uri: String) {
        _uiState.update { it.copy(photoUri = uri) }
    }

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

    fun onHelpClick(){
        _uiState.update { it.copy(showHelpDialog = true) }
    }

    fun onDismissHelpDialog(){
        _uiState.update { it.copy(showHelpDialog = false) }
    }

    fun resetRegisterResult(){
        _uiState.update { it.copy(registerResult = null) }
    }


    private fun register(){
        if(!_uiState.value.isFormValid)return

        viewModelScope.launch {
            _uiState.update { it.copy(registerResult = RequestResult.Loading) }
            val state = _uiState.value
            val user = User(
                id = UUID.randomUUID().toString(),
                name = state.name.trim(),
                city = state.city,
                address = state.address.trim(),
                email = state.email.trim(),
                password = state.password,
                phoneNumber = state.phone,
                profilePictureUrl = state.photoUri
            )
            try {
                val userId = registerUserUseCase(user)
                _uiState.update {
                    it.copy(registeredUserId = userId, registerResult = RequestResult.Success(resources.getString(R.string.register_success)))
                }
            } catch (e: IllegalArgumentException) {
                _uiState.update { it.copy(registerResult = RequestResult.Failure(resources.getString(R.string.register_duplicate))) }
            } catch (e: Exception) {
                // Errores de red o de Firebase: la app no se cierra, se muestra el mensaje
                _uiState.update { it.copy(registerResult = RequestResult.Failure(resources.getString(R.string.network_error))) }
            }
        }
    }
    private fun validateName(name: String): String?{
        return when{
            name.isBlank()-> resources.getString(R.string.error_name_empty)
            name.length< 3-> resources.getString(R.string.error_name_short)
            else->null
        }
    }

    private fun validateEmail(email: String): String?{
        return when{
            email.isBlank()-> resources.getString(R.string.error_email_empty)
            !Patterns.EMAIL_ADDRESS.matcher(email).matches()->resources.getString(R.string.error_email_invalid_register)
            else->null
        }
    }

    private fun validatePhone(phone: String): String?{
        return when {
            phone.isBlank()-> resources.getString(R.string.error_phone_empty)
            phone.length !in 10..10 -> resources.getString(R.string.error_phone_invalid)
            else->null
        }

    }

    private fun validatePassword(password: String): String?{
        return when{
            password.isBlank()->resources.getString(R.string.error_password_empty)
            password.length<6 ->resources.getString(R.string.error_password_short)
            password.length>15 ->resources.getString(R.string.error_password_long)
            else->null
        }
    }

    private fun validateConfirmPassword(password: String,confirmPassword: String): String?{
        return when{
            confirmPassword.isBlank()->resources.getString(R.string.confirm_password_label)
            confirmPassword!=password ->resources.getString(R.string.error_password_mismatch)
            else->null
        }
    }
    private fun validateCity(city: String): String? {
        return if (city.isBlank()) resources.getString(R.string.error_city) else null
    }

    private fun validateAddress(address: String): String? {
        return if (address.isBlank()) resources.getString(R.string.error_address) else null
    }


}