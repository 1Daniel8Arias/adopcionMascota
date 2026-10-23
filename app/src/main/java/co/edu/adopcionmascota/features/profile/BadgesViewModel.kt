package co.edu.adopcionmascota.features.profile

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

// Filtros de la "Vitrina de Insignias"
enum class BadgeFilter(val label: String) {
    ALL("Todas"),
    GRANTED("Obtenidas"),
    IN_PROGRESS("En Progreso"),
    LOCKED("Por Desbloquear")
}

enum class BadgeCategory(val label: String) {
    RESCUE("🐾 Rescate y Adopción"),
    COMMUNITY("🤝 Comunidad y Apoyo")
}

data class BadgeDetail(
    val badge: BadgeUi,
    val category: BadgeCategory,
    val points: Int,
    val date: String? = null,           // Fecha en que se obtuvo
    val requirement: String? = null     // Qué falta para desbloquearla
)

data class BadgesUiState(
    val userName: String = "Camila Morales",
    val levelText: String = "Nivel 2 · Protector",
    val badges: List<BadgeDetail> = emptyList(),
    val filter: BadgeFilter = BadgeFilter.ALL,
    val category: BadgeCategory? = null,           // null = todo el catálogo
    val selectedBadge: BadgeDetail? = null,        // Diálogo con el detalle de la insignia
    val showSeasonDialog: Boolean = false,
    val joinedSeason: Boolean = false
) {
    val grantedCount: Int get() = badges.count { it.badge.status == BadgeStatus.GRANTED }
    val totalCount: Int get() = badges.size
    val completedPercent: Int get() = if (totalCount == 0) 0 else grantedCount * 100 / totalCount
    val earnedPoints: Int get() = badges.filter { it.badge.status == BadgeStatus.GRANTED }.sumOf { it.points }

    fun countFor(filter: BadgeFilter): Int = badges.count { it.matches(filter) }

    val visibleBadges: List<BadgeDetail>
        get() = badges.filter { it.matches(filter) && (category == null || it.category == category) }
}

private fun BadgeDetail.matches(filter: BadgeFilter): Boolean = when (filter) {
    BadgeFilter.ALL -> true
    BadgeFilter.GRANTED -> badge.status == BadgeStatus.GRANTED
    BadgeFilter.IN_PROGRESS -> badge.status == BadgeStatus.IN_PROGRESS
    BadgeFilter.LOCKED -> badge.status == BadgeStatus.LOCKED
}

// Catálogo quemado; las 4 primeras son las mismas que muestra el perfil
fun badgeCatalog(): List<BadgeDetail> = listOf(
    BadgeDetail(
        BadgeUi("first_paw", "Primera Huella", "Completaste exitosamente tu primer reporte de rescate verificado.", BadgeStatus.GRANTED),
        BadgeCategory.RESCUE, points = 50, date = "14 Feb 2024"
    ),
    BadgeDetail(
        BadgeUi("temporary_home", "Hogar Temporal", "Acogiste temporalmente a 3 animales durante su proceso de adopción.", BadgeStatus.GRANTED),
        BadgeCategory.RESCUE, points = 60, date = "02 Mar 2024"
    ),
    BadgeDetail(
        BadgeUi("verified_10", "10 Verificadas", "Alcanza 10 publicaciones verificadas por moderadores.", BadgeStatus.IN_PROGRESS, progress = 7, target = 10),
        BadgeCategory.COMMUNITY, points = 80, requirement = "Faltan 3 publicaciones verificadas"
    ),
    BadgeDetail(
        BadgeUi("hero_month", "Héroe del Mes", "Mayor número de rescates y adopciones concretadas en el mes.", BadgeStatus.LOCKED),
        BadgeCategory.RESCUE, points = 120, requirement = "Requiere Nivel 3 (Guardián) y 10 rescates activos"
    ),
    BadgeDetail(
        BadgeUi("gold_support", "Aporte de Oro", "Aporta insumos o transporte voluntario para 3 brigadas veterinarias.", BadgeStatus.IN_PROGRESS, progress = 2, target = 3),
        BadgeCategory.COMMUNITY, points = 50, requirement = "Falta 1 brigada"
    ),
    BadgeDetail(
        BadgeUi("tracker", "Rastreador Distrital", "Valida 10 reportes de avistamiento con datos precisos.", BadgeStatus.IN_PROGRESS, progress = 7, target = 10),
        BadgeCategory.RESCUE, points = 40, requirement = "Faltan 3 reportes"
    ),
    BadgeDetail(
        BadgeUi("friendly_voice", "Voz Amiga Comunitaria", "Recibe 50 reacciones útiles en comentarios de apoyo.", BadgeStatus.IN_PROGRESS, progress = 38, target = 50),
        BadgeCategory.COMMUNITY, points = 35, requirement = "Faltan 12 reacciones"
    ),
    BadgeDetail(
        BadgeUi("ethics_tutor", "Tutor de la Ética Animal", "Participa como veedor ciudadano en 50 deliberaciones éticas.", BadgeStatus.LOCKED),
        BadgeCategory.COMMUNITY, points = 100, requirement = "Desbloqueable al alcanzar el rango Guardián (500 pts)"
    )
)

class BadgesViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(BadgesUiState(badges = badgeCatalog()))
    val uiState: StateFlow<BadgesUiState> = _uiState.asStateFlow()

    fun onFilterChange(filter: BadgeFilter) = _uiState.update { it.copy(filter = filter) }

    fun onCategoryChange(category: BadgeCategory?) = _uiState.update { it.copy(category = category) }

    fun onBadgeClick(badge: BadgeDetail) = _uiState.update { it.copy(selectedBadge = badge) }

    fun onDismissBadge() = _uiState.update { it.copy(selectedBadge = null) }

    // "Ver Reto Especial" de la temporada activa
    fun onSeasonClick() = _uiState.update { it.copy(showSeasonDialog = true) }

    fun onJoinSeason() = _uiState.update { it.copy(showSeasonDialog = false, joinedSeason = true) }

    fun onDismissSeason() = _uiState.update { it.copy(showSeasonDialog = false) }
}
