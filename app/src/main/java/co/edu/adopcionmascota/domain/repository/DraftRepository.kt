package co.edu.adopcionmascota.domain.repository

import kotlinx.coroutines.flow.Flow

// Borradores de comentarios por publicación ("Borrador autoguardado")
interface DraftRepository {

    fun observeDraft(publicationId: String): Flow<String>

    suspend fun saveDraft(publicationId: String, text: String)

    suspend fun clearDraft(publicationId: String)
}
