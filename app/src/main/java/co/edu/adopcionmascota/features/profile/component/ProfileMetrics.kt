package co.edu.adopcionmascota.features.profile.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowForward
import androidx.compose.material.icons.outlined.Campaign
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.material.icons.outlined.FolderShared
import androidx.compose.material.icons.outlined.HourglassTop
import androidx.compose.material.icons.outlined.ThumbUp
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import co.edu.adopcionmascota.core.theme.AppPrimary
import co.edu.adopcionmascota.core.theme.DarkText
import co.edu.adopcionmascota.core.theme.LightBlueBg
import co.edu.adopcionmascota.core.theme.LightBlueBorder
import co.edu.adopcionmascota.core.theme.SecondaryText
import co.edu.adopcionmascota.features.profile.ProfileUiState

@Composable
fun ProfileMetricsSection(
    state: ProfileUiState,
    onViewPublicationsClick: () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Impacto y Métricas",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = DarkText
                )
                Text(
                    text = "Aportes en la comunidad de rescate",
                    fontSize = 13.sp,
                    color = SecondaryText
                )
            }

            Surface(shape = CircleShape, color = LightBlueBg) {
                Text(
                    text = "Actualizado hoy",
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    fontSize = 12.sp,
                    color = AppPrimary,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        Row(
            modifier = Modifier.height(IntrinsicSize.Min),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            MetricCard(
                icon = Icons.Outlined.Campaign,
                topLabel = "En curso",
                value = state.activePublications.toString(),
                label = "Publicaciones Activas",
                modifier = Modifier.weight(1f).fillMaxHeight()
            )
            MetricCard(
                icon = Icons.Outlined.Favorite,
                topLabel = "Finalizadas",
                value = state.finishedPublications.toString(),
                label = "Finalizadas / Resueltas",
                modifier = Modifier.weight(1f).fillMaxHeight()
            )
        }

        Row(
            modifier = Modifier.height(IntrinsicSize.Min),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            MetricCard(
                icon = Icons.Outlined.HourglassTop,
                topLabel = "Revisión",
                value = state.pendingPublications.toString(),
                label = "Por Verificar",
                modifier = Modifier.weight(1f).fillMaxHeight()
            )
            MetricCard(
                icon = Icons.Outlined.ThumbUp,
                topLabel = "+${state.votesThisMonth} este mes",
                value = state.importantVotes.toString(),
                label = "Votos 'Es importante'",
                modifier = Modifier.weight(1f).fillMaxHeight()
            )
        }

        // Acceso a todas las publicaciones
        Surface(
            onClick = onViewPublicationsClick,
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, LightBlueBorder, RoundedCornerShape(14.dp)),
            shape = RoundedCornerShape(14.dp),
            color = LightBlueBg
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Outlined.FolderShared,
                    contentDescription = null,
                    tint = AppPrimary
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = "Ver todas mis publicaciones (${state.myPublicationsCount})",
                    modifier = Modifier.weight(1f),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = AppPrimary
                )
                Icon(
                    imageVector = Icons.AutoMirrored.Outlined.ArrowForward,
                    contentDescription = null,
                    tint = AppPrimary
                )
            }
        }
    }
}

@Composable
private fun MetricCard(
    icon: ImageVector,
    topLabel: String,
    value: String,
    label: String,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(18.dp),
        color = Color(0xFFE2EDFF)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(Color(0xFFD3E0FA), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = DarkText,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Text(
                    text = topLabel,
                    fontSize = 12.sp,
                    color = AppPrimary,
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = value,
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = DarkText
            )
            Text(
                text = label,
                fontSize = 14.sp,
                color = DarkText
            )
        }
    }
}
