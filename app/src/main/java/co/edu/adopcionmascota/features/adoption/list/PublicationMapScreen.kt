package co.edu.adopcionmascota.features.adoption.list

import androidx.compose.ui.res.stringResource
import co.edu.adopcionmascota.R
import android.content.Intent
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.automirrored.outlined.ViewList
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material.icons.outlined.Tune
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import co.edu.adopcionmascota.core.component.MapBox
import co.edu.adopcionmascota.core.component.AppBottomBar
import co.edu.adopcionmascota.core.component.AppDestination
import co.edu.adopcionmascota.core.component.AppSearchBar
import co.edu.adopcionmascota.core.component.AppTag
import co.edu.adopcionmascota.core.theme.AppPrimary
import co.edu.adopcionmascota.core.theme.AppRed
import co.edu.adopcionmascota.core.theme.DarkText
import co.edu.adopcionmascota.core.theme.SecondaryText
import co.edu.adopcionmascota.domain.model.PetType
import coil3.compose.AsyncImage
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import co.edu.adopcionmascota.core.util.rememberImageModel
import co.edu.adopcionmascota.core.util.relativeTime

// Pantalla "Feed Principal - Vista Mapa": mapa de Mapbox con un marcador por cada mascota
@Composable
fun PublicationMapScreen(
    onNavigateBack: () -> Unit,                     // Botón stringResource(R.string.list) / volver
    onOpenFilters: () -> Unit,                      // "Filtros avanzados"
    onViewPublication: (String) -> Unit,            // stringResource(R.string.view_card)
    onNavigationClick: (AppDestination) -> Unit,
    viewModel: PublicationMapViewModel = hiltViewModel()
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val state by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(state.infoMessage) {
        state.infoMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.resetInfoMessage()
        }
    }

    Scaffold(
        modifier = Modifier.statusBarsPadding(),
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = {
            AppBottomBar(
                currentDestination = AppDestination.EXPLORE,
                onDestinationSelected = onNavigationClick
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            MapBox(
                modifier = Modifier.fillMaxSize(),
                publications = state.visiblePublications,
                locationButtonTopPadding = 160.dp, // Debajo del buscador y los filtros
                onMarkerClick = { viewModel.onMarkerClick(it.id) },
                onMapClick = { viewModel.onCloseCard() }
            )

            // Buscador, botones y filtros sobre el mapa
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = onNavigateBack,
                        colors = IconButtonDefaults.iconButtonColors(containerColor = Color.White)
                    ) {
                        Icon(Icons.AutoMirrored.Outlined.ArrowBack, contentDescription = stringResource(R.string.back_to_list), tint = DarkText)
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                    Box(modifier = Modifier.weight(1f)) {
                        AppSearchBar(
                            value = state.searchText,
                            onValueChange = viewModel::onSearchChange,
                            placeholder = stringResource(R.string.search_map),
                            onFilterClick = onOpenFilters
                        )
                    }
                }
                Row(
                    modifier = Modifier.horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    MapChip(stringResource(R.string.map_all, state.countFor(null)), state.typeFilter == null) { viewModel.onTypeFilterChange(null) }
                    MapChip(stringResource(R.string.map_adoption, state.countFor(PetType.ADOPCION)), state.typeFilter == PetType.ADOPCION) { viewModel.onTypeFilterChange(PetType.ADOPCION) }
                    MapChip(stringResource(R.string.map_lost, state.countFor(PetType.PERDIDO)), state.typeFilter == PetType.PERDIDO) { viewModel.onTypeFilterChange(PetType.PERDIDO) }
                    MapChip(stringResource(R.string.map_found, state.countFor(PetType.ENCONTRADO)), state.typeFilter == PetType.ENCONTRADO) { viewModel.onTypeFilterChange(PetType.ENCONTRADO) }
                    MapChip(stringResource(R.string.map_vet, state.countFor(PetType.VETERINARIA)), state.typeFilter == PetType.VETERINARIA) { viewModel.onTypeFilterChange(PetType.VETERINARIA) }
                }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    MapActionButton(text = stringResource(R.string.list), icon = Icons.AutoMirrored.Outlined.ViewList, onClick = onNavigateBack)
                    MapActionButton(text = stringResource(R.string.filters), icon = Icons.Outlined.Tune, onClick = onOpenFilters)
                }
            }

            // Tarjeta de la mascota seleccionada
            val selected = state.selected
            if (selected != null) {
                SelectedPetCard(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(12.dp),
                    name = selected.name,
                    type = selected.type,
                    subtitle = stringResource(R.string.pet_subtitle, selected.gender, selected.age),
                    description = selected.description,
                    meta = stringResource(R.string.map_meta, relativeTime(selected.createdAt), selected.distancia, selected.location),
                    sightings = state.sightings[selected.id] ?: 0,
                    image = selected.image,
                    isLiked = selected.id in state.favoriteIds,
                    onLike = { viewModel.onLikeClick(selected.id) },
                    onClose = viewModel::onCloseCard,
                    onShare = {
                        val intent = Intent(Intent.ACTION_SEND).apply {
                            type = "text/plain"
                            putExtra(Intent.EXTRA_TEXT, context.getString(R.string.map_share_text, selected.name, selected.location))
                        }
                        context.startActivity(Intent.createChooser(intent, context.getString(R.string.spread_chooser)))
                    },
                    onSeen = viewModel::onSeenClick,
                    onViewDetail = { onViewPublication(selected.id) }
                )
            } else if (state.visiblePublications.isEmpty()) {
                Surface(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(12.dp),
                    shape = RoundedCornerShape(16.dp),
                    color = Color.White
                ) {
                    Text(
                        text = stringResource(R.string.map_empty),
                        modifier = Modifier.padding(16.dp),
                        color = SecondaryText
                    )
                }
            }
        }
    }
}

private fun PetType.markerColor(): Color = when (this) {
    PetType.ADOPCION -> AppPrimary
    PetType.PERDIDO -> AppRed
    PetType.ENCONTRADO -> Color(0xFF067647)
    PetType.VETERINARIA -> Color(0xFF7A5AF8)
}

@Composable
private fun PetType.label(): String = when (this) {
    PetType.ADOPCION -> stringResource(R.string.type_adoption)
    PetType.PERDIDO -> stringResource(R.string.type_lost)
    PetType.ENCONTRADO -> stringResource(R.string.type_found)
    PetType.VETERINARIA -> stringResource(R.string.type_vet)
}

@Composable
private fun MapChip(text: String, selected: Boolean, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        shape = CircleShape,
        color = if (selected) Color.Black else Color.White,
        shadowElevation = 2.dp
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
            fontSize = 12.sp,
            color = if (selected) Color.White else DarkText
        )
    }
}

@Composable
private fun MapActionButton(
    text: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit
) {
    Surface(onClick = onClick, shape = CircleShape, color = Color.White, shadowElevation = 2.dp) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, contentDescription = null, tint = AppPrimary, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text(text = text, fontSize = 12.sp, color = DarkText)
        }
    }
}

@Composable
private fun SelectedPetCard(
    modifier: Modifier,
    name: String,
    type: PetType,
    subtitle: String,
    description: String,
    meta: String,
    sightings: Int,
    image: String,
    isLiked: Boolean,
    onLike: () -> Unit,
    onClose: () -> Unit,
    onShare: () -> Unit,
    onSeen: () -> Unit,
    onViewDetail: () -> Unit
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        color = Color.White,
        shadowElevation = 8.dp
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                AppTag(text = type.label(), backgroundColor = type.markerColor().copy(alpha = 0.12f), textColor = type.markerColor())
                Spacer(modifier = Modifier.weight(1f))
                IconButton(onClick = onLike, modifier = Modifier.size(28.dp)) {
                    Icon(
                        imageVector = if (isLiked) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                        contentDescription = stringResource(R.string.like),
                        tint = if (isLiked) Color.Red else SecondaryText
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                IconButton(onClick = onClose, modifier = Modifier.size(28.dp)) {
                    Icon(Icons.Outlined.Close, contentDescription = stringResource(R.string.close), tint = SecondaryText)
                }
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                AsyncImage(
                    model = rememberImageModel(image),
                    contentDescription = name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(64.dp)
                        .clip(RoundedCornerShape(14.dp))
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = name, fontSize = 17.sp, fontWeight = FontWeight.Bold, color = DarkText)
                    Text(text = subtitle, fontSize = 12.sp, color = SecondaryText)
                    Text(text = description, fontSize = 12.sp, color = DarkText, maxLines = 2)
                }
            }
            Text(text = meta, fontSize = 11.sp, color = SecondaryText)
            if (sightings > 0) {
                Text(text = stringResource(R.string.sightings, sightings), fontSize = 11.sp, color = AppPrimary)
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedButton(onClick = onShare, modifier = Modifier.weight(1f), shape = CircleShape) {
                    Icon(Icons.Outlined.Share, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = stringResource(R.string.spread), fontSize = 12.sp)
                }
                OutlinedButton(onClick = onSeen, modifier = Modifier.weight(1f), shape = CircleShape) {
                    Icon(Icons.Outlined.Visibility, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = stringResource(R.string.seen_it), fontSize = 12.sp)
                }
                Button(
                    onClick = onViewDetail,
                    modifier = Modifier.weight(1f),
                    shape = CircleShape,
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Black)
                ) {
                    Text(text = stringResource(R.string.view_card), fontSize = 12.sp)
                }
            }
        }
    }
}
