package co.edu.adopcionmascota.domain.usecase.user

import co.edu.adopcionmascota.domain.model.User
import co.edu.adopcionmascota.domain.repository.SessionRepository
import co.edu.adopcionmascota.domain.repository.UserRepository
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class GetCurrentUserUseCase @Inject constructor(
    private val sessionRepository: SessionRepository,
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(): User? {
        // Si no hay sesión activa, no hay usuario actual
        val session = sessionRepository.session.first() ?: return null
        // Con el userId de la sesión, obtenemos el usuario completo desde el repositorio
        return userRepository.findById(session.userId)
    }
}
