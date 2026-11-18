package co.edu.adopcionmascota.domain.usecase.auth

import co.edu.adopcionmascota.domain.repository.UserRepository
import javax.inject.Inject

// Guía 20, actividad 2: cambia la contraseña con el código (oobCode) del enlace del correo
class ConfirmPasswordResetUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(code: String, newPassword: String) =
        userRepository.confirmPasswordReset(code, newPassword)
}
