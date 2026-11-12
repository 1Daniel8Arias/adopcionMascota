package co.edu.adopcionmascota.features.notification

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Send
import androidx.compose.material.icons.outlined.Explore
import androidx.compose.material.icons.outlined.Groups
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import co.edu.adopcionmascota.R
import co.edu.adopcionmascota.core.component.AppTag
import co.edu.adopcionmascota.core.theme.AppPrimary
import co.edu.adopcionmascota.core.util.rememberImageModel
import co.edu.adopcionmascota.core.util.relativeTime
import co.edu.adopcionmascota.domain.model.NotificationType
import coil3.compose.AsyncImage

private val CaseDarkText = Color(0xFF101828)
private val CaseSecondaryText = Color(0xFF667085)

// Detalle de las notificaciones que se generan con datos reales:
// publicación aprobada, nuevo comentario, solicitud recibida y solicitud respondida
@Composable
fun CaseNotificationContent(
    state: NotificationDetailUiState,
    onViewCase: (String) -> Unit,
    onViewFeed: () -> Unit,
    onViewRequests: () -> Unit,
    onReplyChange: (String) -> Unit,
    onSendReply: () -> Unit
) {
    val notification = state.notification ?: return
    val publication = state.publication

    DetailCard(color = AppPrimary.copy(alpha = 0.08f)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            AppTag(text = notification.category, backgroundColor = AppPrimary.copy(alpha = 0.15f), textColor = AppPrimary)
            Spacer(modifier = Modifier.weight(1f))
            Text(text = relativeTime(notification.createdAt), fontSize = 11.sp, color = CaseSecondaryText)
        }
        Text(text = notification.title, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = CaseDarkText)
        Text(text = notification.message, fontSize = 13.sp, color = CaseSecondaryText)
    }

    // Publicación relacionada
    if (publication != null) {
        DetailCard {
            AsyncImage(
                model = rememberImageModel(publication.image),
                contentDescription = publication.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp)
                    .clip(RoundedCornerShape(16.dp))
            )
            Text(text = publication.name, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = CaseDarkText)
            Text(
                text = listOf(publication.breed, publication.age, publication.location)
                    .filter { it.isNotBlank() }.joinToString(" · "),
                fontSize = 12.sp,
                color = CaseSecondaryText
            )
            AppTag(text = stringResource(publication.status.label))
        }
    }

    when (notification.type) {
        NotificationType.PUBLICATION_APPROVED -> {
            PrimaryActionButton(text = stringResource(R.string.view_in_feed), onClick = onViewFeed, icon = Icons.Outlined.Explore)
        }

        NotificationType.ADOPTION_REQUEST -> {
            PrimaryActionButton(text = stringResource(R.string.notif_action_view_requests), onClick = onViewRequests, icon = Icons.Outlined.Groups)
        }

        NotificationType.NEW_COMMENT -> {
            // Últimos aportes del caso y respuesta del autor (se guarda como comentario en Room)
            DetailCard {
                SectionLabel(text = stringResource(R.string.case_replies))
                state.comments.take(5).forEach { comment ->
                    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                        Row {
                            Text(text = comment.author, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = CaseDarkText, modifier = Modifier.weight(1f))
                            Text(text = relativeTime(comment.createdAt), fontSize = 11.sp, color = CaseSecondaryText)
                        }
                        Text(text = comment.text, fontSize = 12.sp, color = CaseSecondaryText)
                    }
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    OutlinedTextField(
                        value = state.replyText,
                        onValueChange = onReplyChange,
                        modifier = Modifier.weight(1f),
                        placeholder = { Text(stringResource(R.string.reply_public)) }
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    IconButton(onClick = onSendReply) {
                        Icon(Icons.AutoMirrored.Outlined.Send, contentDescription = stringResource(R.string.send_reply), tint = AppPrimary)
                    }
                }
            }
        }

        else -> Unit
    }

    if (publication != null) {
        PrimaryActionButton(text = stringResource(R.string.view_case), onClick = { onViewCase(publication.id) }, icon = Icons.Outlined.Visibility)
    }
}
