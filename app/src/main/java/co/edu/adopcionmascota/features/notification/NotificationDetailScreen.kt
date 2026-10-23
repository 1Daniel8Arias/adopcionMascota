package co.edu.adopcionmascota.features.notification

import android.content.Intent
import androidx.compose.foundation.background
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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import co.edu.adopcionmascota.core.component.ConfirmAlertDialog
import co.edu.adopcionmascota.core.theme.Background
import co.edu.adopcionmascota.core.theme.DarkText
import co.edu.adopcionmascota.core.theme.SecondaryText
import co.edu.adopcionmascota.core.util.RequestResult

// Detalle de una notificación: cada tipo tiene su propio contenido, como en los 5 frames del Figma
@Composable
fun NotificationDetailScreen(
    notificationId: String,
    onNavigateBack: () -> Unit,                     // Flecha "Volver" → Centro de Notificaciones
    onViewPublication: (String) -> Unit,            // "Ver caso"
    onNavigateToFeed: () -> Unit,                   // "Ver en Feed Público"
    onNavigateToProfile: () -> Unit,                // Vitrina de insignias / perfil completo
    viewModel: NotificationDetailViewModel = viewModel(key = "notification_$notificationId") {
        NotificationDetailViewModel(notificationId)
    }
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val state by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    // Compartir con las apps del teléfono (sin dependencias externas)
    val share: (String) -> Unit = { text ->
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, text)
        }
        context.startActivity(Intent.createChooser(intent, "Compartir"))
    }

    LaunchedEffect(state.infoMessage) {
        state.infoMessage?.let { message ->
            snackbarHostState.showSnackbar(message)
            viewModel.resetInfoMessage()
        }
    }

    LaunchedEffect(state.resubmitResult) {
        when (val result = state.resubmitResult) {
            is RequestResult.Success -> {
                snackbarHostState.showSnackbar(result.message)
                viewModel.resetResubmitResult()
            }

            is RequestResult.Failure -> {
                snackbarHostState.showSnackbar(result.errorMessage)
                viewModel.resetResubmitResult()
            }

            is RequestResult.Loading, null -> Unit
        }
    }

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
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            DetailTopBar(onBackClick = onNavigateBack)

            when (state.notification?.type) {
                NotificationType.PUBLICATION_APPROVED -> ApprovedPublicationContent(
                    onManageRequests = {
                        viewModel.showInfo("Tienes 2 consultas de adoptantes. La gestión de solicitudes estará disponible próximamente")
                    },
                    onViewInFeed = onNavigateToFeed,
                    onShare = {
                        share("¡Max (Golden Mestizo) busca hogar en Patitas Conectadas! Su ficha ya está verificada por moderación ética.")
                    }
                )

                NotificationType.LEVEL_UP -> LevelUpContent(
                    onViewBadges = onNavigateToProfile,
                    onShare = {
                        share("¡Subí a Nivel Guardián en Patitas Conectadas con 10 publicaciones verificadas! 🐾")
                    }
                )

                NotificationType.PUBLICATION_PAUSED -> PausedPublicationContent(
                    state = state,
                    onExamineClick = viewModel::onExamineClick,
                    onLotNumberChange = viewModel::onLotNumberChange,
                    onDayChange = viewModel::onDayChange,
                    onMonthChange = viewModel::onMonthChange,
                    onYearChange = viewModel::onYearChange,
                    onRabiesLotChange = viewModel::onRabiesLotChange,
                    onEthicsConfirmedChange = viewModel::onEthicsConfirmedChange,
                    onResubmit = viewModel::resubmitForReview,
                    onGuidelinesClick = viewModel::onGuidelinesClick
                )

                NotificationType.NEW_COMMENT -> NewCommentContent(
                    state = state,
                    onViewCase = { onViewPublication("1") },
                    onAcceptDonation = viewModel::onAcceptDonation,
                    onWhatsAppClick = {
                        viewModel.showInfo("El chat seguro por WhatsApp estará disponible próximamente")
                    },
                    onToggleLike = viewModel::onToggleLike,
                    onToggleAllReplies = viewModel::onToggleAllReplies,
                    onReplyChange = viewModel::onReplyChange,
                    onSuggestionClick = viewModel::onSuggestionClick,
                    onSendReply = viewModel::sendReply
                )

                NotificationType.BADGE_EARNED -> BadgeEarnedContent(
                    onViewProfile = onNavigateToProfile,
                    onBackToNotifications = onNavigateBack
                )

                null -> Text(
                    text = "Esta notificación ya no está disponible.",
                    color = SecondaryText
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
        }

        if (state.showGuidelinesDialog) {
            ConfirmAlertDialog(
                title = "Pautas del comité ético",
                message = "Toma la foto del carnet desde arriba, con buena luz y sin flash directo. Deben verse completos los sellos, la firma del veterinario y las fechas de vigencia.",
                onConfirm = viewModel::onDismissGuidelines,
                onDismiss = viewModel::onDismissGuidelines,
                confirmText = "Entendido",
                dismissText = "Cerrar"
            )
        }
    }
}

@Composable
private fun DetailTopBar(onBackClick: () -> Unit) {
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
            text = "Detalle de Notificación",
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
            color = DarkText
        )
    }
}

// ---------------------------------------------------------
// Componentes compartidos por los 5 detalles
// ---------------------------------------------------------

@Composable
internal fun DetailCard(
    modifier: Modifier = Modifier,
    color: Color = Color.White,
    content: @Composable () -> Unit
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        color = color
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
internal fun IconBadge(icon: ImageVector, tint: Color, size: Int = 40) {
    Box(
        modifier = Modifier
            .size(size.dp)
            .clip(CircleShape)
            .background(tint.copy(alpha = 0.12f)),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = tint,
            modifier = Modifier.size((size / 2).dp)
        )
    }
}

@Composable
internal fun PrimaryActionButton(
    text: String,
    onClick: () -> Unit,
    icon: ImageVector? = null,
    enabled: Boolean = true,
    container: Color = Color.Black,
    content: Color = Color.White
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp),
        shape = CircleShape,
        colors = ButtonDefaults.buttonColors(
            containerColor = container,
            contentColor = content,
            disabledContainerColor = Color(0xFF9CA3AF),
            disabledContentColor = Color.White
        )
    ) {
        if (icon != null) {
            Icon(imageVector = icon, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(8.dp))
        }
        Text(text = text, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
internal fun SectionLabel(text: String) {
    Text(
        text = text,
        fontSize = 11.sp,
        fontWeight = FontWeight.Bold,
        color = SecondaryText
    )
}
