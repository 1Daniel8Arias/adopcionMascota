package co.edu.adopcionmascota.domain.usecase.user

import co.edu.adopcionmascota.domain.model.User
import co.edu.adopcionmascota.domain.repository.UserRepository
import javax.inject.Inject

class GetUserByIdUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(userId : String): User? = userRepository.findById(userId)
}