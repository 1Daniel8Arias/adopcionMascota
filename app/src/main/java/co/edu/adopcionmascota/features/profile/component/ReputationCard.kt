package co.edu.adopcionmascota.features.profile.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.ChatBubbleOutline
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Shield
import androidx.compose.material.icons.outlined.Verified
import androidx.compose.material.icons.outlined.WorkspacePremium
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import co.edu.adopcionmascota.core.theme.AppPrimary
import co.edu.adopcionmascota.core.theme.DarkText
import co.edu.adopcionmascota.core.theme.LightBlueBg
import co.edu.adopcionmascota.core.theme.SecondaryText
import co.edu.adopcionmascota.features.profile.LevelStatus
import co.edu.adopcionmascota.features.profile.LevelUi
import co.edu.adopcionmascota.features.profile.ProfileUiState

@Composable
fun ReputationCard(
    state: ProfileUiState,
    onClick: () -> Unit = {}      // Abre "Sistema de Reputación y Niveles"
) {
    Surface(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        color = LightBlueBg
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {

            // Encabezado: nivel y puntos
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .background(AppPrimary, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Outlined.WorkspacePremium,
                        contentDescription = null,
                        tint = Color.White
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "REPUTACIÓN COMUNITARIA",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        color = AppPrimary
                    )
                    Text(
                        text = "Nivel ${state.currentLevel}: ${state.levelName}",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = DarkText
                    )
                }

                Row(verticalAlignment = Alignment.Bottom) {
                    Text(
                        text = state.points.toString(),
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = AppPrimary
                    )
                    Text(
                        text = " / ${state.nextLevelPoints} pts",
                        fontSize = 13.sp,
                        color = SecondaryText
                    )
                }
            }

            // Progreso hacia el siguiente nivel
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Meta: Nivel ${state.currentLevel + 1} (${state.nextLevelName})",
                    fontSize = 13.sp,
                    color = DarkText
                )
                Text(
                    text = "${state.progressPercent}% completado",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = DarkText
                )
            }

            LinearProgressIndicator(
                progress = { state.progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(CircleShape),
                color = AppPrimary,
                trackColor = Color(0xFFD3DCF5)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Faltan ${state.missingPoints} pts",
                    fontSize = 13.sp,
                    color = DarkText
                )
                Text(
                    text = "+20 pts por rescate verificado",
                    fontSize = 13.sp,
                    color = AppPrimary
                )
            }

            // Últimas recompensas
            state.recentRewards.forEachIndexed { index, reward ->
                RewardChip(
                    text = reward,
                    icon = if (index == 0) Icons.Outlined.Verified else Icons.Outlined.ChatBubbleOutline
                )
            }

            // Escala de reconocimiento
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                color = Color.White
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(
                        text = "Escala de Reconocimiento",
                        fontSize = 13.sp,
                        color = DarkText
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Top
                    ) {
                        state.levels.forEach { level ->
                            LevelItem(level = level, modifier = Modifier.weight(1f))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun RewardChip(text: String, icon: ImageVector) {
    Surface(shape = CircleShape, color = Color(0xFFE2EDFF)) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = AppPrimary,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(text = text, fontSize = 13.sp, color = DarkText)
        }
    }
}

@Composable
private fun LevelItem(level: LevelUi, modifier: Modifier = Modifier) {

    val isActive = level.status == LevelStatus.ACTIVE

    val icon = when (level.status) {
        LevelStatus.UNLOCKED -> Icons.Outlined.Check
        LevelStatus.ACTIVE -> Icons.Outlined.Shield
        LevelStatus.NEXT -> Icons.Outlined.WorkspacePremium
        LevelStatus.LOCKED -> Icons.Outlined.Lock
    }

    val containerColor = when (level.status) {
        LevelStatus.UNLOCKED, LevelStatus.ACTIVE -> AppPrimary
        else -> Color(0xFFE2EDFF)
    }

    val iconColor = when (level.status) {
        LevelStatus.UNLOCKED, LevelStatus.ACTIVE -> Color.White
        else -> SecondaryText
    }

    val statusText = when (level.status) {
        LevelStatus.UNLOCKED -> "Desbloqueado"
        LevelStatus.ACTIVE -> "Activo"
        LevelStatus.NEXT -> "Próximo"
        LevelStatus.LOCKED -> "Bloqueado"
    }

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(52.dp)
                .then(
                    if (isActive) Modifier.border(3.dp, DarkText, CircleShape) else Modifier
                )
                .padding(if (isActive) 4.dp else 0.dp)
                .background(containerColor, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconColor,
                modifier = Modifier.size(22.dp)
            )
        }

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = "${level.number}. ${level.shortName}",
            fontSize = 12.sp,
            fontWeight = if (isActive) FontWeight.Bold else FontWeight.Normal,
            color = if (level.status == LevelStatus.LOCKED || level.status == LevelStatus.NEXT)
                SecondaryText else DarkText,
            textAlign = TextAlign.Center
        )

        Text(
            text = statusText,
            fontSize = 11.sp,
            color = if (level.status == LevelStatus.UNLOCKED) AppPrimary else SecondaryText,
            textAlign = TextAlign.Center
        )
    }
}
