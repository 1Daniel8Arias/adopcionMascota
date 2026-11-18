package co.edu.adopcionmascota.domain.repository

import co.edu.adopcionmascota.domain.model.Publication
import co.edu.adopcionmascota.domain.model.PublicationStatus
import kotlinx.coroutines.flow.Flow

interface PublicationRepository {

    // Todas las publicaciones (excepto las eliminadas), de la más reciente a la más antigua
    val publication: Flow<List<Publication>>

    fun observeById(id: String): Flow<Publication?>

    fun observeByOwner(ownerId: String): Flow<List<Publication>>

    fun observeByStatus(status: PublicationStatus): Flow<List<Publication>>

    suspend fun save(publication: Publication)

    suspend fun update(publication: Publication)

    suspend fun findById(id: String): Publication?

    // Suma o resta "Me encanta" al contador de la publicación
    suspend fun addLikes(id: String, delta: Int)

    suspend fun updateStatus(id: String, status: PublicationStatus, rejectionReason: String? = null, reviewedAt: Long? = null)
}
