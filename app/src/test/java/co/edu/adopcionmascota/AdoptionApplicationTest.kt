package co.edu.adopcionmascota

import androidx.lifecycle.SavedStateHandle
import co.edu.adopcionmascota.domain.exception.AiRateLimitException
import co.edu.adopcionmascota.domain.model.AdoptionApplication
import co.edu.adopcionmascota.domain.model.AdoptionRequestType
import co.edu.adopcionmascota.domain.model.AvailableTime
import co.edu.adopcionmascota.domain.model.CompatibilityLevel
import co.edu.adopcionmascota.domain.model.CompatibilityNote
import co.edu.adopcionmascota.domain.model.HousingType
import co.edu.adopcionmascota.domain.model.OtherPets
import co.edu.adopcionmascota.domain.model.PetExperience
import co.edu.adopcionmascota.domain.model.Publication
import co.edu.adopcionmascota.domain.model.User
import co.edu.adopcionmascota.domain.model.UserRole
import co.edu.adopcionmascota.domain.model.UserSession
import co.edu.adopcionmascota.domain.repository.AdoptionLetterRepository
import co.edu.adopcionmascota.domain.usecase.notification.SendNotificationUseCase
import co.edu.adopcionmascota.domain.usecase.publication.ObservePublicationUseCase
import co.edu.adopcionmascota.domain.usecase.request.EvaluateCompatibilityUseCase
import co.edu.adopcionmascota.domain.usecase.request.GenerateAdoptionLetterUseCase
import co.edu.adopcionmascota.domain.usecase.request.SendAdoptionRequestUseCase
import co.edu.adopcionmascota.domain.usecase.user.GetCurrentUserUseCase
import co.edu.adopcionmascota.features.request.application.AdoptionApplicationViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class AdoptionApplicationTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val evaluate = EvaluateCompatibilityUseCase()
    private val bigDog = TestData.pendingPublication().copy(size = "Grande", species = "Perro")

    private val ideal = AdoptionApplication(
        HousingType.HOUSE_WITH_YARD, PetExperience.EXPERIENCED, AvailableTime.HIGH, OtherPets.NONE
    )
    private val risky = AdoptionApplication(
        HousingType.APARTMENT, PetExperience.FIRST_TIME, AvailableTime.LOW, OtherPets.SEVERAL
    )

    @Test
    fun compatibilidad_alta_conCasaConPatioYExperiencia() {
        val result = evaluate(bigDog, ideal)
        assertEquals(CompatibilityLevel.HIGH, result.level)
        assertTrue(CompatibilityNote.SPACIOUS_HOME in result.notes)
    }

    @Test
    fun compatibilidad_baja_conApartamentoParaPerroGrandeYPocoTiempo() {
        val result = evaluate(bigDog, risky)
        assertEquals(CompatibilityLevel.LOW, result.level)
        assertTrue(CompatibilityNote.SMALL_SPACE_LARGE_PET in result.notes)
        assertTrue(CompatibilityNote.LOW_TIME in result.notes)
        assertTrue(result.score in 0..100)
    }

    // ---------- Envío y manejo del 429 ----------

    private val users = FakeUserRepository()
    private val publications = FakePublicationRepository()
    private val requests = FakeAdoptionRequestRepository()
    private val resources = FakeResourceProvider()
    private val currentUser = GetCurrentUserUseCase(FakeSessionRepository(UserSession(TestData.visitor.id, UserRole.USER)), users)

    private class FakeLetterRepository(private val fail: Exception? = null) : AdoptionLetterRepository {
        override suspend fun generateLetter(publication: Publication, applicant: User, application: AdoptionApplication, language: String): String {
            fail?.let { throw it }
            return "Carta para ${publication.name} escrita por ${applicant.name}. ".repeat(3)
        }
    }

    private suspend fun viewModel(letters: AdoptionLetterRepository): AdoptionApplicationViewModel {
        users.save(TestData.author); users.save(TestData.visitor)
        publications.save(bigDog)
        return AdoptionApplicationViewModel(
            SavedStateHandle(mapOf("publicationId" to bigDog.id)),
            ObservePublicationUseCase(publications),
            GenerateAdoptionLetterUseCase(letters, publications, currentUser),
            SendAdoptionRequestUseCase(
                requests, publications, currentUser, SendNotificationUseCase(FakeNotificationRepository()), resources
            ),
            resources
        )
    }

    private fun AdoptionApplicationViewModel.answer(a: AdoptionApplication) {
        onHousingChange(a.housing); onExperienceChange(a.experience)
        onTimeChange(a.availableTime); onOtherPetsChange(a.otherPets)
    }

    @Test
    fun cartaConIA_seEditaYSeGuardaConCompatibilidad() = runTest {
        val vm = viewModel(FakeLetterRepository())
        vm.answer(ideal)
        vm.onGenerateLetter()
        assertTrue(vm.uiState.value.aiGenerated)
        vm.onLetterChange(vm.uiState.value.letter + " Editada por mí.")
        vm.onSend()

        val saved = requests.observeByOwner(TestData.author.id).first().single()
        assertEquals(AdoptionRequestType.ADOPTION, saved.type)
        assertTrue(saved.message.endsWith("Editada por mí."))
        assertNotNull(saved.compatibilityScore)
        assertEquals(HousingType.HOUSE_WITH_YARD.name, saved.housing)
        assertTrue(saved.aiGenerated)
    }

    @Test
    fun error429_muestraMensajeAmableYPermiteEscribirAMano() = runTest {
        val vm = viewModel(FakeLetterRepository(fail = AiRateLimitException()))
        vm.answer(ideal)
        vm.onGenerateLetter()
        val state = vm.uiState.value
        assertTrue(state.showLetterEditor)
        assertNotNull(state.aiMessage)
        assertFalse(state.aiGenerated)

        vm.onLetterChange("Hola, me encantaría adoptar a Rocky. Tengo casa con patio y mucho tiempo.")
        vm.onSend()
        val saved = requests.observeByOwner(TestData.author.id).first().single()
        assertFalse(saved.aiGenerated)
    }
}
