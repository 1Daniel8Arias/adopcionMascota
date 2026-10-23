package co.edu.adopcionmascota.features.profile.component

import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Checklist
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Pets
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import co.edu.adopcionmascota.features.profile.BadgeStatus
import co.edu.adopcionmascota.features.profile.BadgeUi

@Composable
fun BadgesSection(
    badges: List<BadgeUi>,
    totalBadges: Int,
    onViewAllClick: () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Vitrina de Insignias",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = DarkText
                )
                Text(
                    text = "Reconocimientos de la comunidad",
                    fontSize = 13.sp,
                    color = SecondaryText
                )
            }
            TextButton(onClick = onViewAllClick) {
                Text(
                    text = "Ver todas ($totalBadges)",
                    color = AppPrimary,
                    fontWeight = FontWeight.Medium,
                    fontSize = 14.sp
                )
            }
        }

        // Cuadrícula de 2 columnas (sin Lazy porque ya estamos dentro de un scroll)
        badges.chunked(2).forEach { rowBadges ->
            Row(
                modifier = Modifier.height(IntrinsicSize.Min),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                rowBadges.forEach { badge ->
                    BadgeCard(
                        badge = badge,
                        modifier = Modifier.weight(1f).fillMaxHeight()
                    )
                }
                // Si la fila queda incompleta, se rellena para mantener el ancho
                if (rowBadges.size == 1) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

// El icono se decide por el id para no guardar ImageVector en el estado
private fun badgeIcon(id: String): ImageVector = when (id) {
    "first_paw" -> Icons.Outlined.Pets
    "temporary_home" -> Icons.Outlined.Home
    "verified_10" -> Icons.Outlined.Checklist
    else -> Icons.Outlined.Lock
}

@Composable
private fun BadgeCard(
    badge: BadgeUi,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(18.dp),
        color = LightBlueBg
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            when (badge.status) {
                BadgeStatus.GRANTED -> {
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .background(
                                if (badge.id == "first_paw") Color(0xFFDDDAFF) else AppPrimary,
                                CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = badgeIcon(badge.id),
                            contentDescription = null,
                            tint = if (badge.id == "first_paw") DarkText else Color.White,
                            modifier = Modifier.size(34.dp)
                        )
                    }
                }

                BadgeStatus.IN_PROGRESS -> {
                    Box(
                        modifier = Modifier.size(72.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            progress = { badge.percent / 100f },
                            modifier = Modifier.size(72.dp),
                            color = AppPrimary,
                            trackColor = Color(0xFFD3DCF5),
                            strokeWidth = 4.dp
                        )
                        Icon(
                            imageVector = badgeIcon(badge.id),
                            contentDescription = null,
                            tint = SecondaryText,
                            modifier = Modifier.size(30.dp)
                        )
                    }
                }

                BadgeStatus.LOCKED -> {
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .background(Color(0xFFD3E0FA), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Lock,
                            contentDescription = null,
                            tint = SecondaryText,
                            modifier = Modifier.size(30.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = badge.title,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = DarkText,
                textAlign = TextAlign.Center
            )
            Text(
                text = badge.description,
                fontSize = 13.sp,
                color = SecondaryText,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            Surface(shape = CircleShape, color = Color(0xFFE2E4FF)) {
                Text(
                    text = when (badge.status) {
                        BadgeStatus.GRANTED -> "✓ Otorgada"
                        BadgeStatus.IN_PROGRESS -> "${badge.percent}% alcanzado"
                        BadgeStatus.LOCKED -> "Bloqueada"
                    },
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 5.dp),
                    fontSize = 12.sp,
                    color = if (badge.status == BadgeStatus.GRANTED) AppPrimary else SecondaryText
                )
            }
        }
    }
}
