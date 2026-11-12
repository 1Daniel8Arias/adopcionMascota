package co.edu.adopcionmascota.features.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.edu.adopcionmascota.domain.model.Badge
import co.edu.adopcionmascota.domain.model.BadgeStatus
import co.edu.adopcionmascota.domain.model.PublicationStatus
import co.edu.adopcionmascota.domain.model.ReputationLevel
import co.edu.adopcionmascota.domain.usecase.auth.LogoutUseCase
import co.edu.adopcionmascota.domain.usecase.comment.ObserveAllCommentsUseCase
import co.edu.adopcionmascota.domain.usecase.publication.ObserveMyPublicationsUseCase
import co.edu.adopcionmascota.domain.usecase.user.ObserveCurrentUserUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

private val LEVEL_NAMES = ReputationLevel.entries.map { it.displayName }

enum class LevelStatus { UNLOCKED, ACTIVE, NEXT, LOCKED }

data class LevelUi(
    val number: Int,
    val shortName: String,
    val status: LevelStatus
)

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
    val badges: List<Badge> = emptyList()
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

@HiltViewModel
class ProfileViewModel @Inject constructor(
    observeCurrentUserUseCase: ObserveCurrentUserUseCase,
    observeMyPublicationsUseCase: ObserveMyPublicationsUseCase,
    observeAllCommentsUseCase: ObserveAllCommentsUseCase,
    private val logoutUseCase: LogoutUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    init {
        // Datos del usuario de la sesión, sus publicaciones y sus aportes (todo desde Room)
        viewModelScope.launch {
            combine(
                observeCurrentUserUseCase(),
                observeMyPublicationsUseCase(),
                observeAllCommentsUseCase()
            ) { user, publications, comments -> Triple(user, publications, comments) }
                .collect { (user, publications, comments) ->
                    if (user == null) return@collect
                    val myComments = comments.filter { it.authorId == user.id }
                    val monthAgo = System.currentTimeMillis() - 30L * 24 * 60 * 60 * 1000
                    val verified = publications.count { it.status == PublicationStatus.VERIFIED || it.status == PublicationStatus.RESOLVED }
                    val level = user.level
                    _uiState.update {
                        it.copy(
                            name = user.name,
                            email = user.email,
                            location = user.city,
                            memberSince = SimpleDateFormat("MMMM yyyy", Locale.getDefault()).format(Date(user.createdAt))
                                .replaceFirstChar { c -> c.uppercase() },
                            photoUrl = user.profilePictureUrl,
                            isActiveVolunteer = user.isTemporaryHome || user.lostPetAlerts,
                            activePublications = publications.count { p -> p.status == PublicationStatus.VERIFIED || p.status == PublicationStatus.PAUSED },
                            finishedPublications = publications.count { p -> p.status == PublicationStatus.RESOLVED },
                            pendingPublications = publications.count { p -> p.status == PublicationStatus.PENDING || p.status == PublicationStatus.REJECTED },
                            importantVotes = myComments.sumOf { c -> c.thanks },
                            votesThisMonth = myComments.count { c -> c.createdAt >= monthAgo },
                            myPublicationsCount = publications.size,
                            currentLevel = level.number,
                            points = user.points,
                            nextLevelPoints = level.next?.minPoints ?: user.points,
                            badges = profileBadges(verified, publications.size, user.isTemporaryHome, level)
                        )
                    }
                }
        }
    }

    fun logout() {
        // Al borrarse la sesión, AppNavigation muestra automáticamente AuthNavigation
        viewModelScope.launch {
            logoutUseCase()
        }
    }
}

// Las 4 insignias del perfil se calculan con la actividad real del usuario
fun profileBadges(verified: Int, publications: Int, isTemporaryHome: Boolean, level: ReputationLevel): List<Badge> = listOf(
    Badge(
        id = "first_paw",
        title = "Primera Huella",
        description = "Primer reporte verificado",
        status = if (verified >= 1) BadgeStatus.GRANTED else if (publications > 0) BadgeStatus.IN_PROGRESS else BadgeStatus.LOCKED,
        progress = verified.coerceAtMost(1),
        target = 1
    ),
    Badge(
        id = "temporary_home",
        title = "Hogar Temporal",
        description = "Acogida solidaria",
        status = if (isTemporaryHome) BadgeStatus.GRANTED else BadgeStatus.LOCKED
    ),
    Badge(
        id = "verified_10",
        title = "10 Verificadas",
        description = "Progreso ${verified.coerceAtMost(10)} / 10",
        status = if (verified >= 10) BadgeStatus.GRANTED else BadgeStatus.IN_PROGRESS,
        progress = verified.coerceAtMost(10),
        target = 10
    ),
    Badge(
        id = "hero_month",
        title = "Héroe del Mes",
        description = "Mayor rescate mensual",
        status = if (level >= ReputationLevel.GUARDIAN) BadgeStatus.IN_PROGRESS else BadgeStatus.LOCKED
    )
)
