package co.edu.adopcionmascota.features.adoption.list

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
import co.edu.adopcionmascota.core.component.AppFloatingButton
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
    viewModel: PublicationListViewModel = viewModel(),
    onPublicationClick: (Publication) -> Unit = {},
    onPublishClick: () -> Unit = {},
    onNavigationClick: (AppDestination) -> Unit = {},
    onNavigationToPublications:()-> Unit,
    onMapClick: () -> Unit = {},          // Botón "Mapa" → Vista Mapa
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
                title = "Patitas Con...",
                location = "Armenia, Quindío",
                onNotificationClick = onNotificationClick,
                onProfileClick = onProfileClick
            )
        },

        floatingActionButton = {

            AppFloatingButton(
                text = "Publicar",
                onClick = onNavigationToPublications
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
                        "Buscar por nombre, raza, zona...",

                    onFilterClick = onFiltersClick
                )
            }

            // Aviso de filtros avanzados aplicados
            if (state.advancedFilter.isActive) {
                item {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        AppTag(text = "Filtros avanzados activos (${state.advancedFilter.activeCount})")
                        TextButton(onClick = viewModel::onClearAdvancedFilters) {
                            Text("Limpiar")
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
                    distanceLabel = if (state.advancedFilter.maxDistanceKm < 20) "< ${state.advancedFilter.maxDistanceKm} km" else "Distancia",
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
                                    "${publication.name} en ${publication.location}: ${publication.description} · Patitas Conectadas"
                                )
                            }
                            context.startActivity(Intent.createChooser(intent, "Compartir publicación"))
                        }
                    )
                }
            }

            // =================================================
            // CAMPAÑA VETERINARIA
            // =================================================


        }

}

