package co.edu.adopcionmascota.features.publication

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
    viewModel: DeletePublicationViewModel = viewModel(key = "delete_pub_$publicationId") {
        DeletePublicationViewModel(publicationId)
    }
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
                        Icon(Icons.AutoMirrored.Outlined.ArrowBack, contentDescription = "Volver", tint = DarkText)
                    }
                    Text(text = "Eliminar Publicación", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = DarkText)
                }

                if (publication == null) {
                    Text(text = "La publicación ya no existe.", color = SecondaryText)
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
                            model = publication.image,
                            contentDescription = publication.name,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(72.dp)
                                .clip(RoundedCornerShape(14.dp))
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text(text = "Caso #${publication.id}", fontSize = 11.sp, color = SecondaryText)
                            Text(text = "${publication.name} (${publication.breed})", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = DarkText)
                            Text(text = publication.location, fontSize = 12.sp, color = SecondaryText)
                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                AppTag(text = publication.status.label)
                                AppTag(text = "${publication.followers} seguidores")
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
                            Text(text = "Esta acción es permanente y no se puede deshacer", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = Danger)
                            Text(
                                text = "Se borrarán de forma irrevocable el expediente de ${publication.name}, su galería fotográfica, las ${publication.followers} personas que guardaron su perfil y todas las interacciones vecinales registradas.",
                                fontSize = 12.sp,
                                color = DarkText
                            )
                        }
                    }
                }

                // Opciones recomendadas antes de borrar
                Text(text = "OPCIONES RECOMENDADAS ANTES DE BORRAR", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = SecondaryText)
                Surface(shape = RoundedCornerShape(16.dp), color = Green.copy(alpha = 0.08f)) {
                    Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(text = "¿${publication.name} ya encontró una familia?", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = DarkText)
                        Text(
                            text = "¡Celebremos juntos! Marcar como adoptado envía una notificación de alegría a quienes apoyaron y archiva la historia de éxito sin perderla.",
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
                            Text(text = "Celebrar: Marcar a ${publication.name} como Adoptado")
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
                            Text(text = "Pausar publicación temporalmente", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = DarkText)
                            Text(text = "Ocultar del mapa mientras está en periodo de prueba", fontSize = 11.sp, color = SecondaryText)
                        }
                        OutlinedButton(
                            onClick = viewModel::onPause,
                            enabled = publication.status != MyPublicationStatus.PAUSED,
                            shape = CircleShape
                        ) {
                            Text(text = if (publication.status == MyPublicationStatus.PAUSED) "Pausada" else "Pausar")
                        }
                    }
                }

                // Motivo de la baja
                Surface(shape = RoundedCornerShape(16.dp), color = Color.White) {
                    Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(text = "Motivo de la baja del caso", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = DarkText)
                        Text(text = "Ayúdanos a mantener estadísticas transparentes de rescate en la comunidad.", fontSize = 12.sp, color = SecondaryText)
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
                                    Text(text = reason.title, fontSize = 13.sp, color = DarkText)
                                    Text(text = reason.description, fontSize = 11.sp, color = SecondaryText)
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
                        text = "Entiendo que al proceder perderé definitivamente el seguimiento de ${publication.followers} personas, las solicitudes pendientes y los comentarios de apoyo recibidos para ${publication.name}.",
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
                        text = if (state.deleteResult is RequestResult.Loading) "Eliminando..." else "Eliminar definitivamente esta publicación",
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
                    Text(text = "Conservar publicación de ${publication.name}")
                }

                Text(
                    text = "Compromiso de Trazabilidad y Ética: Patitas Conectadas audita las cancelaciones de rescate para asegurar el bienestar animal y la veracidad de los casos comunitarios.",
                    fontSize = 11.sp,
                    color = SecondaryText
                )
            }
        }

        if (state.deleteResult is RequestResult.Success) {
            val deleted = MyPublicationsStore.lastDeleted
            PublicationSuccessSheet(
                title = "¡Publicación Eliminada con Éxito!",
                message = "La publicación de ${deleted?.first?.name.orEmpty()} ha sido dada de baja del feed y del mapa comunitario.",
                detail = "Motivo: ${deleted?.second.orEmpty()}. Se notificó a los seguidores y el expediente quedó resguardado en el archivo histórico sin exposición pública.",
                primaryText = "Volver a \"Mis Publicaciones\"",
                onPrimaryClick = onNavigateToMyPublications
            )
        }
    }
}
