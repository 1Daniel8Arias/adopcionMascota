package co.edu.adopcionmascota.features.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowRightAlt
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.VolunteerActivism
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import co.edu.adopcionmascota.R
import co.edu.adopcionmascota.core.component.IconoText
import co.edu.adopcionmascota.core.component.IconoTextField
import co.edu.adopcionmascota.core.util.RequestResult

@Preview(showBackground = true)
@Composable
fun LoginScreen(
    viewModel: LoginViewModel = viewModel()
) {


    val state by viewModel.uiState.collectAsState()

    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Imagen superior
        Image(
            modifier = Modifier
                .size(210.dp)
                .align(Alignment.CenterHorizontally)
                .padding(top = 30.dp),
            painter = painterResource(R.drawable.icono),
            contentDescription = "Welcome Image"
        )

        // Textos de bienvenida
        Text(
            text = "Bienvenido de vuelta",
            fontSize = 28.sp,
            fontWeight = FontWeight.ExtraBold,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )

        Text(
            text = "Conectando hogares, rescatando vidas",
            fontSize = 15.sp,
            color = Color(0xFF4A5568),
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )

        // Tarjeta informativa
        IconoText(
            texto = "Adopta, reporta y ayuda a mascotas comunitarias.",
            icono = Icons.Outlined.VolunteerActivism
        )
        Spacer(modifier = Modifier.height(16.dp))

        // Campo de correo
        IconoTextField(
            titulo = "Correo electrónico",
            value =state.email,
            onValueChange = viewModel::onEmailChange,
            placeholderText = "tu.correo@ejemplo.com",
            icono = Icons.Outlined.Email,
            isError = state.emailError != null,
            mensajeError = state.emailError

        )

        // Campo de contraseña
        IconoTextField(
            titulo = "Contraseña",
            value = state.password,
            onValueChange = viewModel::onPasswordChange,
            placeholderText = "••••••••",
            icono = Icons.Outlined.Lock,
            visualTransformation = PasswordVisualTransformation(),
            isError = state.passwordError != null,
            mensajeError = state.passwordError
        )

        // Enlace "¿Olvidaste tu contraseña?"
        TextButton(
            onClick = { /* Navegación */ },
            modifier = Modifier.align(Alignment.End)
        ) {
            Text(
                text = "¿Olvidaste tu contraseña?",
                color = Color(0xFF4A5CDE),
                fontSize = 14.sp
            )
        }

        ButtonLogin(
            onClick = viewModel::login,
            isFormValid = state.isFormValid,
            loginResult = state.loginResult
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "¿No tienes una cuenta aun?",
                fontSize = 14.sp,
                modifier = Modifier.align(Alignment.CenterVertically)
            )
            TextButton(
                onClick = {}
            ) {
                Text(
                    text = "Registrarme",
                            color = Color(0xFF4A5CDE),
                    fontSize = 14.sp,

                )
            }
        }


    }
}

@Composable
fun ButtonLogin(
    onClick: () -> Unit,
    isFormValid: Boolean,
    loginResult: RequestResult?
) {
    val isLoading = loginResult is RequestResult.Loading

    Button(
        onClick = onClick,
        // Se desactiva si el formulario no es válido O si está cargando
        enabled = isFormValid && !isLoading,
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp),
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFF4A5CDE),
            contentColor = Color.White,
            disabledContainerColor = Color(0xFF9EA8F0), // Color azul tenue cuando está deshabilitado
            disabledContentColor = Color.White
        )
    ) {
        if (isLoading) {
            Text(
                text = "Iniciando sesión...",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        } else {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Iniciar Sesión",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.width(8.dp))
                Icon(
                    imageVector = Icons.Outlined.ArrowRightAlt,
                    contentDescription = null
                )
            }
        }
    }
}