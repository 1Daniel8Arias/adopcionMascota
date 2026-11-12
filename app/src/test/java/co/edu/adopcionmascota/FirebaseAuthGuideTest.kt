package co.edu.adopcionmascota

import co.edu.adopcionmascota.core.util.RequestResult
import co.edu.adopcionmascota.domain.exception.EmailAlreadyInUseException
import co.edu.adopcionmascota.domain.usecase.auth.ConfirmPasswordResetUseCase
import co.edu.adopcionmascota.domain.usecase.auth.StartSessionUseCase
import co.edu.adopcionmascota.domain.usecase.user.RegisterUserUseCase
import co.edu.adopcionmascota.features.password.ResetPasswordViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

// Comportamientos de las guías 19 y 20 (Firestore y Firebase Authentication)
class FirebaseAuthGuideTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun registro_correoRepetido_lanzaEmailAlreadyInUse() = runTest {
        val users = FakeUserRepository().apply { save(TestData.author) }
        val result = runCatching { RegisterUserUseCase(users)(TestData.author.copy(id = "otro")) }
        assertTrue(result.exceptionOrNull() is EmailAlreadyInUseException)
    }

    @Test
    fun registro_retornaElIdDelUsuario() = runTest {
        val id = RegisterUserUseCase(FakeUserRepository())(TestData.visitor)
        assertEquals(TestData.visitor.id, id)
    }

    @Test
    fun correoSinVerificar_noAbreLaSesion() = runTest {
        val users = FakeUserRepository().apply { save(TestData.visitor); verified = false }
        val session = FakeSessionRepository()
        val started = StartSessionUseCase(users, session)(TestData.visitor.id)
        assertFalse(started)
        assertNull(session.session.first())
    }

    @Test
    fun correoVerificado_abreLaSesion() = runTest {
        val users = FakeUserRepository().apply { save(TestData.visitor) }
        val session = FakeSessionRepository()
        assertTrue(StartSessionUseCase(users, session)(TestData.visitor.id))
        assertEquals(TestData.visitor.id, session.session.first()?.userId)
    }

    private fun resetViewModel() = ResetPasswordViewModel(
        ConfirmPasswordResetUseCase(FakeUserRepository()), FakeResourceProvider()
    )

    @Test
    fun restablecer_codigoInvalido_muestraError() = runTest {
        val vm = resetViewModel()
        vm.onPasswordChange("Nueva123")
        vm.onConfirmPasswordChange("Nueva123")
        vm.confirm("vencido")
        assertEquals(
            RequestResult.Failure(R.string.reset_password_invalid_code.toString()),
            vm.uiState.value.result
        )
    }

    @Test
    fun restablecer_codigoValido_exito() = runTest {
        val vm = resetViewModel()
        vm.onPasswordChange("Nueva123")
        vm.onConfirmPasswordChange("Nueva123")
        vm.confirm("valid")
        assertTrue(vm.uiState.value.result is RequestResult.Success)
    }

    @Test
    fun restablecer_contrasenasDistintas_noEsValido() {
        val vm = resetViewModel()
        vm.onPasswordChange("Nueva123")
        vm.onConfirmPasswordChange("Otra123")
        assertFalse(vm.uiState.value.isFormValid)
    }
}
