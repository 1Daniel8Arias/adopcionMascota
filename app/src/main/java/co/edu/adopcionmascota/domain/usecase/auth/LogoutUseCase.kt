package co.edu.adopcionmascota.domain.usecase.auth

import co.edu.adopcionmascota.domain.repository.SessionRepository
import co.edu.adopcionmascota.domain.repository.UserRepository
import javax.inject.Inject

class LogoutUseCase @Inject constructor(
    private val userRepository: UserRepository,
    private val sessionRepository: SessionRepository
) {
    suspend operator fun invoke(){
        // Primero se cierra la sesión de Firebase Authentication y luego la local (DataStore)
        runCatching { userRepository.logout() }
        sessionRepository.clearSession()
    }
}
