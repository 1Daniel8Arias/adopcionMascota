package co.edu.adopcionmascota.features.password

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape

import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons

import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.automirrored.outlined.ArrowForward
import androidx.compose.material.icons.automirrored.outlined.HelpOutline
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material.icons.outlined.AlternateEmail
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.MarkEmailRead
import androidx.compose.material.icons.outlined.SupportAgent
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import co.edu.adopcionmascota.core.component.IconoText
import co.edu.adopcionmascota.core.component.IconoTextField

@Preview(showBackground = true)
@Composable
fun PasswordScreen() {
    var email by remember { mutableStateOf("camila.morales@ejemplo.com") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8FAFC))
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {



        // ------------------------------------------------------------------
        // 3. ILUSTRACIÓN CONCÉNTRICA
        // ------------------------------------------------------------------
        Box(
            modifier = Modifier
                .size(130.dp)
                .align(Alignment.CenterHorizontally),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .background(Color(0xFFEBF2FF), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(85.dp)
                        .background(Color(0xFFD6E4FF), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Outlined.MarkEmailRead,
                        contentDescription = null,
                        modifier = Modifier.size(40.dp),
                        tint = Color(0xFF3B52E1)
                    )
                }
            }

            Box(
                modifier = Modifier
                    .size(40.dp)
                    .align(Alignment.BottomEnd)
                    .background(Color(0xFF4C6FFF), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Pets,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                    tint = Color.White
                )
            }
        }

        // ------------------------------------------------------------------
        // 4. TÍTULO Y TEXTO EXPLICATIVO
        // ------------------------------------------------------------------
        Text(
            text = "¿Olvidaste tu contraseña?",
            fontSize = 22.sp,
            fontWeight = FontWeight.ExtraBold,
            color = Color(0xFF0F172A),
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )

        Text(
            text = "No te preocupes. Ingresa el correo electrónico asociado a tu cuenta de Patitas Conectadas y te enviaremos un enlace seguro para restablecer tu acceso.",
            fontSize = 13.sp,
            color = Color(0xFF64748B),
            textAlign = TextAlign.Center,
            lineHeight = 18.sp,
            modifier = Modifier.fillMaxWidth()
        )

        // ------------------------------------------------------------------
        // 5. CAMPO DE ENTRADA (Usando IconoTextField)
        // ------------------------------------------------------------------
        Column(modifier = Modifier.fillMaxWidth()) {
            IconoTextField(
                titulo = "Correo electrónico registrado",
                value = email,
                onValueChange = { email = it },
                placeholderText = "camila.morales@ejemplo.com",
                icono = Icons.Outlined.AlternateEmail,
                modifier = Modifier.fillMaxWidth()
            )

            Row(
                modifier = Modifier.padding(top = 6.dp, start = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Icon(
                    imageVector = Icons.Outlined.Info,
                    contentDescription = null,
                    tint = Color(0xFF64748B),
                    modifier = Modifier.size(14.dp)
                )
                Text(
                    text = "Asegúrate de que coincida con tu correo registrado en Bogotá.",
                    fontSize = 11.sp,
                    color = Color(0xFF64748B)
                )
            }
        }

        // ------------------------------------------------------------------
        // 6. BOTÓN PRINCIPAL
        // ------------------------------------------------------------------
        Button(
            onClick = { },
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            shape = CircleShape,
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF000000),
                contentColor = Color.White
            )
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Enviar enlace de recuperación",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.width(8.dp))
                Icon(
                    imageVector = Icons.AutoMirrored.Outlined.ArrowForward,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
            }
        }

        // ------------------------------------------------------------------
        // 7. TARJETAS INFORMATIVAS (Usando IconoText)
        // ------------------------------------------------------------------
        IconoText(
            texto = "¿No recibes el mensaje?\nRevisa tu carpeta de spam o correo no deseado. El enlace es válido durante 30 minutos por razones de seguridad ciudadana y protección animal.",
            icono = Icons.AutoMirrored.Outlined.HelpOutline
        )

        IconoText(
            texto = "Soporte Comunitario Bogotá\n¿Perdiste acceso total a tu correo o cambiaste de número? Nuestro equipo de bienestar animal en Bogotá te acompaña en el proceso de validación.",
            icono = Icons.Outlined.SupportAgent
        )

        // ------------------------------------------------------------------
        // 8. BOTÓN SECUNDARIO Y NAVEGACIÓN
        // ------------------------------------------------------------------
        Button(
            onClick = { },
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            shape = CircleShape,
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFFE2EDFF),
                contentColor = Color(0xFF0F172A)
            )
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Volver a Iniciar Sesión",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "¿Aún no tienes cuenta? ",
                fontSize = 13.sp,
                color = Color(0xFF64748B)
            )
            Text(
                text = "Regístrate aquí",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF3B52E1),
                modifier = Modifier.clickable { }
            )
        }
    }
}