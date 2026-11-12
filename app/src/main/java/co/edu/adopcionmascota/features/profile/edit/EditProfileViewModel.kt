package co.edu.adopcionmascota.features.profile.edit

import co.edu.adopcionmascota.R
import javax.inject.Inject
import dagger.hilt.android.lifecycle.HiltViewModel
import co.edu.adopcionmascota.core.util.ResourceProvider
import co.edu.adopcionmascota.domain.model.User
import co.edu.adopcionmascota.domain.usecase.user.GetCurrentUserUseCase
import co.edu.adopcionmascota.domain.usecase.user.UpdateUserUseCase
import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.edu.adopcionmascota.core.util.RequestResult
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
    val deactivateResult: RequestResult? = null,

    // Cambios aplicados en la última edición, para mostrarlos en "Perfil editado"
    val changes: List<String> = emptyList()
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

@HiltViewModel
class EditProfileViewModel @Inject constructor(
    private val getCurrentUserUseCase: GetCurrentUserUseCase,
    private val updateUserUseCase: UpdateUserUseCase,
    private val resources: ResourceProvider
) : ViewModel() {

    // Usuario tal como está guardado en Room, para comparar los cambios
    private var original: User? = null

    // Un único flujo con el estado completo de la pantalla
    private val _uiState = MutableStateFlow(EditProfileUiState())
    val uiState: StateFlow<EditProfileUiState> = _uiState.asStateFlow()

    // La lista de ciudades no cambia nunca, así que no hace parte del UI State
    val localities = listOf(
        "Armenia",
        "Calarcá",
        "Circasia",
        "Filandia",
        "Montenegro",
        "Salento",
        "Pereira",
        "Manizales"
    )

    init {
        loadProfile()
    }

    // Los datos se leen del usuario de la sesión guardado en Room
    private fun loadProfile() {
        viewModelScope.launch {
            val user = getCurrentUserUseCase() ?: return@launch
            original = user
            _uiState.value = EditProfileUiState(
                photoUrl = user.profilePictureUrl,
                levelName = "Nivel ${user.level.number} · ${user.level.displayName}",
                name = user.name,
                email = user.email,
                isEmailVerified = true,
                phone = user.phoneNumber,
                locality = user.city,
                neighborhood = user.address,
                bio = user.bio,
                isTemporaryHome = user.isTemporaryHome,
                lostPetAlerts = user.lostPetAlerts,
                showPublicPhone = user.showPublicPhone
            )
        }
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
            val old = original ?: return@launch
            _uiState.update { it.copy(saveResult = RequestResult.Loading) }
            val state = _uiState.value
            val updated = old.copy(
                profilePictureUrl = state.photoUrl,
                name = state.name.trim(),
                email = state.email.trim(),
                phoneNumber = state.phone.trim(),
                city = state.locality,
                address = state.neighborhood.trim(),
                bio = state.bio.trim(),
                isTemporaryHome = state.isTemporaryHome,
                lostPetAlerts = state.lostPetAlerts,
                showPublicPhone = state.showPublicPhone
            )
            try {
                updateUserUseCase(updated)
                original = updated
                _uiState.update {
                    it.copy(
                        changes = describeChanges(old, updated),
                        saveResult = RequestResult.Success(resources.getString(R.string.changes_saved))
                    )
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(saveResult = RequestResult.Failure(e.message.orEmpty())) }
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
            _uiState.update {
                it.copy(deactivateResult = RequestResult.Success(resources.getString(R.string.account_deactivated)))
            }
        }
    }

    fun resetDeactivateResult() {
        _uiState.update { it.copy(deactivateResult = null) }
    }

    // ---------- Opciones aún no disponibles ----------

    // Imagen elegida en la galería; se guarda con "Guardar Cambios"
    fun onPhotoSelected(uri: String) {
        _uiState.update { it.copy(photoUrl = uri) }
    }

    fun onNotificationPreferencesClick() {
        _uiState.update {
            it.copy(infoMessage = resources.getString(R.string.notif_pref_soon))
        }
    }

    fun onHelpClick() {
        _uiState.update {
            it.copy(infoMessage = resources.getString(R.string.edit_help))
        }
    }

    // Permite limpiar el mensaje después de mostrarlo en pantalla
    fun resetInfoMessage() {
        _uiState.update { it.copy(infoMessage = null) }
    }

    // Lista de cambios que se muestra en "Perfil editado"
    private fun describeChanges(old: User, new: User): List<String> {
        val changes = mutableListOf<String>()
        if (old.name != new.name) changes.add(resources.getString(R.string.change_name, new.name))
        if (old.email != new.email) changes.add(resources.getString(R.string.change_email, new.email))
        if (old.phoneNumber != new.phoneNumber) changes.add(resources.getString(R.string.change_phone, new.phoneNumber))
        if (old.city != new.city || old.address != new.address) {
            changes.add(resources.getString(R.string.change_location, new.address.ifBlank { new.city }, new.city))
        }
        if (old.isTemporaryHome != new.isTemporaryHome) {
            val value = resources.getString(if (new.isTemporaryHome) R.string.level_active else R.string.inactive)
            changes.add(resources.getString(R.string.change_foster, value))
        }
        if (old.lostPetAlerts != new.lostPetAlerts) {
            val value = resources.getString(if (new.lostPetAlerts) R.string.alerts_on else R.string.alerts_off)
            changes.add(resources.getString(R.string.change_alerts, value))
        }
        if (old.showPublicPhone != new.showPublicPhone) {
            val value = resources.getString(if (new.showPublicPhone) R.string.phone_visible else R.string.phone_hidden)
            changes.add(resources.getString(R.string.change_public_phone, value))
        }
        if (old.bio != new.bio) changes.add(resources.getString(R.string.change_bio))
        return changes
    }

    // ---------- Validaciones ----------

    private fun validateName(name: String): String? {
        return when {
            name.isBlank() -> resources.getString(R.string.error_name_empty)
            name.length < 3 -> resources.getString(R.string.error_name_short)
            else -> null
        }
    }

    private fun validateEmail(email: String): String? {
        return when {
            email.isBlank() -> resources.getString(R.string.error_email_empty)
            !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> resources.getString(R.string.error_email_invalid)
            else -> null
        }
    }

    private fun validatePhone(phone: String): String? {
        // Se cuentan solo los dígitos, así se permiten espacios y el prefijo +57
        val digits = phone.count { it.isDigit() }
        return when {
            phone.isBlank() -> resources.getString(R.string.error_phone_required)
            phone.any { !it.isDigit() && it != ' ' && it != '+' } -> resources.getString(R.string.error_phone_chars)
            digits != 10 && digits != 12 -> resources.getString(R.string.error_phone_digits)
            else -> null
        }
    }

    private fun validateLocality(locality: String): String? {
        return if (locality.isBlank()) resources.getString(R.string.error_locality) else null
    }
}
