package co.edu.adopcionmascota.domain.repository

import kotlinx.coroutines.flow.Flow

// Publicaciones a las que un usuario les dio "Me encanta"
interface FavoriteRepository {

    fun observeIds(userId: String): Flow<List<String>>

    suspend fun isFavorite(userId: String, publicationId: String): Boolean

    suspend fun add(userId: String, publicationId: String)

    suspend fun remove(userId: String, publicationId: String)
}
