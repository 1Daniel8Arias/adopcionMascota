package co.edu.adopcionmascota.domain.model

// Niveles de reputación definidos en el enunciado del proyecto
enum class ReputationLevel(val displayName: String, val minPoints: Int) {
    AMIGO_ANIMAL("Amigo Animal", 0),
    PROTECTOR("Protector", 100),
    GUARDIAN("Guardián", 500),
    HEROE("Héroe de las Mascotas", 1000);

    val number: Int get() = ordinal + 1

    val next: ReputationLevel? get() = entries.getOrNull(ordinal + 1)

    companion object {
        fun forPoints(points: Int): ReputationLevel = entries.last { points >= it.minPoints }
    }
}

// Puntos que se otorgan por cada acción
object ReputationPoints {
    const val PUBLICATION_APPROVED = 20
    const val COMMENT = 5
    const val ADOPTION_COMPLETED = 50
}
