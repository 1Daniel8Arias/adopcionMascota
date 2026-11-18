package co.edu.adopcionmascota.domain.usecase.user

import co.edu.adopcionmascota.domain.exception.EmailAlreadyInUseException
import co.edu.adopcionmascota.domain.model.User
import co.edu.adopcionmascota.domain.repository.UserRepository
import javax.inject.Inject

class RegisterUserUseCase @Inject constructor(
    private val userRepository: UserRepository
){
    // Retorna el id con el que quedó registrado el usuario
    suspend operator fun invoke(user: User): String {
        if(userRepository.findByEmail(user.email) != null){
            throw EmailAlreadyInUseException()
        }
        return userRepository.save(user)
    }
}
