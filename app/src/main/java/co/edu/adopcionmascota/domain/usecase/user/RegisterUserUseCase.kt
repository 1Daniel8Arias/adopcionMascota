package co.edu.adopcionmascota.domain.usecase.user

import co.edu.adopcionmascota.domain.model.User
import co.edu.adopcionmascota.domain.repository.UserRepository
import javax.inject.Inject

class RegisterUserUseCase @Inject constructor(
    private val userRepository: UserRepository
){
    suspend operator fun invoke(user: User){
        if(userRepository.findByEmail(user.email) != null){
            throw IllegalArgumentException("El usuario ya existe")
        }
        userRepository.save(user)
    }
}