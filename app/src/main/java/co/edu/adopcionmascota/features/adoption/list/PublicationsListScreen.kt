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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import co.edu.adopcionmascota.domain.PetType
import co.edu.adopcionmascota.domain.Publications
import co.edu.adopcionmascota.features.adoption.list.component.ExploreFilter
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

@Composable
fun PublicationListScreen(
    viewModel: PublicationListViewModel = viewModel(),
    onPublicationClick: (Publications) -> Unit = {},
    onPublishClick: () -> Unit = {},
    onNavigationClick: (AppDestination) -> Unit = {},
    onNavigationToPublications:()-> Unit

) {

    // =====================================================
    // ESTADO DEL VIEWMODEL
    // =====================================================

    val publications by viewModel.publications.collectAsState()

    // =====================================================
    // ESTADOS DE LA INTERFAZ
    // =====================================================

    var searchText by remember {
        mutableStateOf("")
    }

    var selectedFilter by remember {
        mutableStateOf(ExploreFilter.TODAS)
    }

    var selectedView by remember {
        mutableStateOf(ExploreViewMode.LIST)
    }

    // =====================================================
    // FILTRAR PUBLICACIONES
    // =====================================================

    val filteredPublications = publications.filter { publication ->

        // -----------------------------
        // BUSQUEDA
        // -----------------------------

        val matchesSearch =
            searchText.isBlank() ||
                    publication.name.contains(
                        searchText,
                        ignoreCase = true
                    ) ||
                    publication.description.contains(
                        searchText,
                        ignoreCase = true
                    ) ||
                    publication.location.contains(
                        searchText,
                        ignoreCase = true
                    )

        // -----------------------------
        // FILTRO
        // -----------------------------

        val matchesFilter = when (selectedFilter) {

            ExploreFilter.TODAS -> true

            ExploreFilter.ADOPCION ->
                publication.type == PetType.ADOPCION

            ExploreFilter.PERDIDO ->
                publication.type == PetType.PERDIDO

            ExploreFilter.ENCONTRADO ->
                publication.type == PetType.ENCONTRADO
        }

        matchesSearch && matchesFilter
    }

    // =====================================================
    // ESTRUCTURA PRINCIPAL
    // =====================================================

    Scaffold(
        modifier = Modifier.statusBarsPadding(),
        topBar = {

            AppTopBar(
                title = "Patitas Con...",
                location = "Armenia, Quindío"
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
                    value = searchText,

                    onValueChange = {
                        searchText = it
                    },

                    placeholder =
                        "Buscar por nombre, raza, zona...",

                    onFilterClick = {
                        // Aquí posteriormente podemos
                        // abrir filtros avanzados.
                    }
                )
            }

            // =================================================
            // LISTA / MAPA
            // =================================================

            item {

                ExploreViewSelector(

                    selectedMode = selectedView,

                    onModeSelected = {
                        selectedView = it
                    }
                )
            }

            // =================================================
            // FILTROS
            // =================================================

            item {

                ExploreFilters(

                    selectedFilter = selectedFilter,

                    onFilterSelected = {
                        selectedFilter = it
                    }
                )
            }

            // =================================================
            // PUBLICACIONES
            // =================================================

                items(
                    items = filteredPublications,
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
                            // Compartir publicación
                        }
                    )
                }
            }

            // =================================================
            // CAMPAÑA VETERINARIA
            // =================================================


        }

}

