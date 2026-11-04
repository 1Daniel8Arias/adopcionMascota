package co.edu.adopcionmascota

import co.edu.adopcionmascota.domain.usecase.auth.LoginUserUseCase
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
        val viewModel = LoginViewModel(
            LoginUserUseCase(FakeUserRepository(), FakeSessionRepository()),
            FakeResourceProvider()
        )
        viewModel.onEmailChange("")
        assertEquals(R.string.error_email_empty.toString(), viewModel.uiState.value.emailError)
    }

    @Test
    fun contrasenaVacia_muestraErrorObligatorio() {
        val viewModel = LoginViewModel(
            LoginUserUseCase(FakeUserRepository(), FakeSessionRepository()),
            FakeResourceProvider()
        )
        viewModel.onPasswordChange("")
        assertEquals(R.string.error_password_empty.toString(), viewModel.uiState.value.passwordError)
    }

    @Test
    fun contrasenaCorta_muestraErrorDeLongitud() {
        val viewModel = LoginViewModel(
            LoginUserUseCase(FakeUserRepository(), FakeSessionRepository()),
            FakeResourceProvider()
        )
        viewModel.onPasswordChange("123")
        assertEquals(R.string.error_password_short.toString(), viewModel.uiState.value.passwordError)
    }

    @Test
    fun contrasenaValida_noTieneError() {
        val viewModel = LoginViewModel(
            LoginUserUseCase(FakeUserRepository(), FakeSessionRepository()),
            FakeResourceProvider()
        )
        viewModel.onPasswordChange("123456")
        assertNull(viewModel.uiState.value.passwordError)
    }

    @Test
    fun formularioIncompleto_noEsValido() {
        val viewModel = LoginViewModel(
            LoginUserUseCase(FakeUserRepository(), FakeSessionRepository()),
            FakeResourceProvider()
        )
        viewModel.onPasswordChange("123456")
        assertFalse(viewModel.uiState.value.isFormValid)
    }

    @Test
    fun resetForm_limpiaElEstado() {
        val viewModel = LoginViewModel(
            LoginUserUseCase(FakeUserRepository(), FakeSessionRepository()),
            FakeResourceProvider()
        )
        viewModel.onPasswordChange("123")
        viewModel.resetForm()
        assertEquals("", viewModel.uiState.value.password)
        assertNull(viewModel.uiState.value.passwordError)
    }
}
