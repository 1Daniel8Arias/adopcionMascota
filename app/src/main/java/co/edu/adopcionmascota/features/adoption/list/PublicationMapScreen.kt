package co.edu.adopcionmascota.features.adoption.list

import android.content.Intent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
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
import androidx.compose.material.icons.outlined.MyLocation
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import co.edu.adopcionmascota.R
import co.edu.adopcionmascota.core.component.AppBottomBar
import co.edu.adopcionmascota.core.component.AppDestination
import co.edu.adopcionmascota.core.component.AppSearchBar
import co.edu.adopcionmascota.core.component.AppTag
import co.edu.adopcionmascota.core.theme.AppPrimary
import co.edu.adopcionmascota.core.theme.AppRed
import co.edu.adopcionmascota.core.theme.DarkText
import co.edu.adopcionmascota.core.theme.SecondaryText
import co.edu.adopcionmascota.domain.PetType
import coil3.compose.AsyncImage

// Pantalla "Feed Principal - Vista Mapa": imagen estática de un mapa con marcadores de las mascotas
@Composable
fun PublicationMapScreen(
    onNavigateBack: () -> Unit,                     // Botón "Lista" / volver
    onOpenFilters: () -> Unit,                      // "Filtros avanzados"
    onViewPublication: (String) -> Unit,            // "Ver Ficha"
    onNavigationClick: (AppDestination) -> Unit,
    viewModel: PublicationMapViewModel = viewModel()
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
            // Mapa estático con los marcadores encima
            BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
                Image(
                    painter = painterResource(id = R.drawable.icono_mapa),
                    contentDescription = "Mapa de mascotas cercanas",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )

                // "Tú estás aquí"
                Column(
                    modifier = Modifier.offset(x = maxWidth * 0.5f - 40.dp, y = maxHeight * 0.45f),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(18.dp)
                            .clip(CircleShape)
                            .background(Color.White)
                            .padding(3.dp)
                            .clip(CircleShape)
                            .background(AppPrimary)
                    )
                    Surface(shape = RoundedCornerShape(8.dp), color = Color.White) {
                        Text(
                            text = "Tú estás aquí",
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                            fontSize = 10.sp,
                            color = DarkText
                        )
                    }
                }

                state.visibleMarkers.forEach { marker ->
                    val selected = marker.publication.id == state.selectedId
                    Surface(
                        onClick = { viewModel.onMarkerClick(marker.publication.id) },
                        modifier = Modifier.offset(x = maxWidth * marker.x - 28.dp, y = maxHeight * marker.y),
                        shape = CircleShape,
                        color = if (selected) Color.Black else Color.White,
                        shadowElevation = 4.dp
                    ) {
                        Row(
                            modifier = Modifier.padding(4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            AsyncImage(
                                model = marker.publication.image,
                                contentDescription = marker.publication.name,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .size(30.dp)
                                    .clip(CircleShape)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = marker.publication.name,
                                modifier = Modifier.padding(end = 6.dp),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = if (selected) Color.White else marker.publication.type.markerColor()
                            )
                        }
                    }
                }
            }

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
                        Icon(Icons.AutoMirrored.Outlined.ArrowBack, contentDescription = "Volver a la lista", tint = DarkText)
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                    Box(modifier = Modifier.weight(1f)) {
                        AppSearchBar(
                            value = state.searchText,
                            onValueChange = viewModel::onSearchChange,
                            placeholder = "Buscar mascotas cerca a ti...",
                            onFilterClick = onOpenFilters
                        )
                    }
                }
                Row(
                    modifier = Modifier.horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    MapChip("Todos (${state.countFor(null)})", state.typeFilter == null) { viewModel.onTypeFilterChange(null) }
                    MapChip("Adopción (${state.countFor(PetType.ADOPCION)})", state.typeFilter == PetType.ADOPCION) { viewModel.onTypeFilterChange(PetType.ADOPCION) }
                    MapChip("🚨 Perdidos (${state.countFor(PetType.PERDIDO)})", state.typeFilter == PetType.PERDIDO) { viewModel.onTypeFilterChange(PetType.PERDIDO) }
                    MapChip("Encontrados (${state.countFor(PetType.ENCONTRADO)})", state.typeFilter == PetType.ENCONTRADO) { viewModel.onTypeFilterChange(PetType.ENCONTRADO) }
                }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    MapActionButton(text = "Lista", icon = Icons.AutoMirrored.Outlined.ViewList, onClick = onNavigateBack)
                    MapActionButton(text = "Re-centrar área", icon = Icons.Outlined.MyLocation, onClick = viewModel::onRecenter)
                    MapActionButton(text = "Filtros", icon = Icons.Outlined.Tune, onClick = onOpenFilters)
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
                    subtitle = "${selected.gender} • ${selected.age}",
                    description = selected.description,
                    meta = "${selected.time} · A ${selected.distancia} de ti · ${selected.location}",
                    sightings = state.sightings[selected.id] ?: 0,
                    image = selected.image,
                    onClose = viewModel::onCloseCard,
                    onShare = {
                        val intent = Intent(Intent.ACTION_SEND).apply {
                            type = "text/plain"
                            putExtra(Intent.EXTRA_TEXT, "Ayúdanos a difundir: ${selected.name} en ${selected.location} · Patitas Conectadas")
                        }
                        context.startActivity(Intent.createChooser(intent, "Difundir"))
                    },
                    onSeen = viewModel::onSeenClick,
                    onViewDetail = { onViewPublication(selected.id) }
                )
            } else if (state.visibleMarkers.isEmpty()) {
                Surface(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(12.dp),
                    shape = RoundedCornerShape(16.dp),
                    color = Color.White
                ) {
                    Text(
                        text = "No hay mascotas con estos filtros en el mapa",
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
}

private fun PetType.label(): String = when (this) {
    PetType.ADOPCION -> "En Adopción"
    PetType.PERDIDO -> "🚨 Perdido"
    PetType.ENCONTRADO -> "Encontrado"
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
    image: Int,
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
                IconButton(onClick = onClose, modifier = Modifier.size(28.dp)) {
                    Icon(Icons.Outlined.Close, contentDescription = "Cerrar", tint = SecondaryText)
                }
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                AsyncImage(
                    model = image,
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
                Text(text = "Has reportado $sightings avistamiento(s)", fontSize = 11.sp, color = AppPrimary)
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedButton(onClick = onShare, modifier = Modifier.weight(1f), shape = CircleShape) {
                    Icon(Icons.Outlined.Share, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = "Difundir", fontSize = 12.sp)
                }
                OutlinedButton(onClick = onSeen, modifier = Modifier.weight(1f), shape = CircleShape) {
                    Icon(Icons.Outlined.Visibility, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = "Lo he visto", fontSize = 12.sp)
                }
                Button(
                    onClick = onViewDetail,
                    modifier = Modifier.weight(1f),
                    shape = CircleShape,
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Black)
                ) {
                    Text(text = "Ver Ficha", fontSize = 12.sp)
                }
            }
        }
    }
}
