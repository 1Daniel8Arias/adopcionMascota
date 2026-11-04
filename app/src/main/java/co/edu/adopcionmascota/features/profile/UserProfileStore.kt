package co.edu.adopcionmascota.features.profile

import co.edu.adopcionmascota.core.util.ResourceProvider
import co.edu.adopcionmascota.R
import co.edu.adopcionmascota.domain.model.User
import co.edu.adopcionmascota.domain.model.UserProfile
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

object UserProfileStore {

    private val _profile = MutableStateFlow(UserProfile())
    val profile: StateFlow<UserProfile> = _profile.asStateFlow()

    // Cambios aplicados en la última edición, para mostrarlos en "Perfil editado"
    private val _lastChanges = MutableStateFlow<List<String>>(emptyList())
    val lastChanges: StateFlow<List<String>> = _lastChanges.asStateFlow()

    // Carga en el perfil los datos del usuario que inició sesión
    fun loadFromUser(user: User) {
        if (_profile.value.email == user.email) return
        _lastChanges.value = emptyList()
        _profile.value = UserProfile(
            photoUrl = user.profilePictureUrl,
            name = user.name,
            email = user.email,
            phone = user.phoneNumber,
            locality = user.city,
            neighborhood = user.address,
            bio = ""
        )
    }

    fun save(newProfile: UserProfile, resources: ResourceProvider) {
        _lastChanges.value = describeChanges(_profile.value, newProfile, resources)
        _profile.value = newProfile
    }

    // Al eliminar la cuenta se vuelve a los datos iniciales
    fun clear() {
        _profile.value = UserProfile()
        _lastChanges.value = emptyList()
    }

    private fun describeChanges(old: UserProfile, new: UserProfile, resources: ResourceProvider): List<String> {
        val changes = mutableListOf<String>()
        if (old.name != new.name) changes.add(resources.getString(R.string.change_name, new.name))
        if (old.email != new.email) changes.add(resources.getString(R.string.change_email, new.email))
        if (old.phone != new.phone) changes.add(resources.getString(R.string.change_phone, new.phone))
        if (old.locality != new.locality || old.neighborhood != new.neighborhood) {
            val sector = new.neighborhood.substringBefore(" /").ifBlank { new.localityName }
            changes.add(resources.getString(R.string.change_location, sector, new.localityName))
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
}
