package co.edu.adopcionmascota

import co.edu.adopcionmascota.domain.usecase.user.RegisterUserUseCase
import co.edu.adopcionmascota.features.register.RegisterViewModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class RegisterViewModelTest {

    @Test
    fun nombreCorto_muestraError() {
        val viewModel = RegisterViewModel(
            RegisterUserUseCase(FakeUserRepository()),
            FakeResourceProvider()
        )
        viewModel.onNameChange("Al")
        assertEquals(R.string.error_name_short.toString(), viewModel.uiState.value.nameError)
    }

    @Test
    fun nombreValido_noTieneError() {
        val viewModel = RegisterViewModel(
            RegisterUserUseCase(FakeUserRepository()),
            FakeResourceProvider()
        )
        viewModel.onNameChange("Camila Morales")
        assertNull(viewModel.uiState.value.nameError)
    }

    @Test
    fun telefonoDeLongitudIncorrecta_muestraError() {
        val viewModel = RegisterViewModel(
            RegisterUserUseCase(FakeUserRepository()),
            FakeResourceProvider()
        )
        viewModel.onPhoneChange("31012")
        assertEquals(R.string.error_phone_invalid.toString(), viewModel.uiState.value.phoneError)
    }

    @Test
    fun telefonoDeDiezDigitos_noTieneError() {
        val viewModel = RegisterViewModel(
            RegisterUserUseCase(FakeUserRepository()),
            FakeResourceProvider()
        )
        viewModel.onPhoneChange("3101234567")
        assertNull(viewModel.uiState.value.phoneError)
    }

    @Test
    fun contrasenaMuyLarga_muestraError() {
        val viewModel = RegisterViewModel(
            RegisterUserUseCase(FakeUserRepository()),
            FakeResourceProvider()
        )
        viewModel.onPasswordChange("1234567890123456")
        assertEquals(R.string.error_password_long.toString(), viewModel.uiState.value.passwordError)
    }

    @Test
    fun contrasenasDistintas_muestraError() {
        val viewModel = RegisterViewModel(
            RegisterUserUseCase(FakeUserRepository()),
            FakeResourceProvider()
        )
        viewModel.onPasswordChange("patitas123")
        viewModel.onConfirmPasswordChange("patitas124")
        assertEquals(R.string.error_password_mismatch.toString(), viewModel.uiState.value.confirmPasswordError)
    }

    @Test
    fun cambiarLaContrasena_revalidaLaConfirmacion() {
        val viewModel = RegisterViewModel(
            RegisterUserUseCase(FakeUserRepository()),
            FakeResourceProvider()
        )
        viewModel.onPasswordChange("patitas123")
        viewModel.onConfirmPasswordChange("patitas124")
        viewModel.onPasswordChange("patitas124")
        assertNull(viewModel.uiState.value.confirmPasswordError)
    }

    @Test
    fun ciudadVacia_muestraError() {
        val viewModel = RegisterViewModel(
            RegisterUserUseCase(FakeUserRepository()),
            FakeResourceProvider()
        )
        viewModel.onCityChange("")
        assertEquals(R.string.error_city.toString(), viewModel.uiState.value.cityError)
    }

    @Test
    fun sinAceptarTerminos_elFormularioNoEsValido() {
        val viewModel = RegisterViewModel(
            RegisterUserUseCase(FakeUserRepository()),
            FakeResourceProvider()
        )
        viewModel.onNameChange("Camila Morales")
        viewModel.onPasswordChange("patitas123")
        viewModel.onConfirmPasswordChange("patitas123")
        viewModel.onAcceptTermsChange(false)
        assertFalse(viewModel.uiState.value.isFormValid)
    }

    @Test
    fun registrarConFormularioInvalido_noAbreLaConfirmacion() {
        val viewModel = RegisterViewModel(
            RegisterUserUseCase(FakeUserRepository()),
            FakeResourceProvider()
        )
        viewModel.onRegisterClick()
        assertFalse(viewModel.uiState.value.showConfirmDialog)
    }

    @Test
    fun ayuda_seAbreYSeCierra() {
        val viewModel = RegisterViewModel(
            RegisterUserUseCase(FakeUserRepository()),
            FakeResourceProvider()
        )
        viewModel.onHelpClick()
        assertTrue(viewModel.uiState.value.showHelpDialog)
        viewModel.onDismissHelpDialog()
        assertFalse(viewModel.uiState.value.showHelpDialog)
    }
}
