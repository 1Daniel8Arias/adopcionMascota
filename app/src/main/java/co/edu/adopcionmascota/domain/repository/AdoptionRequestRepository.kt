package co.edu.adopcionmascota.domain.repository

import co.edu.adopcionmascota.domain.model.AdoptionRequest
import co.edu.adopcionmascota.domain.model.AdoptionRequestStatus
import kotlinx.coroutines.flow.Flow

interface AdoptionRequestRepository {

    val requests: Flow<List<AdoptionRequest>>

    // Solicitudes que recibe el autor de las publicaciones
    fun observeByOwner(ownerId: String): Flow<List<AdoptionRequest>>

    // Solicitudes que un usuario envió para una publicación
    fun observeByRequester(requesterId: String, publicationId: String): Flow<List<AdoptionRequest>>

    suspend fun findById(id: String): AdoptionRequest?

    suspend fun save(request: AdoptionRequest)

    suspend fun updateStatus(id: String, status: AdoptionRequestStatus)
}
