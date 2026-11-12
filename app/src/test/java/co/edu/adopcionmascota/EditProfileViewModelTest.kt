package co.edu.adopcionmascota

import co.edu.adopcionmascota.features.profile.edit.BIO_MAX_LENGTH
import co.edu.adopcionmascota.features.profile.edit.EditProfileViewModel
import co.edu.adopcionmascota.features.profile.edit.TOTAL_PROFILE_FIELDS
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import co.edu.adopcionmascota.domain.model.UserRole
import co.edu.adopcionmascota.domain.model.UserSession
import co.edu.adopcionmascota.domain.usecase.user.GetCurrentUserUseCase
import co.edu.adopcionmascota.domain.usecase.user.UpdateUserUseCase
import kotlinx.coroutines.runBlocking

class EditProfileViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    // El formulario se llena con el usuario de la sesión guardado en el repositorio
    private fun newViewModel(): EditProfileViewModel {
        val users = FakeUserRepository()
        val session = FakeSessionRepository(UserSession(TestData.author.id, UserRole.USER))
        runBlocking { users.save(TestData.author) }
        return EditProfileViewModel(
            GetCurrentUserUseCase(session, users),
            UpdateUserUseCase(users),
            FakeResourceProvider()
        )
    }

    @Test
    fun datosIniciales_sonValidosYCompletos() {
        val state = newViewModel().uiState.value
        assertTrue(state.isFormValid)
        assertEquals(TOTAL_PROFILE_FIELDS, state.completedFields)
    }

    @Test
    fun nombreVacio_muestraErrorYInvalidaElFormulario() {
        val viewModel = newViewModel()
        viewModel.onNameChange("")
        assertEquals(R.string.error_name_empty.toString(), viewModel.uiState.value.nameError)
        assertFalse(viewModel.uiState.value.isFormValid)
    }

    @Test
    fun telefonoConLetras_muestraError() {
        val viewModel = newViewModel()
        viewModel.onPhoneChange("310 ABC 4567")
        assertEquals(R.string.error_phone_chars.toString(), viewModel.uiState.value.phoneError)
    }

    @Test
    fun telefonoConMenosDigitos_muestraError() {
        val viewModel = newViewModel()
        viewModel.onPhoneChange("310 123")
        assertEquals(R.string.error_phone_digits.toString(), viewModel.uiState.value.phoneError)
    }

    @Test
    fun telefonoConPrefijo57_esValido() {
        val viewModel = newViewModel()
        viewModel.onPhoneChange("+57 320 456 4567")
        assertNull(viewModel.uiState.value.phoneError)
    }

    @Test
    fun bio_noSuperaElMaximo() {
        val viewModel = newViewModel()
        val original = viewModel.uiState.value.bio
        viewModel.onBioChange("a".repeat(BIO_MAX_LENGTH + 1))
        assertEquals(original, viewModel.uiState.value.bio)
    }

    @Test
    fun localidadVacia_muestraError() {
        val viewModel = newViewModel()
        viewModel.onLocalityChange("")
        assertEquals(R.string.error_locality.toString(), viewModel.uiState.value.localityError)
    }

    @Test
    fun barrioVacio_bajaElContadorDeCamposCompletos() {
        val viewModel = newViewModel()
        viewModel.onNeighborhoodChange("")
        assertEquals(TOTAL_PROFILE_FIELDS - 1, viewModel.uiState.value.completedFields)
        // El barrio es opcional: el formulario sigue siendo válido
        assertTrue(viewModel.uiState.value.isFormValid)
    }

    @Test
    fun descartar_abreElDialogoYConfirmarRestauraLosDatos() {
        val viewModel = newViewModel()
        val originalName = viewModel.uiState.value.name
        viewModel.onNameChange("Otro nombre")
        viewModel.onDiscardClick()
        assertTrue(viewModel.uiState.value.showExitDialog)
        viewModel.onConfirmDiscard()
        assertEquals(originalName, viewModel.uiState.value.name)
        assertFalse(viewModel.uiState.value.showExitDialog)
    }
}
