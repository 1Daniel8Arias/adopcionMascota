package co.edu.adopcionmascota.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import co.edu.adopcionmascota.data.local.entity.CommentEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CommentDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(comment: CommentEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(comments: List<CommentEntity>)

    @Query("SELECT * FROM comments ORDER BY createdAt DESC")
    fun observeAll(): Flow<List<CommentEntity>>

    @Query("SELECT * FROM comments WHERE publicationId = :publicationId ORDER BY createdAt DESC")
    fun observeByPublication(publicationId: String): Flow<List<CommentEntity>>

    @Query(
        "UPDATE comments SET " +
            "thanks = thanks + CASE WHEN thankedByMe THEN -1 ELSE 1 END, " +
            "thankedByMe = NOT thankedByMe WHERE id = :id"
    )
    suspend fun toggleThanks(id: String)

    // Vacía la tabla: Room es una caché de Firestore y se reemplaza con cada snapshot
    @Query("DELETE FROM comments")
    suspend fun clear()
}
