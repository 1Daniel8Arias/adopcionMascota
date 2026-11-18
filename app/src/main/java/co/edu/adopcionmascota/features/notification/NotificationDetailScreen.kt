package co.edu.adopcionmascota.features.notification

import androidx.compose.ui.res.stringResource
import co.edu.adopcionmascota.R
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import co.edu.adopcionmascota.domain.model.NotificationType
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
    onNavigateBack: () -> Unit,                     // Flecha stringResource(R.string.back) → Centro de Notificaciones
    onViewPublication: (String) -> Unit,            // stringResource(R.string.view_case)
    onNavigateToFeed: () -> Unit,                   // "Ver en Feed Público"
    onNavigateToProfile: () -> Unit,                // Vitrina de insignias / perfil completo
    onViewRequests: () -> Unit = {},                // Solicitudes de adopción recibidas
    viewModel: NotificationDetailViewModel = hiltViewModel()
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
        context.startActivity(Intent.createChooser(intent, context.getString(R.string.share_chooser)))
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
                // Notificaciones generadas por la app con datos reales de Room
                NotificationType.PUBLICATION_APPROVED,
                NotificationType.NEW_COMMENT,
                NotificationType.ADOPTION_REQUEST,
                NotificationType.REQUEST_ANSWERED -> CaseNotificationContent(
                    state = state,
                    onViewCase = onViewPublication,
                    onViewFeed = onNavigateToFeed,
                    onViewRequests = onViewRequests,
                    onReplyChange = viewModel::onReplyChange,
                    onSendReply = viewModel::sendReply
                )

                NotificationType.LEVEL_UP -> LevelUpContent(
                    userName = state.userName,
                    onViewBadges = onNavigateToProfile,
                    onShare = {
                        share(context.getString(R.string.share_level_up))
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

                NotificationType.BADGE_EARNED -> BadgeEarnedContent(
                    onViewProfile = onNavigateToProfile,
                    onBackToNotifications = onNavigateBack
                )

                null -> Text(
                    text = stringResource(R.string.notification_unavailable),
                    color = SecondaryText
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
        }

        if (state.showGuidelinesDialog) {
            ConfirmAlertDialog(
                title = stringResource(R.string.guidelines_title),
                message = stringResource(R.string.guidelines_message),
                onConfirm = viewModel::onDismissGuidelines,
                onDismiss = viewModel::onDismissGuidelines,
                confirmText = stringResource(R.string.understood),
                dismissText = stringResource(R.string.close)
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
                contentDescription = stringResource(R.string.back),
                tint = DarkText
            )
        }
        Text(
            text = stringResource(R.string.notification_detail),
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
