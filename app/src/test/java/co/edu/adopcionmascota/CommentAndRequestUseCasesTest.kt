package co.edu.adopcionmascota

import co.edu.adopcionmascota.domain.model.AdoptionRequestStatus
import co.edu.adopcionmascota.domain.model.AdoptionRequestType
import co.edu.adopcionmascota.domain.model.ContributionType
import co.edu.adopcionmascota.domain.model.NotificationType
import co.edu.adopcionmascota.domain.model.PublicationStatus
import co.edu.adopcionmascota.domain.model.ReputationPoints
import co.edu.adopcionmascota.domain.model.User
import co.edu.adopcionmascota.domain.model.UserRole
import co.edu.adopcionmascota.domain.model.UserSession
import co.edu.adopcionmascota.domain.usecase.comment.AddCommentUseCase
import co.edu.adopcionmascota.domain.usecase.notification.SendNotificationUseCase
import co.edu.adopcionmascota.domain.usecase.request.RespondAdoptionRequestUseCase
import co.edu.adopcionmascota.domain.usecase.request.SendAdoptionRequestUseCase
import co.edu.adopcionmascota.domain.usecase.user.GetCurrentUserUseCase
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

// Comentar y solicitar adopción generan notificaciones para el autor de la publicación
class CommentAndRequestUseCasesTest {

    private lateinit var users: FakeUserRepository
    private lateinit var publications: FakePublicationRepository
    private lateinit var comments: FakeCommentRepository
    private lateinit var notifications: FakeNotificationRepository
    private lateinit var requests: FakeAdoptionRequestRepository
    private val resources = FakeResourceProvider()

    @Before
    fun setUp() = runTest {
        users = FakeUserRepository()
        publications = FakePublicationRepository()
        comments = FakeCommentRepository()
        notifications = FakeNotificationRepository()
        requests = FakeAdoptionRequestRepository()
        users.save(TestData.author)
        users.save(TestData.visitor)
        publications.save(TestData.pendingPublication().copy(status = PublicationStatus.VERIFIED))
    }

    private fun addComment(asUser: User) = AddCommentUseCase(
        comments, publications, users,
        GetCurrentUserUseCase(FakeSessionRepository(UserSession(asUser.id, UserRole.USER)), users),
        SendNotificationUseCase(notifications), resources
    )

    private fun sendRequest(asUser: User) = SendAdoptionRequestUseCase(
        requests, publications,
        GetCurrentUserUseCase(FakeSessionRepository(UserSession(asUser.id, UserRole.USER)), users),
        SendNotificationUseCase(notifications), resources
    )

    @Test
    fun comentar_guardaElComentarioYNotificaAlAutor() = runTest {
        addComment(TestData.visitor)("p1", "Puedo ayudar con el transporte", ContributionType.SUPPORT)

        val saved = comments.observeByPublication("p1").first().single()
        assertEquals(TestData.visitor.id, saved.authorId)
        assertEquals(TestData.visitor.name, saved.author)

        val notification = notifications.observeByUser(TestData.author.id).first().single()
        assertEquals(NotificationType.NEW_COMMENT, notification.type)
        assertEquals(TestData.visitor.name, notification.title)

        assertEquals(ReputationPoints.COMMENT, users.findById(TestData.visitor.id)!!.points)
    }

    @Test
    fun elAutorComentaSuPropiaPublicacion_noSeNotificaASiMismo() = runTest {
        addComment(TestData.author)("p1", "Gracias a todos por la ayuda", ContributionType.SUPPORT)
        assertTrue(notifications.items.value.isEmpty())
    }

    @Test
    fun solicitarAdopcion_guardaLaSolicitudYNotificaAlAutor() = runTest {
        sendRequest(TestData.visitor)("p1", AdoptionRequestType.ADOPTION)

        val request = requests.observeByOwner(TestData.author.id).first().single()
        assertEquals(TestData.visitor.id, request.requesterId)
        assertEquals(AdoptionRequestStatus.PENDING, request.status)

        val notification = notifications.observeByUser(TestData.author.id).first().single()
        assertEquals(NotificationType.ADOPTION_REQUEST, notification.type)
    }

    @Test(expected = IllegalArgumentException::class)
    fun solicitarDosVeces_lanzaError() = runTest {
        sendRequest(TestData.visitor)("p1", AdoptionRequestType.ADOPTION)
        sendRequest(TestData.visitor)("p1", AdoptionRequestType.ADOPTION)
    }

    @Test(expected = IllegalArgumentException::class)
    fun elAutorNoPuedeSolicitarSuPropiaPublicacion() = runTest {
        sendRequest(TestData.author)("p1", AdoptionRequestType.ADOPTION)
    }

    @Test
    fun aceptarAdopcion_cierraElCasoYNotificaAlSolicitante() = runTest {
        val request = sendRequest(TestData.visitor)("p1", AdoptionRequestType.ADOPTION)

        RespondAdoptionRequestUseCase(requests, publications, users, SendNotificationUseCase(notifications), resources)(
            request.id, accept = true
        )

        assertEquals(AdoptionRequestStatus.ACCEPTED, requests.findById(request.id)!!.status)
        assertEquals(PublicationStatus.RESOLVED, publications.findById("p1")!!.status)

        val answer = notifications.observeByUser(TestData.visitor.id).first().single()
        assertEquals(NotificationType.REQUEST_ANSWERED, answer.type)
    }
}
