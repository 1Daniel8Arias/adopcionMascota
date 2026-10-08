package co.edu.adopcionmascota.features.publication

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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.AddAPhoto
import androidx.compose.material.icons.outlined.AddCircle
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Explore
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.MyLocation
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Pets
import androidx.compose.material.icons.outlined.Place
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Send
import androidx.compose.material.icons.outlined.VerifiedUser
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import co.edu.adopcionmascota.core.theme.*
import co.edu.adopcionmascota.core.util.RequestResult

@Preview(showBackground = true, heightDp = 1620)
@Composable
fun PublicationScreen(
    viewModel: PublicationViewModel = viewModel(),
    onNavigateBack: () -> Unit = {},
    onSaveDraft: () -> Unit = {},
    onPublishSuccess: () -> Unit = {}
) {
    val state by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(state.publicationResult) {
        if (state.publicationResult is RequestResult.Success) {
            snackbarHostState.showSnackbar("Publicación confirmada")
            onPublishSuccess()
            viewModel.resetPublicationResult()
        }
    }

    Scaffold(
        // 3. Vincular el hostState al Scaffold
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        bottomBar = { PublicationBottomNavigationBar() },
        containerColor = Background
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            PublicationHeaderSection(
                onNavigateBack = onNavigateBack,
                onSaveDraft = onSaveDraft
            )

            EthicalCommitmentCard()

            CategorySelectorSection(
                selectedCategory = state.category,
                onCategorySelected = viewModel::onCategorySelected
            )

            PetPhotosSection(
                photos = state.photos,
                onAddPhotoClick = { viewModel.onAddPhoto() },
                onRemovePhotoClick = { photo -> viewModel.onRemovePhoto(photo) }
            )

            AnimalDetailsSection(
                title = state.title,
                onTitleChange = viewModel::onTitleChanged,
                selectedSpecies = state.species,
                onSpeciesSelected = viewModel::onSpeciesSelected,
                breed = state.breed,
                onBreedChange = viewModel::onBreedChanged,
                selectedSize = state.size,
                onSizeSelected = viewModel::onSizeSelected
            )

            HealthStatusSection(
                isVaccinated = state.isVaccinated,
                isDewormed = state.isDewormed,
                isSterilized = state.isSterilized,
                hasMicrochip = state.hasMicrochip,
                onVaccinatedToggle = viewModel::toggleVaccinated,
                onDewormedToggle = viewModel::toggleDewormed,
                onSterilizedToggle = viewModel::toggleSterilized,
                onMicrochipToggle = viewModel::toggleMicrochip
            )

            HistorySection(
                history = state.history,
                onHistoryChange = viewModel::onHistoryChanged
            )

            LocationSection(
                address = state.address,
                cityRegion = state.cityRegion,
                latitude = state.latitude,
                longitude = state.longitude
            )

            PublishFooterSection(
                isFormValid = state.isFormValid,
                publicationResult = state.publicationResult,
                onPublish = { viewModel.publishPet() }
            )
        }
    }
}

@Composable
fun PublicationHeaderSection(
    onNavigateBack: () -> Unit,
    onSaveDraft: () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                    contentDescription = "Volver",
                    modifier = Modifier
                        .clickable { onNavigateBack() }
                        .padding(end = 8.dp),
                    tint = DarkText
                )
                Text(
                    text = "Nueva\nPublicación",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = DarkText,
                    lineHeight = 22.sp
                )
            }
            Text(
                text = "Guardar\nborrador",
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                color = DarkText,
                modifier = Modifier.clickable { onSaveDraft() }
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            StepItem(text = "Paso 1: Información\nesencial", isActive = true, modifier = Modifier.weight(1f))
            StepItem(text = "Paso 2: Historia y\nSalud", isActive = false, modifier = Modifier.weight(1f))
            StepItem(text = "Paso 3:\nContacto", isActive = false, modifier = Modifier.weight(1f))
        }
    }
}

@Composable
private fun StepItem(text: String, isActive: Boolean, modifier: Modifier = Modifier) {
    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(3.dp)
                .background(
                    if (isActive) DarkText else LightBlueBorder,
                    CircleShape
                )
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = text,
            fontSize = 10.sp,
            color = if (isActive) DarkText else SecondaryText,
            fontWeight = if (isActive) FontWeight.Bold else FontWeight.Normal
        )
    }
}

@Composable
fun EthicalCommitmentCard() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(LightBlueBg, RoundedCornerShape(16.dp))
            .padding(14.dp),
        verticalAlignment = Alignment.Top
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .background(PrimaryBlue, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Outlined.VerifiedUser,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(20.dp)
            )
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(
                text = "Compromiso ético de moderación",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = DarkText
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "Tu publicación pasará por revisión comunitaria en Bogotá para asegurar autenticidad y resguardar el bienestar animal ante comercio ilegal.",
                fontSize = 11.sp,
                color = SecondaryText,
                lineHeight = 15.sp
            )
        }
    }
}

@Composable
fun CategorySelectorSection(
    selectedCategory: String,
    onCategorySelected: (String) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Categoría *", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = DarkText)
            Text("Obligatorio", fontSize = 11.sp, color = SecondaryText)
        }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            val categories = listOf(
                Triple("Adopción", Icons.Outlined.Favorite, Modifier.weight(1f)),
                Triple("Perdido", Icons.Outlined.Search, Modifier.weight(1f)),
                Triple("Encontrado", Icons.Outlined.Place, Modifier.weight(1f))
            )
            categories.forEach { (category, icon, modifier) ->
                CategoryChip(
                    text = category,
                    icon = icon,
                    isSelected = selectedCategory == category,
                    onClick = { onCategorySelected(category) },
                    modifier = modifier
                )
            }
        }
    }
}

@Composable
private fun CategoryChip(
    text: String,
    icon: ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .background(
                if (isSelected) SelectedCategoryBg else LightBlueBg,
                CircleShape
            )
            .clickable { onClick() }
            .padding(vertical = 10.dp, horizontal = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = if (isSelected) Color.White else SecondaryText,
            modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = text,
            fontSize = 12.sp,
            color = if (isSelected) Color.White else SecondaryText,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
fun PetPhotosSection(
    photos: List<String>,
    onAddPhotoClick: () -> Unit,
    onRemovePhotoClick: (String) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Fotos de la mascota *", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = DarkText)
            Text("${photos.size} de 5 añadidas", fontSize = 11.sp, color = SecondaryText)
        }
        Text(
            text = "Sube fotografías claras con buena luz natural. Mínimo 1 foto obligatoria.",
            fontSize = 11.sp,
            color = SecondaryText
        )
        Spacer(modifier = Modifier.height(4.dp))

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            itemsIndexed(photos) { index, photo ->
                PhotoMockCard(
                    isPrincipal = index == 0,
                    onRemove = { onRemovePhotoClick(photo) }
                )
            }

            if (photos.size < 5) {
                item {
                    AddPhotoButton(onClick = onAddPhotoClick)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnimalDetailsSection(
    title: String,
    onTitleChange: (String) -> Unit,
    selectedSpecies: String,
    onSpeciesSelected: (String) -> Unit,
    breed: String,
    onBreedChange: (String) -> Unit,
    selectedSize: String,
    onSizeSelected: (String) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text("Detalles del animal", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = DarkText)

        Column {
            Text("Título del anuncio *", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = DarkText)
            Spacer(modifier = Modifier.height(4.dp))
            OutlinedTextField(
                value = title,
                onValueChange = onTitleChange,
                placeholder = { Text("Perrito mestizo juguetón busca familia amorosa") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedContainerColor = Color.White,
                    focusedContainerColor = Color.White
                )
            )
            Text(
                "Usa palabras afectuosas que describan su personalidad.",
                fontSize = 11.sp,
                color = SecondaryText,
                modifier = Modifier.padding(top = 2.dp)
            )
        }

        Column {
            Text("Especie *", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = DarkText)
            Spacer(modifier = Modifier.height(6.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf("Perro", "Gato", "Conejo", "Otro").forEach { species ->
                    SpeciesItem(
                        name = species,
                        isSelected = selectedSpecies == species,
                        onClick = { onSpeciesSelected(species) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        Column {
            Text("Raza aproximada", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = DarkText)
            Spacer(modifier = Modifier.height(4.dp))
            OutlinedTextField(
                value = breed,
                onValueChange = onBreedChange,
                placeholder = { Text("Cruce de Golden Retriever") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(unfocusedContainerColor = Color.White)
            )
        }

        Column {
            Text("Tamaño estimado", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = DarkText)
            Spacer(modifier = Modifier.height(6.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(LightBlueBg, CircleShape)
                    .padding(4.dp)
            ) {
                listOf("Pequeño", "Mediano", "Grande").forEach { size ->
                    SizeSegment(
                        text = size,
                        isSelected = selectedSize == size,
                        onClick = { onSizeSelected(size) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@Composable
private fun SpeciesItem(
    name: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .background(
                if (isSelected) PrimaryBlue else LightBlueBg,
                RoundedCornerShape(12.dp)
            )
            .clickable { onClick() }
            .padding(vertical = 10.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Outlined.Pets,
            contentDescription = null,
            tint = if (isSelected) Color.White else DarkText,
            modifier = Modifier.size(18.dp)
        )
        Text(
            text = name,
            fontSize = 12.sp,
            color = if (isSelected) Color.White else DarkText,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
private fun SizeSegment(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .background(
                if (isSelected) Color.White else Color.Transparent,
                CircleShape
            )
            .clickable { onClick() }
            .padding(vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            fontSize = 12.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            color = DarkText
        )
    }
}

@Composable
fun HealthStatusSection(
    isVaccinated: Boolean,
    isDewormed: Boolean,
    isSterilized: Boolean,
    hasMicrochip: Boolean,
    onVaccinatedToggle: () -> Unit,
    onDewormedToggle: () -> Unit,
    onSterilizedToggle: () -> Unit,
    onMicrochipToggle: () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text("Estado de salud verificado", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = DarkText)
        Text(
            "Selecciona todos los tratamientos médicos al día que posee.",
            fontSize = 11.sp,
            color = SecondaryText
        )

        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            HealthChip("Vacunado", isChecked = isVaccinated, onClick = onVaccinatedToggle)
            HealthChip("Desparasitado", isChecked = isDewormed, onClick = onDewormedToggle)
        }
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            HealthChip("Esterilizado / Castrado", isChecked = isSterilized, onClick = onSterilizedToggle)
            HealthChip("Microchip", isChecked = hasMicrochip, onClick = onMicrochipToggle)
        }
    }
}

@Composable
private fun HealthChip(text: String, isChecked: Boolean, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .background(
                if (isChecked) PrimaryBlue else LightBlueBg,
                CircleShape
            )
            .clickable { onClick() }
            .padding(horizontal = 12.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (isChecked) {
            Icon(
                imageVector = Icons.Outlined.Check,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(14.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
        }
        Text(
            text = text,
            fontSize = 11.sp,
            color = if (isChecked) Color.White else SecondaryText,
            fontWeight = FontWeight.Medium
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistorySection(
    history: String,
    onHistoryChange: (String) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Historia y temperamento", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = DarkText)
            Text("${history.length} / 500", fontSize = 11.sp, color = SecondaryText)
        }
        OutlinedTextField(
            value = history,
            onValueChange = onHistoryChange,
            placeholder = { Text("Describe cómo convive con personas, felinos o caninos...") },
            modifier = Modifier
                .fillMaxWidth()
                .height(110.dp),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedContainerColor = Color.White,
                focusedContainerColor = Color.White
            )
        )
        Text(
            "Describe cómo convive con personas, felinos o caninos y si requiere cuidados particulares.",
            fontSize = 11.sp,
            color = SecondaryText
        )
    }
}

@Composable
fun LocationSection(
    address: String,
    cityRegion: String,
    latitude: Double,
    longitude: Double
) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Outlined.LocationOn,
                    contentDescription = null,
                    tint = DarkText,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text("Ubicación del reporte", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = DarkText)
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Outlined.MyLocation,
                    contentDescription = null,
                    tint = DarkText,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text("Mi GPS", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = DarkText)
            }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(160.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(MapBg),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Outlined.LocationOn,
                contentDescription = null,
                tint = Color.Black,
                modifier = Modifier.size(36.dp)
            )

            Box(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 10.dp)
                    .background(Color.Black, CircleShape)
                    .padding(horizontal = 12.dp, vertical = 4.dp)
            ) {
                Text("Arrastra para ajustar", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
            }

            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 10.dp)
                    .background(Color.White.copy(alpha = 0.9f), CircleShape)
                    .padding(horizontal = 12.dp, vertical = 4.dp)
            ) {
                Text("Lat: $latitude, Long: $longitude", color = Color.Black, fontSize = 10.sp)
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(LightBlueBg, RoundedCornerShape(12.dp))
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                Icon(
                    imageVector = Icons.Outlined.LocationOn,
                    contentDescription = null,
                    tint = SecondaryText
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(address, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = DarkText)
                    Text(cityRegion, fontSize = 10.sp, color = SecondaryText)
                }
            }
            Icon(
                imageVector = Icons.Outlined.Edit,
                contentDescription = null,
                tint = SecondaryText,
                modifier = Modifier.size(16.dp)
            )
        }
    }
}

@Composable
fun PublishFooterSection(
    isFormValid: Boolean,
    publicationResult: RequestResult?,
    onPublish: () -> Unit
) {
    val isLoading = publicationResult is RequestResult.Loading

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Button(
            onClick = onPublish,
            enabled = isFormValid && !isLoading,
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            shape = CircleShape,
            colors = ButtonDefaults.buttonColors(
                containerColor = BlackButton,
                disabledContainerColor = Color.LightGray
            )
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    color = Color.White,
                    modifier = Modifier.size(20.dp),
                    strokeWidth = 2.dp
                )
            } else {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Outlined.Send,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Publicar Mascota (Enviar a Moderación)", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        if (publicationResult is RequestResult.Failure) {
            Text(
                text = publicationResult.errorMessage,
                color = MaterialTheme.colorScheme.error,
                fontSize = 12.sp,
                textAlign = TextAlign.Center
            )
        }

        Text(
            text = "Al hacer clic en publicar, certificas que no existe ánimo de lucro y aceptas las normas de bienestar animal de Patitas Conectadas.",
            fontSize = 10.sp,
            color = SecondaryText,
            textAlign = TextAlign.Center,
            lineHeight = 14.sp
        )
    }
}

@Composable
fun PublicationBottomNavigationBar() {
    NavigationBar(containerColor = LightBlueBg) {
        NavigationBarItem(
            selected = false,
            onClick = {},
            icon = { Icon(Icons.Outlined.Explore, contentDescription = null) },
            label = { Text("Explorar", fontSize = 10.sp) }
        )
        NavigationBarItem(
            selected = true,
            onClick = {},
            icon = { Icon(Icons.Outlined.AddCircle, contentDescription = null) },
            label = { Text("Publicar", fontSize = 10.sp) }
        )
        NavigationBarItem(
            selected = false,
            onClick = {},
            icon = { Icon(Icons.Outlined.Notifications, contentDescription = null) },
            label = { Text("Alertas", fontSize = 10.sp) }
        )
        NavigationBarItem(
            selected = false,
            onClick = {},
            icon = { Icon(Icons.Outlined.Person, contentDescription = null) },
            label = { Text("Perfil", fontSize = 10.sp) }
        )
    }
}

@Composable
private fun PhotoMockCard(
    isPrincipal: Boolean,
    onRemove: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(90.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(if (isPrincipal) Color(0xFFCBD5E1) else LightBlueBg)
            .border(1.dp, LightBlueBorder, RoundedCornerShape(16.dp))
    ) {
        Icon(
            imageVector = Icons.Outlined.Pets,
            contentDescription = null,
            tint = PrimaryBlue,
            modifier = Modifier
                .size(36.dp)
                .align(Alignment.Center)
        )

        IconButton(
            onClick = onRemove,
            modifier = Modifier
                .size(22.dp)
                .align(Alignment.TopEnd)
                .padding(2.dp)
                .background(Color.Black.copy(alpha = 0.5f), CircleShape)
        ) {
            Icon(
                imageVector = Icons.Outlined.Close,
                contentDescription = "Eliminar foto",
                tint = Color.White,
                modifier = Modifier.size(12.dp)
            )
        }

        if (isPrincipal) {
            Text(
                text = "Principal",
                fontSize = 9.sp,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .background(DarkText, RoundedCornerShape(topEnd = 8.dp))
                    .padding(horizontal = 6.dp, vertical = 2.dp)
            )
        }
    }
}

@Composable
private fun AddPhotoButton(onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(90.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(LightBlueBg)
            .border(1.dp, LightBlueBorder, RoundedCornerShape(16.dp))
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                imageVector = Icons.Outlined.AddAPhoto,
                contentDescription = null,
                tint = PrimaryBlue,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text("+ Foto", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = DarkText)
            Text("Simulada", fontSize = 8.sp, color = SecondaryText)
        }
    }
}