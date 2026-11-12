package co.edu.adopcionmascota.domain.usecase.auth

import co.edu.adopcionmascota.domain.repository.UserRepository
import javax.inject.Inject

// Recuperación de contraseña: Firebase envía el correo con el enlace para restablecerla
class SendPasswordResetUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(email: String) = userRepository.sendPasswordReset(email.trim())
}
