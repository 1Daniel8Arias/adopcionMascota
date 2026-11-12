package co.edu.adopcionmascota

import co.edu.adopcionmascota.core.util.ResourceProvider
import co.edu.adopcionmascota.domain.model.AdoptionRequest
import co.edu.adopcionmascota.domain.model.AdoptionRequestStatus
import co.edu.adopcionmascota.domain.model.Comment
import co.edu.adopcionmascota.domain.model.Notification
import co.edu.adopcionmascota.domain.model.Publication
import co.edu.adopcionmascota.domain.model.PublicationStatus
import co.edu.adopcionmascota.domain.model.User
import co.edu.adopcionmascota.domain.model.UserRole
import co.edu.adopcionmascota.domain.model.UserSession
import co.edu.adopcionmascota.domain.repository.AdoptionRequestRepository
import co.edu.adopcionmascota.domain.repository.CommentRepository
import co.edu.adopcionmascota.domain.repository.NotificationRepository
import co.edu.adopcionmascota.domain.repository.PublicationRepository
import co.edu.adopcionmascota.domain.repository.SessionRepository
import co.edu.adopcionmascota.domain.repository.UserRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.rules.TestWatcher
import org.junit.runner.Description

// Repositorios en memoria que reemplazan a Room en las pruebas unitarias

// Devuelve el id del recurso como texto, así las pruebas no dependen del idioma
class FakeResourceProvider : ResourceProvider {
    override fun getString(id: Int, vararg args: Any): String = id.toString()
}

// Los ViewModels lanzan corrutinas en viewModelScope (Dispatchers.Main)
@OptIn(ExperimentalCoroutinesApi::class)
class MainDispatcherRule(
    private val dispatcher: TestDispatcher = UnconfinedTestDispatcher()
) : TestWatcher() {
    override fun starting(description: Description) = Dispatchers.setMain(dispatcher)
    override fun finished(description: Description) = Dispatchers.resetMain()
}

class FakeUserRepository : UserRepository {
    val users = MutableStateFlow<List<User>>(emptyList())
    override suspend fun save(user: User): String { users.update { it + user }; return user.id }
    override suspend fun logout() {}
    override suspend fun sendPasswordReset(email: String) {
        if (users.value.none { it.email == email }) throw co.edu.adopcionmascota.domain.exception.UserNotFoundException()
    }
    override suspend fun update(user: User) = users.update { list -> list.map { if (it.id == user.id) user else it } }
    override suspend fun delete(id: String) = users.update { list -> list.filterNot { it.id == id } }
    override suspend fun findById(id: String): User? = users.value.firstOrNull { it.id == id }
    override fun observeById(id: String): Flow<User?> = users.map { list -> list.firstOrNull { it.id == id } }
    override suspend fun login(email: String, password: String): User? =
        users.value.firstOrNull { it.email == email && it.password == password }
    override suspend fun findByEmail(email: String): User? = users.value.firstOrNull { it.email == email }
    override suspend fun addPoints(id: String, points: Int) =
        users.update { list -> list.map { if (it.id == id) it.copy(points = it.points + points) else it } }
    override fun observeCount(): Flow<Int> = users.map { it.size }
}

class FakeSessionRepository(initial: UserSession? = null) : SessionRepository {
    private val _session = MutableStateFlow(initial)
    override val session: Flow<UserSession?> = _session
    override suspend fun saveSession(userId: String, role: UserRole) { _session.value = UserSession(userId, role) }
    override suspend fun clearSession() { _session.value = null }
}

class FakePublicationRepository : PublicationRepository {
    val items = MutableStateFlow<List<Publication>>(emptyList())
    override val publication: Flow<List<Publication>> =
        items.map { list -> list.filter { it.status != PublicationStatus.DELETED } }
    override fun observeById(id: String): Flow<Publication?> = items.map { list -> list.firstOrNull { it.id == id } }
    override fun observeByOwner(ownerId: String): Flow<List<Publication>> =
        publication.map { list -> list.filter { it.ownerId == ownerId } }
    override fun observeByStatus(status: PublicationStatus): Flow<List<Publication>> =
        items.map { list -> list.filter { it.status == status } }
    override suspend fun save(publication: Publication) = items.update { it + publication }
    override suspend fun update(publication: Publication) =
        items.update { list -> list.map { if (it.id == publication.id) publication else it } }
    override suspend fun findById(id: String): Publication? = items.value.firstOrNull { it.id == id }
    override suspend fun addLikes(id: String, delta: Int) =
        items.update { list -> list.map { if (it.id == id) it.copy(likes = (it.likes + delta).coerceAtLeast(0)) else it } }
    override suspend fun updateStatus(id: String, status: PublicationStatus, rejectionReason: String?, reviewedAt: Long?) =
        items.update { list ->
            list.map {
                if (it.id == id) it.copy(status = status, rejectionReason = rejectionReason, reviewedAt = reviewedAt ?: it.reviewedAt) else it
            }
        }
}

class FakeCommentRepository : CommentRepository {
    val items = MutableStateFlow<List<Comment>>(emptyList())
    override val comments: Flow<List<Comment>> = items
    override fun observeByPublication(publicationId: String): Flow<List<Comment>> =
        items.map { list -> list.filter { it.publicationId == publicationId } }
    override suspend fun save(comment: Comment) = items.update { listOf(comment) + it }
    override suspend fun toggleThanks(commentId: String) = items.update { list ->
        list.map {
            if (it.id != commentId) it
            else it.copy(thankedByMe = !it.thankedByMe, thanks = it.thanks + if (it.thankedByMe) -1 else 1)
        }
    }
}

class FakeNotificationRepository : NotificationRepository {
    val items = MutableStateFlow<List<Notification>>(emptyList())
    override fun observeByUser(userId: String): Flow<List<Notification>> =
        items.map { list -> list.filter { it.userId == userId } }
    override suspend fun findById(id: String): Notification? = items.value.firstOrNull { it.id == id }
    override suspend fun save(notification: Notification) = items.update { listOf(notification) + it }
    override suspend fun update(notification: Notification) =
        items.update { list -> list.map { if (it.id == notification.id) notification else it } }
    override suspend fun markAsRead(id: String) =
        items.update { list -> list.map { if (it.id == id) it.copy(isRead = true) else it } }
    override suspend fun markAllAsRead(userId: String) =
        items.update { list -> list.map { if (it.userId == userId) it.copy(isRead = true) else it } }
}

class FakeAdoptionRequestRepository : AdoptionRequestRepository {
    val items = MutableStateFlow<List<AdoptionRequest>>(emptyList())
    override val requests: Flow<List<AdoptionRequest>> = items
    override fun observeByOwner(ownerId: String): Flow<List<AdoptionRequest>> =
        items.map { list -> list.filter { it.ownerId == ownerId } }
    override fun observeByRequester(requesterId: String, publicationId: String): Flow<List<AdoptionRequest>> =
        items.map { list -> list.filter { it.requesterId == requesterId && it.publicationId == publicationId } }
    override suspend fun findById(id: String): AdoptionRequest? = items.value.firstOrNull { it.id == id }
    override suspend fun save(request: AdoptionRequest) = items.update { it + request }
    override suspend fun updateStatus(id: String, status: AdoptionRequestStatus) =
        items.update { list -> list.map { if (it.id == id) it.copy(status = status) else it } }
}

class FakeFavoriteRepository : co.edu.adopcionmascota.domain.repository.FavoriteRepository {
    val items = MutableStateFlow<List<Pair<String, String>>>(emptyList())   // (userId, publicationId)
    override fun observeIds(userId: String): Flow<List<String>> =
        items.map { list -> list.filter { it.first == userId }.map { it.second }.reversed() }
    override suspend fun isFavorite(userId: String, publicationId: String): Boolean = (userId to publicationId) in items.value
    override suspend fun add(userId: String, publicationId: String) = items.update { it + (userId to publicationId) }
    override suspend fun remove(userId: String, publicationId: String) = items.update { it - (userId to publicationId) }
}

// Datos de prueba compartidos
object TestData {
    val author = User(
        id = "1", name = "Santiago López", city = "Armenia", address = "Barrio Centro",
        email = "santiago@email.com", password = "111111", phoneNumber = "+57 310 123 4567",
        bio = "Rescatista voluntario"
    )
    val visitor = User(
        id = "2", name = "Valentina Gómez", city = "Pereira", address = "Barrio Cuba",
        email = "valentina@email.com", password = "222222"
    )
    val moderator = User(
        id = "3", name = "Andrés Martínez", city = "Manizales", address = "Palermo",
        email = "andres@email.com", password = "333333", role = UserRole.MODERATOR
    )

    fun pendingPublication(id: String = "p1") = Publication(
        id = id,
        name = "Rocky",
        description = "Perro rescatado",
        location = "Armenia",
        status = PublicationStatus.PENDING,
        type = co.edu.adopcionmascota.domain.model.PetType.ADOPCION,
        image = "imagen_rocky",
        ownerId = author.id
    )
}
