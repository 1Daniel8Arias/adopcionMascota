package co.edu.adopcionmascota.features.register

import androidx.activity.compose.BackHandler
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.NotificationsActive
import androidx.compose.material.icons.outlined.Pets
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import co.edu.adopcionmascota.core.component.AppTag
import co.edu.adopcionmascota.core.theme.AppPrimary
import co.edu.adopcionmascota.core.theme.Background
import co.edu.adopcionmascota.core.theme.DarkText
import co.edu.adopcionmascota.core.theme.LightBlueBg
import co.edu.adopcionmascota.core.theme.SecondaryText

// Pantalla "comfirmacion" del Figma: bienvenida después de crear la cuenta
@Composable
fun RegisterSuccessScreen(
    name: String,
    onContinueToFeed: () -> Unit,           // "Continuar al Feed Principal"
    onConfigureAlerts: () -> Unit,          // "Configurar preferencias de alerta"
    viewModel: RegisterSuccessViewModel = viewModel(key = "register_success_$name") {
        RegisterSuccessViewModel(name)
    }
) {
    val state by viewModel.uiState.collectAsState()

    // La cuenta ya está creada: "atrás" no vuelve al formulario
    BackHandler { onContinueToFeed() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
            .statusBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Spacer(modifier = Modifier.height(24.dp))

        Box(
            modifier = Modifier
                .size(110.dp)
                .clip(CircleShape)
                .background(LightBlueBg),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Outlined.CheckCircle,
                contentDescription = null,
                tint = AppPrimary,
                modifier = Modifier.size(56.dp)
            )
        }

        Text(
            text = "¡Bienvenid@ a la Manada,\n${state.firstName}!",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = DarkText,
            textAlign = TextAlign.Center
        )

        Text(
            text = "Tu cuenta de Patitas Conectadas ha sido configurada correctamente. Te hemos asignado el Nivel 1: Amigo Animal para comenzar a adoptar y reportar de forma segura.",
            fontSize = 14.sp,
            color = SecondaryText,
            textAlign = TextAlign.Center
        )

        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            color = Color.White
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(AppPrimary),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Outlined.Pets, contentDescription = null, tint = Color.White)
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(text = state.name, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = DarkText)
                    AppTag(text = state.levelName)
                    Text(text = state.enabledFeatures, fontSize = 12.sp, color = SecondaryText)
                }
                Text(text = "${state.points} pts", fontWeight = FontWeight.Bold, color = AppPrimary)
            }
        }

        Button(
            onClick = onContinueToFeed,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = CircleShape,
            colors = ButtonDefaults.buttonColors(containerColor = Color.Black)
        ) {
            Text(text = "Continuar al Feed Principal", fontWeight = FontWeight.SemiBold)
        }

        TextButton(onClick = onConfigureAlerts) {
            Icon(Icons.Outlined.NotificationsActive, contentDescription = null, tint = AppPrimary, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text(text = "Configurar preferencias de alerta", color = AppPrimary)
        }
    }
}
