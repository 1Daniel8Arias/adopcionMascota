package co.edu.adopcionmascota.features.profile

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Shield
import androidx.compose.material.icons.automirrored.outlined.TrendingFlat
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
import co.edu.adopcionmascota.core.theme.AppPrimary
import co.edu.adopcionmascota.core.theme.Background
import co.edu.adopcionmascota.core.theme.DarkText
import co.edu.adopcionmascota.core.theme.LightBlueBg
import co.edu.adopcionmascota.core.theme.SecondaryText

private val Green = Color(0xFF067647)

// Pantalla "Sistema de Reputación y Niveles"
@Composable
fun ReputationScreen(
    onNavigateBack: () -> Unit,             // "Volver a mi Perfil"
    onViewBadges: () -> Unit,               // "Ver Vitrina de Insignias"
    viewModel: ReputationViewModel = viewModel()
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
                    Text(text = "Sistema de Reputación", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = DarkText)
                }
            }

            // Nivel actual y progreso
            item {
                Surface(shape = RoundedCornerShape(20.dp), color = AppPrimary.copy(alpha = 0.08f)) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(text = state.userName, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = DarkText)
                        Text(text = state.location, fontSize = 12.sp, color = SecondaryText)
                        state.currentLevel?.let { level ->
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                AppTag(text = "NIVEL ${level.number}")
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(text = level.name, fontSize = 15.sp, fontWeight = FontWeight.SemiBold, color = DarkText)
                            }
                        }
                        Row {
                            Text(text = "Puntos acumulados", modifier = Modifier.weight(1f), fontSize = 13.sp, color = SecondaryText)
                            Text(
                                text = "${state.points} / ${state.nextLevel?.minPoints ?: state.points} pts",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = AppPrimary
                            )
                        }
                        LinearProgressIndicator(
                            progress = { state.progress },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                                .clip(CircleShape),
                            color = AppPrimary,
                            trackColor = LightBlueBg
                        )
                        Text(
                            text = state.nextLevel?.let { "Faltan ${state.missingPoints} pts para ${it.name}" }
                                ?: "¡Alcanzaste el máximo rango!",
                            fontSize = 12.sp,
                            color = SecondaryText
                        )
                    }
                }
            }

            item {
                Text(text = "ESCALA DE NIVELES", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = SecondaryText)
            }

            items(state.levels, key = { it.number }) { level ->
                LevelRow(
                    level = level,
                    status = state.statusOf(level),
                    expanded = state.expandedLevel == level.number,
                    onClick = { viewModel.onLevelClick(level.number) }
                )
            }

            // Cómo sumar puntos
            item {
                Surface(shape = RoundedCornerShape(20.dp), color = Color.White) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(text = "¿Cómo sumar puntos de reputación?", fontSize = 15.sp, fontWeight = FontWeight.SemiBold, color = DarkText)
                        Text(text = "Acciones comunitarias verificadas por la red", fontSize = 12.sp, color = SecondaryText)
                        state.actions.forEach { action ->
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.AutoMirrored.Outlined.TrendingFlat, contentDescription = null, tint = AppPrimary, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(text = action.text, modifier = Modifier.weight(1f), fontSize = 13.sp, color = DarkText)
                                Text(text = "+${action.points} pts", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Green)
                            }
                        }
                    }
                }
            }

            item {
                Surface(shape = RoundedCornerShape(16.dp), color = LightBlueBg) {
                    Text(
                        text = "La reputación en Patitas Conectadas no se compra ni se transfiere; se construye protegiendo la vida animal.",
                        modifier = Modifier.padding(14.dp),
                        fontSize = 12.sp,
                        color = SecondaryText
                    )
                }
            }

            item {
                Button(
                    onClick = onViewBadges,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = CircleShape,
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Black)
                ) {
                    Text(text = "Ver Vitrina de Insignias (${state.grantedBadges} obtenidas)")
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
    }
}

@Composable
private fun LevelRow(
    level: ReputationLevel,
    status: LevelStatus,
    expanded: Boolean,
    onClick: () -> Unit
) {
    val (label, color) = when (status) {
        LevelStatus.UNLOCKED -> "Completado" to Green
        LevelStatus.ACTIVE -> "En Curso" to AppPrimary
        LevelStatus.NEXT -> "Siguiente" to Color(0xFFB54708)
        LevelStatus.LOCKED -> if (level.maxPoints == null) "Máximo Rango" to SecondaryText else "Bloqueado" to SecondaryText
    }

    Surface(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = if (status == LevelStatus.ACTIVE) AppPrimary.copy(alpha = 0.08f) else Color.White
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(color.copy(alpha = 0.12f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = when (status) {
                            LevelStatus.UNLOCKED -> Icons.Outlined.CheckCircle
                            LevelStatus.ACTIVE -> Icons.Outlined.Shield
                            else -> Icons.Outlined.Lock
                        },
                        contentDescription = null,
                        tint = color,
                        modifier = Modifier.size(18.dp)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = "Nivel ${level.number} · ${level.name}", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = DarkText)
                    Text(text = level.rangeText, fontSize = 12.sp, color = SecondaryText)
                }
                AppTag(text = label, backgroundColor = color.copy(alpha = 0.12f), textColor = color)
            }
            Text(text = level.description, fontSize = 12.sp, color = SecondaryText)
            if (expanded) {
                level.benefits.forEach { benefit ->
                    Text(text = "• $benefit", fontSize = 12.sp, color = DarkText)
                }
            } else {
                Text(text = "Toca para ver los beneficios", fontSize = 11.sp, color = AppPrimary)
            }
        }
    }
}
