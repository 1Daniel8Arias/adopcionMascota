package co.edu.adopcionmascota.core.component

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
                    contentDescription = "Explorar"
                )
            },
            label = {
                Text("Explorar")
            }
        )

        NavigationBarItem(
            selected = currentDestination == AppDestination.PUBLISH,
            onClick = {
                onDestinationSelected(AppDestination.PUBLISH)
            },
            icon = {
                Icon(
                    Icons.Outlined.AddCircleOutline,
                    contentDescription = "Publicar"
                )
            },
            label = {
                Text("Publicar")
            }
        )

        NavigationBarItem(
            selected = currentDestination == AppDestination.ALERTS,
            onClick = {
                onDestinationSelected(AppDestination.ALERTS)
            },
            icon = {
                Icon(
                    Icons.Outlined.Notifications,
                    contentDescription = "Alertas"
                )
            },
            label = {
                Text("Alertas")
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
                    contentDescription = "Perfil"
                )
            },
            label = {
                Text("Perfil")
            }
        )

    }
}