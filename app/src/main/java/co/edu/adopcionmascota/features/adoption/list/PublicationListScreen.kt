package co.edu.adopcionmascota.features.adoption.list

import androidx.compose.ui.res.stringResource
import co.edu.adopcionmascota.R
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import android.content.Intent
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import co.edu.adopcionmascota.domain.model.Publication
import co.edu.adopcionmascota.features.adoption.list.component.ExploreFilters
import co.edu.adopcionmascota.features.adoption.list.component.ExploreViewMode
import co.edu.adopcionmascota.features.adoption.list.component.ExploreViewSelector
import co.edu.adopcionmascota.features.adoption.list.component.PublicationCard

import co.edu.adopcionmascota.core.component.AppBottomBar
import co.edu.adopcionmascota.core.component.AppDestination
import co.edu.adopcionmascota.core.component.AppSearchBar
import co.edu.adopcionmascota.core.component.AppTopBar
import androidx.compose.runtime.collectAsState
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.ui.Alignment
import co.edu.adopcionmascota.core.component.AppTag

@Composable
fun PublicationListScreen(
    viewModel: PublicationListViewModel = hiltViewModel(),
    onPublicationClick: (Publication) -> Unit = {},
    onPublishClick: () -> Unit = {},
    onNavigationClick: (AppDestination) -> Unit = {},
    onMapClick: () -> Unit = {},          // Botón stringResource(R.string.map) → Vista Mapa
    onFiltersClick: () -> Unit = {},      // "Filtros avanzados" → Filtros y Búsqueda
    onNotificationClick: () -> Unit = {}, // Campana de la barra superior
    onProfileClick: () -> Unit = {}       // Avatar de la barra superior

) {

    // =====================================================
    // ESTADO DEL VIEWMODEL
    // =====================================================

    val state by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    // =====================================================
    // ESTRUCTURA PRINCIPAL
    // =====================================================

    Scaffold(
        modifier = Modifier.statusBarsPadding(),
        topBar = {

            AppTopBar(
                title = stringResource(R.string.app_short_name),
                location = stringResource(R.string.feed_location),
                onNotificationClick = onNotificationClick,
                onProfileClick = onProfileClick
            )
        },

        bottomBar = {

            AppBottomBar(
                currentDestination = AppDestination.EXPLORE,
                onDestinationSelected = onNavigationClick
            )
        }

    ) { paddingValues ->

        LazyColumn(

            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),

            contentPadding = PaddingValues(
                start = 14.dp,
                end = 14.dp,
                top = 10.dp,
                bottom = 100.dp
            ),

            verticalArrangement =
                Arrangement.spacedBy(12.dp)
        ) {

            // =================================================
            // BUSCADOR
            // =================================================

            item {

                AppSearchBar(
                    value = state.searchText,

                    onValueChange = viewModel::onSearchChange,

                    placeholder =
                        stringResource(R.string.search_feed),

                    onFilterClick = onFiltersClick
                )
            }

            // Aviso de filtros avanzados aplicados
            if (state.advancedFilter.isActive) {
                item {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        AppTag(text = stringResource(R.string.advanced_filters_active, state.advancedFilter.activeCount))
                        TextButton(onClick = viewModel::onClearAdvancedFilters) {
                            Text(stringResource(R.string.clear))
                        }
                    }
                }
            }

            // =================================================
            // LISTA / MAPA
            // =================================================

            item {

                ExploreViewSelector(

                    // El feed siempre está en modo lista; "Mapa" abre la Vista Mapa
                    selectedMode = ExploreViewMode.LIST,

                    onModeSelected = {
                        if (it == ExploreViewMode.MAP) onMapClick()
                    },

                    // La etiqueta de distancia abre los filtros avanzados
                    distanceLabel = if (state.advancedFilter.maxDistanceKm < 20) stringResource(R.string.distance_less_than, state.advancedFilter.maxDistanceKm) else stringResource(R.string.distance),
                    onDistanceClick = onFiltersClick
                )
            }

            // =================================================
            // FILTROS
            // =================================================

            item {

                ExploreFilters(

                    selectedFilter = state.selectedFilter,

                    onFilterSelected = viewModel::onFilterSelected
                )
            }

            // =================================================
            // PUBLICACIONES
            // =================================================

                items(
                    items = state.visiblePublications,
                    key = {
                        it.id
                    }
                ) { publication ->

                    PublicationCard(

                        publication = publication,

                        onActionClick = {
                            onPublicationClick(publication)
                        },

                        onShareClick = {
                            val intent = Intent(Intent.ACTION_SEND).apply {
                                type = "text/plain"
                                putExtra(
                                    Intent.EXTRA_TEXT,
                                    context.getString(R.string.share_publication_text, publication.name, publication.location, publication.description)
                                )
                            }
                            context.startActivity(Intent.createChooser(intent, context.getString(R.string.share_publication)))
                        }
                    )
                }
            }

            // =================================================
            // CAMPAÑA VETERINARIA
            // =================================================


        }

}

