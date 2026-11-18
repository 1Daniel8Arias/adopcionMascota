package co.edu.adopcionmascota.data.repository.remote

import co.edu.adopcionmascota.data.local.dao.PublicationDao
import co.edu.adopcionmascota.data.local.mapper.toDomain
import co.edu.adopcionmascota.data.remote.Collections
import co.edu.adopcionmascota.data.remote.publication.PublicationDto
import co.edu.adopcionmascota.domain.model.Publication
import co.edu.adopcionmascota.domain.model.PublicationStatus
import co.edu.adopcionmascota.domain.repository.ImageRepository
import co.edu.adopcionmascota.domain.repository.PublicationRepository
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

// Publicaciones: se escriben en Firestore y se leen de Room (caché sincronizada en tiempo real)
@Singleton
class PublicationRepositoryImpl @Inject constructor(
    firestore: FirebaseFirestore,
    private val publicationDao: PublicationDao,
    private val imageRepository: ImageRepository
) : PublicationRepository {

    private val collection = firestore.collection(Collections.PUBLICATIONS)

    override val publication: Flow<List<Publication>> =
        publicationDao.observeAll().map { list -> list.map { it.toDomain() } }

    override fun observeById(id: String): Flow<Publication?> =
        publicationDao.observeById(id).map { it?.toDomain() }

    override fun observeByOwner(ownerId: String): Flow<List<Publication>> =
        publicationDao.observeByOwner(ownerId).map { list -> list.map { it.toDomain() } }

    override fun observeByStatus(status: PublicationStatus): Flow<List<Publication>> =
        publicationDao.observeByStatus(status.name).map { list -> list.map { it.toDomain() } }

    override suspend fun save(publication: Publication) {
        // La foto elegida en la galería se sube a la nube y se guarda su URL
        val image = imageRepository.upload(publication.image, "publications/${publication.id}")
        val dto = PublicationDto.fromDomain(publication.copy(image = image))
        collection.document(dto.id).set(dto.toMap()).await()
        publicationDao.insert(dto.toEntity())
    }

    override suspend fun update(publication: Publication) {
        val image = imageRepository.upload(publication.image, "publications/${publication.id}")
        val dto = PublicationDto.fromDomain(publication.copy(image = image))
        // "likes" se maneja con incrementos atómicos y no se sobrescribe
        collection.document(dto.id).set(dto.toMap() - "likes", SetOptions.merge()).await()
        publicationDao.update(dto.toEntity())
    }

    override suspend fun findById(id: String): Publication? = publicationDao.findById(id)?.toDomain()

    override suspend fun addLikes(id: String, delta: Int) {
        collection.document(id).update("likes", FieldValue.increment(delta.toLong())).await()
        publicationDao.addLikes(id, delta)
    }

    override suspend fun updateStatus(id: String, status: PublicationStatus, rejectionReason: String?, reviewedAt: Long?) {
        val fields = mutableMapOf<String, Any?>("status" to status.name, "rejectionReason" to rejectionReason)
        if (reviewedAt != null) fields["reviewedAt"] = reviewedAt
        collection.document(id).update(fields).await()
        publicationDao.updateStatus(id, status.name, rejectionReason, reviewedAt)
    }
}
