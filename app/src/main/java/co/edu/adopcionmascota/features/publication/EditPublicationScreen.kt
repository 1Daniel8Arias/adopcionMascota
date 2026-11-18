package co.edu.adopcionmascota.features.publication

import androidx.compose.ui.res.stringResource
import co.edu.adopcionmascota.R
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import co.edu.adopcionmascota.core.component.rememberGalleryPicker
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.ui.text.style.TextAlign
import co.edu.adopcionmascota.core.theme.SecondaryText
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import co.edu.adopcionmascota.core.theme.AppPrimary
import co.edu.adopcionmascota.core.theme.Background
import co.edu.adopcionmascota.core.theme.DarkText
import co.edu.adopcionmascota.core.util.RequestResult

// Pantalla "03 · Editar publicación — Formulario" + "Editar Publicación - Editado Correctamente"
@Composable
fun EditPublicationScreen(
    publicationId: String,
    onNavigateBack: () -> Unit,
    onNavigateToMyPublications: () -> Unit,     // stringResource(R.string.view_my_publications)
    viewModel: EditPublicationViewModel = hiltViewModel()
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val state by viewModel.uiState.collectAsState()

    LaunchedEffect(state.infoMessage) {
        state.infoMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.resetInfoMessage()
        }
    }

    LaunchedEffect(state.saveResult) {
        val result = state.saveResult
        if (result is RequestResult.Failure) snackbarHostState.showSnackbar(result.errorMessage)
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            modifier = Modifier.statusBarsPadding(),
            containerColor = Background,
            snackbarHost = { SnackbarHost(snackbarHostState) }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Outlined.ArrowBack, contentDescription = stringResource(R.string.back), tint = DarkText)
                    }
                    Text(
                        text = stringResource(R.string.edit_publication),
                        modifier = Modifier.weight(1f),
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = DarkText
                    )
                    TextButton(onClick = viewModel::onSaveDraft) {
                        Text(text = stringResource(R.string.save_draft), color = DarkText, fontSize = 13.sp)
                    }
                }

                if (state.original == null) {
                    Text(text = stringResource(R.string.publication_not_found), color = DarkText)
                    return@Column
                }

                // Se reutilizan las secciones del formulario "Nueva Publicación"
                EthicalCommitmentCard()

                CategorySelectorSection(
                    selectedCategory = state.category,
                    onCategorySelected = viewModel::onCategorySelected
                )

                PetPhotosSection(
                    photos = state.photos,
                    onAddPhotoClick = rememberGalleryPicker { viewModel.onPhotoSelected(it.toString()) },
                    onRemovePhotoClick = viewModel::onRemovePhoto
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

                Button(
                    onClick = viewModel::saveChanges,
                    enabled = state.isFormValid && !state.isSaving,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = CircleShape,
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Black)
                ) {
                    Icon(Icons.Outlined.Edit, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (state.isSaving) stringResource(R.string.saving) else stringResource(R.string.save_changes_update),
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }

        if (state.saveResult is RequestResult.Success) {
            PublicationSuccessSheet(
                title = stringResource(R.string.publication_edited),
                message = stringResource(R.string.publication_edited_detail, state.title),
                detail = stringResource(R.string.publication_edited_more),
                primaryText = stringResource(R.string.view_my_publications),
                onPrimaryClick = onNavigateToMyPublications
            )
        }
    }
}

// Hoja de confirmación compartida por Crear, Editar y Eliminar publicación
@Composable
fun PublicationSuccessSheet(
    title: String,
    message: String,
    detail: String,
    primaryText: String,
    onPrimaryClick: () -> Unit,
    secondaryText: String? = null,
    onSecondaryClick: () -> Unit = {}
) {
    // "Atrás" hace lo mismo que el botón principal
    BackHandler { onPrimaryClick() }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.55f))
            // Bloquea los toques al formulario que queda detrás
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = {}
            ),
        contentAlignment = Alignment.BottomCenter
    ) {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
            color = Color.White
        ) {
            Column(
                modifier = Modifier
                    .navigationBarsPadding()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Icon(
                    imageVector = Icons.Outlined.CheckCircle,
                    contentDescription = null,
                    tint = AppPrimary,
                    modifier = Modifier.size(56.dp)
                )
                Text(text = title, fontSize = 22.sp, fontWeight = FontWeight.Bold, color = DarkText, textAlign = TextAlign.Center)
                Text(text = message, fontSize = 13.sp, color = DarkText, textAlign = TextAlign.Center)
                Text(text = detail, fontSize = 12.sp, color = SecondaryText, textAlign = TextAlign.Center)
                Button(
                    onClick = onPrimaryClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = CircleShape,
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Black)
                ) {
                    Text(text = primaryText, fontWeight = FontWeight.SemiBold)
                }
                if (secondaryText != null) {
                    TextButton(onClick = onSecondaryClick) {
                        Text(text = secondaryText, color = AppPrimary)
                    }
                }
            }
        }
    }
}
