package co.edu.adopcionmascota.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import co.edu.adopcionmascota.data.local.entity.FavoriteEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoriteDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(favorite: FavoriteEntity): Long

    @Query("DELETE FROM favorites WHERE userId = :userId AND publicationId = :publicationId")
    suspend fun delete(userId: String, publicationId: String): Int

    @Query("SELECT EXISTS(SELECT 1 FROM favorites WHERE userId = :userId AND publicationId = :publicationId)")
    suspend fun exists(userId: String, publicationId: String): Boolean

    // Ids de las publicaciones que le encantan al usuario, de la más reciente a la más antigua
    @Query("SELECT publicationId FROM favorites WHERE userId = :userId ORDER BY createdAt DESC")
    fun observeIdsByUser(userId: String): Flow<List<String>>

    // Vacía la tabla: Room es una caché de Firestore y se reemplaza con cada snapshot
    @Query("DELETE FROM favorites")
    suspend fun clear()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<FavoriteEntity>)
}
