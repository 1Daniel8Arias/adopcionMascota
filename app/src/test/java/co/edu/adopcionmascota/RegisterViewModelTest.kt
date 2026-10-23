package co.edu.adopcionmascota

import co.edu.adopcionmascota.features.register.RegisterViewModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class RegisterViewModelTest {

    @Test
    fun nombreCorto_muestraError() {
        val viewModel = RegisterViewModel()
        viewModel.onNameChange("Al")
        assertEquals("El nombre debe tener al menos 3 caracteres", viewModel.uiState.value.nameError)
    }

    @Test
    fun nombreValido_noTieneError() {
        val viewModel = RegisterViewModel()
        viewModel.onNameChange("Camila Morales")
        assertNull(viewModel.uiState.value.nameError)
    }

    @Test
    fun telefonoDeLongitudIncorrecta_muestraError() {
        val viewModel = RegisterViewModel()
        viewModel.onPhoneChange("31012")
        assertEquals("Numero de telefono no valido", viewModel.uiState.value.phoneError)
    }

    @Test
    fun telefonoDeDiezDigitos_noTieneError() {
        val viewModel = RegisterViewModel()
        viewModel.onPhoneChange("3101234567")
        assertNull(viewModel.uiState.value.phoneError)
    }

    @Test
    fun contrasenaMuyLarga_muestraError() {
        val viewModel = RegisterViewModel()
        viewModel.onPasswordChange("1234567890123456")
        assertEquals("La contraseña debe ser menor a 15 caracteres", viewModel.uiState.value.passwordError)
    }

    @Test
    fun contrasenasDistintas_muestraError() {
        val viewModel = RegisterViewModel()
        viewModel.onPasswordChange("patitas123")
        viewModel.onConfirmPasswordChange("patitas124")
        assertEquals("Las contraseñas no coinciden", viewModel.uiState.value.confirmPasswordError)
    }

    @Test
    fun cambiarLaContrasena_revalidaLaConfirmacion() {
        val viewModel = RegisterViewModel()
        viewModel.onPasswordChange("patitas123")
        viewModel.onConfirmPasswordChange("patitas124")
        viewModel.onPasswordChange("patitas124")
        assertNull(viewModel.uiState.value.confirmPasswordError)
    }

    @Test
    fun ciudadVacia_muestraError() {
        val viewModel = RegisterViewModel()
        viewModel.onCityChange("")
        assertEquals("Selecciona una ciudad", viewModel.uiState.value.cityError)
    }

    @Test
    fun sinAceptarTerminos_elFormularioNoEsValido() {
        val viewModel = RegisterViewModel()
        viewModel.onNameChange("Camila Morales")
        viewModel.onPasswordChange("patitas123")
        viewModel.onConfirmPasswordChange("patitas123")
        viewModel.onAcceptTermsChange(false)
        assertFalse(viewModel.uiState.value.isFormValid)
    }

    @Test
    fun registrarConFormularioInvalido_noAbreLaConfirmacion() {
        val viewModel = RegisterViewModel()
        viewModel.onRegisterClick()
        assertFalse(viewModel.uiState.value.showConfirmDialog)
    }

    @Test
    fun ayuda_seAbreYSeCierra() {
        val viewModel = RegisterViewModel()
        viewModel.onHelpClick()
        assertTrue(viewModel.uiState.value.showHelpDialog)
        viewModel.onDismissHelpDialog()
        assertFalse(viewModel.uiState.value.showHelpDialog)
    }
}
