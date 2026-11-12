package co.edu.adopcionmascota

import co.edu.adopcionmascota.domain.model.PublicationStatus
import co.edu.adopcionmascota.domain.model.UserRole
import co.edu.adopcionmascota.domain.model.UserSession
import co.edu.adopcionmascota.domain.usecase.favorite.ObserveFavoriteIdsUseCase
import co.edu.adopcionmascota.domain.usecase.favorite.ObserveFavoritePublicationsUseCase
import co.edu.adopcionmascota.domain.usecase.favorite.ToggleFavoriteUseCase
import co.edu.adopcionmascota.domain.usecase.user.GetCurrentUserUseCase
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

// "Me encanta" se guarda por usuario y alimenta la pantalla "Mis favoritos"
class FavoriteUseCasesTest {

    private lateinit var users: FakeUserRepository
    private lateinit var publications: FakePublicationRepository
    private lateinit var favorites: FakeFavoriteRepository
    private val session = FakeSessionRepository(UserSession(TestData.visitor.id, UserRole.USER))

    @Before
    fun setUp() = runTest {
        users = FakeUserRepository()
        publications = FakePublicationRepository()
        favorites = FakeFavoriteRepository()
        users.save(TestData.visitor)
        publications.save(TestData.pendingPublication("p1").copy(status = PublicationStatus.VERIFIED, likes = 3))
        publications.save(TestData.pendingPublication("p2").copy(status = PublicationStatus.VERIFIED, name = "Luna"))
    }

    private fun toggle() = ToggleFavoriteUseCase(GetCurrentUserUseCase(session, users), favorites, publications)

    @Test
    fun darMeEncanta_apareceEnMisFavoritosYSumaAlContador() = runTest {
        assertTrue(toggle()("p1"))

        assertEquals(setOf("p1"), ObserveFavoriteIdsUseCase(session, favorites)().first())
        val mine = ObserveFavoritePublicationsUseCase(session, favorites, publications)().first()
        assertEquals(listOf("p1"), mine.map { it.id })
        assertEquals(4, publications.findById("p1")!!.likes)
    }

    @Test
    fun quitarMeEncanta_saleDeMisFavoritosYRestaAlContador() = runTest {
        toggle()("p1")
        assertFalse(toggle()("p1"))

        assertTrue(ObserveFavoritePublicationsUseCase(session, favorites, publications)().first().isEmpty())
        assertEquals(3, publications.findById("p1")!!.likes)
    }

    @Test
    fun favoritos_seMuestranDelMasRecienteAlMasAntiguo() = runTest {
        toggle()("p1")
        toggle()("p2")
        val mine = ObserveFavoritePublicationsUseCase(session, favorites, publications)().first()
        assertEquals(listOf("p2", "p1"), mine.map { it.id })
    }
}
