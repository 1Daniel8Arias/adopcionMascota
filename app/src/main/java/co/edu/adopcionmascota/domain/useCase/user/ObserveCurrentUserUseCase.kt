package co.edu.adopcionmascota.domain.usecase.user

import co.edu.adopcionmascota.domain.model.User
import co.edu.adopcionmascota.domain.repository.SessionRepository
import co.edu.adopcionmascota.domain.repository.UserRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject

// Usuario de la sesión actual; se actualiza solo cuando cambian sus datos en Room
class ObserveCurrentUserUseCase @Inject constructor(
    private val sessionRepository: SessionRepository,
    private val userRepository: UserRepository
) {
    @OptIn(ExperimentalCoroutinesApi::class)
    operator fun invoke(): Flow<User?> = sessionRepository.session.flatMapLatest { session ->
        if (session == null) flowOf(null) else userRepository.observeById(session.userId)
    }
}
