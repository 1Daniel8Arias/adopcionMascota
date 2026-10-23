package co.edu.adopcionmascota.features.notification

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.material.icons.outlined.ChatBubbleOutline
import androidx.compose.material.icons.outlined.DoneAll
import androidx.compose.material.icons.outlined.EmojiEvents
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.NotificationsNone
import androidx.compose.material.icons.outlined.ReportProblem
import androidx.compose.material.icons.outlined.Verified
import androidx.compose.material.icons.outlined.WorkspacePremium
import androidx.compose.material3.Icon
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import co.edu.adopcionmascota.core.component.AppBottomBar
import co.edu.adopcionmascota.core.component.AppDestination
import co.edu.adopcionmascota.core.theme.AppPrimary
import co.edu.adopcionmascota.core.theme.AppRed
import co.edu.adopcionmascota.core.theme.Background
import co.edu.adopcionmascota.core.theme.DarkText
import co.edu.adopcionmascota.core.theme.SecondaryText

private val SoftBlue = Color(0xFFEDF4FF)
private val UnreadBg = Color(0xFFF3F7FF)

// Pantalla "notificacion" (Centro de Notificaciones) y su filtro "notificacion no leidos"
@Composable
fun NotificationsScreen(
    onNotificationClick: (String) -> Unit,          // Abre el detalle de la notificación
    onNavigationClick: (AppDestination) -> Unit,    // Barra inferior
    viewModel: NotificationsViewModel = viewModel()
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
                currentDestination = AppDestination.ALERTS,
                onDestinationSelected = onNavigationClick
            )
        }
    ) { paddingValues ->

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                NotificationsTopBar(
                    userName = state.userName,
                    userLocation = state.userLocation,
                    unreadCount = state.unreadCount,
                    // La campana lleva directo al filtro de no leídas
                    onBellClick = { viewModel.onFilterChange(NotificationFilter.UNREAD) }
                )
            }

            item {
                NotificationsHeader(
                    unreadCount = state.unreadCount,
                    onMarkAllClick = viewModel::onMarkAllAsRead
                )
            }

            item {
                FilterRow(
                    state = state,
                    onFilterChange = viewModel::onFilterChange
                )
            }

            if (state.visibleNotifications.isEmpty()) {
                item {
                    EmptyNotifications(filter = state.filter)
                }
            }

            items(state.visibleNotifications, key = { it.id }) { notification ->
                NotificationCard(
                    notification = notification,
                    onClick = {
                        viewModel.onNotificationOpened(notification.id)
                        onNotificationClick(notification.id)
                    }
                )
            }

            item { Spacer(modifier = Modifier.size(8.dp)) }
        }
    }
}

@Composable
private fun NotificationsTopBar(
    userName: String,
    userLocation: String,
    unreadCount: Int,
    onBellClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(Color(0xFFDFE0FF)),
            contentAlignment = Alignment.Center
        ) {
            Text(text = "CM", fontWeight = FontWeight.SemiBold, color = Color(0xFF000865))
        }
        Spacer(modifier = Modifier.width(10.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(text = userName, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = DarkText)
            Text(text = userLocation, fontSize = 12.sp, color = SecondaryText)
        }
        Surface(
            onClick = onBellClick,
            shape = CircleShape,
            color = SoftBlue
        ) {
            Box(modifier = Modifier.size(40.dp), contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = Icons.Outlined.Notifications,
                    contentDescription = "Ver no leídas",
                    tint = DarkText
                )
                if (unreadCount > 0) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(4.dp)
                            .size(16.dp)
                            .clip(CircleShape)
                            .background(AppRed),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "$unreadCount", fontSize = 9.sp, color = Color.White)
                    }
                }
            }
        }
    }
}

@Composable
private fun NotificationsHeader(
    unreadCount: Int,
    onMarkAllClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "Notificaciones",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = DarkText
            )
            Text(
                text = if (unreadCount == 0) "Estás al día" else "$unreadCount pendientes",
                fontSize = 13.sp,
                color = AppPrimary
            )
        }
        TextButton(onClick = onMarkAllClick) {
            Icon(
                imageVector = Icons.Outlined.DoneAll,
                contentDescription = null,
                tint = AppPrimary,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = "Marcar todas\nleídas",
                fontSize = 12.sp,
                lineHeight = 14.sp,
                color = AppPrimary
            )
        }
    }
}

@Composable
private fun FilterRow(
    state: NotificationsUiState,
    onFilterChange: (NotificationFilter) -> Unit
) {
    Row(
        modifier = Modifier.horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        NotificationFilter.entries.forEach { filter ->
            val isSelected = filter == state.filter
            Surface(
                onClick = { onFilterChange(filter) },
                shape = CircleShape,
                color = if (isSelected) Color.Black else SoftBlue
            ) {
                Text(
                    text = state.labelFor(filter),
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    color = if (isSelected) Color.White else DarkText
                )
            }
        }
    }
}

@Composable
private fun EmptyNotifications(filter: NotificationFilter) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 40.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Outlined.NotificationsNone,
            contentDescription = null,
            tint = SecondaryText,
            modifier = Modifier.size(40.dp)
        )
        Spacer(modifier = Modifier.size(8.dp))
        Text(
            text = if (filter == NotificationFilter.UNREAD) "No tienes notificaciones sin leer"
            else "No hay notificaciones en esta categoría",
            fontSize = 14.sp,
            color = SecondaryText
        )
    }
}

@Composable
private fun NotificationCard(
    notification: AppNotification,
    onClick: () -> Unit
) {
    val accent = notification.type.accentColor()

    Surface(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = if (notification.isRead) Color.White else UnreadBg,
        shadowElevation = if (notification.isRead) 0.dp else 1.dp
    ) {
        Row(modifier = Modifier.padding(14.dp)) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(accent.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = notification.type.icon(),
                    contentDescription = null,
                    tint = accent,
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = notification.category,
                        modifier = Modifier.weight(1f),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = accent
                    )
                    Text(text = notification.time, fontSize = 11.sp, color = SecondaryText)
                    if (!notification.isRead) {
                        Spacer(modifier = Modifier.width(6.dp))
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(AppPrimary)
                        )
                    }
                }
                Text(
                    text = notification.title,
                    fontSize = 15.sp,
                    fontWeight = if (notification.isRead) FontWeight.Medium else FontWeight.Bold,
                    color = DarkText
                )
                Text(
                    text = notification.message,
                    fontSize = 13.sp,
                    color = SecondaryText
                )
                Row(
                    modifier = Modifier.padding(top = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    notification.referenceId?.let { reference ->
                        Text(
                            text = reference,
                            modifier = Modifier.weight(1f),
                            fontSize = 11.sp,
                            color = Color(0xFF76777C)
                        )
                    } ?: Spacer(modifier = Modifier.weight(1f))
                    // La acción rápida abre el mismo detalle que la tarjeta
                    notification.actionLabel?.let { label ->
                        Surface(
                            onClick = onClick,
                            shape = CircleShape,
                            color = accent.copy(alpha = 0.12f)
                        ) {
                            Text(
                                text = label,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = accent
                            )
                        }
                    }
                }
            }
        }
    }
}

fun NotificationType.icon(): ImageVector = when (this) {
    NotificationType.PUBLICATION_APPROVED -> Icons.Outlined.Verified
    NotificationType.LEVEL_UP -> Icons.Outlined.EmojiEvents
    NotificationType.PUBLICATION_PAUSED -> Icons.Outlined.ReportProblem
    NotificationType.NEW_COMMENT -> Icons.Outlined.ChatBubbleOutline
    NotificationType.BADGE_EARNED -> Icons.Outlined.WorkspacePremium
}

fun NotificationType.accentColor(): Color = when (this) {
    NotificationType.PUBLICATION_APPROVED -> Color(0xFF067647)
    NotificationType.LEVEL_UP -> AppPrimary
    NotificationType.PUBLICATION_PAUSED -> Color(0xFFBA1A1A)
    NotificationType.NEW_COMMENT -> Color(0xFF3A49D8)
    NotificationType.BADGE_EARNED -> Color(0xFFB54708)
}
