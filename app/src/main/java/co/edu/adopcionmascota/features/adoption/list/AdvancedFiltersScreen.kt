package co.edu.adopcionmascota.features.adoption.list

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import co.edu.adopcionmascota.core.theme.AppPrimary
import co.edu.adopcionmascota.core.theme.Background
import co.edu.adopcionmascota.core.theme.DarkText
import co.edu.adopcionmascota.core.theme.LightBlueBg
import co.edu.adopcionmascota.core.theme.SecondaryText
import co.edu.adopcionmascota.domain.PetType
import coil3.compose.AsyncImage

// Pantalla "filtro" (Filtros y Búsqueda)
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AdvancedFiltersScreen(
    onNavigateBack: () -> Unit,
    onShowResults: () -> Unit,          // "Mostrar N Resultados" → feed principal
    viewModel: AdvancedFiltersViewModel = viewModel()
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val state by viewModel.uiState.collectAsState()

    LaunchedEffect(state.infoMessage) {
        state.infoMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.resetInfoMessage()
        }
    }

    Scaffold(
        modifier = Modifier.statusBarsPadding(),
        containerColor = Background,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Button(
                    onClick = {
                        viewModel.onApply()
                        onShowResults()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = CircleShape,
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Black)
                ) {
                    Text(text = "Mostrar ${state.resultsCount} Resultados", fontWeight = FontWeight.SemiBold)
                }
                TextButton(onClick = viewModel::onSaveFavorite, modifier = Modifier.fillMaxWidth()) {
                    Icon(Icons.Outlined.BookmarkBorder, contentDescription = null, tint = AppPrimary, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = "Guardar como búsqueda favorita", color = AppPrimary)
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onNavigateBack) {
                    Icon(Icons.AutoMirrored.Outlined.ArrowBack, contentDescription = "Volver", tint = DarkText)
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = "Filtros y Búsqueda", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = DarkText)
                    Text(text = "Feed Principal", fontSize = 12.sp, color = SecondaryText)
                }
                TextButton(onClick = viewModel::onReset) {
                    Text(text = "Restablecer", color = AppPrimary)
                }
            }

            Surface(shape = RoundedCornerShape(14.dp), color = AppPrimary.copy(alpha = 0.1f)) {
                Text(
                    text = "${state.resultsCount} mascotas encontradas en tu radio de ${state.maxDistanceKm} km",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = AppPrimary
                )
            }

            FilterSection(title = "Tipo de Publicación", subtitle = "Multiselección") {
                FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    SelectChip("Adopción · Hogar definitivo", PetType.ADOPCION in state.types) { viewModel.onTypeToggle(PetType.ADOPCION) }
                    SelectChip("Perdidas · Búsqueda activa", PetType.PERDIDO in state.types) { viewModel.onTypeToggle(PetType.PERDIDO) }
                    SelectChip("Encontradas · Reporte callejero", PetType.ENCONTRADO in state.types) { viewModel.onTypeToggle(PetType.ENCONTRADO) }
                }
            }

            FilterSection(title = "Distancia de búsqueda", subtitle = "Perímetro activo: ${state.maxDistanceKm} km") {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    DISTANCE_OPTIONS.forEach { km ->
                        SelectChip("$km km", state.maxDistanceKm == km) { viewModel.onDistanceSelected(km) }
                    }
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = "Notificar mascotas perdidas cercanas", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = DarkText)
                        Text(text = "Recibe alertas en tu perímetro de ${state.maxDistanceKm} km", fontSize = 11.sp, color = SecondaryText)
                    }
                    Switch(checked = state.notifyLostNearby, onCheckedChange = viewModel::onNotifyLostChange)
                }
            }

            FilterSection(title = "Características del Animal") {
                Text(text = "Especie", fontSize = 12.sp, color = SecondaryText)
                FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("Perros", "Gatos", "Conejos", "Otros").forEach {
                        SelectChip(it, it in state.species) { viewModel.onSpeciesToggle(it) }
                    }
                }
                Text(text = "Tamaño", fontSize = 12.sp, color = SecondaryText)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("Pequeño", "Mediano", "Grande").forEach {
                        SelectChip(it, it in state.sizes) { viewModel.onSizeToggle(it) }
                    }
                }
                Text(text = "Etapa de Vida", fontSize = 12.sp, color = SecondaryText)
                FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("Cachorro (<1 año)", "Joven (1-3 años)", "Adulto (3-7 años)", "Senior (8+ años)").forEach {
                        SelectChip(it, it in state.lifeStages) { viewModel.onLifeStageToggle(it) }
                    }
                }
                Text(text = "Sexo", fontSize = 12.sp, color = SecondaryText)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    SelectChip("Macho", state.sex == "Macho") { viewModel.onSexSelected("Macho") }
                    SelectChip("Hembra", state.sex == "Hembra") { viewModel.onSexSelected("Hembra") }
                    SelectChip("Cualquiera", state.sex == null) { viewModel.onSexSelected(null) }
                }
            }

            FilterSection(title = "Sellos Patitas Conectadas", subtitle = "Filtros de bienestar y seguridad veterinaria") {
                FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf(
                        "Verificación veterinaria oficial",
                        "Esterilizado / Castrado",
                        "Apto con niños y otros animales",
                        "Casos prioritarios / Hogar de paso"
                    ).forEach {
                        SelectChip(it, it in state.seals) { viewModel.onSealToggle(it) }
                    }
                }
            }

            if (state.highlighted.isNotEmpty()) {
                Text(text = "MASCOTAS DESTACADAS EN ESTE FILTRO", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = SecondaryText)
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    state.highlighted.forEach { publication ->
                        Surface(
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(16.dp),
                            color = Color.White
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                AsyncImage(
                                    model = publication.image,
                                    contentDescription = publication.name,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(90.dp)
                                        .clip(RoundedCornerShape(12.dp))
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(text = publication.name, fontWeight = FontWeight.Bold, color = DarkText)
                                Text(text = "${publication.location} • ${publication.age}", fontSize = 11.sp, color = SecondaryText)
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
private fun FilterSection(
    title: String,
    subtitle: String? = null,
    content: @Composable () -> Unit
) {
    Surface(shape = RoundedCornerShape(20.dp), color = Color.White) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = title, modifier = Modifier.weight(1f), fontSize = 15.sp, fontWeight = FontWeight.SemiBold, color = DarkText)
                if (subtitle != null) Text(text = subtitle, fontSize = 11.sp, color = SecondaryText)
            }
            content()
        }
    }
}

@Composable
private fun SelectChip(text: String, selected: Boolean, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        shape = CircleShape,
        color = if (selected) Color.Black else LightBlueBg
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (selected) {
                Icon(Icons.Outlined.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
                Spacer(modifier = Modifier.width(4.dp))
            }
            Text(text = text, fontSize = 12.sp, color = if (selected) Color.White else DarkText)
        }
    }
}
