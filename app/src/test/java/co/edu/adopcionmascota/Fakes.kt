package co.edu.adopcionmascota

import co.edu.adopcionmascota.core.util.ResourceProvider
import co.edu.adopcionmascota.domain.model.User
import co.edu.adopcionmascota.domain.model.UserRole
import co.edu.adopcionmascota.domain.model.UserSession
import co.edu.adopcionmascota.domain.repository.SessionRepository
import co.edu.adopcionmascota.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

// Devuelve el id del recurso como texto, así las pruebas no dependen del idioma
class FakeResourceProvider : ResourceProvider {
    override fun getString(id: Int, vararg args: Any): String = id.toString()
}

class FakeUserRepository : UserRepository {
    private val users = mutableListOf<User>()
    override suspend fun save(user: User) { users.add(user) }
    override suspend fun findById(id: String): User? = users.firstOrNull { it.id == id }
    override suspend fun login(email: String, password: String): User? =
        users.firstOrNull { it.email == email && it.password == password }
    override suspend fun findByEmail(email: String): User? = users.firstOrNull { it.email == email }
}

class FakeSessionRepository : SessionRepository {
    override val session: Flow<UserSession?> = flowOf(null)
    override suspend fun saveSession(userId: String, role: UserRole) = Unit
    override suspend fun clearSession() = Unit
}
