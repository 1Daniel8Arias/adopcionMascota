package co.edu.adopcionmascota.domain.repository

import co.edu.adopcionmascota.domain.model.User
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    // Crea la cuenta y retorna el id definitivo del usuario (con Firebase es el UID de Auth)
    suspend fun save(user: User): String
    suspend fun update(user: User)
    suspend fun delete(id: String)
    suspend fun findById(id: String): User?
    fun observeById(id: String): Flow<User?>
    // Retorna null si las credenciales son inválidas
    suspend fun login(email: String, password: String): User?
    suspend fun findByEmail(email: String): User?
    suspend fun addPoints(id: String, points: Int)
    fun observeCount(): Flow<Int>
    // Cierra la sesión del proveedor de autenticación (si lo hay)
    suspend fun logout()
    // Envía el correo para restablecer la contraseña
    suspend fun sendPasswordReset(email: String)
    // Cambia la contraseña con el código (oobCode) del enlace del correo
    suspend fun confirmPasswordReset(code: String, newPassword: String)
    // false si el usuario autenticado aún no confirmó su correo (guía 20, actividad 1)
    suspend fun isEmailVerified(): Boolean
}
