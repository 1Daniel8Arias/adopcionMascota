package co.edu.adopcionmascota.domain.usecase.auth

import co.edu.adopcionmascota.domain.repository.SessionRepository
import co.edu.adopcionmascota.domain.repository.UserRepository
import javax.inject.Inject

// Abre la sesión de un usuario recién registrado, sin pedirle que inicie sesión otra vez
class StartSessionUseCase @Inject constructor(
    private val userRepository: UserRepository,
    private val sessionRepository: SessionRepository
) {
    suspend operator fun invoke(userId: String) {
        val user = userRepository.findById(userId) ?: return
        sessionRepository.saveSession(user.id, user.role)
    }
}
