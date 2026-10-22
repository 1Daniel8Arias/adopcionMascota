package co.edu.adopcionmascota.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import androidx.compose.material3.Surface
import co.edu.adopcionmascota.features.home.HomeScreen
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.dialog
import androidx.compose.ui.window.DialogProperties
import androidx.navigation.toRoute
import co.edu.adopcionmascota.features.adoption.detail.PublicationDetailScreen
import co.edu.adopcionmascota.features.adoption.list.PublicationListScreen
import co.edu.adopcionmascota.features.login.LoginScreen
import co.edu.adopcionmascota.features.password.PasswordScreen
import co.edu.adopcionmascota.features.publication.PublicationScreen
import co.edu.adopcionmascota.features.register.RegisterScreen
import co.edu.adopcionmascota.core.component.AppDestination
import co.edu.adopcionmascota.features.profile.ProfileScreen
import co.edu.adopcionmascota.features.profile.edit.EditProfileScreen
import co.edu.adopcionmascota.features.profile.updated.ProfileUpdatedScreen
import co.edu.adopcionmascota.features.profile.delete.DeleteAccountScreen
import co.edu.adopcionmascota.features.profile.deleted.AccountDeletedScreen

@Composable
fun AppNavigation(){
    val navController= rememberNavController()

    Surface(
        modifier=Modifier.fillMaxSize()
    ){
        NavHost(
            navController=navController,
            startDestination=MainRoutes.Home
        ){
             composable<MainRoutes.Home> {
                 HomeScreen(
                     onNavigateToLogin = {
                         navController.navigate(MainRoutes.Login)
                     },
                     onNavigateToRegister = {
                         navController.navigate(MainRoutes.Register)
                     }
                 )
             }
            composable<MainRoutes.Login> {
                LoginScreen(
                    onNavigationPublicLists = {
                        // Solo aquí (login exitoso) se limpia la pila
                        navController.navigate(MainRoutes.PublicationList) {
                            popUpTo(MainRoutes.Home) { inclusive = true }
                        }
                    },
                    onNavigationPasswordScreen = {
                        navController.navigate(MainRoutes.PasswordScreen)   // sin popUpTo
                    },
                    onNavigationRegister = {
                        navController.navigate(MainRoutes.Register)
                    }
                )
            }

            composable<MainRoutes.Register>{
                RegisterScreen(
                    onNavigateToBack = {
                        navController.popBackStack()
                    }
                )
            }
            composable<MainRoutes.PublicationList>{
                PublicationListScreen(
                    onPublicationClick = { publication ->
                        navController.navigate(
                            MainRoutes.PublicationDetail(publication.id)
                        )
                    },
                    onNavigationToPublications={
                        navController.navigate(MainRoutes.PublicationScreen)
                    },
                    onNavigationClick = { destination ->
                        // Por ahora solo el item "Perfil" de la barra inferior navega
                        if (destination == AppDestination.PROFILE) {
                            navController.navigate(MainRoutes.Profile)
                        }
                    }
                )

            }

            composable<MainRoutes.PublicationDetail>{
                val args= it.toRoute<MainRoutes.PublicationDetail>()
                PublicationDetailScreen(
                    pubblicationId = args.publicationId
                )

            }

            composable<MainRoutes.PasswordScreen>{
                PasswordScreen()
            }

            composable<MainRoutes.PublicationScreen> {
                PublicationScreen()
            }

            composable<MainRoutes.Profile> {
                ProfileScreen(
                    onNavigateBack = {
                        navController.popBackStack()
                    },
                    onNavigationClick = { destination ->
                        // "Explorar" regresa a la lista de publicaciones
                        if (destination == AppDestination.EXPLORE) {
                            navController.popBackStack()
                        }
                    },
                    // Los dos botones de editar ("Editar Perfil" y "Editar Datos Personales")
                    // abren la misma pantalla de edición
                    onEditProfile = {
                        navController.navigate(MainRoutes.EditProfile)
                    },
                    onEditPersonalData = {
                        navController.navigate(MainRoutes.EditProfile)
                    },
                    // "Eliminar mi cuenta" abre la confirmación del Figma
                    onDeleteAccount = {
                        navController.navigate(MainRoutes.DeleteAccount)
                    }
                )
            }

            composable<MainRoutes.EditProfile> {
                EditProfileScreen(
                    onNavigateBack = {
                        navController.popBackStack()
                    },
                    // "Guardar Cambios" lleva a "Perfil editado" y quita la edición de la pila
                    onProfileSaved = {
                        navController.navigate(MainRoutes.ProfileUpdated) {
                            popUpTo(MainRoutes.EditProfile) { inclusive = true }
                        }
                    },
                    onNavigateToPassword = {
                        navController.navigate(MainRoutes.PasswordScreen)
                    },
                    onAccountDeactivated = {
                        // Igual que al eliminar la cuenta: inicio y pila limpia
                        navController.navigate(MainRoutes.Home) {
                            popUpTo(MainRoutes.PublicationList) { inclusive = true }
                        }
                    }
                )
            }

            composable<MainRoutes.ProfileUpdated> {
                ProfileUpdatedScreen(
                    // El perfil sigue en la pila, así que basta con regresar
                    onBackToProfile = {
                        navController.popBackStack(MainRoutes.Profile, inclusive = false)
                    },
                    onGoToFeed = {
                        navController.popBackStack(MainRoutes.PublicationList, inclusive = false)
                    },
                    onEditAgain = {
                        navController.navigate(MainRoutes.EditProfile) {
                            popUpTo(MainRoutes.ProfileUpdated) { inclusive = true }
                        }
                    }
                )
            }

            // Se muestra como diálogo a pantalla completa para que el perfil se vea detrás
            dialog<MainRoutes.DeleteAccount>(
                dialogProperties = DialogProperties(
                    usePlatformDefaultWidth = false,
                    decorFitsSystemWindows = false
                )
            ) {
                DeleteAccountScreen(
                    onCancel = {
                        navController.popBackStack()
                    },
                    onAccountDeleted = {
                        // Se quita la sesión de la pila; solo queda la despedida
                        navController.navigate(MainRoutes.AccountDeleted) {
                            popUpTo(MainRoutes.PublicationList) { inclusive = true }
                        }
                    }
                )
            }

            composable<MainRoutes.AccountDeleted> {
                AccountDeletedScreen(
                    onGoToHome = {
                        navController.navigate(MainRoutes.Home) {
                            popUpTo(MainRoutes.AccountDeleted) { inclusive = true }
                        }
                    }
                )
            }



        }

    }
}