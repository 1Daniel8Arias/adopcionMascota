package co.edu.adopcionmascota.features.notification

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
            AppTag(text = "PUBLICACIÓN APROBADA", backgroundColor = Green.copy(alpha = 0.15f), textColor = Green)
            Spacer(modifier = Modifier.weight(1f))
            Text(text = "ID: max_1", fontSize = 11.sp, color = SecondaryText)
        }
        Text(
            text = "¡Tu publicación ya está activa!",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = DarkText
        )
        Text(
            text = "Max superó la verificación de bienestar y tenencia responsable. Ya está visible en el feed comunitario.",
            fontSize = 13.sp,
            color = SecondaryText
        )
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Outlined.CheckCircle, contentDescription = null, tint = Green, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text(text = "Indexado en radar de adopción activa", fontSize = 12.sp, color = DarkText, modifier = Modifier.weight(1f))
            AppTag(text = "● En vivo", backgroundColor = Green.copy(alpha = 0.15f), textColor = Green)
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
                Text(text = "Max · 3 meses", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = DarkText)
                Text(text = "Golden Mestizo • Tamaño Mediano", fontSize = 12.sp, color = SecondaryText)
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
            listOf("Vacunas al día", "Esterilizado", "Apto con niños", "Hogar responsable").forEach {
                AppTag(text = it)
            }
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Outlined.Shield, contentDescription = null, tint = AppPrimary, modifier = Modifier.size(14.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = "Protocolo ético completado hoy, 10:45 AM · Ficha Protegida",
                fontSize = 11.sp,
                color = SecondaryText
            )
        }
    }

    DetailCard {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(text = "Rendimiento en tiempo real", fontSize = 15.sp, fontWeight = FontWeight.SemiBold, color = DarkText, modifier = Modifier.weight(1f))
            AppTag(text = "Última hora")
        }
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            StatBox(
                modifier = Modifier.weight(1f),
                icon = Icons.Outlined.Visibility,
                label = "Vistas iniciales",
                value = "142",
                detail = "+38% en su primera hora"
            )
            StatBox(
                modifier = Modifier.weight(1f),
                icon = Icons.Outlined.LocationOn,
                label = "Zona prioritaria",
                value = "Usaquén & Chapinero",
                detail = "Radio de 6 km"
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
                    Text(text = "92% Índice de Compatibilidad", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = DarkText)
                    Text(text = "3 familias precalificadas en espera", fontSize = 12.sp, color = SecondaryText)
                }
                AppTag(text = "Alta demanda", backgroundColor = AppRed.copy(alpha = 0.12f), textColor = AppRed)
            }
        }
    }

    PrimaryActionButton(
        text = "Gestionar Consultas de Adoptantes (2)",
        onClick = onManageRequests,
        icon = Icons.Outlined.ChatBubbleOutline
    )
    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        Box(modifier = Modifier.weight(1f)) {
            PrimaryActionButton(
                text = "Ver en Feed",
                onClick = onViewInFeed,
                icon = Icons.Outlined.Explore,
                container = AppPrimary
            )
        }
        Box(modifier = Modifier.weight(1f)) {
            PrimaryActionButton(
                text = "Compartir",
                onClick = onShare,
                icon = Icons.Outlined.Share,
                container = LightBlueBg,
                content = DarkText
            )
        }
    }

    DetailCard(color = SoftBlue) {
        Text(text = "Compromiso ético Patitas Conectadas", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = DarkText)
        Text(
            text = "Todas las solicitudes recibidas filtrarán automáticamente perfiles con contrato de adopción firmado y visita virtual acordada.",
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
        AppTag(text = "COMUNIDAD & IMPACTO", backgroundColor = AppPrimary.copy(alpha = 0.15f), textColor = AppPrimary)
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconBadge(icon = Icons.Outlined.EmojiEvents, tint = AppPrimary, size = 56)
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(text = "¡Subiste a Nivel Guardián!", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = DarkText)
                Text(text = "Nivel 3 · +50 Puntos de Reputación Ganados", fontSize = 12.sp, color = AppPrimary)
            }
        }
        Text(
            text = "Desbloqueaste la insignia «10 Publicaciones Verificadas» y aumentaste tu prestigio comunitario.",
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
                Text(text = "Voluntaria de Rescate & Verificadora", fontSize = 12.sp, color = SecondaryText)
            }
            AppTag(text = "Guardián Nv. 3")
        }
        Row {
            Text(text = "Reputación en la red", fontSize = 12.sp, color = SecondaryText, modifier = Modifier.weight(1f))
            Text(text = "390 / 500 pts", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = AppPrimary)
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
        Text(text = "Faltan 110 pts para Defensora Mayor (Nv. 4) · 78%", fontSize = 11.sp, color = SecondaryText)
    }

    DetailCard {
        SectionLabel(text = "NUEVOS PRIVILEGIOS DESBLOQUEADOS")
        PrivilegeRow(
            icon = Icons.Outlined.ArrowUpward,
            title = "Reportes destacados",
            text = "Tus notificaciones y reportes de rescate crítico aparecen en la cabecera principal de la app vecinal."
        )
        PrivilegeRow(
            icon = Icons.Outlined.WorkspacePremium,
            title = "Insignia Dorada en Comentarios",
            text = "Tu sello oficial de Guardián resalta todas tus respuestas y consejos en hilos de adopción comunitaria."
        )
        PrivilegeRow(
            icon = Icons.Outlined.Verified,
            title = "Validar Reportes Vecinales",
            text = "Poder de revisión para aprobar o clasificar avisos de extravíos y animales encontrados en tu distrito."
        )
    }

    DetailCard {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = "10 Publicaciones que lo Hicieron Posible", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = DarkText)
                Text(text = "Casos verificados, seguidos y confirmados por tu labor", fontSize = 11.sp, color = SecondaryText)
            }
            AppTag(text = "10 / 10")
        }
        CaseRow(name = "Max", detail = "Cachorro rescatado", status = "Adoptado Feliz")
        CaseRow(name = "Luna", detail = "Esterilización verif.", status = "En Acogida")
        CaseRow(name = "Pancho", detail = "Caso especial salud", status = "Recuperado")
        CaseRow(name = "Oliver & Hermanos", detail = "Camada protegida", status = "Verificado")
        Text(text = "+6 Mascotas · Historial auditado", fontSize = 12.sp, color = SecondaryText)
    }

    PrimaryActionButton(text = "Ver mi Vitrina de Insignias", onClick = onViewBadges, icon = Icons.Outlined.WorkspacePremium)
    PrimaryActionButton(
        text = "Compartir Logro en Comunidad",
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
                text = if (state.isResubmitted) "EN REVISIÓN PRIORITARIA" else "REVISIÓN ÉTICA • PRIORIDAD ALTA",
                backgroundColor = Danger.copy(alpha = 0.12f),
                textColor = Danger
            )
            Spacer(modifier = Modifier.weight(1f))
            Text(text = "ID: luna_2", fontSize = 11.sp, color = SecondaryText)
        }
        Text(
            text = if (state.isResubmitted) "Corrección enviada a revisión" else "Publicación Pausada Temporalmente",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = DarkText
        )
        Text(
            text = if (state.isResubmitted)
                "El comité ético revisará los datos del carnet de Luna. Te avisaremos en cuanto se reactive su visibilidad."
            else
                "La visibilidad pública de Luna fue pausada para proteger la veracidad médica ante adoptantes. Actualiza la evidencia para reactivarla de inmediato.",
            fontSize = 13.sp,
            color = SecondaryText
        )
    }

    DetailCard {
        Text(text = "Dictamen del Moderador Voluntario (Comité Ético)", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = DarkText)
        Text(
            text = "“Las capturas adjuntas del carnet sanitario no permiten validar los sellos de la vacuna polivalente ni la fecha de vigencia antirrábica por reflejos y desenfoque. Por favor, sube una toma cenital clara y legible.”",
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
                Text(text = "2 años • Gatita", fontSize = 12.sp, color = SecondaryText)
                Text(text = "Refugio Colitas Felices • Fila de espera: 4 postulantes", fontSize = 11.sp, color = SecondaryText)
            }
        }
        AppTag(
            text = if (state.isResubmitted) "En revisión" else "Pausada por Moderación",
            backgroundColor = Danger.copy(alpha = 0.12f),
            textColor = Danger
        )
    }

    // Paso 1: evidencia sanitaria (la foto depende de la cámara, que no se integra todavía)
    DetailCard {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = "Evidencia Sanitaria Requerida", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = DarkText)
                Text(text = "Carnet Antirrábico y Séxtuple / Polivalente", fontSize = 12.sp, color = SecondaryText)
            }
            AppTag(text = "Paso 1 de 2")
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconBadge(icon = Icons.Outlined.ReportProblem, tint = Danger, size = 36)
            Spacer(modifier = Modifier.width(10.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = "Rechazada · Ilegible / Reflejo", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = Danger)
                Text(text = "Foto actual no admitida", fontSize = 11.sp, color = SecondaryText)
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
                    Text(text = "Subir foto nítida", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = DarkText)
                    Text(text = "Buena luz, sin flash directo, sellos legibles", fontSize = 11.sp, color = SecondaryText)
                }
                Text(text = "Examinar", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = AppPrimary)
            }
        }
        Text(text = "Toma recomendada: sin reflejos · carnet completo · firma y sello", fontSize = 11.sp, color = SecondaryText)
    }

    // Paso 2: validación de lote y fechas
    DetailCard {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = "Validación de Lote y Fechas", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = DarkText)
                Text(text = "Confirma los datos para agilizar el desbloqueo prioritario", fontSize = 12.sp, color = SecondaryText)
            }
            AppTag(text = "Paso 2 de 2")
        }

        OutlinedTextField(
            value = state.lotNumber,
            onValueChange = onLotNumberChange,
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Número de Lote (Polivalente / Séxtuple) *") },
            placeholder = { Text("POL-8842-XB") },
            singleLine = true,
            enabled = !state.isResubmitted,
            isError = state.lotNumberError != null,
            supportingText = state.lotNumberError?.let { { Text(it) } }
        )

        Text(text = "Fecha Aplicación *", fontSize = 12.sp, color = SecondaryText)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            DateField(value = state.applicationDay, onValueChange = onDayChange, placeholder = "DD", enabled = !state.isResubmitted, modifier = Modifier.weight(1f))
            DateField(value = state.applicationMonth, onValueChange = onMonthChange, placeholder = "MM", enabled = !state.isResubmitted, modifier = Modifier.weight(1f))
            DateField(value = state.applicationYear, onValueChange = onYearChange, placeholder = "AAAA", enabled = !state.isResubmitted, modifier = Modifier.weight(1.4f))
        }
        state.dateError?.let { Text(text = it, color = AppRed, fontSize = 12.sp) }

        OutlinedTextField(
            value = state.rabiesLot,
            onValueChange = onRabiesLotChange,
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Lote Antirrábica *") },
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
                text = "Confirmo bajo código ético de rescate que la cartilla adjunta pertenece al ejemplar indicado y ha sido sellada por un profesional veterinario colegiado.",
                fontSize = 12.sp,
                color = DarkText
            )
        }
    }

    PrimaryActionButton(
        text = when {
            state.isResubmitted -> "Enviado a Revisión Prioritaria"
            state.isResubmitting -> "Enviando..."
            else -> "Reenviar a Revisión Prioritaria"
        },
        onClick = onResubmit,
        icon = Icons.AutoMirrored.Outlined.Send,
        enabled = state.isCorrectionFormValid && !state.isResubmitting && !state.isResubmitted
    )
    TextButton(onClick = onGuidelinesClick, modifier = Modifier.fillMaxWidth()) {
        Icon(Icons.AutoMirrored.Outlined.HelpOutline, contentDescription = null, tint = AppPrimary, modifier = Modifier.size(16.dp))
        Spacer(modifier = Modifier.width(6.dp))
        Text(text = "Consultar pautas con el moderador voluntario", color = AppPrimary, fontSize = 13.sp)
    }
    DetailCard(color = SoftBlue) {
        Text(
            text = "Cola Prioritaria Activa: las correcciones de perfil pausado se auditan en un promedio de 45 minutos en horario diurno.",
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
        AppTag(text = "NOTIFICACIÓN DE RESCATE", backgroundColor = AppPrimary.copy(alpha = 0.15f), textColor = AppPrimary)
        Text(text = "Nuevo comentario relevante en el caso de Max", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = DarkText)
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
                SectionLabel(text = "CASO")
                Text(text = "Max", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = DarkText)
                Text(text = "Golden Mestizo • 3 meses • Buscando alimento y cuidados", fontSize = 11.sp, color = SecondaryText)
            }
            TextButton(onClick = onViewCase) {
                Text(text = "Ver caso", color = AppPrimary, fontWeight = FontWeight.SemiBold)
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
                Text(text = "Médico Veterinario Verificado", fontSize = 11.sp, color = AppPrimary)
                Text(text = "Hace 18 minutos • Clínica Vet Usaquén", fontSize = 11.sp, color = SecondaryText)
            }
        }
        Text(
            text = "“Puedo aportar un bulto de 15kg de alimento medicado y desparasitación gratuita en mi clínica de Usaquén para que Max continúe estable. Cuenten conmigo para evaluarlo sin costo.”",
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
                    contentDescription = "Agradecer",
                    tint = if (state.isCommentLiked) AppPrimary else SecondaryText,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(text = "${state.commentLikes}", fontSize = 13.sp, color = SecondaryText)
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = "${state.replies.size} respuestas", fontSize = 13.sp, color = SecondaryText, modifier = Modifier.weight(1f))
            AppTag(text = "Donación Verificada", backgroundColor = Green.copy(alpha = 0.12f), textColor = Green)
        }
        PrimaryActionButton(
            text = if (state.isDonationAccepted) "Donación aceptada ✓" else "Aceptar donación y coordinar entrega",
            onClick = onAcceptDonation,
            icon = Icons.Outlined.CheckCircle,
            enabled = !state.isDonationAccepted
        )
        PrimaryActionButton(
            text = "WhatsApp seguro",
            onClick = onWhatsAppClick,
            icon = Icons.Outlined.ChatBubbleOutline,
            container = LightBlueBg,
            content = DarkText
        )
    }

    // Respuestas en el caso
    DetailCard {
        Row(verticalAlignment = Alignment.CenterVertically) {
            SectionLabel(text = "RESPUESTAS EN ESTE CASO")
            Spacer(modifier = Modifier.weight(1f))
            if (state.replies.size > 1) {
                TextButton(onClick = onToggleAllReplies) {
                    Text(text = if (state.showAllReplies) "Ver menos" else "Ver todas", color = AppPrimary, fontSize = 12.sp)
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
        Text(text = "Tu respuesta como gestora de Max", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = DarkText)
        Text(text = "Sugerencias rápidas", fontSize = 11.sp, color = AppPrimary)
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
                placeholder = { Text("Responder público...") }
            )
            IconButton(onClick = onSendReply) {
                Icon(Icons.AutoMirrored.Outlined.Send, contentDescription = "Enviar respuesta", tint = AppPrimary)
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
            Text(text = "Reconocimiento Comunitario", fontSize = 12.sp, color = Gold, fontWeight = FontWeight.SemiBold)
            IconBadge(icon = Icons.Outlined.WorkspacePremium, tint = Gold, size = 72)
            Text(text = "Primera Huella", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = DarkText)
            Text(text = "Concedida en Enero 2024", fontSize = 12.sp, color = SecondaryText)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                AppTag(text = "Respaldo veterinario oficial")
                AppTag(text = "25 pts", backgroundColor = Gold.copy(alpha = 0.15f), textColor = Gold)
                AppTag(text = "VERIFICADO", backgroundColor = Green.copy(alpha = 0.12f), textColor = Green)
            }
            Text(
                text = "Mención honorífica por tu destacado compromiso con el bienestar animal y la adopción responsable en la comunidad.",
                fontSize = 13.sp,
                color = SecondaryText
            )
        }
    }

    DetailCard {
        SectionLabel(text = "CASO QUE ORIGINÓ EL LOGRO")
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
                Text(text = "Caso #1 · Max", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = DarkText)
                Text(text = "Cachorro mestizo • Clínica Veterinaria San Roque", fontSize = 11.sp, color = SecondaryText)
            }
            AppTag(text = "Adoptado", backgroundColor = Green.copy(alpha = 0.12f), textColor = Green)
        }
        Text(
            text = "\"Gracias al reporte oportuno y la confirmación de vacunas y desparasitación, Max encontró un hogar definitivo en menos de 3 semanas. Tu dedicación transformó su vida.\"",
            fontSize = 12.sp,
            color = SecondaryText
        )
    }

    DetailCard {
        Row {
            Text(text = "Próximas insignias al alcance", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = DarkText, modifier = Modifier.weight(1f))
            AppTag(text = "3 disponibles")
        }
        NextBadgeRow(title = "Hogar Temporal", points = "+50 pts", text = "Brinda refugio de paso a un peludo en recuperación")
        NextBadgeRow(title = "Rescatista Constante", points = "+75 pts", text = "3 casos validados por entidades aliadas")
        NextBadgeRow(title = "Héroe del Mes", points = "+120 pts", text = "Lidera adopciones comunitarias en tu localidad")
    }

    PrimaryActionButton(text = "Ver Perfil Completo y Logros", onClick = onViewProfile, icon = Icons.Outlined.Person)
    PrimaryActionButton(
        text = "Volver a Notificaciones",
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
