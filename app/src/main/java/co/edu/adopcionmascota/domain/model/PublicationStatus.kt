package co.edu.adopcionmascota.domain.model

import androidx.annotation.StringRes
import co.edu.adopcionmascota.R

// Ciclo de vida de una publicación:
// PENDING (en revisión) -> VERIFIED (aprobada, visible en el feed) o REJECTED (rechazada con motivo)
// VERIFIED -> RESOLVED (caso cerrado) o PAUSED (oculta por el autor)
enum class PublicationStatus(@StringRes val label: Int) {
    PENDING(R.string.status_review),
    VERIFIED(R.string.status_active),
    REJECTED(R.string.status_observation),
    RESOLVED(R.string.status_resolved),
    PAUSED(R.string.status_paused),
    DELETED(R.string.status_deleted)
}
