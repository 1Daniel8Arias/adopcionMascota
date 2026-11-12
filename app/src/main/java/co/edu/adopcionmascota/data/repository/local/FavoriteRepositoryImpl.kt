package co.edu.adopcionmascota.data.repository.local

import co.edu.adopcionmascota.data.local.dao.FavoriteDao
import co.edu.adopcionmascota.data.local.entity.FavoriteEntity
import co.edu.adopcionmascota.domain.repository.FavoriteRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FavoriteRepositoryImpl @Inject constructor(
    private val favoriteDao: FavoriteDao
) : FavoriteRepository {

    override fun observeIds(userId: String): Flow<List<String>> = favoriteDao.observeIdsByUser(userId)

    override suspend fun isFavorite(userId: String, publicationId: String): Boolean =
        favoriteDao.exists(userId, publicationId)

    override suspend fun add(userId: String, publicationId: String) {
        favoriteDao.insert(FavoriteEntity(userId, publicationId, System.currentTimeMillis()))
    }

    override suspend fun remove(userId: String, publicationId: String) {
        favoriteDao.delete(userId, publicationId)
    }
}
