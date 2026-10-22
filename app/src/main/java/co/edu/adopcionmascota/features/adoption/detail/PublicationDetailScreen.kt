package co.edu.adopcionmascota.features.adoption.detail

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import co.edu.adopcionmascota.R
import co.edu.adopcionmascota.core.component.AppTag
import co.edu.adopcionmascota.core.component.AppTopBar
import co.edu.adopcionmascota.core.component.ConfirmAlertDialog
import co.edu.adopcionmascota.core.util.RequestResult
import co.edu.adopcionmascota.domain.Publication
import co.edu.adopcionmascota.features.comment.Comment
import co.edu.adopcionmascota.features.comment.CommentCard
import coil3.compose.AsyncImage

@Composable
fun PublicationDetailScreen(
    publicationId: String,
    onNavigateBack: () -> Unit = {},          // "Button - Volver" → feed
    onNavigateToComment: () -> Unit = {},     // Abre "Crear comentario"
    onNotificationClick: () -> Unit = {},     // Campana de la barra superior
    onProfileClick: () -> Unit = {},          // Avatar de la barra superior
    viewModel: PublicationDetailViewModel = viewModel(key = "detail_$publicationId") {
        PublicationDetailViewModel(publicationId)
    }
) {
    val state by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current

    LaunchedEffect(state.infoMessage) {
        state.infoMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.resetInfoMessage()
        }
    }

    LaunchedEffect(state.requestResult) {
        when (val result = state.requestResult) {
            is RequestResult.Success -> {
                snackbarHostState.showSnackbar(result.message)
                viewModel.resetRequestResult()
            }

            is RequestResult.Failure -> {
                snackbarHostState.showSnackbar(result.errorMessage)
                viewModel.resetRequestResult()
            }

            is RequestResult.Loading, null -> Unit
        }
    }

    // Abre WhatsApp con un mensaje para el publicador (sin permisos ni dependencias)
    val openWhatsApp: (String, String) -> Unit = { phone, petName ->
        val message = Uri.encode("Hola, vi la publicación de $petName en Patitas Conectadas")
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://wa.me/$phone?text=$message"))
        try {
            context.startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            viewModel.showInfo("No hay una app disponible para abrir WhatsApp")
        }
    }

    Scaffold(
        modifier = Modifier.statusBarsPadding(),
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onNavigateBack) {
                    Icon(Icons.AutoMirrored.Outlined.ArrowBack, contentDescription = "Volver")
                }
                Box(modifier = Modifier.weight(1f)) {
                    AppTopBar(
                        title = "Patitas Conectadas",
                        location = "Armenia, Quindío",
                        onNotificationClick = onNotificationClick,
                        onProfileClick = onProfileClick
                    )
                }
            }
        },
        bottomBar = {
            if (state.publication != null) {
                PublicationBottomBar(
                    primaryText = state.primaryActionText,
                    temporaryHomeSent = DetailRequest.TEMPORARY_HOME in state.sentRequests,
                    primarySent = DetailRequest.ADOPTION in state.sentRequests,
                    isSending = state.requestResult is RequestResult.Loading,
                    onTemporaryHomeClick = { viewModel.onRequestClick(DetailRequest.TEMPORARY_HOME) },
                    onPrimaryClick = { viewModel.onRequestClick(DetailRequest.ADOPTION) }
                )
            }
        }
    ) { padding ->
        val publication = state.publication
        val info = state.info

        if (publication == null || info == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Text("Esta publicación ya no está disponible")
            }
            return@Scaffold
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(12.dp)
        ) {

            item {
                Card(
                    shape = RoundedCornerShape(20.dp)
                ) {

                    Box {

                        AsyncImage(
                            model = publication.image,
                            contentDescription = publication.name,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(260.dp),
                            contentScale = ContentScale.Crop
                        )

                        // "Me importa": suma o quita el me gusta
                        Card(
                            modifier = Modifier
                                .padding(12.dp)
                                .align(Alignment.TopEnd)
                                .clickable(onClick = viewModel::onLikeClick)
                        ) {

                            Row(
                                modifier = Modifier.padding(
                                    horizontal = 8.dp,
                                    vertical = 4.dp
                                )
                            ) {

                                Icon(
                                    if (state.isLiked) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                    "Me importa",
                                    tint = Color.Blue
                                )

                                Spacer(
                                    Modifier.width(4.dp)
                                )

                                Text("${state.likes}")
                            }
                        }
                    }
                }
            }
            item {
                PetInfoCard(
                    publication = publication,
                    info = info,
                    statusText = state.statusText
                )
            }

            item {
                HealthCard(tags = info.healthTags)
            }

            item {
                HistoryCard(name = publication.name, history = info.history)
            }

            item {
                PublisherCard(
                    name = info.publisherName,
                    level = info.publisherLevel,
                    onWhatsAppClick = { openWhatsApp(info.publisherPhone, publication.name) }
                )
            }

            item {
                MapCard()
            }

            item {
                CommentsCard(
                    comments = state.comments,
                    onCommentClick = onNavigateToComment,
                    onThanksClick = viewModel::onThanksClick
                )
            }
        }
    }

    // Confirmación antes de enviar la solicitud
    state.pendingRequest?.let { request ->
        val name = state.publication?.name.orEmpty()
        ConfirmAlertDialog(
            title = if (request == DetailRequest.TEMPORARY_HOME) "¿Ofrecer hogar de paso?" else "¿${state.primaryActionText}?",
            message = if (request == DetailRequest.TEMPORARY_HOME)
                "El publicador recibirá tu ofrecimiento para acoger temporalmente a $name."
            else
                "El publicador recibirá tu solicitud y se pondrá en contacto contigo sobre $name.",
            onConfirm = viewModel::onConfirmRequest,
            onDismiss = viewModel::onDismissRequest,
            confirmText = "Enviar"
        )
    }
}

@Composable
fun PetInfoCard(
    publication: Publication,
    info: PetDetailInfo,
    statusText: String
) {

    ElevatedCard {

        Column(
            modifier = Modifier.padding(16.dp)
        ) {

            Row {

                Text(
                    text = publication.name,
                    style = MaterialTheme.typography.headlineSmall
                )

                Spacer(
                    Modifier.width(8.dp)
                )

                Text(if (publication.gender == "Hembra") "♀" else "♂")
            }

            Text(
                "${info.breed} · ${publication.age} · ${info.size}"
            )

            Spacer(
                Modifier.height(12.dp)
            )

            // Estado de la publicación (etiqueta, no es un botón)
            AppTag(statusText)
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun HealthCard(tags: List<String>) {

    ElevatedCard {

        Column(
            modifier = Modifier.padding(16.dp)
        ) {

            Text(
                "Salud y Cuidados",
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(
                Modifier.height(12.dp)
            )

            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                tags.forEach { AppTag(it) }
            }
        }
    }
}

@Composable
fun HistoryCard(name: String, history: String) {

    ElevatedCard {

        Column(
            modifier = Modifier.padding(16.dp)
        ) {

            Text(
                "Historia de $name",
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(
                Modifier.height(8.dp)
            )

            Text(history)
        }
    }
}

@Composable
fun PublisherCard(
    name: String,
    level: String,
    onWhatsAppClick: () -> Unit
) {

    ElevatedCard {

        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Icon(
                Icons.Default.Person,
                null
            )

            Spacer(
                Modifier.width(12.dp)
            )

            Column(
                modifier = Modifier.weight(1f)
            ) {

                Text(name)

                Text(level)
            }

            Button(
                onClick = onWhatsAppClick
            ) {
                Text("WhatsApp")
            }
        }
    }
}
@Composable
fun MapCard() {

    ElevatedCard(
        modifier = Modifier.fillMaxWidth()
    ) {

        Column(
            modifier = Modifier.padding(16.dp)
        ) {

            Text(
                text = "Punto de Referencia"
            )

            Spacer(
                modifier = Modifier.height(12.dp)
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
            ) {

                Image(
                    painter = painterResource(
                        id = R.drawable.icono_mapa
                    ),
                    contentDescription = "Mapa de referencia",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )

                // Aquí podrías colocar un marcador
                Text(
                    text = "📍",
                    modifier = Modifier
                        .align(Alignment.Center)
                )
            }
        }
    }
}
@Composable
fun CommentsCard(
    comments: List<Comment>,
    onCommentClick: () -> Unit,
    onThanksClick: (String) -> Unit
) {

    ElevatedCard {

        Column(
            modifier = Modifier.padding(16.dp)
        ) {

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    "Comentarios y Avances",
                    modifier = Modifier.weight(1f)
                )
                AppTag("${comments.size} aportes")
            }

            Spacer(
                Modifier.height(12.dp)
            )

            // Se muestran los aportes más recientes
            comments.take(3).forEach { comment ->
                CommentCard(
                    comment = comment,
                    onThanksClick = { onThanksClick(comment.id) },
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }

            // El campo abre la pantalla "Crear comentario" (como en el prototipo)
            Box {
                OutlinedTextField(
                    value = "",
                    onValueChange = {},
                    modifier = Modifier.fillMaxWidth(),
                    readOnly = true,
                    placeholder = {
                        Text("Aportar información...")
                    }
                )
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .clickable(onClick = onCommentClick)
                )
            }

            Spacer(
                Modifier.height(8.dp)
            )

            Button(
                onClick = onCommentClick,
                modifier = Modifier.align(
                    Alignment.End
                )
            ) {
                Text("Comentar")
            }
        }
    }
}
@Composable
fun PublicationBottomBar(
    primaryText: String,
    temporaryHomeSent: Boolean,
    primarySent: Boolean,
    isSending: Boolean,
    onTemporaryHomeClick: () -> Unit,
    onPrimaryClick: () -> Unit
) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {

        FilledTonalButton(
            onClick = onTemporaryHomeClick,
            enabled = !isSending,
            modifier = Modifier.weight(1f)
        ) {
            Text(if (temporaryHomeSent) "Hogar de Paso ✓" else "Hogar de Paso")
        }

        Button(
            onClick = onPrimaryClick,
            enabled = !isSending,
            modifier = Modifier.weight(1f)
        ) {
            Text(if (primarySent) "Solicitud enviada ✓" else primaryText)
        }
    }
}
