package co.edu.adopcionmascota.features.moderator.panel

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Inbox
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.outlined.Cancel
import androidx.compose.material.icons.outlined.Inventory2
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import coil3.request.ImageRequest
import coil3.request.crossfade
import coil3.size.Size

private val ScreenBackground = Color(0xFFF3F7FF)
private val BrandColor = Color(0xFF536DFE)
private val DarkBlue = Color(0xFF17365D)
private val MutedText = Color(0xFF64748B)

// Azul claro para las tarjetas
private val CardBackground = Color(0xFFEAF2FF)
private val CardBorder = Color(0xFFD5E4FF)
private val StatusOptions = listOf(
    "Pendientes (8)",
    "Revisadas (142)",
    "Reportadas"
)

private val FilterOptions = listOf(
    "Todas",
    "Posible venta",
    "Adopción",
    "Urgente"
)


@Composable
fun ModerationScreen() {

    var selectedTab by remember {
        mutableIntStateOf(0)
    }

    var selectedFilter by remember {
        mutableStateOf("Todas")
    }

    // Lista de publicaciones
    val publications = remember {
        fakePublications()
    }

    // Filtrar publicaciones según el filtro seleccionado
    val filteredPublications = remember(
        publications,
        selectedFilter
    ) {
        when (selectedFilter) {
            "Con alertas" -> publications.filter {
                it.ethicsAlert
            }

            "Sin alertas" -> publications.filter {
                !it.ethicsAlert
            }

            "Vacunas incompletas" -> publications.filter {
                !it.vaccinesComplete
            }

            "Sin esterilizar" -> publications.filter {
                !it.sterilized
            }

            else -> publications
        }
    }

    Scaffold(
        containerColor = ScreenBackground,
        topBar = {
            ModerationTopBar()
        },
        bottomBar = {
            BottomNavigationBar()
        }
    ) { padding ->

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(vertical = 16.dp)
        ) {

            // Encabezado
            item {
                Column {
                    Text(
                        text = "Panel de Moderación",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = DarkBlue
                    )

                    Spacer(
                        Modifier.height(4.dp)
                    )

                    Text(
                        text = "Audita y valida solicitudes de adopción",
                        color = MutedText
                    )
                }
            }

            // Pestañas de estado
            item {
                StatusTabs(
                    selectedTab = selectedTab,
                    onTabSelected = {
                        selectedTab = it
                    }
                )
            }

            // Métricas
            item {
                DashboardMetrics()
            }

            // Filtros
            item {
                FilterSection(
                    selectedFilter = selectedFilter,
                    onFilterSelected = {
                        selectedFilter = it
                    }
                )
            }

            // Tarjetas dinámicas
            if (filteredPublications.isEmpty()) {

                item {
                    Text(
                        text = "No hay publicaciones que coincidan con este filtro.",
                        color = MutedText,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 24.dp)
                    )
                }

            } else {

                items(
                    items = filteredPublications,
                    key = { publication -> publication.id }
                ) { publication ->

                    AdoptionCard(
                        publication = publication,

                        onReject = { selectedPublication ->
                            // TODO: conectar con el ViewModel
                            // para rechazar la publicación.
                        },

                        onInspect = { selectedPublication ->
                            // TODO: navegar al detalle
                            // usando selectedPublication.id.
                        },

                        onApprove = { selectedPublication ->
                            // TODO: conectar con el ViewModel
                            // para aprobar la publicación.
                        }
                    )
                }
            }
        }
    }
}


@Composable
fun ModerationTopBar(
    pendingCount: Int = 8
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(horizontal = 20.dp, vertical = 12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    text = "Patitas Conectadas",
                    fontSize = 21.sp,
                    fontWeight = FontWeight.Bold,
                    color = DarkBlue
                )

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Outlined.LocationOn,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = MutedText
                    )

                    Spacer(Modifier.width(4.dp))

                    Text(
                        text = "Bogotá, Colombia",
                        fontSize = 12.sp,
                        color = MutedText
                    )

                    Icon(
                        imageVector = Icons.Default.KeyboardArrowDown,
                        contentDescription = null,
                        tint = MutedText
                    )
                }
            }

            IconButton(onClick = { }) {
                Icon(
                    imageVector = Icons.Default.Notifications,
                    contentDescription = "Notificaciones"
                )
            }

            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFE7F0FF)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Security,
                    contentDescription = "Perfil de moderador",
                    tint = DarkBlue
                )
            }
        }

        Spacer(Modifier.height(12.dp))

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Surface(
                color = BrandColor,
                shape = RoundedCornerShape(50)
            ) {
                Row(
                    modifier = Modifier.padding(
                        horizontal = 12.dp,
                        vertical = 7.dp
                    ),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.Verified,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(Modifier.width(5.dp))
                    Text("Oficial", color = Color.White)
                }
            }

            Surface(
                color = Color(0xFF0F172A),
                shape = RoundedCornerShape(50)
            ) {
                Text(
                    text = "$pendingCount pendientes",
                    modifier = Modifier.padding(
                        horizontal = 12.dp,
                        vertical = 7.dp
                    ),
                    color = Color.White
                )
            }
        }
    }
}

@Composable
fun StatusTabs(
    selectedTab: Int,
    onTabSelected: (Int) -> Unit
) {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(StatusOptions.size) { index ->
            ModerationTab(
                text = StatusOptions[index],
                selected = selectedTab == index,
                icon = when (index) {
                    0 -> Icons.Outlined.Inventory2
                    1 -> Icons.Default.CheckCircle
                    else -> Icons.Outlined.Cancel
                },
                onClick = { onTabSelected(index) }
            )
        }
    }
}

@Composable
fun ModerationTab(
    text: String,
    selected: Boolean,
    icon: ImageVector,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .height(44.dp)
            .clip(RoundedCornerShape(24.dp))
            .clickable(onClick = onClick),
        color = if (selected) BrandColor else Color(0xFFE9EDF5),
        shape = RoundedCornerShape(24.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(18.dp),
                tint = if (selected) Color.White else MutedText
            )

            Text(
                text = text,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                fontSize = 13.sp,
                color = if (selected) Color.White else MutedText
            )
        }
    }
}

@Composable
fun DashboardMetrics() {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            MetricCard(
                value = "8",
                title = "Por revisar",
                icon = Icons.Default.Inbox,
                modifier = Modifier.weight(1f)
            )

            MetricCard(
                value = "14",
                title = "Aprobadas hoy",
                icon = Icons.Default.ThumbUp,
                modifier = Modifier.weight(1f)
            )
        }

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            MetricCard(
                value = "12 min",
                title = "Tiempo promedio",
                icon = Icons.Default.Timer,
                modifier = Modifier.weight(1f)
            )

            MetricCard(
                value = "0",
                title = "Reportes críticos",
                icon = Icons.Default.Security,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
fun MetricCard(
    value: String,
    title: String,
    icon: ImageVector,
    modifier: Modifier = Modifier
) {
    ElevatedCard(
        modifier = modifier.height(125.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = CardBackground
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = BrandColor,
                modifier = Modifier.size(22.dp)
            )

            Text(
                text = value,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = DarkBlue
            )

            Text(
                text = title,
                fontSize = 12.sp,
                color = MutedText
            )
        }
    }
}

@Composable
fun FilterSection(
    selectedFilter: String,
    onFilterSelected: (String) -> Unit
) {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(FilterOptions) { filter ->
            FilterChip(
                selected = selectedFilter == filter,
                onClick = { onFilterSelected(filter) },
                label = { Text(filter) }
            )
        }
    }
}


@Composable
fun AdoptionCard(
    publication: ModerationPublicationUi,
    onReject: (ModerationPublicationUi) -> Unit = {},
    onInspect: (ModerationPublicationUi) -> Unit = {},
    onApprove: (ModerationPublicationUi) -> Unit = {}
) {
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .border(
                width = 1.dp,
                color = CardBorder,
                shape = RoundedCornerShape(20.dp)
            ),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = CardBackground
        ),
        elevation = CardDefaults.elevatedCardElevation(
            defaultElevation = 3.dp
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Información del usuario
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFE7F0FF)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = publication.userName
                            .split(" ")
                            .mapNotNull { it.firstOrNull()?.toString() }
                            .take(2)
                            .joinToString(""),
                        fontWeight = FontWeight.Bold,
                        color = DarkBlue
                    )
                }

                Spacer(Modifier.width(12.dp))

                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = publication.userName,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = publication.userLevel,
                        color = MutedText,
                        fontSize = 12.sp
                    )

                    Text(
                        text = publication.publicationAge,
                        color = MutedText,
                        fontSize = 11.sp
                    )
                }

                Text(
                    text = "#${publication.id}",
                    color = MutedText,
                    fontSize = 12.sp
                )
            }

            Spacer(Modifier.height(16.dp))

            // Título de la publicación
            Text(
                text = publication.title,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(Modifier.height(8.dp))

            // Información del animal
            Text(
                text = "${publication.animalType} • ${publication.animalAge}",
                color = MutedText,
                fontSize = 13.sp
            )

            Spacer(Modifier.height(4.dp))

            Text(
                text = publication.city,
                color = MutedText,
                fontSize = 13.sp
            )

            Spacer(Modifier.height(12.dp))

            // Imagen

            Image(
                painter = painterResource(id = publication.imageUrl),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .clip(RoundedCornerShape(16.dp)),
                contentScale = ContentScale.Crop
            )
            Spacer(Modifier.height(12.dp))

            // Condiciones del animal
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                AssistChip(
                    onClick = {},
                    label = {
                        Text(
                            if (publication.vaccinesComplete) {
                                "Vacunas completas"
                            } else {
                                "Vacunas incompletas"
                            }
                        )
                    }
                )

                AssistChip(
                    onClick = {},
                    label = {
                        Text(
                            if (publication.sterilized) {
                                "Esterilizado"
                            } else {
                                "Sin esterilizar"
                            }
                        )
                    }
                )
            }

            // Alerta ética: solo aparece si existe una alerta
            if (publication.ethicsAlert) {
                Spacer(Modifier.height(8.dp))

                Surface(
                    color = Color(0xFFFFE5E5),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = publication.ethicsMessage.ifBlank {
                            "Posible infracción detectada"
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        color = Color(0xFFB91C1C),
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Spacer(Modifier.height(16.dp))

            // Acciones de moderación
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = {
                        onReject(publication)
                    },
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(
                        horizontal = 4.dp
                    )
                ) {
                    Text("Rechazar")
                }

                FilledTonalButton(
                    onClick = {
                        onInspect(publication)
                    },
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(
                        horizontal = 4.dp
                    )
                ) {
                    Text("Inspeccionar")
                }

                Button(
                    onClick = {
                        onApprove(publication)
                    },
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(
                        horizontal = 4.dp
                    )
                ) {
                    Text("Aprobar")
                }
            }
        }
    }
}


@Composable
fun BottomNavigationBar() {
    NavigationBar(
        containerColor = Color.White
    ) {
        NavigationBarItem(
            selected = true,
            onClick = { },
            icon = {
                Icon(Icons.Default.Pets, contentDescription = null)
            },
            label = { Text("Explorar") }
        )

        NavigationBarItem(
            selected = false,
            onClick = { },
            icon = {
                Icon(Icons.Default.AddCircle, contentDescription = null)
            },
            label = { Text("Publicar") }
        )

        NavigationBarItem(
            selected = false,
            onClick = { },
            icon = {
                Icon(
                    Icons.Default.Notifications,
                    contentDescription = null
                )
            },
            label = { Text("Alertas") }
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun ModerationScreenPreview() {
    MaterialTheme {
        ModerationScreen()
    }
}
