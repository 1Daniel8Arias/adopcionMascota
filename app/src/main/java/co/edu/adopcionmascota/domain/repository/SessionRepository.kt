package co.edu.adopcionmascota.domain.repository

import co.edu.adopcionmascota.domain.model.UserRole
import co.edu.adopcionmascota.domain.model.UserSession
import kotlinx.coroutines.flow.Flow

interface SessionRepository {

    val session: Flow<UserSession?>

    suspend fun saveSession(UserId: String,role: UserRole)

    suspend fun clearSession()
}