package co.edu.adopcionmascota.navigation

import co.edu.adopcionmascota.R
import co.edu.adopcionmascota.features.profile.ProfileViewModel
import co.edu.adopcionmascota.features.moderator.panel.ModerationScreen
import co.edu.adopcionmascota.features.moderator.ModeratorProfileViewModel
import co.edu.adopcionmascota.features.moderator.ModeratorScreen
import co.edu.adopcionmascota.core.component.LocalUserRole
import androidx.compose.runtime.CompositionLocalProvider
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
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import co.edu.adopcionmascota.domain.model.UserRole
import co.edu.adopcionmascota.features.request.AdoptionRequestsScreen
import co.edu.adopcionmascota.features.password.ResetPasswordScreen
import co.edu.adopcionmascota.features.request.application.AdoptionApplicationScreen
import co.edu.adopcionmascota.features.statistics.StatisticsScreen
import co.edu.adopcionmascota.features.favorites.FavoritesScreen


@Composable
fun AppNavigation(
    resetPasswordCode: String? = null,
    onResetPasswordHandled: () -> Unit = {},
    sessionViewModel: SessionViewModel = hiltViewModel()
) {
    // Observa el estado de la sesión guardada en Data Store
    val sessionState by sessionViewModel.sessionState.collectAsStateWithLifecycle()

    Surface(modifier = Modifier.fillMaxSize()) {
        // El enlace de recuperación abierto desde el correo tiene prioridad (guía 20, actividad 2)
        if (resetPasswordCode != null) {
            ResetPasswordScreen(oobCode = resetPasswordCode, onFinished = onResetPasswordHandled)
            return@Surface
        }
        when (val state = sessionState) {
            is SessionState.Loading -> {
                // Indicador de carga mientras se lee la sesión guardada
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }

            // Sin sesión: pantallas de autenticación
            is SessionState.NotAuthenticated -> AuthNavigation()

            // Con sesión: navegación principal según el rol guardado
            is SessionState.Authenticated -> MainNavigation(role = state.session.role)
        }
    }
}

@Composable
private fun AuthNavigation() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = MainRoutes.Home
    ) {
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
            // Sin callback al autenticar: al guardarse la sesión,
            // AppNavigation cambia automáticamente a MainNavigation
            LoginScreen(
                onNavigationPasswordScreen = {
                    navController.navigate(MainRoutes.PasswordScreen)
                },
                onNavigationRegister = {
                    navController.navigate(MainRoutes.Register)
                }
            )
        }

        composable<MainRoutes.Register> {
            RegisterScreen(
                onNavigateToBack = {
                    navController.popBackStack()
                },
                // Registro exitoso → pantalla de confirmación (sin volver al formulario)
                onRegisterSuccess = { name, userId ->
                    navController.navigate(MainRoutes.RegisterSuccess(name, userId)) {
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

        // Sus botones abren la sesión del usuario nuevo y AppNavigation cambia de grafo
        composable<MainRoutes.RegisterSuccess> {
            RegisterSuccessScreen(
                // Correo sin verificar: se vuelve al login (guía 20, actividad 1)
                onGoToLogin = {
                    navController.navigate(MainRoutes.Login) { popUpTo(MainRoutes.Home) }
                }
            )
        }

        composable<MainRoutes.PasswordScreen> {
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
    }
}

@Composable
private fun MainNavigation(role: UserRole) {
    val navController = rememberNavController()

    // Cada rol arranca en su pantalla principal
    val isAdmin = role == UserRole.MODERATOR
    val homeRoute: MainRoutes = if (isAdmin) MainRoutes.ModeratorPanel else MainRoutes.PublicationList

    // Barra inferior compartida: cada pestaña abre su pantalla sin apilar copias
    val navigateToTab: (AppDestination) -> Unit = { destination ->
        when (destination) {
            AppDestination.EXPLORE ->
                if (isAdmin) {
                    navController.navigate(MainRoutes.PublicationList) {
                        popUpTo(homeRoute)
                        launchSingleTop = true
                    }
                } else {
                    navController.popBackStack(MainRoutes.PublicationList, inclusive = false)
                }

            AppDestination.PUBLISH -> navController.navigate(MainRoutes.PublicationScreen)

            AppDestination.ALERTS -> navController.navigate(MainRoutes.Notifications) {
                popUpTo(homeRoute)
                launchSingleTop = true
            }

            AppDestination.PROFILE -> {
                val profileRoute: MainRoutes = if (isAdmin) MainRoutes.Moderator else MainRoutes.Profile
                navController.navigate(profileRoute) {
                    popUpTo(homeRoute)
                    launchSingleTop = true
                }
            }

            AppDestination.MODERATE -> {
                navController.popBackStack(MainRoutes.ModeratorPanel, inclusive = false)
                Unit
            }
        }
    }

    CompositionLocalProvider(LocalUserRole provides role) {
        NavHost(
            navController = navController,
            startDestination = homeRoute
        ) {
            composable<MainRoutes.ModeratorPanel> {
                ModerationScreen(
                    onNavigationClick = navigateToTab,
                    // "Inspeccionar" abre el detalle completo de la publicación
                    onInspect = { publicationId ->
                        navController.navigate(MainRoutes.PublicationDetail(publicationId))
                    },
                    onStatistics = {
                        navController.navigate(MainRoutes.Statistics)
                    }
                )
            }

            composable<MainRoutes.Statistics> {
                StatisticsScreen(
                    onNavigateBack = {
                        navController.popBackStack()
                    }
                )
            }

            composable<MainRoutes.Favorites> {
                FavoritesScreen(
                    onNavigateBack = {
                        navController.popBackStack()
                    },
                    onPublicationClick = { publicationId ->
                        navController.navigate(MainRoutes.PublicationDetail(publicationId))
                    }
                )
            }

            composable<MainRoutes.AdoptionRequests> {
                AdoptionRequestsScreen(
                    onNavigateBack = {
                        navController.popBackStack()
                    },
                    onViewPublication = { publicationId ->
                        navController.navigate(MainRoutes.PublicationDetail(publicationId))
                    }
                )
            }

            composable<MainRoutes.Moderator> {
                val moderatorViewModel: ModeratorProfileViewModel = hiltViewModel()
                ModeratorScreen(
                    onNavigationClick = navigateToTab,
                    onLogout = moderatorViewModel::logout,
                    onNotifications = { navigateToTab(AppDestination.ALERTS) },
                    onViewHistory = { navController.navigate(MainRoutes.Statistics) }
                )
            }

            composable<MainRoutes.PublicationList> {
                PublicationListScreen(
                    onPublicationClick = { publication ->
                        navController.navigate(
                            MainRoutes.PublicationDetail(publication.id)
                        )
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

            composable<MainRoutes.PublicationDetail> {
                val args = it.toRoute<MainRoutes.PublicationDetail>()
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
                    },
                    onStartAdoptionApplication = {
                        navController.navigate(MainRoutes.AdoptionApplication(args.publicationId))
                    }
                )

            }

            composable<MainRoutes.AdoptionApplication> {
                AdoptionApplicationScreen(
                    onNavigateBack = { navController.popBackStack() },
                    // Al volver, el detalle ya muestra la solicitud como enviada
                    onSent = { navController.popBackStack() }
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
                    },
                    onViewRequests = {
                        navController.navigate(MainRoutes.AdoptionRequests)
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

            composable<MainRoutes.PasswordScreen> {
                // Con sesión iniciada se llega desde "Editar Perfil": ambos botones regresan
                PasswordScreen(
                    onNavigateToLogin = {
                        navController.popBackStack()
                    },
                    onNavigateToRegister = {
                        navController.popBackStack()
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
                    onNavigationClick = navigateToTab,
                    onRequestsClick = {
                        navController.navigate(MainRoutes.AdoptionRequests)
                    }
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
                            putExtra(
                                Intent.EXTRA_TEXT,
                                context.getString(R.string.share_profile_text)
                            )
                        }
                        context.startActivity(Intent.createChooser(intent, context.getString(R.string.share_profile_chooser)))
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
                    },
                    onStatistics = {
                        navController.navigate(MainRoutes.Statistics)
                    },
                    onFavorites = {
                        navController.navigate(MainRoutes.Favorites)
                    }
                )
            }

            composable<MainRoutes.EditProfile> {
                val profileViewModel: ProfileViewModel = hiltViewModel()
                EditProfileScreen(
                    onNavigateBack = {
                        navController.popBackStack()
                    },
                    // "Guardar Cambios" lleva a "Perfil editado" y quita la edición de la pila
                    onProfileSaved = { changes ->
                        navController.navigate(MainRoutes.ProfileUpdated(changes)) {
                            popUpTo(MainRoutes.EditProfile) { inclusive = true }
                        }
                    },
                    onNavigateToPassword = {
                        navController.navigate(MainRoutes.PasswordScreen)
                    },
                    // Igual que al eliminar la cuenta: se cierra la sesión y vuelve al inicio
                    onAccountDeactivated = profileViewModel::logout
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
                            popUpTo<MainRoutes.ProfileUpdated> { inclusive = true }
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
                            popUpTo(homeRoute) { inclusive = true }
                        }
                    }
                )
            }

            composable<MainRoutes.AccountDeleted> {
                val profileViewModel: ProfileViewModel = hiltViewModel()
                AccountDeletedScreen(
                    // Se cierra la sesión aquí para que antes se vea la despedida
                    onGoToHome = profileViewModel::logout
                )
            }


        }

    }
}