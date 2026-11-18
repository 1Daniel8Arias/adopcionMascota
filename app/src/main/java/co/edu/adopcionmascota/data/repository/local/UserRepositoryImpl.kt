package co.edu.adopcionmascota.data.repository.local

import co.edu.adopcionmascota.data.local.dao.UserDao
import co.edu.adopcionmascota.data.local.mapper.toDomain
import co.edu.adopcionmascota.data.local.mapper.toEntity
import co.edu.adopcionmascota.domain.model.User
import co.edu.adopcionmascota.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepositoryImpl @Inject constructor(
    private val userDao: UserDao
) : UserRepository {

    override suspend fun save(user: User): String {
        userDao.insert(user.toEntity())
        return user.id
    }

    override suspend fun update(user: User) {
        userDao.update(user.toEntity())
    }

    override suspend fun delete(id: String) {
        userDao.delete(id)
    }

    override suspend fun findById(id: String): User? = userDao.findById(id)?.toDomain()

    override fun observeById(id: String): Flow<User?> = userDao.observeById(id).map { it?.toDomain() }

    override suspend fun login(email: String, password: String): User? =
        userDao.login(email, password)?.toDomain()

    override suspend fun findByEmail(email: String): User? = userDao.findByEmail(email)?.toDomain()

    override suspend fun addPoints(id: String, points: Int) {
        userDao.addPoints(id, points)
    }

    override fun observeCount(): Flow<Int> = userDao.observeCount()

    override suspend fun logout() {
        // Room no maneja sesiones: la sesión vive en DataStore
    }

    override suspend fun confirmPasswordReset(code: String, newPassword: String) {
        // Sin backend no hay códigos de recuperación
        throw co.edu.adopcionmascota.domain.exception.InvalidResetCodeException()
    }

    override suspend fun isEmailVerified(): Boolean = true

    override suspend fun sendPasswordReset(email: String) {
        // Sin backend no se puede enviar el correo; solo se valida que la cuenta exista
        if (userDao.findByEmail(email) == null) throw co.edu.adopcionmascota.domain.exception.UserNotFoundException()
    }
}
