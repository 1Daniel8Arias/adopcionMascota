package co.edu.adopcionmascota.features.adoption.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.edu.adopcionmascota.R
import co.edu.adopcionmascota.core.util.RequestResult
import co.edu.adopcionmascota.core.util.ResourceProvider
import co.edu.adopcionmascota.domain.model.AdoptionRequestStatus
import co.edu.adopcionmascota.domain.model.AdoptionRequestType
import co.edu.adopcionmascota.domain.model.Comment
import co.edu.adopcionmascota.domain.model.PetType
import co.edu.adopcionmascota.domain.model.Publication
import co.edu.adopcionmascota.domain.model.User
import co.edu.adopcionmascota.domain.model.UserRole
import co.edu.adopcionmascota.domain.usecase.comment.ObserveCommentsUseCase
import co.edu.adopcionmascota.domain.usecase.comment.ToggleThanksUseCase
import co.edu.adopcionmascota.domain.usecase.publication.ObservePublicationUseCase
import co.edu.adopcionmascota.domain.usecase.request.ObserveMyRequestsUseCase
import co.edu.adopcionmascota.domain.usecase.request.SendAdoptionRequestUseCase
import co.edu.adopcionmascota.domain.usecase.user.GetUserByIdUseCase
import co.edu.adopcionmascota.domain.usecase.user.ObserveCurrentUserUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import co.edu.adopcionmascota.domain.usecase.favorite.ObserveFavoriteIdsUseCase
import co.edu.adopcionmascota.domain.usecase.favorite.ToggleFavoriteUseCase

// Datos de la ficha que se muestran en las tarjetas; se construyen con la publicación y su autor
data class PetDetailInfo(
    val breed: String,
    val size: String,
    val healthTags: List<String>,
    val history: String,
    val publisherName: String,
    val publisherLevel: String,
    val publisherPhone: String
)

// Tipo de solicitud que el usuario puede enviar desde la ficha
enum class DetailRequest { ADOPTION, TEMPORARY_HOME }

data class PublicationDetailUiState(
    val publication: Publication? = null,
    val info: PetDetailInfo? = null,
    val likes: Int = 0,
    val isLiked: Boolean = false,
    val comments: List<Comment> = emptyList(),
    val primaryActionText: String = "",     // Texto del botón principal según el tipo
    val statusText: String = "",
    val canSendRequests: Boolean = false,   // El autor y el moderador no envían solicitudes

    // Solicitud de adopción / hogar de paso
    val pendingRequest: DetailRequest? = null,      // Diálogo de confirmación abierto
    val sentRequests: Set<DetailRequest> = emptySet(),
    val requestResult: RequestResult? = null,
    val infoMessage: String? = null
) {
    val commentsCount: Int
        get() = comments.size
}

@HiltViewModel
class PublicationDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    observePublicationUseCase: ObservePublicationUseCase,
    observeCommentsUseCase: ObserveCommentsUseCase,
    observeCurrentUserUseCase: ObserveCurrentUserUseCase,
    observeMyRequestsUseCase: ObserveMyRequestsUseCase,
    private val getUserByIdUseCase: GetUserByIdUseCase,
    private val toggleThanksUseCase: ToggleThanksUseCase,
    private val sendAdoptionRequestUseCase: SendAdoptionRequestUseCase,
    observeFavoriteIdsUseCase: ObserveFavoriteIdsUseCase,
    private val toggleFavoriteUseCase: ToggleFavoriteUseCase,
    private val resources: ResourceProvider
) : ViewModel() {

    // Hilt no recibe parámetros propios: el id llega con los argumentos de la ruta
    private val publicationId: String = checkNotNull(savedStateHandle["publicationId"])

    private val _uiState = MutableStateFlow(PublicationDetailUiState())
    val uiState: StateFlow<PublicationDetailUiState> = _uiState.asStateFlow()

    init {
        // La publicación, el usuario actual y su autor vienen de Room
        viewModelScope.launch {
            combine(observePublicationUseCase(publicationId), observeCurrentUserUseCase()) { publication, user ->
                publication to user
            }.collect { (publication, user) ->
                val owner = publication?.let { getUserByIdUseCase(it.ownerId) }
                _uiState.update {
                    it.copy(
                        publication = publication,
                        info = publication?.let { p -> buildInfo(p, owner) },
                        likes = publication?.likes ?: 0,
                        primaryActionText = primaryActionFor(publication),
                        statusText = statusFor(publication),
                        canSendRequests = publication != null && user != null &&
                                user.role == UserRole.USER && user.id != publication.ownerId
                    )
                }
            }
        }

        // "Me encanta" del usuario guardado en Room
        viewModelScope.launch {
            observeFavoriteIdsUseCase().collect { ids ->
                _uiState.update { it.copy(isLiked = publicationId in ids) }
            }
        }

        // Al publicar un comentario nuevo, el detalle se actualiza solo
        viewModelScope.launch {
            observeCommentsUseCase(publicationId).collect { comments ->
                _uiState.update { it.copy(comments = comments) }
            }
        }

        // Las solicitudes ya enviadas siguen marcadas aunque se cierre la app
        viewModelScope.launch {
            observeMyRequestsUseCase(publicationId).collect { requests ->
                val sent = requests.filter { it.status == AdoptionRequestStatus.PENDING }.map {
                    if (it.type == AdoptionRequestType.ADOPTION) DetailRequest.ADOPTION else DetailRequest.TEMPORARY_HOME
                }.toSet()
                _uiState.update { it.copy(sentRequests = sent) }
            }
        }
    }

    private fun buildInfo(publication: Publication, owner: User?): PetDetailInfo {
        val tags = buildList {
            if (publication.isVaccinated) add(resources.getString(R.string.vaccinated))
            if (publication.isSterilized) add(resources.getString(R.string.sterilized))
            if (publication.isDewormed) add(resources.getString(R.string.dewormed))
            if (publication.hasMicrochip) add(resources.getString(R.string.microchip))
        }
        return PetDetailInfo(
            breed = publication.breed.ifBlank { publication.species },
            size = publication.size,
            healthTags = tags,
            history = publication.description,
            publisherName = owner?.name.orEmpty(),
            publisherLevel = owner?.let { resources.getString(R.string.level_tag, it.level.number) + " · " + it.level.displayName }.orEmpty(),
            publisherPhone = owner?.phoneNumber?.filter { it.isDigit() }.orEmpty()
        )
    }

    private fun primaryActionFor(publication: Publication?): String {
        val name = publication?.name.orEmpty()
        return when (publication?.type) {
            PetType.PERDIDO -> resources.getString(R.string.detail_action_seen)
            PetType.ENCONTRADO -> resources.getString(R.string.detail_action_mine)
            PetType.VETERINARIA -> resources.getString(R.string.detail_action_vet)
            else -> resources.getString(R.string.detail_action_adopt, name)
        }
    }

    private fun statusFor(publication: Publication?): String = when (publication?.type) {
        PetType.PERDIDO -> resources.getString(R.string.detail_status_wanted)
        PetType.ENCONTRADO -> resources.getString(R.string.detail_status_family)
        PetType.VETERINARIA -> resources.getString(R.string.detail_status_vet)
        else -> resources.getString(R.string.detail_status_home)
    }

    fun onThanksClick(commentId: String) {
        viewModelScope.launch { toggleThanksUseCase(commentId) }
    }

    // El contador y el corazón se actualizan solos desde Room
    fun onLikeClick() {
        viewModelScope.launch { toggleFavoriteUseCase(publicationId) }
    }

    // ---------- Solicitudes ----------

    fun onRequestClick(request: DetailRequest) {
        if (request in _uiState.value.sentRequests) {
            _uiState.update { it.copy(infoMessage = resources.getString(R.string.request_already_sent)) }
            return
        }
        _uiState.update { it.copy(pendingRequest = request) }
    }

    fun onDismissRequest() {
        _uiState.update { it.copy(pendingRequest = null) }
    }

    // La solicitud se guarda en Room y el autor recibe una notificación
    fun onConfirmRequest() {
        val request = _uiState.value.pendingRequest ?: return
        _uiState.update { it.copy(pendingRequest = null) }

        viewModelScope.launch {
            _uiState.update { it.copy(requestResult = RequestResult.Loading) }
            try {
                val type = if (request == DetailRequest.ADOPTION) AdoptionRequestType.ADOPTION else AdoptionRequestType.TEMPORARY_HOME
                sendAdoptionRequestUseCase(publicationId, type)
                val name = _uiState.value.publication?.name.orEmpty()
                val message = when (request) {
                    DetailRequest.ADOPTION -> when (_uiState.value.publication?.type) {
                        PetType.PERDIDO -> resources.getString(R.string.request_lost_sent, name)
                        PetType.ENCONTRADO -> resources.getString(R.string.request_found_sent, name)
                        else -> resources.getString(R.string.request_adopt_sent, name)
                    }
                    DetailRequest.TEMPORARY_HOME -> resources.getString(R.string.request_foster_sent, name)
                }
                _uiState.update { it.copy(requestResult = RequestResult.Success(message)) }
            } catch (e: Exception) {
                _uiState.update { it.copy(requestResult = RequestResult.Failure(e.message.orEmpty())) }
            }
        }
    }

    fun resetRequestResult() {
        _uiState.update { it.copy(requestResult = null) }
    }

    fun showInfo(message: String) {
        _uiState.update { it.copy(infoMessage = message) }
    }

    fun resetInfoMessage() {
        _uiState.update { it.copy(infoMessage = null) }
    }
}
