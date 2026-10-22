package co.edu.adopcionmascota.features.profile

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

// Datos del usuario que comparten el perfil y la pantalla de edición
data class UserProfile(
    val photoUrl: String = "https://i.pravatar.cc/300?img=47",
    val name: String = "Camila Morales Restrepo",
    val email: String = "camila.morales@ejemplo.com",
    val isEmailVerified: Boolean = true,
    val phone: String = "+57 310 123 4567",
    val locality: String = "Usaquén (Zona Norte)",
    val neighborhood: String = "Cedritos / Santa Bárbara",
    val bio: String = "Amante de los animales, hogar de paso temporal para cachorros y promotora de esterilización responsable en Bogotá",
    val isTemporaryHome: Boolean = true,
    val lostPetAlerts: Boolean = true,
    val showPublicPhone: Boolean = false
) {

    val localityName: String
        get() = locality.substringBefore(" (")
}


object UserProfileStore {

    private val _profile = MutableStateFlow(UserProfile())
    val profile: StateFlow<UserProfile> = _profile.asStateFlow()

    // Cambios aplicados en la última edición, para mostrarlos en "Perfil editado"
    private val _lastChanges = MutableStateFlow<List<String>>(emptyList())
    val lastChanges: StateFlow<List<String>> = _lastChanges.asStateFlow()

    fun save(newProfile: UserProfile) {
        _lastChanges.value = describeChanges(_profile.value, newProfile)
        _profile.value = newProfile
    }

    // Al eliminar la cuenta se vuelve a los datos iniciales
    fun clear() {
        _profile.value = UserProfile()
        _lastChanges.value = emptyList()
    }

    private fun describeChanges(old: UserProfile, new: UserProfile): List<String> {
        val changes = mutableListOf<String>()
        if (old.name != new.name) changes.add("Nombre: ${new.name}")
        if (old.email != new.email) changes.add("Correo: ${new.email}")
        if (old.phone != new.phone) changes.add("Teléfono: ${new.phone}")
        if (old.locality != new.locality || old.neighborhood != new.neighborhood) {
            val sector = new.neighborhood.substringBefore(" /").ifBlank { new.localityName }
            changes.add("Ubicación: $sector / ${new.localityName}")
        }
        if (old.isTemporaryHome != new.isTemporaryHome) {
            changes.add("Hogar de Paso: ${if (new.isTemporaryHome) "Activo" else "Inactivo"}")
        }
        if (old.lostPetAlerts != new.lostPetAlerts) {
            changes.add("Alertas: ${if (new.lostPetAlerts) "Activas" else "Inactivas"}")
        }
        if (old.showPublicPhone != new.showPublicPhone) {
            changes.add("Teléfono público: ${if (new.showPublicPhone) "Visible" else "Oculto"}")
        }
        if (old.bio != new.bio) changes.add("Bio comunitaria actualizada")
        return changes
    }
}
