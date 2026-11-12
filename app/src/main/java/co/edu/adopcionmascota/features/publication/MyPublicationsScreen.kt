package co.edu.adopcionmascota.features.publication

import androidx.compose.ui.res.stringResource
import co.edu.adopcionmascota.R
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import co.edu.adopcionmascota.domain.model.Publication
import co.edu.adopcionmascota.domain.model.PublicationStatus
import co.edu.adopcionmascota.domain.model.PetType
import co.edu.adopcionmascota.core.util.rememberImageModel
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.ChatBubbleOutline
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.DeleteOutline
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Groups
import androidx.compose.material.icons.outlined.HourglassTop
import androidx.compose.material.icons.automirrored.outlined.MenuBook
import androidx.compose.material.icons.outlined.ReportProblem
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import co.edu.adopcionmascota.core.component.AppBottomBar
import co.edu.adopcionmascota.core.component.AppDestination
import co.edu.adopcionmascota.core.component.AppSearchBar
import co.edu.adopcionmascota.core.component.AppTag
import co.edu.adopcionmascota.core.component.ConfirmAlertDialog
import co.edu.adopcionmascota.core.theme.AppPrimary
import co.edu.adopcionmascota.core.theme.Background
import co.edu.adopcionmascota.core.theme.DarkText
import co.edu.adopcionmascota.core.theme.LightBlueBg
import co.edu.adopcionmascota.core.theme.SecondaryText
import coil3.compose.AsyncImage

private val Green = Color(0xFF067647)
private val Danger = Color(0xFFBA1A1A)
private val Warning = Color(0xFFB54708)

// Pantalla "01 · Mis publicaciones — Gestión"
@Composable
fun MyPublicationsScreen(
    onNavigateBack: () -> Unit,
    onCreateClick: () -> Unit,                       // stringResource(R.string.create)
    onEditClick: (String) -> Unit,                   // stringResource(R.string.edit), stringResource(R.string.edit_info), stringResource(R.string.fix_resend)
    onDeleteClick: (String) -> Unit,                 // Icono de eliminar
    onViewPublication: (String) -> Unit,             // Nombre de la mascota / stringResource(R.string.success_story)
    onNavigationClick: (AppDestination) -> Unit,
    onRequestsClick: () -> Unit = {},                // Solicitudes de adopción recibidas
    viewModel: MyPublicationsViewModel = hiltViewModel()
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val state by viewModel.uiState.collectAsState()

    LaunchedEffect(state.infoMessage) {
        state.infoMessage?.let { message ->
            snackbarHostState.showSnackbar(message)
            viewModel.resetInfoMessage()
        }
    }

    Scaffold(
        modifier = Modifier.statusBarsPadding(),
        containerColor = Background,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = {
            AppBottomBar(
                currentDestination = AppDestination.PROFILE,
                onDestinationSelected = onNavigationClick
            )
        }
    ) { paddingValues ->

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Outlined.ArrowBack, contentDescription = stringResource(R.string.back), tint = DarkText)
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = stringResource(R.string.my_publications), fontSize = 22.sp, fontWeight = FontWeight.Bold, color = DarkText)
                            Spacer(modifier = Modifier.width(8.dp))
                            AppTag(text = stringResource(R.string.total_count, state.publications.size))
                        }
                        Text(
                            text = stringResource(R.string.active_resolved, state.activeCount, state.resolvedCount),
                            fontSize = 12.sp,
                            color = SecondaryText
                        )
                    }
                    Button(
                        onClick = onCreateClick,
                        shape = CircleShape,
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Black)
                    ) {
                        Icon(Icons.Outlined.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = stringResource(R.string.create))
                    }
                }
            }

            item {
                Row(
                    modifier = Modifier.horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    MyPublicationsTab.entries.forEach { tab ->
                        val selected = tab == state.tab
                        Surface(
                            onClick = { viewModel.onTabChange(tab) },
                            shape = CircleShape,
                            color = if (selected) Color.Black else LightBlueBg
                        ) {
                            Text(
                                text = stringResource(R.string.filter_count, stringResource(tab.label), state.countFor(tab)),
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                                fontSize = 13.sp,
                                color = if (selected) Color.White else DarkText
                            )
                        }
                    }
                }
            }

            item {
                Surface(shape = RoundedCornerShape(16.dp), color = AppPrimary.copy(alpha = 0.1f)) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Outlined.Groups, contentDescription = null, tint = AppPrimary)
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(text = stringResource(R.string.local_support), fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = DarkText)
                            Text(
                                text = stringResource(R.string.reach_neighbors, state.totalReach),
                                fontSize = 12.sp,
                                color = SecondaryText
                            )
                        }
                    }
                }
            }

            // Solicitudes de adopción y hogar de paso que otros usuarios enviaron
            item {
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = Green.copy(alpha = 0.12f),
                    modifier = Modifier.clickable(onClick = onRequestsClick)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Outlined.Groups, contentDescription = null, tint = Green)
                        Spacer(modifier = Modifier.width(10.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(text = stringResource(R.string.requests_received), fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = DarkText)
                            Text(
                                text = stringResource(R.string.requests_pending_count, state.pendingRequests),
                                fontSize = 12.sp,
                                color = SecondaryText
                            )
                        }
                        Text(text = stringResource(R.string.view), color = Green, fontWeight = FontWeight.SemiBold)
                    }
                }
            }

            item {
                AppSearchBar(
                    value = state.searchText,
                    onValueChange = viewModel::onSearchChange,
                    placeholder = stringResource(R.string.search_my_publications)
                )
            }

            if (state.visiblePublications.isEmpty()) {
                item {
                    Text(
                        text = stringResource(R.string.no_publications_category),
                        modifier = Modifier.padding(vertical = 24.dp),
                        color = SecondaryText
                    )
                }
            }

            items(state.visiblePublications, key = { it.id }) { publication ->
                when (publication.status) {
                    PublicationStatus.VERIFIED, PublicationStatus.PAUSED -> ActivePublicationCard(
                        publication = publication,
                        comments = state.commentCounts[publication.id] ?: 0,
                        requests = state.requestCounts[publication.id] ?: 0,
                        onNameClick = { onViewPublication(publication.id) },
                        onMarkResolved = { viewModel.onMarkResolved(publication.id) },
                        onEdit = { onEditClick(publication.id) },
                        onDelete = { onDeleteClick(publication.id) }
                    )

                    PublicationStatus.PENDING -> InReviewCard(
                        publication = publication,
                        onCancel = { viewModel.onCancelRequestClick(publication) },
                        onEdit = { onEditClick(publication.id) }
                    )

                    PublicationStatus.RESOLVED -> ResolvedCard(
                        publication = publication,
                        onViewStory = { onViewPublication(publication.id) },
                        onDelete = { onDeleteClick(publication.id) }
                    )

                    PublicationStatus.REJECTED -> if (publication.id !in state.hiddenNotices) {
                        ObservationCard(
                            publication = publication,
                            onDiscard = { viewModel.onDiscardNotice(publication.id) },
                            onFix = { onEditClick(publication.id) }
                        )
                    }

                    PublicationStatus.DELETED -> Unit
                }
            }

            item {
                Surface(shape = RoundedCornerShape(16.dp), color = LightBlueBg) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(text = stringResource(R.string.publications_help), fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = DarkText)
                        Text(
                            text = stringResource(R.string.publications_help_detail),
                            fontSize = 12.sp,
                            color = SecondaryText
                        )
                        TextButton(onClick = viewModel::onGuideClick) {
                            Icon(Icons.AutoMirrored.Outlined.MenuBook, contentDescription = null, tint = AppPrimary, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(text = stringResource(R.string.photo_guide), color = AppPrimary)
                        }
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
            }
        }

        state.publicationToCancel?.let { publication ->
            ConfirmAlertDialog(
                title = stringResource(R.string.cancel_request_title),
                message = stringResource(R.string.cancel_request_message, publication.name),
                onConfirm = viewModel::onConfirmCancelRequest,
                onDismiss = viewModel::onDismissCancelRequest,
                confirmText = stringResource(R.string.cancel_request),
                dismissText = stringResource(R.string.back)
            )
        }

        if (state.showGuideDialog) {
            ConfirmAlertDialog(
                title = stringResource(R.string.photo_guide),
                message = stringResource(R.string.photo_guide_message),
                onConfirm = viewModel::onDismissGuide,
                onDismiss = viewModel::onDismissGuide,
                confirmText = stringResource(R.string.understood),
                dismissText = stringResource(R.string.close)
            )
        }
    }
}

@Composable
private fun PublicationHeader(
    publication: Publication,
    tag: String,
    tagColor: Color,
    onNameClick: (() -> Unit)? = null
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        AsyncImage(
            model = rememberImageModel(publication.image),
            contentDescription = publication.name,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(64.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(LightBlueBg)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            AppTag(text = tag, backgroundColor = tagColor.copy(alpha = 0.12f), textColor = tagColor)
            // Tocar el nombre abre el detalle de la publicación (como en el prototipo)
            Text(
                text = publication.name,
                modifier = Modifier.clickable(enabled = onNameClick != null) { onNameClick?.invoke() },
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                color = if (onNameClick != null) AppPrimary else DarkText
            )
            Text(
                text = stringResource(R.string.category_breed, publication.type.label(), publication.breed.ifBlank { publication.species }),
                fontSize = 12.sp,
                color = SecondaryText,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(text = publication.location, fontSize = 11.sp, color = SecondaryText)
        }
    }
}

@Composable
private fun CardContainer(content: @Composable () -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        color = Color.White
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) { content() }
    }
}

@Composable
private fun ActivePublicationCard(
    publication: Publication,
    comments: Int,
    requests: Int,
    onNameClick: () -> Unit,
    onMarkResolved: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    CardContainer {
        Row(verticalAlignment = Alignment.Top) {
            Box(modifier = Modifier.weight(1f)) {
                PublicationHeader(
                    publication = publication,
                    tag = stringResource(publication.status.label),
                    tagColor = if (publication.status == PublicationStatus.PAUSED) Warning else Green,
                    onNameClick = onNameClick
                )
            }
            IconButton(onClick = onDelete) {
                Icon(Icons.Outlined.DeleteOutline, contentDescription = stringResource(R.string.delete_publication_lower), tint = Danger)
            }
        }
        Text(text = publication.description, fontSize = 12.sp, color = SecondaryText, maxLines = 2, overflow = TextOverflow.Ellipsis)
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            Stat(icon = Icons.Outlined.Star, value = publication.likes, label = stringResource(R.string.important_label))
            Stat(icon = Icons.Outlined.ChatBubbleOutline, value = comments, label = stringResource(R.string.comments))
            Stat(icon = Icons.Outlined.Share, value = requests, label = stringResource(R.string.requests_label))
        }
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            OutlinedButton(onClick = onMarkResolved, modifier = Modifier.weight(1f), shape = CircleShape) {
                Icon(Icons.Outlined.CheckCircle, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(text = stringResource(R.string.mark_resolved), fontSize = 12.sp)
            }
            Button(
                onClick = onEdit,
                modifier = Modifier.weight(1f),
                shape = CircleShape,
                colors = ButtonDefaults.buttonColors(containerColor = Color.Black)
            ) {
                Icon(Icons.Outlined.Edit, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(text = stringResource(R.string.edit), fontSize = 12.sp)
            }
        }
    }
}

@Composable
private fun Stat(icon: ImageVector, value: Int, label: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, contentDescription = null, tint = AppPrimary, modifier = Modifier.size(14.dp))
        Spacer(modifier = Modifier.width(4.dp))
        Text(text = "$value", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = DarkText)
        Spacer(modifier = Modifier.width(3.dp))
        Text(text = label, fontSize = 11.sp, color = SecondaryText)
    }
}

@Composable
private fun InReviewCard(
    publication: Publication,
    onCancel: () -> Unit,
    onEdit: () -> Unit
) {
    CardContainer {
        PublicationHeader(publication = publication, tag = stringResource(publication.status.label), tagColor = AppPrimary)
        Surface(shape = RoundedCornerShape(12.dp), color = LightBlueBg) {
            Row(modifier = Modifier.padding(12.dp)) {
                Icon(Icons.Outlined.HourglassTop, contentDescription = null, tint = AppPrimary, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(text = stringResource(R.string.community_validation), fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = DarkText)
                    Text(text = publication.description, fontSize = 12.sp, color = SecondaryText)
                }
            }
        }
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            OutlinedButton(onClick = onCancel, modifier = Modifier.weight(1f), shape = CircleShape) {
                Text(text = stringResource(R.string.cancel_request_upper), fontSize = 12.sp, color = Danger)
            }
            Button(
                onClick = onEdit,
                modifier = Modifier.weight(1f),
                shape = CircleShape,
                colors = ButtonDefaults.buttonColors(containerColor = Color.Black)
            ) {
                Text(text = stringResource(R.string.edit_info), fontSize = 12.sp)
            }
        }
    }
}

@Composable
private fun ResolvedCard(
    publication: Publication,
    onViewStory: () -> Unit,
    onDelete: () -> Unit
) {
    CardContainer {
        Row(verticalAlignment = Alignment.Top) {
            Box(modifier = Modifier.weight(1f)) {
                PublicationHeader(publication = publication, tag = stringResource(publication.status.label), tagColor = Green)
            }
            IconButton(onClick = onDelete) {
                Icon(Icons.Outlined.DeleteOutline, contentDescription = stringResource(R.string.delete_publication_lower), tint = Danger)
            }
        }
        Text(text = publication.description, fontSize = 12.sp, color = SecondaryText)
        TextButton(onClick = onViewStory) {
            Text(text = stringResource(R.string.success_story), color = AppPrimary, fontWeight = FontWeight.SemiBold)
        }
    }
}

@Composable
private fun ObservationCard(
    publication: Publication,
    onDiscard: () -> Unit,
    onFix: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        color = Danger.copy(alpha = 0.06f)
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Outlined.ReportProblem, contentDescription = null, tint = Danger)
                Spacer(modifier = Modifier.width(8.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = stringResource(R.string.moderation_notice, publication.name), fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = DarkText)
                    Text(text = stringResource(R.string.paused_temporarily), fontSize = 12.sp, color = SecondaryText)
                }
                AppTag(text = stringResource(publication.status.label), backgroundColor = Danger.copy(alpha = 0.12f), textColor = Danger)
            }
            Text(text = stringResource(R.string.community_moderator_reason), fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = DarkText)
            Text(text = publication.rejectionReason.orEmpty(), fontSize = 12.sp, color = SecondaryText)
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedButton(onClick = onDiscard, modifier = Modifier.weight(1f), shape = CircleShape) {
                    Text(text = stringResource(R.string.discard), fontSize = 12.sp)
                }
                Button(
                    onClick = onFix,
                    modifier = Modifier.weight(1f),
                    shape = CircleShape,
                    colors = ButtonDefaults.buttonColors(containerColor = Danger)
                ) {
                    Text(text = stringResource(R.string.fix_resend), fontSize = 12.sp)
                }
            }
        }
    }
}

// Nombre visible de la categoría de la publicación
@Composable
fun PetType.label(): String = when (this) {
    PetType.ADOPCION -> stringResource(R.string.adoption)
    PetType.PERDIDO -> stringResource(R.string.option_lost)
    PetType.ENCONTRADO -> stringResource(R.string.type_found)
    PetType.VETERINARIA -> stringResource(R.string.option_vet)
}
