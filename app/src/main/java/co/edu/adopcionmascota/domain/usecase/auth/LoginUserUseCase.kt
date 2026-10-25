package co.edu.adopcionmascota.domain.usecase.auth

import co.edu.adopcionmascota.domain.model.User
import co.edu.adopcionmascota.domain.repository.SessionRepository
import co.edu.adopcionmascota.domain.repository.UserRepository
import javax.inject.Inject

class LoginUserUseCase @Inject constructor(
    private val userRepository: UserRepository,
    private val sessionRepository: SessionRepository
) {
    suspend operator fun invoke(email: String, password: String): User? {
        val user = userRepository.login(email, password)
        // Si las credenciales son válidas, guardamos la sesión del usuario
        if (user != null) {
            sessionRepository.saveSession(user.id, user.role)
        }
        return user
    }

}