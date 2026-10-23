package co.edu.adopcionmascota.features.comment

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.automirrored.outlined.ArrowForward
import androidx.compose.material.icons.automirrored.outlined.HelpOutline
import androidx.compose.material.icons.automirrored.outlined.Send
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material.icons.outlined.AlternateEmail
import androidx.compose.material.icons.outlined.AddPhotoAlternate
import androidx.compose.material.icons.outlined.Explore
import androidx.compose.material.icons.outlined.Forum
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.LocalHospital
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.NotificationsActive
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.QuestionAnswer
import androidx.compose.material.icons.outlined.Shield
import androidx.compose.material.icons.outlined.ThumbUp
import androidx.compose.material.icons.automirrored.outlined.TrendingUp
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import co.edu.adopcionmascota.R
import co.edu.adopcionmascota.core.component.AppTag
import co.edu.adopcionmascota.core.component.ConfirmAlertDialog
import co.edu.adopcionmascota.core.theme.AppPrimary
import co.edu.adopcionmascota.core.theme.AppRed
import co.edu.adopcionmascota.core.theme.Background
import co.edu.adopcionmascota.core.theme.DarkText
import co.edu.adopcionmascota.core.theme.SecondaryText
import co.edu.adopcionmascota.core.util.RequestResult
import coil3.compose.AsyncImage

private val SoftBlue = Color(0xFFEDF4FF)
private val ChipBlue = Color(0xFFD8EAFF)
private val SelectedChip = Color(0xFF5565F2)

// Pantalla "Mockup 19 crear comentario" + hoja "¡Aporte Agregado con Éxito!"
@Composable
fun CreateCommentScreen(
    publicationId: String,
    onNavigateBack: () -> Unit,        // "Descartar y volver" / "Volver a la Publicación"
    onNavigateToFeed: () -> Unit,      // "Ir al Feed Principal"
    viewModel: CreateCommentViewModel = viewModel(key = "comment_$publicationId") {
        CreateCommentViewModel(publicationId)
    }
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val state by viewModel.uiState.collectAsState()

    // "Atrás" se comporta igual que "Descartar y volver"
    BackHandler(enabled = !state.showDiscardDialog) {
        if (viewModel.onDiscardClick()) onNavigateBack()
    }

    LaunchedEffect(state.publishResult) {
        when (val result = state.publishResult) {
            is RequestResult.Failure -> snackbarHostState.showSnackbar(result.errorMessage)
            // El éxito se muestra con la hoja inferior; no se navega automáticamente
            is RequestResult.Success, is RequestResult.Loading, null -> Unit
        }
    }

    LaunchedEffect(state.infoMessage) {
        state.infoMessage?.let { message ->
            snackbarHostState.showSnackbar(message)
            viewModel.resetInfoMessage()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
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

                CommentTopBar(
                    onBackClick = { if (viewModel.onDiscardClick()) onNavigateBack() },
                    onHelpClick = viewModel::onHelpClick
                )

                CaseSummaryCard(state = state)

                ContributionTypeSelector(
                    selected = state.type,
                    onSelect = viewModel::onTypeChange
                )

                CommentForm(
                    state = state,
                    onTextChange = viewModel::onTextChange,
                    onAttachPhotoClick = viewModel::onAttachPhotoClick,
                    onShareLocationClick = viewModel::onShareLocationClick,
                    onMentionClick = viewModel::onMentionClick
                )

                CommitmentSection(
                    petName = state.petName,
                    notifyReplies = state.notifyReplies,
                    onNotifyRepliesChange = viewModel::onNotifyRepliesChange
                )

                CommentActions(
                    isPublishing = state.isPublishing,
                    onPublishClick = viewModel::publishComment,
                    onDiscardClick = { if (viewModel.onDiscardClick()) onNavigateBack() }
                )

                RecentCommentsWall(
                    comments = state.recentComments,
                    total = state.totalComments,
                    onThanksClick = viewModel::onThanksClick
                )

                Spacer(modifier = Modifier.height(8.dp))
            }
        }

        // Hoja de éxito ("Overlay+OverlayBlur" del Figma)
        val published = state.publishedComment
        if (state.publishResult is RequestResult.Success && published != null) {
            CommentSuccessSheet(
                petName = state.petName,
                comment = published,
                onBackToPublication = onNavigateBack,
                onGoToFeed = onNavigateToFeed
            )
        }

        if (state.showDiscardDialog) {
            ConfirmAlertDialog(
                title = "¿Descartar tu aporte?",
                message = "Se borrará el borrador que escribiste para ${state.petName}.",
                onConfirm = {
                    viewModel.onConfirmDiscard()
                    onNavigateBack()
                },
                onDismiss = viewModel::onDismissDiscardDialog,
                confirmText = "Descartar"
            )
        }
    }
}

@Composable
private fun CommentTopBar(
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
                contentDescription = "Volver",
                tint = DarkText
            )
        }
        Text(
            text = "PATITAS\nCONECTADAS",
            modifier = Modifier.weight(1f),
            fontSize = 11.sp,
            lineHeight = 11.sp,
            fontWeight = FontWeight.SemiBold,
            color = AppPrimary
        )
        IconButton(onClick = onHelpClick) {
            Icon(
                imageVector = Icons.AutoMirrored.Outlined.HelpOutline,
                contentDescription = "Ayuda y soporte",
                tint = SecondaryText
            )
        }
        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
                .background(Color.Black),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Outlined.Person,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(16.dp)
            )
        }
    }
}

@Composable
private fun CaseSummaryCard(state: CreateCommentUiState) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = SoftBlue
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = R.drawable.imagen_max,
                contentDescription = state.petName,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(64.dp)
                    .clip(RoundedCornerShape(8.dp))
            )
            Spacer(modifier = Modifier.width(14.dp))
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = state.petName,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = DarkText
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    AppTag(
                        text = "● En adopción activa",
                        backgroundColor = Color(0xFFDFE0FF),
                        textColor = Color(0xFF000865)
                    )
                }
                Text(
                    text = state.petDescription,
                    fontSize = 12.sp,
                    color = SecondaryText
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Outlined.LocationOn,
                        contentDescription = null,
                        tint = Color(0xFF76777C),
                        modifier = Modifier.size(12.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = state.petLocation,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF76777C)
                    )
                }
            }
        }
    }
}

@Composable
private fun ContributionTypeSelector(
    selected: ContributionType,
    onSelect: (ContributionType) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = "TIPO DE CONTRIBUCIÓN",
            fontSize = 11.sp,
            color = SecondaryText
        )
        Row(
            modifier = Modifier.horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            ContributionType.entries.forEach { type ->
                val isSelected = type == selected
                Surface(
                    onClick = { onSelect(type) },
                    shape = CircleShape,
                    color = if (isSelected) SelectedChip else ChipBlue
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = type.icon(),
                            contentDescription = null,
                            tint = if (isSelected) Color.White else SecondaryText,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = type.label,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = if (isSelected) Color.White else SecondaryText
                        )
                    }
                }
            }
        }
    }
}

private fun ContributionType.icon(): ImageVector = when (this) {
    ContributionType.SUPPORT -> Icons.Outlined.QuestionAnswer
    ContributionType.SIGHTING -> Icons.Outlined.Visibility
    ContributionType.VET_FOOD -> Icons.Outlined.LocalHospital
    ContributionType.TEMPORARY_HOME -> Icons.Outlined.Home
}

@Composable
private fun CommentForm(
    state: CreateCommentUiState,
    onTextChange: (String) -> Unit,
    onAttachPhotoClick: () -> Unit,
    onShareLocationClick: () -> Unit,
    onMentionClick: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = Color.White
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Identidad del usuario activo
            Row(verticalAlignment = Alignment.CenterVertically) {
                Avatar(initials = "CM", background = Color(0xFFDFE0FF), size = 40)
                Spacer(modifier = Modifier.width(10.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = state.authorName, fontSize = 14.sp, color = DarkText)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Outlined.Shield,
                            contentDescription = null,
                            tint = AppPrimary,
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = state.authorLevel, fontSize = 13.sp, color = AppPrimary)
                    }
                }
                AppTag(
                    text = "Público",
                    backgroundColor = Color(0xFFE3EFFF),
                    textColor = Color(0xFF76777C)
                )
            }

            // Área de texto con contador y estado del borrador
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                color = SoftBlue
            ) {
                Column(modifier = Modifier.padding(bottom = 10.dp)) {
                    TextField(
                        value = state.text,
                        onValueChange = onTextChange,
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 4,
                        placeholder = {
                            Text(
                                text = "Aportar información sobre ${state.petName} o apoyo...",
                                fontSize = 14.sp
                            )
                        },
                        isError = state.textError != null,
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            errorContainerColor = Color.Transparent,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                            errorIndicatorColor = Color.Transparent
                        )
                    )
                    Row(modifier = Modifier.padding(horizontal = 16.dp)) {
                        Text(
                            text = "${state.textLength} / $COMMENT_MAX_LENGTH",
                            modifier = Modifier.weight(1f),
                            fontSize = 13.sp,
                            color = Color(0xFF76777C)
                        )
                        if (state.isDraftSaved) {
                            Text(
                                text = "Borrador autoguardado",
                                fontSize = 13.sp,
                                color = AppPrimary
                            )
                        }
                    }
                }
            }
            state.textError?.let { error ->
                Text(text = error, color = AppRed, fontSize = 12.sp)
            }

            // Barra de herramientas de inserción rápida
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onAttachPhotoClick) {
                    Icon(
                        imageVector = Icons.Outlined.AddPhotoAlternate,
                        contentDescription = "Adjuntar foto",
                        tint = AppPrimary
                    )
                }
                IconButton(onClick = onShareLocationClick) {
                    Icon(
                        imageVector = Icons.Outlined.LocationOn,
                        contentDescription = "Compartir ubicación",
                        tint = AppPrimary
                    )
                }
                IconButton(onClick = onMentionClick) {
                    Icon(
                        imageVector = Icons.Outlined.AlternateEmail,
                        contentDescription = "Mencionar voluntario",
                        tint = AppPrimary
                    )
                }
                Spacer(modifier = Modifier.weight(1f))
                Surface(
                    shape = CircleShape,
                    color = Color(0xFFDFE0FF)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Outlined.TrendingUp,
                            contentDescription = null,
                            tint = AppPrimary,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "+$COMMENT_POINTS pts",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = AppPrimary
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun CommitmentSection(
    petName: String,
    notifyReplies: Boolean,
    onNotifyRepliesChange: (Boolean) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp),
            color = SoftBlue
        ) {
            Row(
                modifier = Modifier.padding(12.dp),
                verticalAlignment = Alignment.Top
            ) {
                Icon(
                    imageVector = Icons.Outlined.Shield,
                    contentDescription = null,
                    tint = AppPrimary,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = "Tu aporte será visible para toda la red de rescate. Fomentamos un entorno de colaboración, respeto y veracidad por la seguridad de $petName.",
                    fontSize = 12.sp,
                    color = SecondaryText
                )
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .clickable { onNotifyRepliesChange(!notifyReplies) },
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Outlined.NotificationsActive,
                contentDescription = null,
                tint = SecondaryText,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Notificarme respuestas y cambios de estado",
                modifier = Modifier.weight(1f),
                fontSize = 14.sp,
                color = DarkText
            )
            Checkbox(
                checked = notifyReplies,
                onCheckedChange = onNotifyRepliesChange,
                colors = CheckboxDefaults.colors(checkedColor = Color(0xFF3A49D8))
            )
        }
    }
}

@Composable
private fun CommentActions(
    isPublishing: Boolean,
    onPublishClick: () -> Unit,
    onDiscardClick: () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Button(
            onClick = onPublishClick,
            enabled = !isPublishing,
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            shape = CircleShape,
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Black,
                contentColor = Color.White,
                disabledContainerColor = Color(0xFF9CA3AF),
                disabledContentColor = Color.White
            )
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Outlined.Send,
                contentDescription = null,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = if (isPublishing) "Publicando..." else "Publicar Comentario")
        }

        Button(
            onClick = onDiscardClick,
            enabled = !isPublishing,
            modifier = Modifier
                .fillMaxWidth()
                .height(44.dp),
            shape = CircleShape,
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFFE3EFFF),
                contentColor = SecondaryText
            )
        ) {
            Text(text = "Descartar y volver")
        }
    }
}

@Composable
private fun RecentCommentsWall(
    comments: List<Comment>,
    total: Int,
    onThanksClick: (String) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Outlined.Forum,
                contentDescription = null,
                tint = AppPrimary,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = "Aportes recientes en el caso",
                modifier = Modifier.weight(1f),
                fontSize = 14.sp,
                color = DarkText
            )
            Text(
                text = "$total comentarios",
                fontSize = 14.sp,
                color = Color(0xFF76777C)
            )
        }

        if (comments.isEmpty()) {
            Text(
                text = "Aún no hay aportes. ¡Sé la primera persona en ayudar!",
                fontSize = 13.sp,
                color = SecondaryText
            )
        }

        comments.forEach { comment ->
            CommentCard(comment = comment, onThanksClick = { onThanksClick(comment.id) })
        }
    }
}

// Tarjeta de comentario; también la usa el detalle de la publicación
@Composable
fun CommentCard(
    comment: Comment,
    onThanksClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = Color.White
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Avatar(initials = comment.initials, background = Color(0xFFDFE0FF), size = 32)
                Spacer(modifier = Modifier.width(10.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = comment.author,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = DarkText,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        AppTag(
                            text = comment.authorTag,
                            backgroundColor = Color(0xFFCEE5FF),
                            textColor = SecondaryText
                        )
                    }
                    Text(
                        text = "${comment.time} · ${comment.type.label}",
                        fontSize = 11.sp,
                        color = Color(0xFF76777C)
                    )
                }
                // "Agradecer": suma o quita el agradecimiento
                Row(
                    modifier = Modifier
                        .clip(CircleShape)
                        .clickable(onClick = onThanksClick)
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = if (comment.thankedByMe) Icons.Filled.ThumbUp else Icons.Outlined.ThumbUp,
                        contentDescription = "Agradecer",
                        tint = if (comment.thankedByMe) AppPrimary else Color(0xFF76777C),
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = comment.thanks.toString(),
                        fontSize = 13.sp,
                        color = if (comment.thankedByMe) AppPrimary else Color(0xFF76777C)
                    )
                }
            }
            Text(
                text = comment.text,
                fontSize = 13.sp,
                color = DarkText
            )
        }
    }
}

@Composable
private fun Avatar(initials: String, background: Color, size: Int) {
    Box(
        modifier = Modifier
            .size(size.dp)
            .clip(CircleShape)
            .background(background),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = initials,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFF000865)
        )
    }
}

@Composable
private fun CommentSuccessSheet(
    petName: String,
    comment: Comment,
    onBackToPublication: () -> Unit,
    onGoToFeed: () -> Unit
) {
    // El botón "atrás" con la hoja abierta regresa a la publicación
    BackHandler { onBackToPublication() }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF040915).copy(alpha = 0.6f))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onBackToPublication
            )
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                // Evita que un toque dentro de la hoja la cierre
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = {}
                ),
            shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
            color = Color.White
        ) {
            Column(
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .navigationBarsPadding()
                    .padding(horizontal = 20.dp, vertical = 12.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Box(
                    modifier = Modifier
                        .width(40.dp)
                        .height(4.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFC6C6CC).copy(alpha = 0.6f))
                )

                Text(
                    text = "¡Aporte Agregado con Éxito!",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = DarkText,
                    textAlign = TextAlign.Center
                )
                Text(
                    text = "Tu aporte ya es visible en la ficha de $petName para toda la comunidad de rescate.",
                    fontSize = 12.sp,
                    color = SecondaryText,
                    textAlign = TextAlign.Center
                )

                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFFCEE5FF).copy(alpha = 0.5f)
                ) {
                    Row(modifier = Modifier.padding(14.dp)) {
                        Icon(
                            imageVector = Icons.Outlined.NotificationsActive,
                            contentDescription = null,
                            tint = AppPrimary,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "El rescatista y los seguidores del caso han sido notificados de tu actualización sobre $petName en tiempo real.",
                            fontSize = 12.sp,
                            color = SecondaryText
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFDFE0FF).copy(alpha = 0.5f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.CheckCircle,
                        contentDescription = null,
                        tint = AppPrimary,
                        modifier = Modifier.size(36.dp)
                    )
                }

                // Vista previa del aporte publicado
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    color = SoftBlue
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        AsyncImage(
                            model = R.drawable.imagen_max,
                            contentDescription = petName,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(56.dp)
                                .clip(RoundedCornerShape(12.dp))
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            AppTag(
                                text = "• Aporte Comunitario Verificado",
                                backgroundColor = SelectedChip,
                                textColor = Color.White
                            )
                            Text(
                                text = "“${comment.text.take(40)}${if (comment.text.length > 40) "..." else ""}”",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = DarkText
                            )
                            Text(
                                text = comment.type.label,
                                fontSize = 11.sp,
                                color = SecondaryText
                            )
                        }
                    }
                }

                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFFE3EFFF).copy(alpha = 0.7f)
                ) {
                    Text(
                        text = "+$COMMENT_POINTS puntos de reputación comunitaria otorgados (¡Gracias por tu apoyo!)",
                        modifier = Modifier.padding(14.dp),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF3A49D8)
                    )
                }

                Button(
                    onClick = onBackToPublication,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = CircleShape,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Black,
                        contentColor = Color.White
                    )
                ) {
                    Text(text = "Volver a la Publicación de $petName", fontWeight = FontWeight.SemiBold)
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(
                        imageVector = Icons.AutoMirrored.Outlined.ArrowForward,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp)
                    )
                }

                Button(
                    onClick = onGoToFeed,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = CircleShape,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFE3EFFF),
                        contentColor = DarkText
                    )
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Explore,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "Ir al Feed Principal", fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}
