package co.edu.adopcionmascota.features.profile.edit

import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.edu.adopcionmascota.core.util.RequestResult
import co.edu.adopcionmascota.features.profile.data.UserProfile
import co.edu.adopcionmascota.features.profile.data.UserProfileStore
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// Todo el estado de la pantalla en un solo objeto inmutable
data class EditProfileUiState(
    // Datos de contacto e identidad
    val photoUrl: String = "",
    val levelName: String = "",
    val name: String = "",
    val email: String = "",
    val isEmailVerified: Boolean = false,
    val phone: String = "",
    val locality: String = "",
    val neighborhood: String = "",
    val bio: String = "",

    // Errores de validación
    val nameError: String? = null,
    val emailError: String? = null,
    val phoneError: String? = null,
    val localityError: String? = null,

    // Preferencias de voluntariado
    val isTemporaryHome: Boolean = false,
    val lostPetAlerts: Boolean = false,
    val showPublicPhone: Boolean = false,

    // Diálogos
    val showExitDialog: Boolean = false,
    val showDeactivateDialog: Boolean = false,

    // Mensaje informativo para opciones que aún no están disponibles
    val infoMessage: String? = null,

    // Resultados de las solicitudes
    val saveResult: RequestResult? = null,
    val deactivateResult: RequestResult? = null
) {
    // Propiedad calculada: la pantalla no tiene que repetir esta lógica
    val isFormValid: Boolean
        get() = name.isNotBlank() &&
                email.isNotBlank() &&
                phone.isNotBlank() &&
                locality.isNotBlank() &&
                nameError == null &&
                emailError == null &&
                phoneError == null &&
                localityError == null

    val bioLength: Int
        get() = bio.length

    // Cada campo cuenta como completado si tiene valor y no tiene errores
    val isNameValid: Boolean get() = name.isNotBlank() && nameError == null
    val isEmailValid: Boolean get() = email.isNotBlank() && emailError == null
    val isPhoneValid: Boolean get() = phone.isNotBlank() && phoneError == null
    val isLocalityValid: Boolean get() = locality.isNotBlank() && localityError == null
    val isNeighborhoodValid: Boolean get() = neighborhood.isNotBlank()
    val isBioValid: Boolean get() = bio.isNotBlank()

    val completedFields: Int
        get() = listOf(
            isNameValid, isEmailValid, isPhoneValid,
            isLocalityValid, isNeighborhoodValid, isBioValid
        ).count { it }
}

// Número de campos que se muestran en el contador "x de 6 completados"
const val TOTAL_PROFILE_FIELDS = 6

// Longitud máxima de la presentación comunitaria
const val BIO_MAX_LENGTH = 250

class EditProfileViewModel : ViewModel() {

    // Un único flujo con el estado completo de la pantalla
    private val _uiState = MutableStateFlow(EditProfileUiState())
    val uiState: StateFlow<EditProfileUiState> = _uiState.asStateFlow()

    // La lista de localidades no cambia nunca, así que no hace parte del UI State
    val localities = listOf(
        "Usaquén (Zona Norte)",
        "Chapinero",
        "Santa Fe",
        "Suba",
        "Engativá",
        "Fontibón",
        "Kennedy",
        "Teusaquillo"
    )

    init {
        loadProfile()
    }

    // Los datos se leen del almacén compartido con la pantalla de perfil
    private fun loadProfile() {
        val profile = UserProfileStore.profile.value
        _uiState.value = EditProfileUiState(
            photoUrl = profile.photoUrl,
            levelName = "Nivel 2 · Protector Comunitario",
            name = profile.name,
            email = profile.email,
            isEmailVerified = profile.isEmailVerified,
            phone = profile.phone,
            locality = profile.locality,
            neighborhood = profile.neighborhood,
            bio = profile.bio,
            isTemporaryHome = profile.isTemporaryHome,
            lostPetAlerts = profile.lostPetAlerts,
            showPublicPhone = profile.showPublicPhone
        )
    }

    // ---------- Campos del formulario ----------

    fun onNameChange(newName: String) {
        _uiState.update { state ->
            state.copy(
                name = newName,
                nameError = validateName(newName)
            )
        }
    }

    fun onEmailChange(newEmail: String) {
        _uiState.update { state ->
            state.copy(
                email = newEmail,
                emailError = validateEmail(newEmail),
                // Si cambia el correo, deja de estar verificado
                isEmailVerified = false
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

    fun onLocalityChange(newLocality: String) {
        _uiState.update { state ->
            state.copy(
                locality = newLocality,
                localityError = validateLocality(newLocality)
            )
        }
    }

    fun onNeighborhoodChange(newNeighborhood: String) {
        _uiState.update { it.copy(neighborhood = newNeighborhood) }
    }

    fun onBioChange(newBio: String) {
        // No se aceptan más caracteres que el máximo permitido
        if (newBio.length <= BIO_MAX_LENGTH) {
            _uiState.update { it.copy(bio = newBio) }
        }
    }

    // ---------- Preferencias de voluntariado ----------

    fun onTemporaryHomeChange(enabled: Boolean) {
        _uiState.update { it.copy(isTemporaryHome = enabled) }
    }

    fun onLostPetAlertsChange(enabled: Boolean) {
        _uiState.update { it.copy(lostPetAlerts = enabled) }
    }

    fun onShowPublicPhoneChange(enabled: Boolean) {
        _uiState.update { it.copy(showPublicPhone = enabled) }
    }

    // ---------- Guardar cambios ----------

    fun saveChanges() {
        // Si el formulario no es válido, no se hace nada
        if (!_uiState.value.isFormValid) return

        viewModelScope.launch {
            _uiState.update { it.copy(saveResult = RequestResult.Loading) }
            delay(1500) // Simula el tiempo que tardaría guardar en el servidor
            val state = _uiState.value
            UserProfileStore.save(
                UserProfile(
                    photoUrl = state.photoUrl,
                    name = state.name.trim(),
                    email = state.email.trim(),
                    isEmailVerified = state.isEmailVerified,
                    phone = state.phone.trim(),
                    locality = state.locality,
                    neighborhood = state.neighborhood.trim(),
                    bio = state.bio.trim(),
                    isTemporaryHome = state.isTemporaryHome,
                    lostPetAlerts = state.lostPetAlerts,
                    showPublicPhone = state.showPublicPhone
                )
            )
            _uiState.update {
                it.copy(saveResult = RequestResult.Success("Cambios guardados correctamente"))
            }
        }
    }

    fun resetSaveResult() {
        _uiState.update { it.copy(saveResult = null) }
    }

    // ---------- Descartar cambios ----------

    // El usuario presionó "Descartar", "Cancelar y descartar" o el botón "atrás"
    fun onDiscardClick() {
        _uiState.update { it.copy(showExitDialog = true) }
    }

    // El usuario confirmó que quiere salir sin guardar
    fun onConfirmDiscard() {
        // Se vuelven a cargar los datos originales y se oculta el diálogo
        loadProfile()
    }

    // El usuario canceló o cerró el diálogo de salida
    fun onDismissExitDialog() {
        _uiState.update { it.copy(showExitDialog = false) }
    }

    // ---------- Desactivar cuenta ----------

    fun onDeactivateClick() {
        _uiState.update { it.copy(showDeactivateDialog = true) }
    }

    fun onDismissDeactivateDialog() {
        _uiState.update { it.copy(showDeactivateDialog = false) }
    }

    fun onConfirmDeactivate() {
        _uiState.update { it.copy(showDeactivateDialog = false) }
        deactivateAccount()
    }

    private fun deactivateAccount() {
        viewModelScope.launch {
            _uiState.update { it.copy(deactivateResult = RequestResult.Loading) }
            delay(1500) // Simula la petición al servidor
            _uiState.update {
                it.copy(deactivateResult = RequestResult.Success("Cuenta desactivada temporalmente"))
            }
        }
    }

    fun resetDeactivateResult() {
        _uiState.update { it.copy(deactivateResult = null) }
    }

    // ---------- Opciones aún no disponibles ----------

    // La cámara y la galería todavía no hacen parte del alcance del proyecto
    fun onChangePhotoClick() {
        _uiState.update {
            it.copy(infoMessage = "El cambio de fotografía estará disponible cuando se integre la cámara")
        }
    }

    fun onNotificationPreferencesClick() {
        _uiState.update {
            it.copy(infoMessage = "Las preferencias de notificaciones estarán disponibles próximamente")
        }
    }

    fun onHelpClick() {
        _uiState.update {
            it.copy(infoMessage = "Edita tus datos y presiona \"Guardar Cambios\" para conservarlos")
        }
    }

    // Permite limpiar el mensaje después de mostrarlo en pantalla
    fun resetInfoMessage() {
        _uiState.update { it.copy(infoMessage = null) }
    }

    // ---------- Validaciones ----------

    private fun validateName(name: String): String? {
        return when {
            name.isBlank() -> "El nombre es obligatorio"
            name.length < 3 -> "El nombre debe tener al menos 3 caracteres"
            else -> null
        }
    }

    private fun validateEmail(email: String): String? {
        return when {
            email.isBlank() -> "El email es obligatorio"
            !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> "Ingresa un email válido"
            else -> null
        }
    }

    private fun validatePhone(phone: String): String? {
        // Se cuentan solo los dígitos, así se permiten espacios y el prefijo +57
        val digits = phone.count { it.isDigit() }
        return when {
            phone.isBlank() -> "El teléfono es obligatorio"
            phone.any { !it.isDigit() && it != ' ' && it != '+' } -> "Usa solo números, espacios y +"
            digits != 10 && digits != 12 -> "Ingresa un número de 10 dígitos (puedes anteponer +57)"
            else -> null
        }
    }

    private fun validateLocality(locality: String): String? {
        return if (locality.isBlank()) "Selecciona una localidad" else null
    }
}
