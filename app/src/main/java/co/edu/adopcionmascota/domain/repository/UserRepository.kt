package co.edu.adopcionmascota.domain.repository

import co.edu.adopcionmascota.domain.model.User

interface UserRepository{

    suspend fun save(user: User)

    suspend fun findById(id: String): User?

    suspend fun login(email: String,password: String): User?

    suspend fun findByEmail(email: String):User?
}