package co.edu.adopcionmascota.features.register


import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import co.edu.adopcionmascota.core.component.ConfirmAlertDialog
import co.edu.adopcionmascota.core.util.RequestResult
import co.edu.adopcionmascota.features.register.component.AnimalLevelCard

//Imports component register
import co.edu.adopcionmascota.features.register.component.RegisterTopBar
import co.edu.adopcionmascota.features.register.component.RegisterTextField
import co.edu.adopcionmascota.features.register.component.JoinPackCard
import co.edu.adopcionmascota.features.register.component.LocationDropdown
import co.edu.adopcionmascota.features.register.component.NotificationCheckbox
import co.edu.adopcionmascota.features.register.component.PasswordField
import co.edu.adopcionmascota.features.register.component.PasswordSecurityIndicator
import co.edu.adopcionmascota.features.register.component.ProfilePhotoSection
import co.edu.adopcionmascota.features.register.component.RegisterFieldLabel
import co.edu.adopcionmascota.features.register.component.TermsCheckbox

@Composable
fun RegisterScreen(
    onNavigateToBack: () -> Unit,
    viewModel: RegisterViewModel = viewModel()
) {


    val snackbarHostState = remember { SnackbarHostState() }
    val state by viewModel.uiState.collectAsState()
    val scrollState = rememberScrollState()

    BackHandler(enabled = !state.showExitDialog) {
        viewModel.onBackClick()
    }

    LaunchedEffect(state.registerResult) {
        when (val result = state.registerResult) {

            is RequestResult.Success -> {
                snackbarHostState.showSnackbar(result.message)
                viewModel.resetRegisterResult()
            }

            is RequestResult.Failure -> {
                snackbarHostState.showSnackbar(result.erroMessage)
                viewModel.resetRegisterResult()
            }

            is RequestResult.Loading,
            null -> Unit
        }
    }

    Scaffold(
        containerColor = Color(0xFFF7F9FF),

        snackbarHost = {
            SnackbarHost(snackbarHostState) { data ->
                Snackbar(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = Color.White
                ) {
                    Text(data.visuals.message)
                }
            }
        }
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(scrollState)
        ) {

            // ---------------------------------------------------------
            // BARRA SUPERIOR
            // ---------------------------------------------------------

            RegisterTopBar(
                onNavigateToBack = viewModel::onBackClick
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 18.dp)
            ) {

                Spacer(modifier = Modifier.height(14.dp))

                // -----------------------------------------------------
                // TARJETA ÚNETE A LA MANADA
                // -----------------------------------------------------

                JoinPackCard()

                Spacer(modifier = Modifier.height(20.dp))

                // -----------------------------------------------------
                // FOTO
                // -----------------------------------------------------

                ProfilePhotoSection()

                Spacer(modifier = Modifier.height(22.dp))

                // -----------------------------------------------------
                // NIVEL
                // -----------------------------------------------------

                AnimalLevelCard()

                Spacer(modifier = Modifier.height(26.dp))

                // -----------------------------------------------------
                // NOMBRE
                // -----------------------------------------------------

                RegisterFieldLabel("Nombre y apellido completo")

                RegisterTextField(
                    value = state.name,
                    onValueChange = viewModel::onNameChange,
                    placeholder = "Ej. Camila Morales Restrepo",
                    icon = Icons.Default.Person,
                    keyboardType = KeyboardType.Text,
                    error = state.nameError
                )

                Spacer(modifier = Modifier.height(18.dp))

                // -----------------------------------------------------
                // CORREO
                // -----------------------------------------------------

                RegisterFieldLabel("Correo electrónico")

                RegisterTextField(
                    value = state.email,
                    onValueChange = viewModel::onEmailChange,
                    placeholder = "nombre@ejemplo.com",
                    icon = Icons.Default.Email,
                    keyboardType = KeyboardType.Email,
                    error = state.emailError
                )

                Spacer(modifier = Modifier.height(18.dp))

                // -----------------------------------------------------
                // TELÉFONO
                // -----------------------------------------------------

                RegisterFieldLabel("Teléfono / WhatsApp de contacto")

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    // Prefijo de Colombia
                    Surface(
                        modifier = Modifier
                            .height(56.dp)
                            .width(95.dp),
                        shape = RoundedCornerShape(14.dp),
                        color = Color(0xFFEAF1FF)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {

                            Text(
                                text = "🇨🇴",
                                style = MaterialTheme.typography.titleMedium
                            )

                            Spacer(modifier = Modifier.width(6.dp))

                            Text(
                                text = "+57",
                                fontWeight = FontWeight.Medium,
                                color = Color(0xFF222222)
                            )
                        }
                    }


                    RegisterTextField(
                        value = state.phone,
                        onValueChange = viewModel::onPhoneChange,
                        placeholder = "310 123 4567",
                        icon = Icons.Default.Phone,
                        keyboardType = KeyboardType.Phone,
                        error = state.phoneError
                    )
                }

                Text(
                    text = "Vital para coordinar rescates y entregas responsables",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF303030),
                    modifier = Modifier.padding(
                        start = 4.dp,
                        top = 7.dp
                    )
                )

                Spacer(modifier = Modifier.height(18.dp))

                // -----------------------------------------------------
                // LOCALIDAD
                // -----------------------------------------------------

                RegisterFieldLabel("Localidad en Bogotá")

                LocationDropdown(
                    value = state.city,
                    onValueChange = viewModel::onCityChange,
                    cities = viewModel.cities
                )

                Spacer(modifier = Modifier.height(18.dp))

                // -----------------------------------------------------
                // Direccion
                // -----------------------------------------------------
                RegisterFieldLabel("Direccion")

                RegisterTextField(
                    value = state.address,
                    onValueChange = viewModel::onAddressChange,
                    placeholder = "Ej Carrera-Manzana-Calle",
                    icon = Icons.Default.AccountBalance,
                    keyboardType = KeyboardType.Text,
                    error = state.addressError
                )
                Spacer(modifier = Modifier.height(18.dp))


                // -----------------------------------------------------
                // CONTRASEÑA
                // -----------------------------------------------------

                RegisterFieldLabel("Contraseña")

                PasswordField(
                    value = state.password,
                    onValueChange = viewModel::onPasswordChange,
                    placeholder = "Mínimo 8 caracteres",
                    error = state.passwordError
                )

                PasswordSecurityIndicator()

                Spacer(modifier = Modifier.height(18.dp))

                // -----------------------------------------------------
                // CONFIRMAR CONTRASEÑA
                // -----------------------------------------------------

                RegisterFieldLabel("Confirmar contraseña")

                PasswordField(
                    value = state.confirmPassword,
                    onValueChange = viewModel::onConfirmPasswordChange,
                    placeholder = "Repite tu contraseña",
                    error = state.confirmPasswordError
                )

                Spacer(modifier = Modifier.height(20.dp))

                // -----------------------------------------------------
                // TÉRMINOS
                // -----------------------------------------------------

                TermsCheckbox(
                    checked = state.acceptTerms,
                    onCheckedChange = viewModel::onAcceptTermsChange
                )

                Spacer(modifier = Modifier.height(10.dp))

                // -----------------------------------------------------
                // NOTIFICACIONES
                // -----------------------------------------------------

                NotificationCheckbox(
                    checked = state.receiveNotifications,
                    onCheckedChange = viewModel::onReceiveNotificationsChange
                )

                Spacer(modifier = Modifier.height(26.dp))

                // -----------------------------------------------------
                // BOTÓN CREAR CUENTA
                // -----------------------------------------------------

                Button(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(30.dp),
                    colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                        containerColor = Color.Black,
                        contentColor = Color.White
                    ),
                    enabled = state.isFormValid &&
                            state.registerResult !is RequestResult.Loading,
                    onClick = viewModel::onRegisterClick,

                ) {

                    Icon(
                        imageVector = Icons.Default.Favorite,
                        contentDescription = null
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    if (state.registerResult is RequestResult.Loading) {
                        Text(text = "Creando cuenta...")

                    } else {
                        Text(text = "Crear mi Cuenta")
                    }
                    Spacer(modifier = Modifier.height(26.dp))
                }


                    // -----------------------------------------------------
                    // LOGIN
                    // -----------------------------------------------------

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        Text(
                            text = "¿Ya tienes una cuenta?",
                            color = Color(0xFF333333)
                        )

                        TextButton(
                            onClick = { }
                        ) {
                            Text(
                                text = "Iniciar Sesión",
                                color = Color(0xFF172BE5),
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(30.dp))

            }
        }

        // -------------------------------------------------------------
        // DIÁLOGO SALIR
        // -------------------------------------------------------------

        if (state.showExitDialog) {
            ConfirmAlertDialog(
                title = "¿Está seguro de salir?",
                message = "Si sale ahora, se perderán los datos que ha ingresado.",
                onConfirm = {
                    viewModel.onConfirmExit() // Limpiar el formulario y ocultar el diálogo
                    onNavigateToBack() // Navegar hacia atrás
                },
                onDismiss = viewModel::onDismissExitDialog,
                confirmText = "Salir"
            )
        }

        if (state.showConfirmDialog) {
            ConfirmAlertDialog(
                title = "¿Está seguro de enviar los datos?",
                message = "Está a punto de crear una cuenta con el email ${state.email}.",
                onConfirm = viewModel::onConfirmRegister,
                onDismiss = viewModel::onDismissConfirmDialog
            )
        }
    }
}


