package co.edu.adopcionmascota.domain.usecase.auth

import co.edu.adopcionmascota.domain.model.UserSession
import co.edu.adopcionmascota.domain.repository.SessionRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveSessionUseCase @Inject constructor(
    private val sessionRepository: SessionRepository
) {
    operator fun invoke(): Flow<UserSession?> = sessionRepository.session
}