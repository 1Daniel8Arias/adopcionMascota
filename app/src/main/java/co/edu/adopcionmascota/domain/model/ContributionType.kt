package co.edu.adopcionmascota.domain.model

import co.edu.adopcionmascota.R
import androidx.annotation.StringRes
// Tipos de aporte que un usuario puede hacer al comentar una publicación
enum class ContributionType(@StringRes val label: Int) {
    SUPPORT(R.string.contribution_support),
    SIGHTING(R.string.contribution_sighting),
    VET_FOOD(R.string.contribution_vet),
    TEMPORARY_HOME(R.string.contribution_foster)
}
