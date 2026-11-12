package co.edu.adopcionmascota.data.repository.remote

import co.edu.adopcionmascota.data.local.dao.FavoriteDao
import co.edu.adopcionmascota.data.local.entity.FavoriteEntity
import co.edu.adopcionmascota.data.remote.Collections
import co.edu.adopcionmascota.domain.repository.FavoriteRepository
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

// Me encanta: subcolección users/{uid}/favorites/{publicationId}
@Singleton
class FavoriteRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val favoriteDao: FavoriteDao
) : FavoriteRepository {

    private fun doc(userId: String, publicationId: String) =
        firestore.collection(Collections.USERS).document(userId)
            .collection(Collections.FAVORITES).document(publicationId)

    override fun observeIds(userId: String): Flow<List<String>> = favoriteDao.observeIdsByUser(userId)

    override suspend fun isFavorite(userId: String, publicationId: String): Boolean =
        favoriteDao.exists(userId, publicationId)

    override suspend fun add(userId: String, publicationId: String) {
        val now = System.currentTimeMillis()
        doc(userId, publicationId).set(mapOf("createdAt" to now)).await()
        favoriteDao.insert(FavoriteEntity(userId, publicationId, now))
    }

    override suspend fun remove(userId: String, publicationId: String) {
        doc(userId, publicationId).delete().await()
        favoriteDao.delete(userId, publicationId)
    }
}
