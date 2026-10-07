package co.edu.adopcionmascota.navigation

import kotlinx.serialization.Serializable

sealed class MainRoutes {

    @Serializable
    data object Home : MainRoutes()
@Serializable
data object Login : MainRoutes()


}

