package co.edu.adopcionmascota.features.notification

import androidx.compose.ui.res.stringResource
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Send
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material.icons.outlined.ArrowUpward
import androidx.compose.material.icons.outlined.CameraAlt
import androidx.compose.material.icons.outlined.ChatBubbleOutline
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.EmojiEvents
import androidx.compose.material.icons.outlined.Explore
import androidx.compose.material.icons.outlined.Groups
import androidx.compose.material.icons.automirrored.outlined.HelpOutline
import androidx.compose.material.icons.outlined.LocalHospital
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.ReportProblem
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material.icons.outlined.Shield
import androidx.compose.material.icons.outlined.ThumbUp
import androidx.compose.material.icons.outlined.Verified
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.WorkspacePremium
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import co.edu.adopcionmascota.R
import co.edu.adopcionmascota.core.component.AppTag
import co.edu.adopcionmascota.core.theme.AppPrimary
import co.edu.adopcionmascota.core.theme.AppRed
import co.edu.adopcionmascota.core.theme.DarkText
import co.edu.adopcionmascota.core.theme.LightBlueBg
import co.edu.adopcionmascota.core.theme.SecondaryText
import coil3.compose.AsyncImage

private val Green = Color(0xFF067647)
private val Danger = Color(0xFFBA1A1A)
private val Gold = Color(0xFFB54708)
private val SoftBlue = Color(0xFFEDF4FF)

// ---------------------------------------------------------
// Notificación 1: Publicación Aprobada
// ---------------------------------------------------------

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ApprovedPublicationContent(
    onManageRequests: () -> Unit,
    onViewInFeed: () -> Unit,
    onShare: () -> Unit
) {
    DetailCard(color = Green.copy(alpha = 0.08f)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            AppTag(text = stringResource(R.string.publication_approved_tag), backgroundColor = Green.copy(alpha = 0.15f), textColor = Green)
            Spacer(modifier = Modifier.weight(1f))
            Text(text = "ID: max_1", fontSize = 11.sp, color = SecondaryText)
        }
        Text(
            text = stringResource(R.string.publication_active),
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = DarkText
        )
        Text(
            text = stringResource(R.string.publication_active_detail),
            fontSize = 13.sp,
            color = SecondaryText
        )
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Outlined.CheckCircle, contentDescription = null, tint = Green, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text(text = stringResource(R.string.indexed_radar), fontSize = 12.sp, color = DarkText, modifier = Modifier.weight(1f))
            AppTag(text = stringResource(R.string.live), backgroundColor = Green.copy(alpha = 0.15f), textColor = Green)
        }
    }

    DetailCard {
        AsyncImage(
            model = R.drawable.imagen_max,
            contentDescription = "Max",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
                .clip(RoundedCornerShape(16.dp))
        )
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = stringResource(R.string.max_age), fontSize = 18.sp, fontWeight = FontWeight.Bold, color = DarkText)
                Text(text = stringResource(R.string.max_breed), fontSize = 12.sp, color = SecondaryText)
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(text = "84", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = AppPrimary)
                Text(text = "seguidores", fontSize = 11.sp, color = SecondaryText)
            }
        }
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            listOf(stringResource(R.string.vaccines_current), stringResource(R.string.sterilized), stringResource(R.string.tag_children), stringResource(R.string.responsible_home)).forEach {
                AppTag(text = it)
            }
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Outlined.Shield, contentDescription = null, tint = AppPrimary, modifier = Modifier.size(14.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = stringResource(R.string.protocol_done),
                fontSize = 11.sp,
                color = SecondaryText
            )
        }
    }

    DetailCard {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(text = stringResource(R.string.realtime_performance), fontSize = 15.sp, fontWeight = FontWeight.SemiBold, color = DarkText, modifier = Modifier.weight(1f))
            AppTag(text = stringResource(R.string.latest))
        }
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            StatBox(
                modifier = Modifier.weight(1f),
                icon = Icons.Outlined.Visibility,
                label = stringResource(R.string.initial_views),
                value = "142",
                detail = stringResource(R.string.first_hour)
            )
            StatBox(
                modifier = Modifier.weight(1f),
                icon = Icons.Outlined.LocationOn,
                label = stringResource(R.string.priority_zone),
                value = "Usaquén & Chapinero",
                detail = stringResource(R.string.radius_6)
            )
        }
        Surface(shape = RoundedCornerShape(12.dp), color = SoftBlue) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Outlined.Groups, contentDescription = null, tint = AppPrimary)
                Spacer(modifier = Modifier.width(10.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = stringResource(R.string.compatibility), fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = DarkText)
                    Text(text = stringResource(R.string.families_waiting), fontSize = 12.sp, color = SecondaryText)
                }
                AppTag(text = stringResource(R.string.high_demand), backgroundColor = AppRed.copy(alpha = 0.12f), textColor = AppRed)
            }
        }
    }

    PrimaryActionButton(
        text = stringResource(R.string.manage_inquiries),
        onClick = onManageRequests,
        icon = Icons.Outlined.ChatBubbleOutline
    )
    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        Box(modifier = Modifier.weight(1f)) {
            PrimaryActionButton(
                text = stringResource(R.string.view_in_feed),
                onClick = onViewInFeed,
                icon = Icons.Outlined.Explore,
                container = AppPrimary
            )
        }
        Box(modifier = Modifier.weight(1f)) {
            PrimaryActionButton(
                text = stringResource(R.string.share),
                onClick = onShare,
                icon = Icons.Outlined.Share,
                container = LightBlueBg,
                content = DarkText
            )
        }
    }

    DetailCard(color = SoftBlue) {
        Text(text = stringResource(R.string.ethics_commitment), fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = DarkText)
        Text(
            text = stringResource(R.string.ethics_commitment_detail),
            fontSize = 12.sp,
            color = SecondaryText
        )
    }
}

@Composable
private fun StatBox(
    modifier: Modifier,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String,
    detail: String
) {
    Surface(modifier = modifier, shape = RoundedCornerShape(12.dp), color = SoftBlue) {
        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Icon(icon, contentDescription = null, tint = AppPrimary, modifier = Modifier.size(18.dp))
            Text(text = label, fontSize = 11.sp, color = SecondaryText)
            Text(text = value, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = DarkText)
            Text(text = detail, fontSize = 11.sp, color = Green)
        }
    }
}

// ---------------------------------------------------------
// Notificación 2: Ascenso a Nivel Guardián
// ---------------------------------------------------------

@Composable
fun LevelUpContent(
    onViewBadges: () -> Unit,
    onShare: () -> Unit
) {
    DetailCard(color = AppPrimary.copy(alpha = 0.08f)) {
        AppTag(text = stringResource(R.string.community_impact_tag), backgroundColor = AppPrimary.copy(alpha = 0.15f), textColor = AppPrimary)
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconBadge(icon = Icons.Outlined.EmojiEvents, tint = AppPrimary, size = 56)
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(text = stringResource(R.string.level_up_title), fontSize = 20.sp, fontWeight = FontWeight.Bold, color = DarkText)
                Text(text = stringResource(R.string.level_up_detail), fontSize = 12.sp, color = AppPrimary)
            }
        }
        Text(
            text = stringResource(R.string.level_up_badge),
            fontSize = 13.sp,
            color = SecondaryText
        )
    }

    DetailCard {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconBadge(icon = Icons.Outlined.Person, tint = AppPrimary)
            Spacer(modifier = Modifier.width(10.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = "Camila Morales", fontSize = 15.sp, fontWeight = FontWeight.SemiBold, color = DarkText)
                Text(text = stringResource(R.string.volunteer_verifier), fontSize = 12.sp, color = SecondaryText)
            }
            AppTag(text = stringResource(R.string.guardian_level))
        }
        Row {
            Text(text = stringResource(R.string.network_reputation), fontSize = 12.sp, color = SecondaryText, modifier = Modifier.weight(1f))
            Text(text = stringResource(R.string.points_390), fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = AppPrimary)
        }
        LinearProgressIndicator(
            progress = { 0.78f },
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(CircleShape),
            color = AppPrimary,
            trackColor = LightBlueBg
        )
        Text(text = stringResource(R.string.points_missing_110), fontSize = 11.sp, color = SecondaryText)
    }

    DetailCard {
        SectionLabel(text = stringResource(R.string.new_privileges))
        PrivilegeRow(
            icon = Icons.Outlined.ArrowUpward,
            title = stringResource(R.string.featured_reports),
            text = stringResource(R.string.featured_reports_detail)
        )
        PrivilegeRow(
            icon = Icons.Outlined.WorkspacePremium,
            title = stringResource(R.string.golden_badge),
            text = stringResource(R.string.golden_badge_detail)
        )
        PrivilegeRow(
            icon = Icons.Outlined.Verified,
            title = stringResource(R.string.validate_reports),
            text = stringResource(R.string.validate_reports_detail)
        )
    }

    DetailCard {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = stringResource(R.string.ten_publications), fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = DarkText)
                Text(text = stringResource(R.string.ten_publications_detail), fontSize = 11.sp, color = SecondaryText)
            }
            AppTag(text = "10 / 10")
        }
        CaseRow(name = "Max", detail = stringResource(R.string.rescued_puppy), status = stringResource(R.string.adopted_happy))
        CaseRow(name = "Luna", detail = stringResource(R.string.sterilization_verified), status = stringResource(R.string.fostered))
        CaseRow(name = "Pancho", detail = stringResource(R.string.special_health), status = stringResource(R.string.recovered))
        CaseRow(name = "Oliver & Hermanos", detail = stringResource(R.string.protected_litter), status = stringResource(R.string.verified))
        Text(text = stringResource(R.string.more_pets), fontSize = 12.sp, color = SecondaryText)
    }

    PrimaryActionButton(text = stringResource(R.string.view_badges), onClick = onViewBadges, icon = Icons.Outlined.WorkspacePremium)
    PrimaryActionButton(
        text = stringResource(R.string.share_achievement),
        onClick = onShare,
        icon = Icons.Outlined.Share,
        container = LightBlueBg,
        content = DarkText
    )
}

@Composable
private fun PrivilegeRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    text: String
) {
    Row(verticalAlignment = Alignment.Top) {
        IconBadge(icon = icon, tint = AppPrimary, size = 32)
        Spacer(modifier = Modifier.width(10.dp))
        Column {
            Text(text = title, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = DarkText)
            Text(text = text, fontSize = 12.sp, color = SecondaryText)
        }
    }
}

@Composable
private fun CaseRow(name: String, detail: String, status: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
                .background(SoftBlue),
            contentAlignment = Alignment.Center
        ) {
            Text(text = name.take(1), fontWeight = FontWeight.Bold, color = AppPrimary)
        }
        Spacer(modifier = Modifier.width(10.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(text = name, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = DarkText)
            Text(text = detail, fontSize = 11.sp, color = SecondaryText)
        }
        AppTag(text = status, backgroundColor = Green.copy(alpha = 0.12f), textColor = Green)
    }
}

// ---------------------------------------------------------
// Notificación 3: Publicación Pausada (corregir carnet)
// ---------------------------------------------------------

@Composable
fun PausedPublicationContent(
    state: NotificationDetailUiState,
    onExamineClick: () -> Unit,
    onLotNumberChange: (String) -> Unit,
    onDayChange: (String) -> Unit,
    onMonthChange: (String) -> Unit,
    onYearChange: (String) -> Unit,
    onRabiesLotChange: (String) -> Unit,
    onEthicsConfirmedChange: (Boolean) -> Unit,
    onResubmit: () -> Unit,
    onGuidelinesClick: () -> Unit
) {
    DetailCard(color = Danger.copy(alpha = 0.07f)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            AppTag(
                text = if (state.isResubmitted) stringResource(R.string.priority_review_tag) else stringResource(R.string.ethics_review_tag),
                backgroundColor = Danger.copy(alpha = 0.12f),
                textColor = Danger
            )
            Spacer(modifier = Modifier.weight(1f))
            Text(text = "ID: luna_2", fontSize = 11.sp, color = SecondaryText)
        }
        Text(
            text = if (state.isResubmitted) stringResource(R.string.correction_sent) else stringResource(R.string.publication_paused),
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = DarkText
        )
        Text(
            text = if (state.isResubmitted)
                stringResource(R.string.committee_review)
            else
                stringResource(R.string.luna_paused),
            fontSize = 13.sp,
            color = SecondaryText
        )
    }

    DetailCard {
        Text(text = stringResource(R.string.moderator_ruling), fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = DarkText)
        Text(
            text = stringResource(R.string.ruling_text),
            fontSize = 12.sp,
            color = SecondaryText
        )
        Row(verticalAlignment = Alignment.CenterVertically) {
            AsyncImage(
                model = R.drawable.imagen_luna,
                contentDescription = "Luna",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(56.dp)
                    .clip(RoundedCornerShape(12.dp))
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = "Luna", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = DarkText)
                Text(text = stringResource(R.string.luna_age), fontSize = 12.sp, color = SecondaryText)
                Text(text = stringResource(R.string.luna_shelter), fontSize = 11.sp, color = SecondaryText)
            }
        }
        AppTag(
            text = if (state.isResubmitted) stringResource(R.string.in_review) else stringResource(R.string.paused_by_moderation),
            backgroundColor = Danger.copy(alpha = 0.12f),
            textColor = Danger
        )
    }

    // Paso 1: evidencia sanitaria (la foto depende de la cámara, que no se integra todavía)
    DetailCard {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = stringResource(R.string.required_evidence), fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = DarkText)
                Text(text = stringResource(R.string.required_evidence_detail), fontSize = 12.sp, color = SecondaryText)
            }
            AppTag(text = stringResource(R.string.step_1_of_2))
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconBadge(icon = Icons.Outlined.ReportProblem, tint = Danger, size = 36)
            Spacer(modifier = Modifier.width(10.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = stringResource(R.string.rejected_illegible), fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = Danger)
                Text(text = stringResource(R.string.photo_not_accepted), fontSize = 11.sp, color = SecondaryText)
            }
        }
        Surface(
            onClick = onExamineClick,
            shape = RoundedCornerShape(12.dp),
            color = SoftBlue
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Outlined.CameraAlt, contentDescription = null, tint = AppPrimary)
                Spacer(modifier = Modifier.width(10.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = stringResource(R.string.upload_clear_photo), fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = DarkText)
                    Text(text = stringResource(R.string.upload_clear_detail), fontSize = 11.sp, color = SecondaryText)
                }
                Text(text = stringResource(R.string.browse), fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = AppPrimary)
            }
        }
        Text(text = stringResource(R.string.recommended_shot), fontSize = 11.sp, color = SecondaryText)
    }

    // Paso 2: validación de lote y fechas
    DetailCard {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = stringResource(R.string.validate_batch), fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = DarkText)
                Text(text = stringResource(R.string.validate_batch_detail), fontSize = 12.sp, color = SecondaryText)
            }
            AppTag(text = stringResource(R.string.step_2_of_2))
        }

        OutlinedTextField(
            value = state.lotNumber,
            onValueChange = onLotNumberChange,
            modifier = Modifier.fillMaxWidth(),
            label = { Text(stringResource(R.string.batch_number)) },
            placeholder = { Text("POL-8842-XB") },
            singleLine = true,
            enabled = !state.isResubmitted,
            isError = state.lotNumberError != null,
            supportingText = state.lotNumberError?.let { { Text(it) } }
        )

        Text(text = stringResource(R.string.application_date), fontSize = 12.sp, color = SecondaryText)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            DateField(value = state.applicationDay, onValueChange = onDayChange, placeholder = stringResource(R.string.day_hint), enabled = !state.isResubmitted, modifier = Modifier.weight(1f))
            DateField(value = state.applicationMonth, onValueChange = onMonthChange, placeholder = stringResource(R.string.month_hint), enabled = !state.isResubmitted, modifier = Modifier.weight(1f))
            DateField(value = state.applicationYear, onValueChange = onYearChange, placeholder = stringResource(R.string.year_hint), enabled = !state.isResubmitted, modifier = Modifier.weight(1.4f))
        }
        state.dateError?.let { Text(text = it, color = AppRed, fontSize = 12.sp) }

        OutlinedTextField(
            value = state.rabiesLot,
            onValueChange = onRabiesLotChange,
            modifier = Modifier.fillMaxWidth(),
            label = { Text(stringResource(R.string.rabies_batch)) },
            placeholder = { Text("ANT-9910") },
            singleLine = true,
            enabled = !state.isResubmitted,
            isError = state.rabiesLotError != null,
            supportingText = state.rabiesLotError?.let { { Text(it) } }
        )

        Row(
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .clickable(enabled = !state.isResubmitted) { onEthicsConfirmedChange(!state.isEthicsConfirmed) },
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = state.isEthicsConfirmed,
                onCheckedChange = onEthicsConfirmedChange,
                enabled = !state.isResubmitted,
                colors = CheckboxDefaults.colors(checkedColor = AppPrimary)
            )
            Text(
                text = stringResource(R.string.ethics_confirmation),
                fontSize = 12.sp,
                color = DarkText
            )
        }
    }

    PrimaryActionButton(
        text = when {
            state.isResubmitted -> stringResource(R.string.sent_priority)
            state.isResubmitting -> stringResource(R.string.sending)
            else -> stringResource(R.string.resend_priority)
        },
        onClick = onResubmit,
        icon = Icons.AutoMirrored.Outlined.Send,
        enabled = state.isCorrectionFormValid && !state.isResubmitting && !state.isResubmitted
    )
    TextButton(onClick = onGuidelinesClick, modifier = Modifier.fillMaxWidth()) {
        Icon(Icons.AutoMirrored.Outlined.HelpOutline, contentDescription = null, tint = AppPrimary, modifier = Modifier.size(16.dp))
        Spacer(modifier = Modifier.width(6.dp))
        Text(text = stringResource(R.string.consult_guidelines), color = AppPrimary, fontSize = 13.sp)
    }
    DetailCard(color = SoftBlue) {
        Text(
            text = stringResource(R.string.priority_queue),
            fontSize = 12.sp,
            color = SecondaryText
        )
    }
}

@Composable
private fun DateField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    enabled: Boolean,
    modifier: Modifier
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier,
        placeholder = { Text(placeholder) },
        singleLine = true,
        enabled = enabled,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
    )
}

// ---------------------------------------------------------
// Notificación 4: Comentario de Veterinario
// ---------------------------------------------------------

@Composable
fun NewCommentContent(
    state: NotificationDetailUiState,
    onViewCase: () -> Unit,
    onAcceptDonation: () -> Unit,
    onWhatsAppClick: () -> Unit,
    onToggleLike: () -> Unit,
    onToggleAllReplies: () -> Unit,
    onReplyChange: (String) -> Unit,
    onSuggestionClick: (String) -> Unit,
    onSendReply: () -> Unit
) {
    DetailCard(color = AppPrimary.copy(alpha = 0.08f)) {
        AppTag(text = stringResource(R.string.rescue_notification_tag), backgroundColor = AppPrimary.copy(alpha = 0.15f), textColor = AppPrimary)
        Text(text = stringResource(R.string.new_comment_max), fontSize = 20.sp, fontWeight = FontWeight.Bold, color = DarkText)
    }

    // Caso al que pertenece el comentario
    DetailCard {
        Row(verticalAlignment = Alignment.CenterVertically) {
            AsyncImage(
                model = R.drawable.imagen_max,
                contentDescription = "Max",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(56.dp)
                    .clip(RoundedCornerShape(12.dp))
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                SectionLabel(text = stringResource(R.string.case_label))
                Text(text = "Max", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = DarkText)
                Text(text = stringResource(R.string.max_case_detail), fontSize = 11.sp, color = SecondaryText)
            }
            TextButton(onClick = onViewCase) {
                Text(text = stringResource(R.string.view_case), color = AppPrimary, fontWeight = FontWeight.SemiBold)
            }
        }
    }

    // Comentario del veterinario
    DetailCard {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconBadge(icon = Icons.Outlined.LocalHospital, tint = AppPrimary)
            Spacer(modifier = Modifier.width(10.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = "Dr. Andrés Vega", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = DarkText)
                Text(text = stringResource(R.string.verified_vet), fontSize = 11.sp, color = AppPrimary)
                Text(text = stringResource(R.string.vet_time), fontSize = 11.sp, color = SecondaryText)
            }
        }
        Text(
            text = stringResource(R.string.vet_comment),
            fontSize = 13.sp,
            color = DarkText
        )
        Surface(shape = RoundedCornerShape(12.dp), color = SoftBlue) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Outlined.LocationOn, contentDescription = null, tint = AppPrimary, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = "Clínica Veterinaria San Jerónimo", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = DarkText)
                    Text(text = "Cra. 7 #119-42, Usaquén, Bogotá", fontSize = 11.sp, color = SecondaryText)
                }
                Text(text = "2.4 km", fontSize = 12.sp, color = AppPrimary)
            }
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            Row(
                modifier = Modifier
                    .clip(CircleShape)
                    .clickable(onClick = onToggleLike)
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = if (state.isCommentLiked) Icons.Filled.ThumbUp else Icons.Outlined.ThumbUp,
                    contentDescription = stringResource(R.string.thank),
                    tint = if (state.isCommentLiked) AppPrimary else SecondaryText,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(text = "${state.commentLikes}", fontSize = 13.sp, color = SecondaryText)
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = stringResource(R.string.replies_count, state.replies.size), fontSize = 13.sp, color = SecondaryText, modifier = Modifier.weight(1f))
            AppTag(text = stringResource(R.string.verified_donation), backgroundColor = Green.copy(alpha = 0.12f), textColor = Green)
        }
        PrimaryActionButton(
            text = if (state.isDonationAccepted) stringResource(R.string.donation_accepted) else stringResource(R.string.accept_donation),
            onClick = onAcceptDonation,
            icon = Icons.Outlined.CheckCircle,
            enabled = !state.isDonationAccepted
        )
        PrimaryActionButton(
            text = stringResource(R.string.safe_whatsapp),
            onClick = onWhatsAppClick,
            icon = Icons.Outlined.ChatBubbleOutline,
            container = LightBlueBg,
            content = DarkText
        )
    }

    // Respuestas en el caso
    DetailCard {
        Row(verticalAlignment = Alignment.CenterVertically) {
            SectionLabel(text = stringResource(R.string.case_replies))
            Spacer(modifier = Modifier.weight(1f))
            if (state.replies.size > 1) {
                TextButton(onClick = onToggleAllReplies) {
                    Text(text = if (state.showAllReplies) stringResource(R.string.view_less) else stringResource(R.string.view_all), color = AppPrimary, fontSize = 12.sp)
                }
            }
        }
        state.visibleReplies.forEach { reply ->
            Column {
                Row {
                    Text(text = reply.author, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = DarkText, modifier = Modifier.weight(1f))
                    Text(text = reply.time, fontSize = 11.sp, color = SecondaryText)
                }
                Text(text = reply.text, fontSize = 12.sp, color = SecondaryText)
            }
        }
    }

    // Respuesta pública del gestor del caso
    DetailCard(color = SoftBlue) {
        Text(text = stringResource(R.string.reply_as_manager), fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = DarkText)
        Text(text = stringResource(R.string.quick_suggestions), fontSize = 11.sp, color = AppPrimary)
        Row(
            modifier = Modifier.horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            REPLY_SUGGESTIONS.forEach { suggestion ->
                Surface(
                    onClick = { onSuggestionClick(suggestion) },
                    shape = CircleShape,
                    color = Color.White
                ) {
                    Text(
                        text = suggestion.take(28) + if (suggestion.length > 28) "…" else "",
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        fontSize = 12.sp,
                        color = DarkText
                    )
                }
            }
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            OutlinedTextField(
                value = state.replyText,
                onValueChange = onReplyChange,
                modifier = Modifier.weight(1f),
                placeholder = { Text(stringResource(R.string.reply_public)) }
            )
            IconButton(onClick = onSendReply) {
                Icon(Icons.AutoMirrored.Outlined.Send, contentDescription = stringResource(R.string.send_reply), tint = AppPrimary)
            }
        }
    }
}

// ---------------------------------------------------------
// Notificación 5: Insignia Primera Huella
// ---------------------------------------------------------

@Composable
fun BadgeEarnedContent(
    onViewProfile: () -> Unit,
    onBackToNotifications: () -> Unit
) {
    DetailCard(color = Gold.copy(alpha = 0.08f)) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(text = stringResource(R.string.community_recognition), fontSize = 12.sp, color = Gold, fontWeight = FontWeight.SemiBold)
            IconBadge(icon = Icons.Outlined.WorkspacePremium, tint = Gold, size = 72)
            Text(text = stringResource(R.string.first_paw), fontSize = 22.sp, fontWeight = FontWeight.Bold, color = DarkText)
            Text(text = stringResource(R.string.granted_jan), fontSize = 12.sp, color = SecondaryText)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                AppTag(text = stringResource(R.string.official_vet_support))
                AppTag(text = stringResource(R.string.points_25), backgroundColor = Gold.copy(alpha = 0.15f), textColor = Gold)
                AppTag(text = stringResource(R.string.verified_upper), backgroundColor = Green.copy(alpha = 0.12f), textColor = Green)
            }
            Text(
                text = stringResource(R.string.honor_mention),
                fontSize = 13.sp,
                color = SecondaryText
            )
        }
    }

    DetailCard {
        SectionLabel(text = stringResource(R.string.origin_case))
        Row(verticalAlignment = Alignment.CenterVertically) {
            AsyncImage(
                model = R.drawable.imagen_max,
                contentDescription = "Max",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(56.dp)
                    .clip(RoundedCornerShape(12.dp))
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = stringResource(R.string.case_1_max), fontSize = 14.sp, fontWeight = FontWeight.Bold, color = DarkText)
                Text(text = stringResource(R.string.mixed_puppy_clinic), fontSize = 11.sp, color = SecondaryText)
            }
            AppTag(text = stringResource(R.string.adopted), backgroundColor = Green.copy(alpha = 0.12f), textColor = Green)
        }
        Text(
            text = stringResource(R.string.max_thanks),
            fontSize = 12.sp,
            color = SecondaryText
        )
    }

    DetailCard {
        Row {
            Text(text = stringResource(R.string.next_badges), fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = DarkText, modifier = Modifier.weight(1f))
            AppTag(text = stringResource(R.string.three_available))
        }
        NextBadgeRow(title = stringResource(R.string.foster_badge), points = "+50 pts", text = stringResource(R.string.foster_badge_detail))
        NextBadgeRow(title = stringResource(R.string.steady_rescuer), points = "+75 pts", text = stringResource(R.string.steady_rescuer_detail))
        NextBadgeRow(title = stringResource(R.string.hero_month), points = "+120 pts", text = stringResource(R.string.hero_month_detail))
    }

    PrimaryActionButton(text = stringResource(R.string.view_full_profile), onClick = onViewProfile, icon = Icons.Outlined.Person)
    PrimaryActionButton(
        text = stringResource(R.string.back_to_notifications),
        onClick = onBackToNotifications,
        container = LightBlueBg,
        content = DarkText
    )
}

@Composable
private fun NextBadgeRow(title: String, points: String, text: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        IconBadge(icon = Icons.Outlined.EmojiEvents, tint = SecondaryText, size = 32)
        Spacer(modifier = Modifier.width(10.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = DarkText)
            Text(text = text, fontSize = 11.sp, color = SecondaryText)
        }
        Text(text = points, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = Gold)
    }
}
