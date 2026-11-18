package co.edu.adopcionmascota.domain.usecase.user

import co.edu.adopcionmascota.domain.model.User
import co.edu.adopcionmascota.domain.repository.UserRepository
import javax.inject.Inject

class UpdateUserUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(user: User) {
        val other = userRepository.findByEmail(user.email)
        if (other != null && other.id != user.id) {
            throw IllegalArgumentException("El correo ya está registrado")
        }
        userRepository.update(user)
    }
}
