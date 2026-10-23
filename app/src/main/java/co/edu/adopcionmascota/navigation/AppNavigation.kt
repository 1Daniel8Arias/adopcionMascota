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
import co.edu.adopcionmascota.features.comment.CreateCommentScreen
import co.edu.adopcionmascota.features.notification.NotificationDetailScreen
import co.edu.adopcionmascota.features.notification.NotificationsScreen
import co.edu.adopcionmascota.features.adoption.list.AdvancedFiltersScreen
import co.edu.adopcionmascota.features.adoption.list.PublicationMapScreen
import co.edu.adopcionmascota.features.publication.DeletePublicationScreen
import co.edu.adopcionmascota.features.publication.EditPublicationScreen
import co.edu.adopcionmascota.features.publication.MyPublicationsScreen
import co.edu.adopcionmascota.features.register.RegisterSuccessScreen
import co.edu.adopcionmascota.features.profile.BadgesScreen
import co.edu.adopcionmascota.features.profile.ReputationScreen
import android.content.Intent
import androidx.compose.ui.platform.LocalContext

@Composable
fun AppNavigation(){
    val navController= rememberNavController()

    // Barra inferior compartida: cada pestaña abre su pantalla sin apilar copias
    val navigateToTab: (AppDestination) -> Unit = { destination ->
        when (destination) {
            AppDestination.EXPLORE ->
                navController.popBackStack(MainRoutes.PublicationList, inclusive = false)

            AppDestination.PUBLISH -> navController.navigate(MainRoutes.PublicationScreen)

            AppDestination.ALERTS -> navController.navigate(MainRoutes.Notifications) {
                popUpTo(MainRoutes.PublicationList)
                launchSingleTop = true
            }

            AppDestination.PROFILE -> navController.navigate(MainRoutes.Profile) {
                popUpTo(MainRoutes.PublicationList)
                launchSingleTop = true
            }

            AppDestination.MODERATE -> Unit
        }
    }

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
                    },
                    // Registro exitoso → pantalla de confirmación (sin volver al formulario)
                    onRegisterSuccess = { name ->
                        navController.navigate(MainRoutes.RegisterSuccess(name)) {
                            popUpTo(MainRoutes.Home)
                        }
                    },
                    onNavigateToLogin = {
                        navController.navigate(MainRoutes.Login) {
                            popUpTo(MainRoutes.Home)
                        }
                    }
                )
            }

            composable<MainRoutes.RegisterSuccess> {
                val args = it.toRoute<MainRoutes.RegisterSuccess>()
                RegisterSuccessScreen(
                    name = args.name,
                    // "Continuar al Feed Principal": se entra a la app y se limpia la pila
                    onContinueToFeed = {
                        navController.navigate(MainRoutes.PublicationList) {
                            popUpTo(MainRoutes.Home) { inclusive = true }
                        }
                    },
                    // "Configurar preferencias de alerta" abre la edición del perfil (sección de voluntariado)
                    onConfigureAlerts = {
                        navController.navigate(MainRoutes.PublicationList) {
                            popUpTo(MainRoutes.Home) { inclusive = true }
                        }
                        navController.navigate(MainRoutes.EditProfile)
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
                    onMapClick = {
                        navController.navigate(MainRoutes.PublicationMap)
                    },
                    onFiltersClick = {
                        navController.navigate(MainRoutes.AdvancedFilters)
                    },
                    onNotificationClick = {
                        navigateToTab(AppDestination.ALERTS)
                    },
                    onProfileClick = {
                        navigateToTab(AppDestination.PROFILE)
                    },
                    onNavigationClick = navigateToTab
                )

            }

            composable<MainRoutes.PublicationDetail>{
                val args= it.toRoute<MainRoutes.PublicationDetail>()
                PublicationDetailScreen(
                    publicationId = args.publicationId,
                    onNavigateBack = {
                        navController.popBackStack()
                    },
                    onNavigateToComment = {
                        navController.navigate(MainRoutes.CreateComment(args.publicationId))
                    },
                    onNotificationClick = {
                        navigateToTab(AppDestination.ALERTS)
                    },
                    onProfileClick = {
                        navigateToTab(AppDestination.PROFILE)
                    }
                )

            }

            composable<MainRoutes.CreateComment> {
                val args = it.toRoute<MainRoutes.CreateComment>()
                CreateCommentScreen(
                    publicationId = args.publicationId,
                    // "Descartar y volver" y "Volver a la Publicación" regresan al detalle
                    onNavigateBack = {
                        navController.popBackStack()
                    },
                    onNavigateToFeed = {
                        navController.popBackStack(MainRoutes.PublicationList, inclusive = false)
                    }
                )
            }

            composable<MainRoutes.Notifications> {
                NotificationsScreen(
                    onNotificationClick = { notificationId ->
                        navController.navigate(MainRoutes.NotificationDetail(notificationId))
                    },
                    onNavigationClick = navigateToTab
                )
            }

            composable<MainRoutes.NotificationDetail> {
                val args = it.toRoute<MainRoutes.NotificationDetail>()
                NotificationDetailScreen(
                    notificationId = args.notificationId,
                    // "Button - Volver" de los 5 detalles regresa a la lista
                    onNavigateBack = {
                        navController.popBackStack()
                    },
                    onViewPublication = { publicationId ->
                        navController.navigate(MainRoutes.PublicationDetail(publicationId))
                    },
                    onNavigateToFeed = {
                        navController.popBackStack(MainRoutes.PublicationList, inclusive = false)
                    },
                    onNavigateToProfile = {
                        navigateToTab(AppDestination.PROFILE)
                    }
                )
            }

            composable<MainRoutes.Badges> {
                BadgesScreen(
                    onNavigateBack = {
                        navController.popBackStack()
                    },
                    onViewLevels = {
                        navController.navigate(MainRoutes.Reputation) {
                            popUpTo(MainRoutes.Profile)
                        }
                    }
                )
            }

            composable<MainRoutes.Reputation> {
                ReputationScreen(
                    onNavigateBack = {
                        navController.popBackStack()
                    },
                    onViewBadges = {
                        navController.navigate(MainRoutes.Badges) {
                            popUpTo(MainRoutes.Profile)
                        }
                    }
                )
            }

            composable<MainRoutes.PasswordScreen>{
                PasswordScreen(
                    onNavigateToLogin = {
                        navController.popBackStack(MainRoutes.Login, inclusive = false)
                    },
                    onNavigateToRegister = {
                        navController.navigate(MainRoutes.Register) {
                            popUpTo(MainRoutes.Login)
                        }
                    }
                )
            }

            composable<MainRoutes.PublicationScreen> {
                PublicationScreen(
                    onNavigateBack = {
                        navController.popBackStack()
                    },
                    onNavigationClick = navigateToTab,
                    // "Ver en 'Mis Publicaciones'" de la hoja de éxito
                    onPublishSuccess = {
                        navController.navigate(MainRoutes.MyPublications) {
                            popUpTo(MainRoutes.PublicationScreen) { inclusive = true }
                        }
                    }
                )
            }

            composable<MainRoutes.PublicationMap> {
                PublicationMapScreen(
                    onNavigateBack = {
                        navController.popBackStack()
                    },
                    onOpenFilters = {
                        navController.navigate(MainRoutes.AdvancedFilters)
                    },
                    onViewPublication = { publicationId ->
                        navController.navigate(MainRoutes.PublicationDetail(publicationId))
                    },
                    onNavigationClick = navigateToTab
                )
            }

            composable<MainRoutes.AdvancedFilters> {
                AdvancedFiltersScreen(
                    onNavigateBack = {
                        navController.popBackStack()
                    },
                    // "Mostrar N Resultados" vuelve al feed principal con los filtros aplicados
                    onShowResults = {
                        navController.popBackStack(MainRoutes.PublicationList, inclusive = false)
                    }
                )
            }

            composable<MainRoutes.MyPublications> {
                MyPublicationsScreen(
                    onNavigateBack = {
                        navController.popBackStack()
                    },
                    onCreateClick = {
                        navController.navigate(MainRoutes.PublicationScreen)
                    },
                    onEditClick = { publicationId ->
                        navController.navigate(MainRoutes.EditPublication(publicationId))
                    },
                    onDeleteClick = { publicationId ->
                        navController.navigate(MainRoutes.DeletePublication(publicationId))
                    },
                    onViewPublication = { publicationId ->
                        navController.navigate(MainRoutes.PublicationDetail(publicationId))
                    },
                    onNavigationClick = navigateToTab
                )
            }

            composable<MainRoutes.EditPublication> {
                val args = it.toRoute<MainRoutes.EditPublication>()
                EditPublicationScreen(
                    publicationId = args.publicationId,
                    onNavigateBack = {
                        navController.popBackStack()
                    },
                    onNavigateToMyPublications = {
                        navController.popBackStack(MainRoutes.MyPublications, inclusive = false)
                    }
                )
            }

            composable<MainRoutes.DeletePublication> {
                val args = it.toRoute<MainRoutes.DeletePublication>()
                DeletePublicationScreen(
                    publicationId = args.publicationId,
                    onNavigateBack = {
                        navController.popBackStack()
                    },
                    onNavigateToMyPublications = {
                        navController.popBackStack(MainRoutes.MyPublications, inclusive = false)
                    }
                )
            }

            composable<MainRoutes.Profile> {
                val context = LocalContext.current
                ProfileScreen(
                    onNavigateBack = {
                        navController.popBackStack()
                    },
                    onNavigationClick = navigateToTab,
                    // "Mis Publicaciones" del perfil abre la gestión de publicaciones
                    onMyPublications = {
                        navController.navigate(MainRoutes.MyPublications)
                    },
                    onViewAllBadges = {
                        navController.navigate(MainRoutes.Badges)
                    },
                    onViewReputation = {
                        navController.navigate(MainRoutes.Reputation)
                    },
                    // Las preferencias de alertas y los ajustes están en "Editar Perfil"
                    onAlertPreferences = {
                        navController.navigate(MainRoutes.EditProfile)
                    },
                    onOpenSettings = {
                        navController.navigate(MainRoutes.EditProfile)
                    },
                    onShareProfile = {
                        val intent = Intent(Intent.ACTION_SEND).apply {
                            type = "text/plain"
                            putExtra(Intent.EXTRA_TEXT, "¡Conoce mi perfil de voluntaria en Patitas Conectadas! Nivel 2 · Protector")
                        }
                        context.startActivity(Intent.createChooser(intent, "Compartir perfil"))
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