package co.edu.adopcionmascota.navigation

import kotlinx.serialization.Serializable

sealed class MainRoutes {

    @Serializable
    data object Home : MainRoutes()
@Serializable
data object Login : MainRoutes()

@Serializable
data object Register: MainRoutes()

    @Serializable
    data object PublicationList: MainRoutes()
@Serializable
data class PublicationDetail(val publicationId:String) : MainRoutes()
@Serializable
data object PasswordScreen: MainRoutes()

    @Serializable
    data object PublicationScreen: MainRoutes()

    @Serializable
    data object Profile: MainRoutes()

    @Serializable
    data object EditProfile: MainRoutes()

    @Serializable
    data object ProfileUpdated: MainRoutes()

    @Serializable
    data object DeleteAccount: MainRoutes()

    @Serializable
    data object AccountDeleted: MainRoutes()

}

