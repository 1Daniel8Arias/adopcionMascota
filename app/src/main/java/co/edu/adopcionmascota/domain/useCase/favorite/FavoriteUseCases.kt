package co.edu.adopcionmascota.domain.usecase.favorite

import co.edu.adopcionmascota.domain.model.Publication
import co.edu.adopcionmascota.domain.model.PublicationStatus
import co.edu.adopcionmascota.domain.repository.FavoriteRepository
import co.edu.adopcionmascota.domain.repository.PublicationRepository
import co.edu.adopcionmascota.domain.repository.SessionRepository
import co.edu.adopcionmascota.domain.usecase.user.GetCurrentUserUseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import javax.inject.Inject

// Ids de las publicaciones que le encantan al usuario de la sesión
class ObserveFavoriteIdsUseCase @Inject constructor(
    private val sessionRepository: SessionRepository,
    private val favoriteRepository: FavoriteRepository
) {
    @OptIn(ExperimentalCoroutinesApi::class)
    operator fun invoke(): Flow<Set<String>> = sessionRepository.session.flatMapLatest { session ->
        if (session == null) flowOf(emptySet())
        else favoriteRepository.observeIds(session.userId).map { it.toSet() }
    }
}

// "Mis favoritos": publicaciones que le encantan al usuario, en el orden en que las marcó.
// Las eliminadas no aparecen.
class ObserveFavoritePublicationsUseCase @Inject constructor(
    private val sessionRepository: SessionRepository,
    private val favoriteRepository: FavoriteRepository,
    private val publicationRepository: PublicationRepository
) {
    @OptIn(ExperimentalCoroutinesApi::class)
    operator fun invoke(): Flow<List<Publication>> = sessionRepository.session.flatMapLatest { session ->
        if (session == null) flowOf(emptyList())
        else combine(favoriteRepository.observeIds(session.userId), publicationRepository.publication) { ids, publications ->
            val byId = publications.associateBy { it.id }
            ids.mapNotNull { byId[it] }.filter { it.status != PublicationStatus.DELETED }
        }
    }
}

// Da o quita "Me encanta" y actualiza el contador de la publicación.
// Devuelve true si quedó marcada como favorita.
class ToggleFavoriteUseCase @Inject constructor(
    private val getCurrentUserUseCase: GetCurrentUserUseCase,
    private val favoriteRepository: FavoriteRepository,
    private val publicationRepository: PublicationRepository
) {
    suspend operator fun invoke(publicationId: String): Boolean {
        val user = getCurrentUserUseCase() ?: throw IllegalStateException("No hay una sesión activa")
        return if (favoriteRepository.isFavorite(user.id, publicationId)) {
            favoriteRepository.remove(user.id, publicationId)
            publicationRepository.addLikes(publicationId, -1)
            false
        } else {
            favoriteRepository.add(user.id, publicationId)
            publicationRepository.addLikes(publicationId, 1)
            true
        }
    }
}
