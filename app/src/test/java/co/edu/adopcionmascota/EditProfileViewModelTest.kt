package co.edu.adopcionmascota

import co.edu.adopcionmascota.features.profile.edit.BIO_MAX_LENGTH
import co.edu.adopcionmascota.features.profile.edit.EditProfileViewModel
import co.edu.adopcionmascota.features.profile.edit.TOTAL_PROFILE_FIELDS
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class EditProfileViewModelTest {

    @Test
    fun datosIniciales_sonValidosYCompletos() {
        val state = EditProfileViewModel().uiState.value
        assertTrue(state.isFormValid)
        assertEquals(TOTAL_PROFILE_FIELDS, state.completedFields)
    }

    @Test
    fun nombreVacio_muestraErrorYInvalidaElFormulario() {
        val viewModel = EditProfileViewModel()
        viewModel.onNameChange("")
        assertEquals("El nombre es obligatorio", viewModel.uiState.value.nameError)
        assertFalse(viewModel.uiState.value.isFormValid)
    }

    @Test
    fun telefonoConLetras_muestraError() {
        val viewModel = EditProfileViewModel()
        viewModel.onPhoneChange("310 ABC 4567")
        assertEquals("Usa solo números, espacios y +", viewModel.uiState.value.phoneError)
    }

    @Test
    fun telefonoConMenosDigitos_muestraError() {
        val viewModel = EditProfileViewModel()
        viewModel.onPhoneChange("310 123")
        assertEquals("Ingresa un número de 10 dígitos (puedes anteponer +57)", viewModel.uiState.value.phoneError)
    }

    @Test
    fun telefonoConPrefijo57_esValido() {
        val viewModel = EditProfileViewModel()
        viewModel.onPhoneChange("+57 320 456 4567")
        assertNull(viewModel.uiState.value.phoneError)
    }

    @Test
    fun bio_noSuperaElMaximo() {
        val viewModel = EditProfileViewModel()
        val original = viewModel.uiState.value.bio
        viewModel.onBioChange("a".repeat(BIO_MAX_LENGTH + 1))
        assertEquals(original, viewModel.uiState.value.bio)
    }

    @Test
    fun localidadVacia_muestraError() {
        val viewModel = EditProfileViewModel()
        viewModel.onLocalityChange("")
        assertEquals("Selecciona una localidad", viewModel.uiState.value.localityError)
    }

    @Test
    fun barrioVacio_bajaElContadorDeCamposCompletos() {
        val viewModel = EditProfileViewModel()
        viewModel.onNeighborhoodChange("")
        assertEquals(TOTAL_PROFILE_FIELDS - 1, viewModel.uiState.value.completedFields)
        // El barrio es opcional: el formulario sigue siendo válido
        assertTrue(viewModel.uiState.value.isFormValid)
    }

    @Test
    fun descartar_abreElDialogoYConfirmarRestauraLosDatos() {
        val viewModel = EditProfileViewModel()
        val originalName = viewModel.uiState.value.name
        viewModel.onNameChange("Otro nombre")
        viewModel.onDiscardClick()
        assertTrue(viewModel.uiState.value.showExitDialog)
        viewModel.onConfirmDiscard()
        assertEquals(originalName, viewModel.uiState.value.name)
        assertFalse(viewModel.uiState.value.showExitDialog)
    }
}
