package co.edu.adopcionmascota.features.profile

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import co.edu.adopcionmascota.core.component.AppBottomBar
import co.edu.adopcionmascota.core.component.AppDestination
import co.edu.adopcionmascota.core.component.ConfirmAlertDialog
import co.edu.adopcionmascota.core.theme.Background
import co.edu.adopcionmascota.core.util.RequestResult
import co.edu.adopcionmascota.features.profile.component.AccountSection
import co.edu.adopcionmascota.features.profile.component.BadgesSection
import co.edu.adopcionmascota.features.profile.component.ProfileHeaderCard
import co.edu.adopcionmascota.features.profile.component.ProfileMetricsSection
import co.edu.adopcionmascota.features.profile.component.ProfileStatusRow
import co.edu.adopcionmascota.features.profile.component.ProfileTopBar
import co.edu.adopcionmascota.features.profile.component.ReputationCard

@Composable
fun ProfileScreen(
    onNavigateBack: () -> Unit = {},
    onNavigationClick: (AppDestination) -> Unit = {},
    onEditProfile: () -> Unit = {},
    onMyPublications: () -> Unit = {},
    onEditPersonalData: () -> Unit = {},
    onAlertPreferences: () -> Unit = {},
    onViewAllBadges: () -> Unit = {},
    onShareProfile: () -> Unit = {},
    onOpenSettings: () -> Unit = {},
    onAccountDeleted: () -> Unit = {},
    viewModel: ProfileViewModel = viewModel()
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val state by viewModel.uiState.collectAsState()

    // Mensajes de la eliminación de cuenta.
    // El Snackbar se muestra primero y luego se navega (misma rama, como en el login).
    LaunchedEffect(state.deleteResult) {
        when (val result = state.deleteResult) {
            is RequestResult.Success -> {
                snackbarHostState.showSnackbar(result.message)
                viewModel.resetDeleteResult()
                onAccountDeleted()
            }

            is RequestResult.Failure -> {
                snackbarHostState.showSnackbar(result.errorMessage)
                viewModel.resetDeleteResult()
            }

            is RequestResult.Loading, null -> Unit
        }
    }

    Scaffold(
        modifier = Modifier.statusBarsPadding(),
        containerColor = Background,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = {
            AppBottomBar(
                currentDestination = AppDestination.PROFILE,
                onDestinationSelected = onNavigationClick
            )
        }
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
        ) {

            ProfileTopBar(onNavigateBack = onNavigateBack)

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {

                ProfileStatusRow(
                    isActiveVolunteer = state.isActiveVolunteer,
                    onShareClick = onShareProfile,
                    onSettingsClick = onOpenSettings
                )

                Spacer(modifier = Modifier.height(12.dp))

                ProfileHeaderCard(
                    state = state,
                    onEditClick = onEditProfile
                )

                Spacer(modifier = Modifier.height(24.dp))

                ProfileMetricsSection(
                    state = state,
                    onViewPublicationsClick = onMyPublications
                )

                Spacer(modifier = Modifier.height(24.dp))

                ReputationCard(state = state)

                Spacer(modifier = Modifier.height(24.dp))

                BadgesSection(
                    badges = state.badges,
                    totalBadges = 8, // Pendiente: vendrá del repositorio
                    onViewAllClick = onViewAllBadges
                )

                Spacer(modifier = Modifier.height(24.dp))

                AccountSection(
                    myPublicationsCount = state.myPublicationsCount,
                    onMyPublicationsClick = onMyPublications,
                    onEditPersonalDataClick = onEditPersonalData,
                    onAlertPreferencesClick = onAlertPreferences,
                    onDeleteAccountClick = viewModel::onDeleteAccountClick
                )

                Spacer(modifier = Modifier.height(24.dp))
            }
        }

        // Diálogo de confirmación para eliminar la cuenta
        if (state.showDeleteDialog) {
            ConfirmAlertDialog(
                title = "¿Eliminar tu cuenta?",
                message = "Se borrarán tu perfil, tus publicaciones y tus logros. Esta acción no se puede deshacer.",
                onConfirm = viewModel::onConfirmDeleteAccount,
                onDismiss = viewModel::onDismissDeleteDialog,
                confirmText = "Eliminar"
            )
        }
    }
}