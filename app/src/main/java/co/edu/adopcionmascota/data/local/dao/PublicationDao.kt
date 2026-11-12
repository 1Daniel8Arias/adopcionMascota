package co.edu.adopcionmascota.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import co.edu.adopcionmascota.data.local.entity.PublicationEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PublicationDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(publication: PublicationEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(publications: List<PublicationEntity>)

    @Update
    suspend fun update(publication: PublicationEntity)

    @Query("SELECT * FROM publications WHERE status != 'DELETED' ORDER BY createdAt DESC")
    fun observeAll(): Flow<List<PublicationEntity>>

    @Query("SELECT * FROM publications WHERE id = :id")
    fun observeById(id: String): Flow<PublicationEntity?>

    @Query("SELECT * FROM publications WHERE id = :id")
    suspend fun findById(id: String): PublicationEntity?

    @Query("SELECT * FROM publications WHERE ownerId = :ownerId AND status != 'DELETED' ORDER BY createdAt DESC")
    fun observeByOwner(ownerId: String): Flow<List<PublicationEntity>>

    @Query("SELECT * FROM publications WHERE status = :status ORDER BY createdAt DESC")
    fun observeByStatus(status: String): Flow<List<PublicationEntity>>

    @Query("UPDATE publications SET likes = MAX(likes + :delta, 0) WHERE id = :id")
    suspend fun addLikes(id: String, delta: Int)

    @Query("UPDATE publications SET status = :status, rejectionReason = :rejectionReason, reviewedAt = COALESCE(:reviewedAt, reviewedAt) WHERE id = :id")
    suspend fun updateStatus(id: String, status: String, rejectionReason: String?, reviewedAt: Long?)

    // Vacía la tabla: Room es una caché de Firestore y se reemplaza con cada snapshot
    @Query("DELETE FROM publications")
    suspend fun clear()
}
