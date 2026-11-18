package co.edu.adopcionmascota.features.profile

import co.edu.adopcionmascota.domain.model.Badge
import co.edu.adopcionmascota.domain.model.BadgeStatus
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import androidx.lifecycle.viewModelScope
import co.edu.adopcionmascota.domain.usecase.user.ObserveCurrentUserUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ReputationLevel(
    val number: Int,
    val name: String,
    val minPoints: Int,
    val maxPoints: Int?,            // null = sin límite (último nivel)
    val description: String,
    val benefits: List<String>
) {
    val rangeText: String
        get() = if (maxPoints == null) "$minPoints+ Puntos" else "$minPoints – $maxPoints Puntos"
}

data class PointAction(val text: String, val points: Int)

data class ReputationUiState(
    val userName: String = "",
    val location: String = "",
    val points: Int = 0,                    // Mismos puntos que muestra el perfil (Room)
    val levels: List<ReputationLevel> = emptyList(),
    val actions: List<PointAction> = emptyList(),
    val grantedBadges: Int = 0,
    val expandedLevel: Int? = null          // Nivel con los beneficios desplegados
) {
    val currentLevel: ReputationLevel?
        get() = levels.lastOrNull { points >= it.minPoints }

    val nextLevel: ReputationLevel?
        get() = levels.firstOrNull { it.minPoints > points }

    val missingPoints: Int
        get() = ((nextLevel?.minPoints ?: points) - points).coerceAtLeast(0)

    val progress: Float
        get() {
            val current = currentLevel ?: return 0f
            val next = nextLevel ?: return 1f
            return ((points - current.minPoints).toFloat() / (next.minPoints - current.minPoints)).coerceIn(0f, 1f)
        }

    fun statusOf(level: ReputationLevel): LevelStatus = when {
        level.number < (currentLevel?.number ?: 1) -> LevelStatus.UNLOCKED
        level.number == currentLevel?.number -> LevelStatus.ACTIVE
        level.number == nextLevel?.number -> LevelStatus.NEXT
        else -> LevelStatus.LOCKED
    }
}

@HiltViewModel
class ReputationViewModel @Inject constructor(
    observeCurrentUserUseCase: ObserveCurrentUserUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        ReputationUiState(
            levels = listOf(
                ReputationLevel(
                    1, "Amigo Animal", 0, 99,
                    "Primer paso en la red comunitaria. Permite reportar mascotas perdidas, comentar casos y acceder al feed sin costo.",
                    listOf("Publicar casos", "Alertas radio 1 km", "Reaccionar en feed")
                ),
                ReputationLevel(
                    2, "Protector", 100, 499,
                    "Miembro activo y confiable. Participa en rescates verificados y colabora con hogares de paso temporales.",
                    listOf("Alertas ampliadas (5 km)", "Prioridad en mapa zonal", "Badge verificado en chat")
                ),
                ReputationLevel(
                    3, "Guardián", 500, 999,
                    "Rescatista experimentado y validado por la comunidad. Lidera brigadas y apoya adopciones críticas.",
                    listOf("Voto en casos éticos", "Canal brigadas veterinarias", "Alertas zonales (15 km)")
                ),
                ReputationLevel(
                    4, "Héroe de las Mascotas", 1000, null,
                    "La máxima distinción comunitaria. Líderes de bienestar animal con decenas de adopciones y rescates.",
                    listOf("Distintivo Héroe", "Moderación voluntaria", "Salón de Honor")
                )
            ),
            actions = listOf(
                PointAction("Rescate o adopción exitosa verificada", 50),
                PointAction("Acoger como hogar de paso temporal", 30),
                PointAction("Reporte verificado con datos precisos", 20),
                PointAction("Validación ciudadana en alerta de extravío", 10),
                PointAction("Aporte útil confirmado por rescatistas", 5)
            ),
            grantedBadges = badgeCatalog().count { it.badge.status == BadgeStatus.GRANTED }
        )
    )
    val uiState: StateFlow<ReputationUiState> = _uiState.asStateFlow()

    init {
        // Los puntos se ganan al publicar casos aprobados, comentar y concretar adopciones
        viewModelScope.launch {
            observeCurrentUserUseCase().collect { user ->
                if (user != null) {
                    _uiState.update { it.copy(userName = user.name, location = user.city, points = user.points) }
                }
            }
        }
    }

    // Tocar un nivel muestra u oculta sus beneficios
    fun onLevelClick(number: Int) = _uiState.update {
        it.copy(expandedLevel = if (it.expandedLevel == number) null else number)
    }
}
