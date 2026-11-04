package co.edu.adopcionmascota.domain.model

import co.edu.adopcionmascota.R
import androidx.annotation.StringRes
// Estados de una publicación propia ("Mis Publicaciones")
enum class MyPublicationStatus(@StringRes val label: Int) {
    ACTIVE(R.string.status_active),
    IN_REVIEW(R.string.status_review),
    RESOLVED(R.string.status_resolved),
    OBSERVATION(R.string.status_observation),
    PAUSED(R.string.status_paused)
}
