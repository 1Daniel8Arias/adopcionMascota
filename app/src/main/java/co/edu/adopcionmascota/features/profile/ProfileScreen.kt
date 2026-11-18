package co.edu.adopcionmascota.features.profile

import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.BarChart
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.foundation.layout.width
import androidx.compose.ui.res.stringResource
import co.edu.adopcionmascota.R
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import co.edu.adopcionmascota.core.component.AppBottomBar
import co.edu.adopcionmascota.core.component.AppDestination
import co.edu.adopcionmascota.core.theme.Background
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
    onViewReputation: () -> Unit = {},    // Tarjeta de reputación → stringResource(R.string.reputation_system)
    onDeleteAccount: () -> Unit = {},     // Abre la confirmación "Eliminar cuenta"
    onStatistics: () -> Unit = {},        // Estadísticas de la comunidad
    onFavorites: () -> Unit = {},         // Publicaciones con "Me encanta"
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val state by viewModel.uiState.collectAsState()

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

                ReputationCard(state = state, onClick = onViewReputation)

                Spacer(modifier = Modifier.height(12.dp))

                // Publicaciones a las que el usuario les dio "Me encanta"
                OutlinedButton(onClick = onFavorites, modifier = Modifier.fillMaxWidth()) {
                    Icon(Icons.Outlined.Favorite, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(stringResource(R.string.favorites_title))
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Estadísticas de la comunidad (datos reales de Room)
                OutlinedButton(onClick = onStatistics, modifier = Modifier.fillMaxWidth()) {
                    Icon(Icons.Outlined.BarChart, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(stringResource(R.string.statistics_title))
                }

                Spacer(modifier = Modifier.height(24.dp))

                BadgesSection(
                    badges = state.badges,
                    totalBadges = badgeCatalog().size,
                    onViewAllClick = onViewAllBadges
                )

                Spacer(modifier = Modifier.height(24.dp))

                AccountSection(
                    myPublicationsCount = state.myPublicationsCount,
                    onMyPublicationsClick = onMyPublications,
                    onEditPersonalDataClick = onEditPersonalData,
                    onAlertPreferencesClick = onAlertPreferences,
                    onDeleteAccountClick = onDeleteAccount,
                    onLogoutClick = viewModel::logout
                )

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}