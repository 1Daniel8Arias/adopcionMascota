package co.edu.adopcionmascota.features.adoption.list.component

import androidx.compose.ui.res.stringResource
import co.edu.adopcionmascota.R

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.AddLocationAlt
import androidx.compose.material.icons.filled.Details
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.VolunteerActivism
import androidx.compose.material3.Icon
import androidx.compose.ui.graphics.vector.ImageVector


enum class ExploreFilter {
    TODAS,
    ADOPCION,
    PERDIDO,
    ENCONTRADO
}
@Composable
fun ExploreFilters(
    selectedFilter: ExploreFilter,
    onFilterSelected: (ExploreFilter) -> Unit
) {

    Row(
        modifier = Modifier.fillMaxWidth()
                .horizontalScroll(rememberScrollState()) // Habilita el desplazamiento horizontal
                .padding(horizontal = 16.dp), // Margen lateral para los extremos
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {

        FilterChip(
            selected = selectedFilter == ExploreFilter.TODAS,

            onClick = {
                onFilterSelected(ExploreFilter.TODAS)
            },

            label = {
                Text(stringResource(R.string.all))
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.List,
                    contentDescription = stringResource(R.string.all)
                )
            }
        )

        FilterChip(
            selected = selectedFilter == ExploreFilter.ADOPCION,

            onClick = {
                onFilterSelected(ExploreFilter.ADOPCION)
            },

            label = {
                Text(stringResource(R.string.adoption))
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.VolunteerActivism,
                    contentDescription = stringResource(R.string.all)
                )
            }
        )

        FilterChip(
            selected = selectedFilter == ExploreFilter.PERDIDO,

            onClick = {
                onFilterSelected(ExploreFilter.PERDIDO)
            },

            label = {
                Text(stringResource(R.string.lost))
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Details,
                    contentDescription = stringResource(R.string.lost)
                )
            }
        )

        FilterChip(
            selected = selectedFilter == ExploreFilter.ENCONTRADO,

            onClick = {
                onFilterSelected(ExploreFilter.ENCONTRADO)
            },
            label = {
                Text(stringResource(R.string.found))
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Home,
                    contentDescription =stringResource(R.string.found)
                )
            }
        )
    }
}