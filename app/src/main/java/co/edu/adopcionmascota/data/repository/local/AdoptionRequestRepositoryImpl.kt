package co.edu.adopcionmascota.data.repository.local

import co.edu.adopcionmascota.data.local.dao.AdoptionRequestDao
import co.edu.adopcionmascota.data.local.mapper.toDomain
import co.edu.adopcionmascota.data.local.mapper.toEntity
import co.edu.adopcionmascota.domain.model.AdoptionRequest
import co.edu.adopcionmascota.domain.model.AdoptionRequestStatus
import co.edu.adopcionmascota.domain.repository.AdoptionRequestRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AdoptionRequestRepositoryImpl @Inject constructor(
    private val adoptionRequestDao: AdoptionRequestDao
) : AdoptionRequestRepository {

    override val requests: Flow<List<AdoptionRequest>> =
        adoptionRequestDao.observeAll().map { list -> list.map { it.toDomain() } }

    override fun observeByOwner(ownerId: String): Flow<List<AdoptionRequest>> =
        adoptionRequestDao.observeByOwner(ownerId).map { list -> list.map { it.toDomain() } }

    override fun observeByRequester(requesterId: String, publicationId: String): Flow<List<AdoptionRequest>> =
        adoptionRequestDao.observeByRequester(requesterId, publicationId).map { list -> list.map { it.toDomain() } }

    override suspend fun findById(id: String): AdoptionRequest? = adoptionRequestDao.findById(id)?.toDomain()

    override suspend fun save(request: AdoptionRequest) {
        adoptionRequestDao.insert(request.toEntity())
    }

    override suspend fun updateStatus(id: String, status: AdoptionRequestStatus) {
        adoptionRequestDao.updateStatus(id, status.name)
    }
}
