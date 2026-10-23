package co.edu.adopcionmascota

import co.edu.adopcionmascota.features.login.LoginViewModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Test

// Nota: el formato del email usa android.util.Patterns, que no existe en las pruebas JVM;
// por eso aquí solo se prueba el email vacío.
class LoginViewModelTest {

    @Test
    fun emailVacio_muestraErrorObligatorio() {
        val viewModel = LoginViewModel()
        viewModel.onEmailChange("")
        assertEquals("El email es obligatorio", viewModel.uiState.value.emailError)
    }

    @Test
    fun contrasenaVacia_muestraErrorObligatorio() {
        val viewModel = LoginViewModel()
        viewModel.onPasswordChange("")
        assertEquals("La contraseña es obligatoria", viewModel.uiState.value.passwordError)
    }

    @Test
    fun contrasenaCorta_muestraErrorDeLongitud() {
        val viewModel = LoginViewModel()
        viewModel.onPasswordChange("123")
        assertEquals("La contraseña debe tener al menos 6 caracteres", viewModel.uiState.value.passwordError)
    }

    @Test
    fun contrasenaValida_noTieneError() {
        val viewModel = LoginViewModel()
        viewModel.onPasswordChange("123456")
        assertNull(viewModel.uiState.value.passwordError)
    }

    @Test
    fun formularioIncompleto_noEsValido() {
        val viewModel = LoginViewModel()
        viewModel.onPasswordChange("123456")
        assertFalse(viewModel.uiState.value.isFormValid)
    }

    @Test
    fun resetForm_limpiaElEstado() {
        val viewModel = LoginViewModel()
        viewModel.onPasswordChange("123")
        viewModel.resetForm()
        assertEquals("", viewModel.uiState.value.password)
        assertNull(viewModel.uiState.value.passwordError)
    }
}
