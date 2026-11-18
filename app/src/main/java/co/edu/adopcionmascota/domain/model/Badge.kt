package co.edu.adopcionmascota.domain.model

// Insignia que el usuario obtiene por sus aportes a la comunidad
data class Badge(
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
