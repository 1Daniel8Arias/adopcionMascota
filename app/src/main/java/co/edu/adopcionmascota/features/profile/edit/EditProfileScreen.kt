package co.edu.adopcionmascota.features.profile.edit

import androidx.compose.ui.res.stringResource
import co.edu.adopcionmascota.R
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import co.edu.adopcionmascota.core.component.rememberGalleryPicker
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.automirrored.outlined.HelpOutline
import androidx.compose.material.icons.automirrored.outlined.KeyboardArrowRight
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.outlined.Badge
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.EditNote
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.LockReset
import androidx.compose.material.icons.outlined.NotificationsActive
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.PersonOff
import androidx.compose.material.icons.outlined.Pets
import androidx.compose.material.icons.outlined.Phone
import androidx.compose.material.icons.outlined.Image
import androidx.compose.material.icons.outlined.Place
import androidx.compose.material.icons.outlined.VolunteerActivism
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import co.edu.adopcionmascota.core.component.AppTag
import co.edu.adopcionmascota.core.component.ConfirmAlertDialog
import co.edu.adopcionmascota.core.component.IconoTextField
import co.edu.adopcionmascota.core.theme.AppPrimary
import co.edu.adopcionmascota.core.theme.AppRed
import co.edu.adopcionmascota.core.theme.Background
import co.edu.adopcionmascota.core.theme.DarkText
import co.edu.adopcionmascota.core.theme.LightBlueBg
import co.edu.adopcionmascota.core.theme.SecondaryText
import co.edu.adopcionmascota.core.util.RequestResult
import co.edu.adopcionmascota.features.register.component.LocationDropdown
import coil3.compose.AsyncImage

@Composable
fun EditProfileScreen(
    onNavigateBack: () -> Unit,           // Volver al perfil
    onProfileSaved: (List<String>) -> Unit,   // Ir a "Perfil editado" con la lista de cambios
    onNavigateToPassword: () -> Unit,     // Ir a stringResource(R.string.change_password_security)
    onAccountDeactivated: () -> Unit,     // Salir de la sesión después de desactivar la cuenta
    viewModel: EditProfileViewModel = hiltViewModel()
) {
    val snackbarHostState = remember { SnackbarHostState() }

    // Se observa el estado completo de la pantalla
    val state by viewModel.uiState.collectAsState()

    // Intercepta el botón "atrás" mientras el diálogo de salida no esté visible
    BackHandler(enabled = !state.showExitDialog) {
        viewModel.onDiscardClick()
    }

    // Resultado de guardar: se abre la pantalla de confirmación "Perfil editado"
    LaunchedEffect(state.saveResult) {
        when (val result = state.saveResult) {
            is RequestResult.Success -> {
                viewModel.resetSaveResult()
                onProfileSaved(state.changes)
            }

            is RequestResult.Failure -> {
                snackbarHostState.showSnackbar(result.errorMessage)
                viewModel.resetSaveResult()
            }

            is RequestResult.Loading, null -> Unit
        }
    }

    // Resultado de desactivar la cuenta
    LaunchedEffect(state.deactivateResult) {
        when (val result = state.deactivateResult) {
            is RequestResult.Success -> {
                snackbarHostState.showSnackbar(result.message)
                viewModel.resetDeactivateResult()
                onAccountDeactivated()
            }

            is RequestResult.Failure -> {
                snackbarHostState.showSnackbar(result.errorMessage)
                viewModel.resetDeactivateResult()
            }

            is RequestResult.Loading, null -> Unit
        }
    }

    // Mensajes informativos (opciones que aún no están disponibles)
    LaunchedEffect(state.infoMessage) {
        state.infoMessage?.let { message ->
            snackbarHostState.showSnackbar(message)
            viewModel.resetInfoMessage()
        }
    }

    Scaffold(
        containerColor = Background,
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            EditProfileTopBar(
                onBackClick = viewModel::onDiscardClick,
                onHelpClick = viewModel::onHelpClick
            )

            EditModeRow(onDiscardClick = viewModel::onDiscardClick)

            PhotoSection(
                photoUrl = state.photoUrl,
                levelName = state.levelName,
                onChangePhotoClick = rememberGalleryPicker { viewModel.onPhotoSelected(it.toString()) }
            )

            ContactSection(
                state = state,
                localities = viewModel.localities,
                onNameChange = viewModel::onNameChange,
                onEmailChange = viewModel::onEmailChange,
                onPhoneChange = viewModel::onPhoneChange,
                onLocalityChange = viewModel::onLocalityChange,
                onNeighborhoodChange = viewModel::onNeighborhoodChange,
                onBioChange = viewModel::onBioChange
            )

            VolunteerSection(
                state = state,
                onTemporaryHomeChange = viewModel::onTemporaryHomeChange,
                onLostPetAlertsChange = viewModel::onLostPetAlertsChange,
                onShowPublicPhoneChange = viewModel::onShowPublicPhoneChange
            )

            SecuritySection(
                onPasswordClick = onNavigateToPassword,
                onNotificationsClick = viewModel::onNotificationPreferencesClick
            )

            EditProfileFooter(
                isFormValid = state.isFormValid,
                isComplete = state.completedFields == TOTAL_PROFILE_FIELDS,
                saveResult = state.saveResult,
                onSaveClick = viewModel::saveChanges,
                onCancelClick = viewModel::onDiscardClick,
                onDeactivateClick = viewModel::onDeactivateClick
            )
        }

        // Diálogo para confirmar que se descartan los cambios
        if (state.showExitDialog) {
            ConfirmAlertDialog(
                title = stringResource(R.string.discard_changes_title),
                message = stringResource(R.string.discard_changes_message),
                onConfirm = {
                    viewModel.onConfirmDiscard() // Restaura los datos y oculta el diálogo
                    onNavigateBack()             // Regresa al perfil
                },
                onDismiss = viewModel::onDismissExitDialog,
                confirmText = stringResource(R.string.discard)
            )
        }

        // Diálogo para confirmar la desactivación de la cuenta
        if (state.showDeactivateDialog) {
            ConfirmAlertDialog(
                title = stringResource(R.string.deactivate_title),
                message = stringResource(R.string.deactivate_message),
                onConfirm = viewModel::onConfirmDeactivate,
                onDismiss = viewModel::onDismissDeactivateDialog,
                confirmText = stringResource(R.string.deactivate)
            )
        }
    }
}

@Composable
fun EditProfileTopBar(
    onBackClick: () -> Unit,
    onHelpClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onBackClick) {
            Icon(
                imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                contentDescription = stringResource(R.string.back),
                tint = DarkText
            )
        }

        Column(
            modifier = Modifier.weight(1f),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(R.string.brand_two_lines),
                fontSize = 10.sp,
                lineHeight = 11.sp,
                fontWeight = FontWeight.Bold,
                color = AppPrimary
            )
            Text(
                text = stringResource(R.string.my_profile),
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = DarkText
            )
        }

        IconButton(onClick = onHelpClick) {
            Icon(
                imageVector = Icons.AutoMirrored.Outlined.HelpOutline,
                contentDescription = stringResource(R.string.help),
                tint = DarkText
            )
        }

        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(Color.Black),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Outlined.Person,
                contentDescription = stringResource(R.string.nav_profile),
                tint = Color.White,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
fun EditModeRow(onDiscardClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = LightBlueBg
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Outlined.EditNote,
                    contentDescription = null,
                    tint = AppPrimary,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = stringResource(R.string.profile_editing),
                    fontSize = 12.sp,
                    color = AppPrimary
                )
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        TextButton(onClick = onDiscardClick) {
            Text(
                text = stringResource(R.string.discard),
                color = AppPrimary,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
fun PhotoSection(
    photoUrl: String,
    levelName: String,
    onChangePhotoClick: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Foto del usuario; se cambia eligiendo una imagen de la galería
        Box(modifier = Modifier.size(110.dp)) {
            AsyncImage(
                model = photoUrl,
                contentDescription = stringResource(R.string.profile_photo),
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxSize()
                    .border(4.dp, Color.White, CircleShape)
                    .clip(CircleShape)
                    .background(LightBlueBg)
            )

            Box(
                modifier = Modifier
                    .size(36.dp)
                    .align(Alignment.BottomEnd)
                    .clip(CircleShape)
                    .background(Color.Black),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Outlined.Image,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(18.dp)
                )
            }
        }

        TextButton(onClick = onChangePhotoClick) {
            Text(
                text = stringResource(R.string.change_photo),
                color = AppPrimary,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.width(4.dp))
            Icon(
                imageVector = Icons.Outlined.Edit,
                contentDescription = null,
                tint = AppPrimary,
                modifier = Modifier.size(16.dp)
            )
        }

        Text(
            text = stringResource(R.string.photo_visible),
            fontSize = 13.sp,
            color = SecondaryText,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Etiqueta del nivel del usuario
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = LightBlueBg
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(22.dp)
                        .clip(CircleShape)
                        .background(AppPrimary),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Pets,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(14.dp)
                    )
                }
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = levelName,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    color = DarkText
                )
                Spacer(modifier = Modifier.width(6.dp))
                Icon(
                    imageVector = Icons.Filled.Verified,
                    contentDescription = null,
                    tint = AppPrimary,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}

@Composable
fun ContactSection(
    state: EditProfileUiState,
    localities: List<String>,
    onNameChange: (String) -> Unit,
    onEmailChange: (String) -> Unit,
    onPhoneChange: (String) -> Unit,
    onLocalityChange: (String) -> Unit,
    onNeighborhoodChange: (String) -> Unit,
    onBioChange: (String) -> Unit
) {
    SectionCard {
        SectionTitle(icon = Icons.Outlined.Badge, title = stringResource(R.string.contact_identity))

        CompletionCard(completed = state.completedFields)

        ValidatedField(tag = if (state.isNameValid) stringResource(R.string.tag_valid) else null) {
            IconoTextField(
                titulo = stringResource(R.string.name_surname),
                value = state.name,
                onValueChange = onNameChange,
                placeholderText = stringResource(R.string.full_name_hint),
                icono = Icons.Outlined.Person,
                isError = state.nameError != null,
                mensajeError = state.nameError
            )
        }
        if (state.isNameValid) HelperText(stringResource(R.string.name_verified))

        // El correo muestra "Verificado" solo si no se ha cambiado
        ValidatedField(
            tag = when {
                !state.isEmailValid -> null
                state.isEmailVerified -> stringResource(R.string.tag_verified)
                else -> stringResource(R.string.tag_valid)
            }
        ) {
            IconoTextField(
                titulo = stringResource(R.string.email_label),
                value = state.email,
                onValueChange = onEmailChange,
                placeholderText = "tu.correo@ejemplo.com",
                icono = Icons.Outlined.Email,
                isError = state.emailError != null,
                mensajeError = state.emailError
            )
        }
        HelperText(
            if (state.isEmailVerified) stringResource(R.string.email_confirmed)
            else stringResource(R.string.email_usage)
        )

        ValidatedField(tag = if (state.isPhoneValid) stringResource(R.string.tag_valid_phone) else null) {
            IconoTextField(
                titulo = stringResource(R.string.phone_whatsapp),
                value = state.phone,
                onValueChange = onPhoneChange,
                placeholderText = "+57 300 000 0000",
                icono = Icons.Outlined.Phone,
                isError = state.phoneError != null,
                mensajeError = state.phoneError
            )
        }
        HelperText(
            if (state.isPhoneValid) stringResource(R.string.phone_valid)
            else stringResource(R.string.phone_usage)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            FieldLabel(text = stringResource(R.string.district_bogota), modifier = Modifier.weight(1f))
            if (state.isLocalityValid) ValidTag(stringResource(R.string.tag_assigned))
        }
        // Se reutiliza el desplegable de ubicación del registro
        LocationDropdown(
            value = state.locality,
            onValueChange = onLocalityChange,
            cities = localities
        )
        state.localityError?.let { error ->
            Text(text = error, color = AppRed, fontSize = 12.sp)
        }
        if (state.isLocalityValid) {
            HelperText(stringResource(R.string.locality_assigned, state.locality.substringAfter("(", state.locality).removeSuffix(")")))
        }

        ValidatedField(tag = if (state.isNeighborhoodValid) stringResource(R.string.tag_saved) else null) {
            IconoTextField(
                titulo = stringResource(R.string.neighborhood),
                value = state.neighborhood,
                onValueChange = onNeighborhoodChange,
                placeholderText = stringResource(R.string.neighborhood_hint),
                icono = Icons.Outlined.Place
            )
        }
        if (state.isNeighborhoodValid) HelperText(stringResource(R.string.sector_saved))

        // Presentación con contador de caracteres
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            FieldLabel(
                text = stringResource(R.string.bio_title),
                modifier = Modifier.weight(1f)
            )
            if (state.isBioValid) {
                ValidTag(stringResource(R.string.tag_completed))
                Spacer(modifier = Modifier.width(8.dp))
            }
            Text(
                text = "${state.bioLength}/$BIO_MAX_LENGTH",
                fontSize = 12.sp,
                color = SecondaryText
            )
        }
        TextField(
            value = state.bio,
            onValueChange = onBioChange,
            modifier = Modifier.fillMaxWidth(),
            minLines = 3,
            shape = RoundedCornerShape(16.dp),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color(0xFFF0F4FE),
                unfocusedContainerColor = Color(0xFFF0F4FE),
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent
            )
        )
        HelperText(
            if (state.isBioValid) stringResource(R.string.bio_optimal, state.bioLength, BIO_MAX_LENGTH)
            else stringResource(R.string.bio_help)
        )
    }
}

@Composable
fun VolunteerSection(
    state: EditProfileUiState,
    onTemporaryHomeChange: (Boolean) -> Unit,
    onLostPetAlertsChange: (Boolean) -> Unit,
    onShowPublicPhoneChange: (Boolean) -> Unit
) {
    // El voluntariado está activo si el usuario ofrece hogar de paso o recibe alertas
    val isActive = state.isTemporaryHome || state.lostPetAlerts
    // Solo el nombre de la localidad, sin "(Zona Norte)"
    val localityName = state.locality.substringBefore(" (")

    SectionCard {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.weight(1f)) {
                SectionTitle(
                    icon = Icons.Outlined.VolunteerActivism,
                    title = stringResource(R.string.volunteer_preferences)
                )
            }
            AppTag(
                text = if (isActive) stringResource(R.string.level_active) else stringResource(R.string.inactive),
                backgroundColor = LightBlueBg,
                textColor = AppPrimary
            )
        }

        PreferenceRow(
            title = stringResource(R.string.foster_preference),
            description = stringResource(R.string.foster_preference_detail),
            checked = state.isTemporaryHome,
            onCheckedChange = onTemporaryHomeChange
        )

        HorizontalDivider(color = LightBlueBg)

        PreferenceRow(
            title = stringResource(R.string.lost_alerts),
            description = stringResource(R.string.lost_alerts_detail, localityName),
            checked = state.lostPetAlerts,
            onCheckedChange = onLostPetAlertsChange
        )

        HorizontalDivider(color = LightBlueBg)

        PreferenceRow(
            title = stringResource(R.string.show_public_phone),
            description = stringResource(R.string.show_public_phone_detail),
            checked = state.showPublicPhone,
            onCheckedChange = onShowPublicPhoneChange
        )
    }
}

@Composable
fun SecuritySection(
    onPasswordClick: () -> Unit,
    onNotificationsClick: () -> Unit
) {
    SectionCard {
        SettingsOption(
            icon = Icons.Outlined.LockReset,
            title = stringResource(R.string.change_password_security),
            description = stringResource(R.string.change_password_detail),
            onClick = onPasswordClick
        )
        SettingsOption(
            icon = Icons.Outlined.NotificationsActive,
            title = stringResource(R.string.notification_preferences),
            description = stringResource(R.string.notification_preferences_detail),
            onClick = onNotificationsClick
        )
    }
}

@Composable
fun EditProfileFooter(
    isFormValid: Boolean,
    isComplete: Boolean,
    saveResult: RequestResult?,
    onSaveClick: () -> Unit,
    onCancelClick: () -> Unit,
    onDeactivateClick: () -> Unit
) {
    val isSaving = saveResult is RequestResult.Loading

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Button(
            onClick = onSaveClick,
            // Se deshabilita si el formulario no es válido o mientras guarda
            enabled = isFormValid && !isSaving,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = CircleShape,
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Black,
                contentColor = Color.White,
                disabledContainerColor = Color(0xFF9CA3AF),
                disabledContentColor = Color.White
            )
        ) {
            if (isSaving) {
                Text(text = stringResource(R.string.saving), fontWeight = FontWeight.SemiBold)
            } else {
                Icon(
                    imageVector = Icons.Filled.Check,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    // Con todos los campos completos el botón lo indica, como en "Datos validos"
                    text = if (isComplete) stringResource(R.string.save_changes_ready) else stringResource(R.string.save_changes),
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        Button(
            onClick = onCancelClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = CircleShape,
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFFE2EDFF),
                contentColor = DarkText
            )
        ) {
            Text(text = stringResource(R.string.cancel_discard), fontWeight = FontWeight.Medium)
        }

        TextButton(onClick = onDeactivateClick) {
            Icon(
                imageVector = Icons.Outlined.PersonOff,
                contentDescription = null,
                tint = AppRed,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = stringResource(R.string.deactivate_temp),
                color = AppRed,
                fontSize = 13.sp
            )
        }
    }
}

// ---------------------------------------------------------
// Componentes pequeños de esta pantalla
// ---------------------------------------------------------

@Composable
private fun SectionCard(content: @Composable () -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        color = Color.White
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            content()
        }
    }
}

@Composable
private fun SectionTitle(icon: ImageVector, title: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = AppPrimary
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = title,
            fontSize = 17.sp,
            fontWeight = FontWeight.SemiBold,
            color = DarkText
        )
    }
}

@Composable
private fun FieldLabel(text: String, modifier: Modifier = Modifier) {
    Text(
        text = text,
        modifier = modifier,
        fontSize = 14.sp,
        fontWeight = FontWeight.Medium,
        color = Color(0xFF4A5568)
    )
}

// Tarjeta "x de 6 completados" que aparece en el mockup "Datos validos"
@Composable
private fun CompletionCard(completed: Int) {
    val isComplete = completed == TOTAL_PROFILE_FIELDS
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = LightBlueBg
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(if (isComplete) AppPrimary else Color.White),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.Check,
                    contentDescription = null,
                    tint = if (isComplete) Color.White else AppPrimary,
                    modifier = Modifier.size(18.dp)
                )
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = stringResource(R.string.fields_completed, completed, TOTAL_PROFILE_FIELDS),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = DarkText
                )
                Text(
                    text = if (isComplete) stringResource(R.string.all_fields_valid)
                    else stringResource(R.string.complete_fields),
                    fontSize = 12.sp,
                    color = SecondaryText
                )
            }
        }
    }
}

// Campo con una etiqueta de validación en la esquina superior derecha
@Composable
private fun ValidatedField(tag: String?, content: @Composable () -> Unit) {
    Box(modifier = Modifier.fillMaxWidth()) {
        content()
        if (tag != null) {
            ValidTag(text = tag, modifier = Modifier.align(Alignment.TopEnd))
        }
    }
}

@Composable
private fun ValidTag(text: String, modifier: Modifier = Modifier) {
    AppTag(
        text = text,
        modifier = modifier,
        backgroundColor = LightBlueBg,
        textColor = AppPrimary
    )
}

@Composable
private fun HelperText(text: String) {
    Text(
        text = text,
        fontSize = 12.sp,
        color = SecondaryText
    )
}

@Composable
private fun PreferenceRow(
    title: String,
    description: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
                color = DarkText
            )
            Text(
                text = description,
                fontSize = 12.sp,
                color = SecondaryText
            )
        }
        Spacer(modifier = Modifier.width(12.dp))
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedTrackColor = Color.Black,
                checkedThumbColor = Color.White,
                uncheckedTrackColor = LightBlueBg,
                uncheckedBorderColor = LightBlueBg,
                uncheckedThumbColor = Color.White
            )
        )
    }
}

@Composable
private fun SettingsOption(
    icon: ImageVector,
    title: String,
    description: String,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = Color.White
    ) {
        Row(
            modifier = Modifier.padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(LightBlueBg),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = AppPrimary,
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = DarkText
                )
                Text(
                    text = description,
                    fontSize = 12.sp,
                    color = SecondaryText
                )
            }
            Icon(
                imageVector = Icons.AutoMirrored.Outlined.KeyboardArrowRight,
                contentDescription = null,
                tint = SecondaryText
            )
        }
    }
}
