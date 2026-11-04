package co.edu.adopcionmascota.features.profile

import androidx.compose.ui.res.stringResource
import co.edu.adopcionmascota.R
import co.edu.adopcionmascota.domain.model.BadgeStatus
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.outlined.EmojiEvents
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.WorkspacePremium
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import co.edu.adopcionmascota.core.component.AppTag
import co.edu.adopcionmascota.core.component.ConfirmAlertDialog
import co.edu.adopcionmascota.core.theme.AppPrimary
import co.edu.adopcionmascota.core.theme.Background
import co.edu.adopcionmascota.core.theme.DarkText
import co.edu.adopcionmascota.core.theme.LightBlueBg
import co.edu.adopcionmascota.core.theme.SecondaryText

private val Gold = Color(0xFFB54708)

// Pantalla "Vitrina de Insignias y Gamificación"
@Composable
fun BadgesScreen(
    onNavigateBack: () -> Unit,             // stringResource(R.string.back_to_profile)
    onViewLevels: () -> Unit,               // stringResource(R.string.view_levels)
    viewModel: BadgesViewModel = viewModel()
) {
    val state by viewModel.uiState.collectAsState()

    Scaffold(
        modifier = Modifier.statusBarsPadding(),
        containerColor = Background
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Row(
                    modifier = Modifier.padding(top = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Outlined.ArrowBack, contentDescription = stringResource(R.string.back), tint = DarkText)
                    }
                    Text(text = stringResource(R.string.badges_showcase), fontSize = 20.sp, fontWeight = FontWeight.Bold, color = DarkText)
                }
            }

            // Resumen del progreso
            item {
                Surface(shape = RoundedCornerShape(20.dp), color = AppPrimary.copy(alpha = 0.08f)) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(text = state.userName, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = DarkText)
                        AppTag(text = state.levelText)
                        Row {
                            Text(
                                text = stringResource(R.string.badges_unlocked_count, state.grantedCount, state.totalCount),
                                modifier = Modifier.weight(1f),
                                fontSize = 13.sp,
                                color = DarkText
                            )
                            Text(text = stringResource(R.string.completed_percent, state.completedPercent), fontSize = 13.sp, color = AppPrimary)
                        }
                        LinearProgressIndicator(
                            progress = { state.completedPercent / 100f },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                                .clip(CircleShape),
                            color = AppPrimary,
                            trackColor = LightBlueBg
                        )
                        Text(text = stringResource(R.string.badge_points_earned, state.earnedPoints), fontSize = 12.sp, color = SecondaryText)
                    }
                }
            }

            // Filtros por estado
            item {
                Row(
                    modifier = Modifier.horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    BadgeFilter.entries.forEach { filter ->
                        Chip(
                            text = stringResource(R.string.filter_count, stringResource(filter.label), state.countFor(filter)),
                            selected = state.filter == filter,
                            onClick = { viewModel.onFilterChange(filter) }
                        )
                    }
                }
            }

            // Filtros por categoría
            item {
                Row(
                    modifier = Modifier.horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Chip(stringResource(R.string.whole_catalog), state.category == null) { viewModel.onCategoryChange(null) }
                    BadgeCategory.entries.forEach { category ->
                        Chip(stringResource(category.label), state.category == category) { viewModel.onCategoryChange(category) }
                    }
                }
            }

            if (state.visibleBadges.isEmpty()) {
                item {
                    Text(text = stringResource(R.string.no_badges_filter), color = SecondaryText, modifier = Modifier.padding(16.dp))
                }
            }

            items(state.visibleBadges, key = { it.badge.id }) { detail ->
                BadgeRow(detail = detail, onClick = { viewModel.onBadgeClick(detail) })
            }

            // Temporada activa
            item {
                Surface(shape = RoundedCornerShape(20.dp), color = Gold.copy(alpha = 0.1f)) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Row {
                            AppTag(text = stringResource(R.string.active_season), backgroundColor = Gold.copy(alpha = 0.15f), textColor = Gold)
                            Spacer(modifier = Modifier.weight(1f))
                            Text(text = stringResource(R.string.twelve_days_left), fontSize = 12.sp, color = SecondaryText)
                        }
                        Text(text = stringResource(R.string.mist_guardian), fontSize = 16.sp, fontWeight = FontWeight.Bold, color = DarkText)
                        Text(
                            text = stringResource(R.string.mist_guardian_detail),
                            fontSize = 12.sp,
                            color = SecondaryText
                        )
                        OutlinedButton(onClick = viewModel::onSeasonClick, shape = CircleShape) {
                            Text(text = if (state.joinedSeason) stringResource(R.string.already_joined) else stringResource(R.string.view_special_challenge))
                        }
                    }
                }
            }

            item {
                Button(
                    onClick = onViewLevels,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = CircleShape,
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Black)
                ) {
                    Text(text = stringResource(R.string.view_levels))
                }
            }
            item {
                OutlinedButton(
                    onClick = onNavigateBack,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = CircleShape
                ) {
                    Text(text = stringResource(R.string.back_to_profile))
                }
                Spacer(modifier = Modifier.height(16.dp))
            }
        }

        state.selectedBadge?.let { detail ->
            val message = buildString {
                append(detail.badge.description)
                detail.date?.let { append(stringResource(R.string.badge_obtained, it, detail.points)) }
                detail.requirement?.let { append(stringResource(R.string.badge_requirement, it, detail.points)) }
            }
            ConfirmAlertDialog(
                title = detail.badge.title,
                message = message,
                onConfirm = viewModel::onDismissBadge,
                onDismiss = viewModel::onDismissBadge,
                confirmText = stringResource(R.string.understood),
                dismissText = stringResource(R.string.close)
            )
        }

        if (state.showSeasonDialog) {
            ConfirmAlertDialog(
                title = stringResource(R.string.challenge_title),
                message = stringResource(R.string.challenge_message),
                onConfirm = viewModel::onJoinSeason,
                onDismiss = viewModel::onDismissSeason,
                confirmText = if (state.joinedSeason) stringResource(R.string.keep_participating) else stringResource(R.string.join_challenge)
            )
        }
    }
}

@Composable
private fun Chip(text: String, selected: Boolean, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        shape = CircleShape,
        color = if (selected) Color.Black else LightBlueBg
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
            fontSize = 12.sp,
            color = if (selected) Color.White else DarkText
        )
    }
}

@Composable
private fun BadgeRow(detail: BadgeDetail, onClick: () -> Unit) {
    val badge = detail.badge
    val (statusText, color) = when (badge.status) {
        BadgeStatus.GRANTED -> stringResource(R.string.badge_unlocked) to Color(0xFF067647)
        BadgeStatus.IN_PROGRESS -> stringResource(R.string.badge_in_progress, badge.percent) to AppPrimary
        BadgeStatus.LOCKED -> stringResource(R.string.badge_locked) to SecondaryText
    }

    Surface(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = Color.White
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(color.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = when (badge.status) {
                        BadgeStatus.GRANTED -> Icons.Outlined.WorkspacePremium
                        BadgeStatus.IN_PROGRESS -> Icons.Outlined.EmojiEvents
                        BadgeStatus.LOCKED -> Icons.Outlined.Lock
                    },
                    contentDescription = null,
                    tint = color
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = badge.title, modifier = Modifier.weight(1f), fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = DarkText)
                    AppTag(text = statusText, backgroundColor = color.copy(alpha = 0.12f), textColor = color)
                }
                Text(text = badge.description, fontSize = 12.sp, color = SecondaryText, maxLines = 2)
                if (badge.status == BadgeStatus.IN_PROGRESS) {
                    LinearProgressIndicator(
                        progress = { badge.percent / 100f },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(CircleShape),
                        color = AppPrimary,
                        trackColor = LightBlueBg
                    )
                    Text(text = stringResource(R.string.badge_progress, badge.progress, badge.target), fontSize = 11.sp, color = SecondaryText)
                }
                Text(
                    text = detail.date?.let { stringResource(R.string.badge_impact, it, detail.points) } ?: stringResource(R.string.badge_reward, detail.points),
                    fontSize = 11.sp,
                    color = Gold
                )
            }
        }
    }
}
