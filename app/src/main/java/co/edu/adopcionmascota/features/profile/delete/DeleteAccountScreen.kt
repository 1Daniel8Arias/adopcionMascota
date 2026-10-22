package co.edu.adopcionmascota.features.profile.delete

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.DeleteForever
import androidx.compose.material.icons.outlined.ErrorOutline
import androidx.compose.material.icons.outlined.Lightbulb
import androidx.compose.material.icons.outlined.MilitaryTech
import androidx.compose.material.icons.outlined.Pets
import androidx.compose.material.icons.outlined.Shield
import androidx.compose.material.icons.outlined.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import co.edu.adopcionmascota.core.theme.AppPrimary
import co.edu.adopcionmascota.core.theme.DarkText
import co.edu.adopcionmascota.core.theme.SecondaryText
import co.edu.adopcionmascota.core.util.RequestResult

private val DangerRed = Color(0xFFBA1A1A)
private val DangerBg = Color(0xFFFFDAD6)
private val SoftBlue = Color(0xFFEDF4FF)

// Pantalla "Eliminar cuenta": hoja de confirmación sobre un fondo oscuro
@Composable
fun DeleteAccountScreen(
    onCancel: () -> Unit,           // "Cancelar y conservar mi cuenta" o tocar fuera de la hoja
    onAccountDeleted: () -> Unit,   // Ir a "Cuenta eliminada"
    viewModel: DeleteAccountViewModel = viewModel()
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val state by viewModel.uiState.collectAsState()

    LaunchedEffect(state.deleteResult) {
        when (val result = state.deleteResult) {
            is RequestResult.Success -> {
                viewModel.resetDeleteResult()
                onAccountDeleted()
            }

            is RequestResult.Failure -> {
                snackbarHostState.showSnackbar(result.errorMessage)
                viewModel.resetDeleteResult()
            }

            is RequestResult.Loading, null -> Unit
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.6f))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onCancel
            )
            .statusBarsPadding()
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                // Evita que un toque dentro de la hoja la cierre
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = {}
                ),
            shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
            color = Color.White
        ) {
            Column(
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .navigationBarsPadding()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                DeleteHeader()
                ConsequencesCard()
                BreakSuggestionCard()
                ConfirmationCheck(
                    checked = state.isConfirmed,
                    onCheckedChange = viewModel::onConfirmedChange
                )
                DeleteActions(
                    canDelete = state.canDelete,
                    isDeleting = state.deleteResult is RequestResult.Loading,
                    onDeleteClick = viewModel::deleteAccount,
                    onCancelClick = onCancel
                )
            }
        }

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(Alignment.TopCenter)
        )
    }
}

@Composable
private fun DeleteHeader() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Box(
            modifier = Modifier
                .size(64.dp)
                .background(DangerBg, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Outlined.Warning,
                contentDescription = null,
                tint = DangerRed,
                modifier = Modifier.size(30.dp)
            )
        }
        Text(
            text = "¿Eliminar tu cuenta de\nPatitas Conectadas?",
            fontSize = 24.sp,
            lineHeight = 32.sp,
            fontWeight = FontWeight.Bold,
            color = DarkText,
            textAlign = TextAlign.Center
        )
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Outlined.ErrorOutline,
                contentDescription = null,
                tint = DangerRed,
                modifier = Modifier.size(14.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = "Esta acción es permanente e irreversible",
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = DangerRed
            )
        }
    }
}

@Composable
private fun ConsequencesCard() {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = SoftBlue
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "CONSECUENCIAS DIRECTAS PARA TU PERFIL",
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = SecondaryText
            )
            ConsequenceItem(icon = Icons.Outlined.Shield) {
                Text(
                    text = buildAnnotatedString {
                        append("Perderás tu ")
                        withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                            append("Reputación Comunitaria (Nivel 2: Protector, 340 pts)")
                        }
                        append(" acumulada desde enero 2024.")
                    },
                    fontSize = 12.sp,
                    color = SecondaryText
                )
            }
            ConsequenceItem(icon = Icons.Outlined.Pets) {
                Text(
                    text = "Tus 2 publicaciones activas y el historial de rescates se desvincularán definitivamente.",
                    fontSize = 12.sp,
                    color = SecondaryText
                )
            }
            ConsequenceItem(icon = Icons.Outlined.MilitaryTech) {
                Text(
                    text = "Perderás todas tus insignias obtenidas ('Primera Huella' y 'Hogar Temporal').",
                    fontSize = 12.sp,
                    color = SecondaryText
                )
            }
        }
    }
}

@Composable
private fun ConsequenceItem(icon: ImageVector, content: @Composable () -> Unit) {
    Row(verticalAlignment = Alignment.Top) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = DangerRed,
            modifier = Modifier.size(18.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Box(modifier = Modifier.weight(1f)) { content() }
    }
}

@Composable
private fun BreakSuggestionCard() {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = Color(0xFF5565F2).copy(alpha = 0.1f)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.Top
        ) {
            Icon(
                imageVector = Icons.Outlined.Lightbulb,
                contentDescription = null,
                tint = AppPrimary,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = "¿Solo necesitas un descanso?",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = DarkText
                )
                Text(
                    text = "Puedes pausar tu cuenta o silenciar notificaciones temporales sin perder tus logros ni el historial de rescate comunitario.",
                    fontSize = 12.sp,
                    color = SecondaryText
                )
            }
        }
    }
}

@Composable
private fun ConfirmationCheck(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Surface(
        onClick = { onCheckedChange(!checked) },
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = SoftBlue.copy(alpha = 0.7f)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = checked,
                onCheckedChange = onCheckedChange,
                colors = CheckboxDefaults.colors(checkedColor = Color(0xFF172DF2))
            )
            Text(
                text = "Comprendo que perderé todos mis datos, historial de rescate, insignias y reputación comunitaria acumulada de manera irreversible.",
                fontSize = 12.sp,
                color = DarkText
            )
        }
    }
}

@Composable
private fun DeleteActions(
    canDelete: Boolean,
    isDeleting: Boolean,
    onDeleteClick: () -> Unit,
    onCancelClick: () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        // Solo se habilita cuando se marca la casilla de confirmación
        Button(
            onClick = onDeleteClick,
            enabled = canDelete,
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            shape = CircleShape,
            colors = ButtonDefaults.buttonColors(
                containerColor = DangerBg,
                contentColor = DangerRed,
                disabledContainerColor = DangerBg.copy(alpha = 0.5f),
                disabledContentColor = DangerRed.copy(alpha = 0.5f)
            )
        ) {
            Icon(
                imageVector = Icons.Outlined.DeleteForever,
                contentDescription = null,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = if (isDeleting) "Eliminando..." else "Eliminar definitivamente",
                fontWeight = FontWeight.SemiBold
            )
        }

        Button(
            onClick = onCancelClick,
            enabled = !isDeleting,
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            shape = CircleShape,
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF3A49D8),
                contentColor = Color.White
            )
        ) {
            Icon(
                imageVector = Icons.Outlined.Close,
                contentDescription = null,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = "Cancelar y conservar mi cuenta", fontWeight = FontWeight.SemiBold)
        }
    }
}
