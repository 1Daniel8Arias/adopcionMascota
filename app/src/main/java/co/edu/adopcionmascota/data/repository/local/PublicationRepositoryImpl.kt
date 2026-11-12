package co.edu.adopcionmascota.data.repository.local

import co.edu.adopcionmascota.data.local.dao.PublicationDao
import co.edu.adopcionmascota.data.local.mapper.toDomain
import co.edu.adopcionmascota.data.local.mapper.toEntity
import co.edu.adopcionmascota.domain.model.Publication
import co.edu.adopcionmascota.domain.model.PublicationStatus
import co.edu.adopcionmascota.domain.repository.PublicationRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PublicationRepositoryImpl @Inject constructor(
    private val publicationDao: PublicationDao
) : PublicationRepository {

    override val publication: Flow<List<Publication>> =
        publicationDao.observeAll().map { list -> list.map { it.toDomain() } }

    override fun observeById(id: String): Flow<Publication?> =
        publicationDao.observeById(id).map { it?.toDomain() }

    override fun observeByOwner(ownerId: String): Flow<List<Publication>> =
        publicationDao.observeByOwner(ownerId).map { list -> list.map { it.toDomain() } }

    override fun observeByStatus(status: PublicationStatus): Flow<List<Publication>> =
        publicationDao.observeByStatus(status.name).map { list -> list.map { it.toDomain() } }

    override suspend fun save(publication: Publication) {
        publicationDao.insert(publication.toEntity())
    }

    override suspend fun update(publication: Publication) {
        publicationDao.update(publication.toEntity())
    }

    override suspend fun findById(id: String): Publication? =
        publicationDao.findById(id)?.toDomain()

    override suspend fun addLikes(id: String, delta: Int) {
        publicationDao.addLikes(id, delta)
    }

    override suspend fun updateStatus(id: String, status: PublicationStatus, rejectionReason: String?, reviewedAt: Long?) {
        publicationDao.updateStatus(id, status.name, rejectionReason, reviewedAt)
    }
}
