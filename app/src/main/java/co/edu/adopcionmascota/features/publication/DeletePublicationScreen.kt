package co.edu.adopcionmascota.features.publication

import androidx.compose.ui.res.stringResource
import co.edu.adopcionmascota.R
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import co.edu.adopcionmascota.domain.model.PublicationStatus
import co.edu.adopcionmascota.core.util.rememberImageModel
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Celebration
import androidx.compose.material.icons.outlined.DeleteForever
import androidx.compose.material.icons.outlined.PauseCircle
import androidx.compose.material.icons.outlined.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.RadioButton
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import co.edu.adopcionmascota.core.component.AppTag
import co.edu.adopcionmascota.core.theme.AppPrimary
import co.edu.adopcionmascota.core.theme.Background
import co.edu.adopcionmascota.core.theme.DarkText
import co.edu.adopcionmascota.core.theme.LightBlueBg
import co.edu.adopcionmascota.core.theme.SecondaryText
import co.edu.adopcionmascota.core.util.RequestResult
import coil3.compose.AsyncImage

private val Danger = Color(0xFFBA1A1A)
private val DangerBg = Color(0xFFFFDAD6)
private val Green = Color(0xFF067647)

// Pantalla "04 · Eliminar publicación — Confirmación" + "Eliminada Correctamente"
@Composable
fun DeletePublicationScreen(
    publicationId: String,
    onNavigateBack: () -> Unit,                 // "Conservar publicación"
    onNavigateToMyPublications: () -> Unit,     // "Volver a 'Mis Publicaciones'"
    viewModel: DeletePublicationViewModel = hiltViewModel()
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val state by viewModel.uiState.collectAsState()
    val publication = state.publication

    LaunchedEffect(state.infoMessage) {
        state.infoMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.resetInfoMessage()
        }
    }

    // "Marcar como Adoptado" archiva el caso y vuelve a la lista
    LaunchedEffect(state.leaveScreen) {
        if (state.leaveScreen) onNavigateToMyPublications()
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
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Outlined.ArrowBack, contentDescription = stringResource(R.string.back), tint = DarkText)
                    }
                    Text(text = stringResource(R.string.delete_publication), fontSize = 20.sp, fontWeight = FontWeight.Bold, color = DarkText)
                }

                if (publication == null) {
                    Text(text = stringResource(R.string.publication_gone), color = SecondaryText)
                    return@Column
                }

                // Resumen del caso
                Surface(shape = RoundedCornerShape(20.dp), color = Color.White) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        AsyncImage(
                            model = rememberImageModel(publication.image),
                            contentDescription = publication.name,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(72.dp)
                                .clip(RoundedCornerShape(14.dp))
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text(text = stringResource(R.string.case_number, publication.id), fontSize = 11.sp, color = SecondaryText)
                            Text(text = stringResource(R.string.name_breed, publication.name, publication.breed), fontSize = 16.sp, fontWeight = FontWeight.Bold, color = DarkText)
                            Text(text = publication.location, fontSize = 12.sp, color = SecondaryText)
                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                AppTag(text = stringResource(publication.status.label))
                                AppTag(text = stringResource(R.string.followers_count, publication.likes))
                            }
                        }
                    }
                }

                // Advertencia
                Surface(shape = RoundedCornerShape(16.dp), color = DangerBg.copy(alpha = 0.6f)) {
                    Row(modifier = Modifier.padding(14.dp)) {
                        Icon(Icons.Outlined.Warning, contentDescription = null, tint = Danger)
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(text = stringResource(R.string.permanent_action), fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = Danger)
                            Text(
                                text = stringResource(R.string.delete_pub_detail, publication.name, publication.likes),
                                fontSize = 12.sp,
                                color = DarkText
                            )
                        }
                    }
                }

                // Opciones recomendadas antes de borrar
                Text(text = stringResource(R.string.recommended_options), fontSize = 11.sp, fontWeight = FontWeight.Bold, color = SecondaryText)
                Surface(shape = RoundedCornerShape(16.dp), color = Green.copy(alpha = 0.08f)) {
                    Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(text = stringResource(R.string.found_family, publication.name), fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = DarkText)
                        Text(
                            text = stringResource(R.string.celebrate_detail),
                            fontSize = 12.sp,
                            color = SecondaryText
                        )
                        Button(
                            onClick = viewModel::onMarkAdopted,
                            modifier = Modifier.fillMaxWidth(),
                            shape = CircleShape,
                            colors = ButtonDefaults.buttonColors(containerColor = Green)
                        ) {
                            Icon(Icons.Outlined.Celebration, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(text = stringResource(R.string.celebrate_adopted, publication.name))
                        }
                    }
                }
                Surface(shape = RoundedCornerShape(16.dp), color = LightBlueBg) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Outlined.PauseCircle, contentDescription = null, tint = AppPrimary)
                        Spacer(modifier = Modifier.width(10.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(text = stringResource(R.string.pause_publication), fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = DarkText)
                            Text(text = stringResource(R.string.pause_detail), fontSize = 11.sp, color = SecondaryText)
                        }
                        OutlinedButton(
                            onClick = viewModel::onPause,
                            enabled = publication.status != PublicationStatus.PAUSED,
                            shape = CircleShape
                        ) {
                            Text(text = if (publication.status == PublicationStatus.PAUSED) stringResource(R.string.paused) else stringResource(R.string.pause))
                        }
                    }
                }

                // Motivo de la baja
                Surface(shape = RoundedCornerShape(16.dp), color = Color.White) {
                    Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(text = stringResource(R.string.delete_reason), fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = DarkText)
                        Text(text = stringResource(R.string.delete_reason_detail), fontSize = 12.sp, color = SecondaryText)
                        DeleteReason.entries.forEach { reason ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(10.dp))
                                    .clickable { viewModel.onReasonSelected(reason) },
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(
                                    selected = state.reason == reason,
                                    onClick = { viewModel.onReasonSelected(reason) }
                                )
                                Column {
                                    Text(text = stringResource(reason.title), fontSize = 13.sp, color = DarkText)
                                    Text(text = stringResource(reason.description), fontSize = 11.sp, color = SecondaryText)
                                }
                            }
                        }
                    }
                }

                // Confirmación de seguridad
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(LightBlueBg)
                        .clickable { viewModel.onConfirmedChange(!state.isConfirmed) }
                        .padding(end = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = state.isConfirmed,
                        onCheckedChange = viewModel::onConfirmedChange,
                        colors = CheckboxDefaults.colors(checkedColor = Danger)
                    )
                    Text(
                        text = stringResource(R.string.delete_understand, publication.likes, publication.name),
                        fontSize = 12.sp,
                        color = DarkText
                    )
                }

                Button(
                    onClick = viewModel::deletePublication,
                    enabled = state.canDelete,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = CircleShape,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = DangerBg,
                        contentColor = Danger,
                        disabledContainerColor = DangerBg.copy(alpha = 0.5f),
                        disabledContentColor = Danger.copy(alpha = 0.5f)
                    )
                ) {
                    Icon(Icons.Outlined.DeleteForever, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (state.deleteResult is RequestResult.Loading) stringResource(R.string.deleting) else stringResource(R.string.delete_this_forever),
                        fontWeight = FontWeight.SemiBold
                    )
                }
                Button(
                    onClick = onNavigateBack,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = CircleShape,
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Black)
                ) {
                    Text(text = stringResource(R.string.keep_publication, publication.name))
                }

                Text(
                    text = stringResource(R.string.traceability),
                    fontSize = 11.sp,
                    color = SecondaryText
                )
            }
        }

        if (state.deleteResult is RequestResult.Success) {
            PublicationSuccessSheet(
                title = stringResource(R.string.publication_deleted),
                message = stringResource(R.string.publication_deleted_detail, state.publication?.name.orEmpty()),
                detail = stringResource(R.string.deletion_reason_detail, state.reason?.let { stringResource(it.title) }.orEmpty()),
                primaryText = stringResource(R.string.back_to_my_publications),
                onPrimaryClick = onNavigateToMyPublications
            )
        }
    }
}
