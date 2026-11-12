package co.edu.adopcionmascota.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import co.edu.adopcionmascota.data.local.entity.AdoptionRequestEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AdoptionRequestDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(request: AdoptionRequestEntity)

    @Query("SELECT * FROM adoption_requests ORDER BY createdAt DESC")
    fun observeAll(): Flow<List<AdoptionRequestEntity>>

    @Query("SELECT * FROM adoption_requests WHERE ownerId = :ownerId ORDER BY createdAt DESC")
    fun observeByOwner(ownerId: String): Flow<List<AdoptionRequestEntity>>

    @Query("SELECT * FROM adoption_requests WHERE requesterId = :requesterId AND publicationId = :publicationId")
    fun observeByRequester(requesterId: String, publicationId: String): Flow<List<AdoptionRequestEntity>>

    @Query("SELECT * FROM adoption_requests WHERE id = :id")
    suspend fun findById(id: String): AdoptionRequestEntity?

    @Query("UPDATE adoption_requests SET status = :status WHERE id = :id")
    suspend fun updateStatus(id: String, status: String)

    // Vacía la tabla: Room es una caché de Firestore y se reemplaza con cada snapshot
    @Query("DELETE FROM adoption_requests")
    suspend fun clear()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<AdoptionRequestEntity>)
}
