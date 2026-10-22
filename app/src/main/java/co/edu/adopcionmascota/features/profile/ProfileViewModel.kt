package co.edu.adopcionmascota.features.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.edu.adopcionmascota.features.profile.data.UserProfileStore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// Niveles definidos en el enunciado del proyecto
private val LEVEL_NAMES = listOf(
    "Amigo Animal",
    "Protector",
    "Guardián",
    "Héroe de las Mascotas"
)

enum class LevelStatus { UNLOCKED, ACTIVE, NEXT, LOCKED }

enum class BadgeStatus { GRANTED, IN_PROGRESS, LOCKED }

data class LevelUi(
    val number: Int,
    val shortName: String,
    val status: LevelStatus
)

data class BadgeUi(
    val id: String,
    val title: String,
    val description: String,
    val status: BadgeStatus,
    val progress: Int = 0,
    val target: Int = 0
) {
    val percent: Int
        get() = if (target == 0) 0 else (progress * 100 / target)
}

// Todo el estado de la pantalla en un solo objeto inmutable
data class ProfileUiState(
    // Datos del usuario
    val name: String = "",
    val email: String = "",
    val location: String = "",
    val memberSince: String = "",
    val photoUrl: String = "",
    val isActiveVolunteer: Boolean = false,

    // Métricas
    val activePublications: Int = 0,
    val finishedPublications: Int = 0,
    val pendingPublications: Int = 0,
    val importantVotes: Int = 0,
    val votesThisMonth: Int = 0,
    val myPublicationsCount: Int = 0,

    // Reputación
    val currentLevel: Int = 1,
    val points: Int = 0,
    val nextLevelPoints: Int = 100,
    val recentRewards: List<String> = emptyList(),

    // Insignias
    val badges: List<BadgeUi> = emptyList()
) {
    val levelName: String
        get() = LEVEL_NAMES.getOrElse(currentLevel - 1) { LEVEL_NAMES.first() }
            .substringBefore(" ")

    val nextLevelName: String
        get() = LEVEL_NAMES.getOrNull(currentLevel)?.substringBefore(" ") ?: "Máximo"

    val progress: Float
        get() = if (nextLevelPoints == 0) 0f
        else (points.toFloat() / nextLevelPoints).coerceIn(0f, 1f)

    val progressPercent: Int
        get() = (progress * 100).toInt()

    val missingPoints: Int
        get() = (nextLevelPoints - points).coerceAtLeast(0)

    // La escala se calcula a partir del nivel actual
    val levels: List<LevelUi>
        get() = LEVEL_NAMES.mapIndexed { index, fullName ->
            val number = index + 1
            LevelUi(
                number = number,
                shortName = fullName.substringBefore(" "),
                status = when {
                    number < currentLevel -> LevelStatus.UNLOCKED
                    number == currentLevel -> LevelStatus.ACTIVE
                    number == currentLevel + 1 -> LevelStatus.NEXT
                    else -> LevelStatus.LOCKED
                }
            )
        }

    val grantedBadgesCount: Int
        get() = badges.count { it.status == BadgeStatus.GRANTED }
}

class ProfileViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    init {
        loadProfile()
        observeUserData()
    }

    // Los datos personales salen del almacén compartido, así el perfil
    // muestra los cambios hechos en "Editar Perfil"
    private fun observeUserData() {
        viewModelScope.launch {
            UserProfileStore.profile.collect { profile ->
                _uiState.update {
                    it.copy(
                        name = profile.name,
                        email = profile.email,
                        location = "${profile.localityName}, Bogotá D.C.",
                        photoUrl = profile.photoUrl,
                        isActiveVolunteer = profile.isTemporaryHome || profile.lostPetAlerts
                    )
                }
            }
        }
    }

    // Más adelante esta función consultará el repositorio / Firebase
    private fun loadProfile() {
        _uiState.value = ProfileUiState(
            name = "Camila Morales Restrepo",
            email = "camila.morales@ejemplo.com",
            location = "Usaquén, Bogotá D.C.",
            memberSince = "Enero 2024",
            photoUrl = "https://i.pravatar.cc/300?img=47",
            isActiveVolunteer = true,
            activePublications = 2,
            finishedPublications = 5,
            pendingPublications = 1,
            importantVotes = 148,
            votesThisMonth = 18,
            myPublicationsCount = 4,
            currentLevel = 2,
            points = 340,
            nextLevelPoints = 500,
            recentRewards = listOf(
                "Rescate verificado (+20)",
                "Comentario útil (+5)"
            ),
            badges = listOf(
                BadgeUi(
                    id = "first_paw",
                    title = "Primera Huella",
                    description = "Primer reporte...",
                    status = BadgeStatus.GRANTED
                ),
                BadgeUi(
                    id = "temporary_home",
                    title = "Hogar Temporal",
                    description = "Acogida solidaria",
                    status = BadgeStatus.GRANTED
                ),
                BadgeUi(
                    id = "verified_10",
                    title = "10 Verificadas",
                    description = "Progreso 7 / 10",
                    status = BadgeStatus.IN_PROGRESS,
                    progress = 7,
                    target = 10
                ),
                BadgeUi(
                    id = "hero_month",
                    title = "Héroe del Mes",
                    description = "Mayor rescate mensual",
                    status = BadgeStatus.LOCKED
                )
            )
        )
    }
}
