package co.edu.adopcionmascota.core.component

import androidx.compose.ui.res.stringResource
import co.edu.adopcionmascota.R
import co.edu.adopcionmascota.domain.model.UserRole
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp

enum class AppDestination {
    EXPLORE,
    PUBLISH,
    ALERTS,
    PROFILE,
    MODERATE
}

// Rol de la sesión; lo entrega la navegación principal para no pasarlo por cada pantalla
val LocalUserRole = staticCompositionLocalOf { UserRole.USER }

@Composable
fun AppBottomBar(
    currentDestination: AppDestination,
    onDestinationSelected: (AppDestination) -> Unit
) {

    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 5.dp
    ) {

        NavigationBarItem(
            selected = currentDestination == AppDestination.EXPLORE,
            onClick = {
                onDestinationSelected(AppDestination.EXPLORE)
            },
            icon = {
                Icon(
                    Icons.Outlined.Pets,
                    contentDescription = stringResource(R.string.nav_explore)
                )
            },
            label = {
                Text(stringResource(R.string.nav_explore))
            }
        )

        // Cada rol ve solo su opción: el moderador revisa y el usuario publica
        if (LocalUserRole.current == UserRole.ADMIN) {
            NavigationBarItem(
                selected = currentDestination == AppDestination.MODERATE,
                onClick = {
                    onDestinationSelected(AppDestination.MODERATE)
                },
                icon = {
                    Icon(
                        Icons.Outlined.Gavel,
                        contentDescription = stringResource(R.string.nav_moderate)
                    )
                },
                label = {
                    Text(stringResource(R.string.nav_moderate))
                }
            )
        } else {
            NavigationBarItem(
                selected = currentDestination == AppDestination.PUBLISH,
                onClick = {
                    onDestinationSelected(AppDestination.PUBLISH)
                },
                icon = {
                    Icon(
                        Icons.Outlined.AddCircleOutline,
                        contentDescription = stringResource(R.string.nav_publish)
                    )
                },
                label = {
                    Text(stringResource(R.string.nav_publish))
                }
            )
        }

        NavigationBarItem(
            selected = currentDestination == AppDestination.ALERTS,
            onClick = {
                onDestinationSelected(AppDestination.ALERTS)
            },
            icon = {
                Icon(
                    Icons.Outlined.Notifications,
                    contentDescription = stringResource(R.string.nav_alerts)
                )
            },
            label = {
                Text(stringResource(R.string.nav_alerts))
            }
        )

        NavigationBarItem(
            selected = currentDestination == AppDestination.PROFILE,
            onClick = {
                onDestinationSelected(AppDestination.PROFILE)
            },
            icon = {
                Icon(
                    Icons.Outlined.Person,
                    contentDescription = stringResource(R.string.nav_profile)
                )
            },
            label = {
                Text(stringResource(R.string.nav_profile))
            }
        )

    }
}