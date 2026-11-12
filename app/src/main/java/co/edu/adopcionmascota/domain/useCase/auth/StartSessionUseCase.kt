package co.edu.adopcionmascota.domain.usecase.auth

import co.edu.adopcionmascota.domain.repository.SessionRepository
import co.edu.adopcionmascota.domain.repository.UserRepository
import javax.inject.Inject

// Abre la sesión de un usuario recién registrado, sin pedirle que inicie sesión otra vez.
// Si aún no verificó su correo (guía 20, actividad 1) se cierra la sesión de Firebase y
// retorna false: deberá verificarlo e iniciar sesión después
class StartSessionUseCase @Inject constructor(
    private val userRepository: UserRepository,
    private val sessionRepository: SessionRepository
) {
    suspend operator fun invoke(userId: String): Boolean {
        if (!userRepository.isEmailVerified()) {
            userRepository.logout()
            return false
        }
        val user = userRepository.findById(userId) ?: return false
        sessionRepository.saveSession(user.id, user.role)
        return true
    }
}
