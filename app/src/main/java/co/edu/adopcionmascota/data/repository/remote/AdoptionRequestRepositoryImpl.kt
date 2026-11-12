package co.edu.adopcionmascota.data.repository.remote

import co.edu.adopcionmascota.data.local.dao.AdoptionRequestDao
import co.edu.adopcionmascota.data.local.mapper.toDomain
import co.edu.adopcionmascota.data.remote.dto.AdoptionRequestDto
import co.edu.adopcionmascota.data.remote.dto.Collections
import co.edu.adopcionmascota.domain.model.AdoptionRequest
import co.edu.adopcionmascota.domain.model.AdoptionRequestStatus
import co.edu.adopcionmascota.domain.repository.AdoptionRequestRepository
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AdoptionRequestRepositoryImpl @Inject constructor(
    firestore: FirebaseFirestore,
    private val adoptionRequestDao: AdoptionRequestDao
) : AdoptionRequestRepository {

    private val collection = firestore.collection(Collections.ADOPTION_REQUESTS)

    override val requests: Flow<List<AdoptionRequest>> =
        adoptionRequestDao.observeAll().map { list -> list.map { it.toDomain() } }

    override fun observeByOwner(ownerId: String): Flow<List<AdoptionRequest>> =
        adoptionRequestDao.observeByOwner(ownerId).map { list -> list.map { it.toDomain() } }

    override fun observeByRequester(requesterId: String, publicationId: String): Flow<List<AdoptionRequest>> =
        adoptionRequestDao.observeByRequester(requesterId, publicationId).map { list -> list.map { it.toDomain() } }

    override suspend fun findById(id: String): AdoptionRequest? = adoptionRequestDao.findById(id)?.toDomain()

    override suspend fun save(request: AdoptionRequest) {
        val dto = AdoptionRequestDto.fromDomain(request)
        collection.document(dto.id).set(dto.toMap()).await()
        adoptionRequestDao.insert(dto.toEntity())
    }

    override suspend fun updateStatus(id: String, status: AdoptionRequestStatus) {
        collection.document(id).update("status", status.name).await()
        adoptionRequestDao.updateStatus(id, status.name)
    }
}
