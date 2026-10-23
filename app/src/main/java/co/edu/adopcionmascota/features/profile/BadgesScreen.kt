package co.edu.adopcionmascota.features.profile

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
    onNavigateBack: () -> Unit,             // "Volver a mi Perfil"
    onViewLevels: () -> Unit,               // "Ver Escala de Niveles y Puntos"
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
                        Icon(Icons.AutoMirrored.Outlined.ArrowBack, contentDescription = "Volver", tint = DarkText)
                    }
                    Text(text = "Vitrina de Insignias", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = DarkText)
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
                                text = "${state.grantedCount} de ${state.totalCount} desbloqueadas",
                                modifier = Modifier.weight(1f),
                                fontSize = 13.sp,
                                color = DarkText
                            )
                            Text(text = "${state.completedPercent}% completado", fontSize = 13.sp, color = AppPrimary)
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
                        Text(text = "+${state.earnedPoints} pts ganados con insignias", fontSize = 12.sp, color = SecondaryText)
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
                            text = "${filter.label} (${state.countFor(filter)})",
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
                    Chip("Todo el Catálogo", state.category == null) { viewModel.onCategoryChange(null) }
                    BadgeCategory.entries.forEach { category ->
                        Chip(category.label, state.category == category) { viewModel.onCategoryChange(category) }
                    }
                }
            }

            if (state.visibleBadges.isEmpty()) {
                item {
                    Text(text = "No hay insignias con este filtro.", color = SecondaryText, modifier = Modifier.padding(16.dp))
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
                            AppTag(text = "TEMPORADA ACTIVA", backgroundColor = Gold.copy(alpha = 0.15f), textColor = Gold)
                            Spacer(modifier = Modifier.weight(1f))
                            Text(text = "Quedan 12 días", fontSize = 12.sp, color = SecondaryText)
                        }
                        Text(text = "Guardián de la Niebla", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = DarkText)
                        Text(
                            text = "Campaña de apoyo a rescates de fauna y caninos extraviados. +120 pts épicos.",
                            fontSize = 12.sp,
                            color = SecondaryText
                        )
                        OutlinedButton(onClick = viewModel::onSeasonClick, shape = CircleShape) {
                            Text(text = if (state.joinedSeason) "Ya participas ✓" else "Ver Reto Especial")
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
                    Text(text = "Ver Escala de Niveles y Puntos")
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
                    Text(text = "Volver a mi Perfil")
                }
                Spacer(modifier = Modifier.height(16.dp))
            }
        }

        state.selectedBadge?.let { detail ->
            val message = buildString {
                append(detail.badge.description)
                detail.date?.let { append("\n\nObtenida el $it · +${detail.points} pts") }
                detail.requirement?.let { append("\n\n$it · Recompensa: +${detail.points} pts") }
            }
            ConfirmAlertDialog(
                title = detail.badge.title,
                message = message,
                onConfirm = viewModel::onDismissBadge,
                onDismiss = viewModel::onDismissBadge,
                confirmText = "Entendido",
                dismissText = "Cerrar"
            )
        }

        if (state.showSeasonDialog) {
            ConfirmAlertDialog(
                title = "Reto: Guardián de la Niebla",
                message = "Apoya 3 rescates de fauna o caninos extraviados durante la temporada para obtener la insignia épica y +120 pts.",
                onConfirm = viewModel::onJoinSeason,
                onDismiss = viewModel::onDismissSeason,
                confirmText = if (state.joinedSeason) "Seguir participando" else "Unirme al reto"
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
        BadgeStatus.GRANTED -> "Desbloqueada" to Color(0xFF067647)
        BadgeStatus.IN_PROGRESS -> "En Curso ${badge.percent}%" to AppPrimary
        BadgeStatus.LOCKED -> "Bloqueada" to SecondaryText
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
                    Text(text = "${badge.progress} de ${badge.target} completados", fontSize = 11.sp, color = SecondaryText)
                }
                Text(
                    text = detail.date?.let { "$it · +${detail.points} pts impacto" } ?: "Recompensa: +${detail.points} pts",
                    fontSize = 11.sp,
                    color = Gold
                )
            }
        }
    }
}
