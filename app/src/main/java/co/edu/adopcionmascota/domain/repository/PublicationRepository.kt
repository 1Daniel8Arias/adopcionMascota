package co.edu.adopcionmascota.domain.repository

import co.edu.adopcionmascota.domain.model.Publication
import kotlinx.coroutines.flow.StateFlow

interface PublicationRepository {

    val publication: StateFlow<List<Publication>>

    suspend fun save(publication: Publication)

    suspend fun findById(id: String): Publication?
}