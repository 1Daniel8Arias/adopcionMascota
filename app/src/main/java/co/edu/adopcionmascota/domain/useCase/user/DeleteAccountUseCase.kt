package co.edu.adopcionmascota.domain.usecase.user

import co.edu.adopcionmascota.domain.repository.UserRepository
import javax.inject.Inject

// Elimina al usuario de la sesión. La sesión se cierra después, en la pantalla de despedida
class DeleteAccountUseCase @Inject constructor(
    private val getCurrentUserUseCase: GetCurrentUserUseCase,
    private val userRepository: UserRepository
) {
    suspend operator fun invoke() {
        val user = getCurrentUserUseCase() ?: return
        userRepository.delete(user.id)
    }
}
