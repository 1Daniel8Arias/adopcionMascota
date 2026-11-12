package co.edu.adopcionmascota

import co.edu.adopcionmascota.domain.model.NotificationType
import co.edu.adopcionmascota.domain.model.PublicationStatus
import co.edu.adopcionmascota.domain.model.ReputationPoints
import co.edu.adopcionmascota.domain.model.UserRole
import co.edu.adopcionmascota.domain.model.UserSession
import co.edu.adopcionmascota.domain.usecase.notification.SendNotificationUseCase
import co.edu.adopcionmascota.domain.usecase.publication.ApprovePublicationUseCase
import co.edu.adopcionmascota.domain.usecase.publication.CreateReportUseCase
import co.edu.adopcionmascota.domain.usecase.publication.ObserveFeedUseCase
import co.edu.adopcionmascota.domain.usecase.publication.RejectPublicationUseCase
import co.edu.adopcionmascota.domain.usecase.publication.UpdatePublicationUseCase
import co.edu.adopcionmascota.domain.usecase.user.GetCurrentUserUseCase
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

// Flujo completo: crear -> moderar -> feed, con notificación al autor
class ModerationUseCasesTest {

    private lateinit var users: FakeUserRepository
    private lateinit var publications: FakePublicationRepository
    private lateinit var notifications: FakeNotificationRepository
    private lateinit var sendNotification: SendNotificationUseCase
    private val resources = FakeResourceProvider()

    @Before
    fun setUp() = runTest {
        users = FakeUserRepository()
        publications = FakePublicationRepository()
        notifications = FakeNotificationRepository()
        sendNotification = SendNotificationUseCase(notifications)
        users.save(TestData.author)
        users.save(TestData.moderator)
    }

    @Test
    fun crearPublicacion_quedaEnRevisionConElAutorDeLaSesion() = runTest {
        val session = FakeSessionRepository(UserSession(TestData.author.id, UserRole.USER))
        val create = CreateReportUseCase(publications, GetCurrentUserUseCase(session, users))

        create(TestData.pendingPublication().copy(status = PublicationStatus.VERIFIED, ownerId = ""))

        val saved = publications.findById("p1")!!
        assertEquals(PublicationStatus.PENDING, saved.status)
        assertEquals(TestData.author.id, saved.ownerId)
        // Mientras está en revisión no aparece en el feed
        assertTrue(ObserveFeedUseCase(publications)().first().isEmpty())
    }

    @Test
    fun aprobar_publicaEnElFeedYNotificaAlAutor() = runTest {
        publications.save(TestData.pendingPublication())

        ApprovePublicationUseCase(publications, users, sendNotification, resources)("p1")

        val feed = ObserveFeedUseCase(publications)().first()
        assertEquals(listOf("p1"), feed.map { it.id })

        val notification = notifications.observeByUser(TestData.author.id).first().single()
        assertEquals(NotificationType.PUBLICATION_APPROVED, notification.type)
        assertEquals("p1", notification.publicationId)

        // El autor gana puntos de reputación
        assertEquals(ReputationPoints.PUBLICATION_APPROVED, users.findById(TestData.author.id)!!.points)
    }

    @Test
    fun rechazar_guardaElMotivoYNotificaAlAutor() = runTest {
        publications.save(TestData.pendingPublication())

        RejectPublicationUseCase(publications, sendNotification, resources)("p1", "Fotos de internet")

        val rejected = publications.findById("p1")!!
        assertEquals(PublicationStatus.REJECTED, rejected.status)
        assertEquals("Fotos de internet", rejected.rejectionReason)
        assertTrue(ObserveFeedUseCase(publications)().first().isEmpty())

        val notification = notifications.observeByUser(TestData.author.id).first().single()
        assertEquals(NotificationType.PUBLICATION_PAUSED, notification.type)
    }

    @Test(expected = IllegalArgumentException::class)
    fun rechazarSinMotivo_lanzaError() = runTest {
        publications.save(TestData.pendingPublication())
        RejectPublicationUseCase(publications, sendNotification, resources)("p1", " ")
    }

    @Test
    fun editarPublicacionRechazada_vuelveARevision() = runTest {
        publications.save(TestData.pendingPublication())
        RejectPublicationUseCase(publications, sendNotification, resources)("p1", "Datos incompletos")

        val rejected = publications.findById("p1")!!
        UpdatePublicationUseCase(publications)(rejected.copy(description = "Datos corregidos"))

        val updated = publications.findById("p1")!!
        assertEquals(PublicationStatus.PENDING, updated.status)
        assertNull(updated.rejectionReason)
    }
}
