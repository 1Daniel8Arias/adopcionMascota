package co.edu.adopcionmascota.features.adoption.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.edu.adopcionmascota.core.util.RequestResult
import co.edu.adopcionmascota.domain.PetType
import co.edu.adopcionmascota.domain.Publication
import co.edu.adopcionmascota.features.adoption.list.samplePublications
import co.edu.adopcionmascota.features.comment.Comment
import co.edu.adopcionmascota.features.comment.CommentStore
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// Datos de la ficha que no están en la publicación del feed (quemados por mascota)
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

    // Solicitud de adopción / hogar de paso
    val pendingRequest: DetailRequest? = null,      // Diálogo de confirmación abierto
    val sentRequests: Set<DetailRequest> = emptySet(),
    val requestResult: RequestResult? = null,
    val infoMessage: String? = null
) {
    val commentsCount: Int
        get() = comments.size

    // Texto del botón principal según el tipo de publicación
    val primaryActionText: String
        get() {
            val name = publication?.name.orEmpty()
            return when (publication?.type) {
                PetType.PERDIDO -> "Lo he visto"
                PetType.ENCONTRADO -> "Es mi mascota"
                else -> "Adoptar a $name"
            }
        }

    val statusText: String
        get() = when (publication?.type) {
            PetType.PERDIDO -> "Se busca"
            PetType.ENCONTRADO -> "Busca a su familia"
            else -> "Busca Hogar"
        }
}

// Información quemada de las mascotas del feed
private val PET_DETAILS = mapOf(
    "1" to PetDetailInfo(
        breed = "Golden Mestizo",
        size = "Tamaño Mediano",
        healthTags = listOf("Vacunación al día", "Esterilizado", "Desparasitado", "Chip ID", "Apto para gatos"),
        history = "Max fue rescatado de una quebrada cerca de La Calera. Es juguetón, se lleva bien con niños y otros perros y ya está listo para un hogar definitivo.",
        publisherName = "Fundación Rescate",
        publisherLevel = "Nivel 3 · Guardián",
        publisherPhone = "573001234567"
    ),
    "2" to PetDetailInfo(
        breed = "Siamesa mestiza",
        size = "Tamaño Pequeño",
        healthTags = listOf("Vacunada", "Esterilizada", "Collar rojo"),
        history = "Luna salió de casa en Circasia y no ha regresado. Es tranquila y cariñosa; si la ves, no la persigas y avisa por la app.",
        publisherName = "Camila Morales",
        publisherLevel = "Nivel 2 · Protectora",
        publisherPhone = "573101234567"
    ),
    "3" to PetDetailInfo(
        breed = "Mestizo",
        size = "Tamaño Grande",
        healthTags = listOf("Desparasitado", "Valoración veterinaria pendiente"),
        history = "Rocky fue encontrado en Armenia cerca del parque principal. Está a salvo en un hogar de paso mientras aparece su familia.",
        publisherName = "Refugio Colitas Felices",
        publisherLevel = "Nivel 3 · Guardián",
        publisherPhone = "573209876543"
    )
)

class PublicationDetailViewModel(
    private val publicationId: String
) : ViewModel() {

    private val _uiState = MutableStateFlow(PublicationDetailUiState())
    val uiState: StateFlow<PublicationDetailUiState> = _uiState.asStateFlow()

    init {
        loadPublication()

        // Al publicar un comentario nuevo, el detalle se actualiza solo
        viewModelScope.launch {
            CommentStore.comments.collect { all ->
                _uiState.update { state ->
                    state.copy(comments = all.filter { it.publicationId == publicationId })
                }
            }
        }
    }

    // Por ahora la publicación sale de los datos quemados del feed
    private fun loadPublication() {
        val publication = samplePublications().find { it.id == publicationId }
        _uiState.update {
            it.copy(
                publication = publication,
                info = PET_DETAILS[publicationId],
                likes = publication?.likes?.toIntOrNull() ?: 0
            )
        }
    }

    fun onThanksClick(commentId: String) {
        CommentStore.toggleThanks(commentId)
    }

    fun onLikeClick() {
        _uiState.update {
            it.copy(
                isLiked = !it.isLiked,
                likes = it.likes + if (it.isLiked) -1 else 1
            )
        }
    }

    // ---------- Solicitudes ----------

    fun onRequestClick(request: DetailRequest) {
        if (request in _uiState.value.sentRequests) {
            _uiState.update { it.copy(infoMessage = "Ya enviaste esta solicitud") }
            return
        }
        _uiState.update { it.copy(pendingRequest = request) }
    }

    fun onDismissRequest() {
        _uiState.update { it.copy(pendingRequest = null) }
    }

    fun onConfirmRequest() {
        val request = _uiState.value.pendingRequest ?: return
        _uiState.update { it.copy(pendingRequest = null) }

        viewModelScope.launch {
            _uiState.update { it.copy(requestResult = RequestResult.Loading) }
            delay(1200) // Simula el envío al publicador
            val name = _uiState.value.publication?.name.orEmpty()
            val message = when (request) {
                DetailRequest.ADOPTION -> when (_uiState.value.publication?.type) {
                    PetType.PERDIDO -> "Avisamos al tutor de $name que lo viste"
                    PetType.ENCONTRADO -> "Enviamos tu solicitud para reclamar a $name"
                    else -> "Solicitud de adopción de $name enviada"
                }
                DetailRequest.TEMPORARY_HOME -> "Te ofreciste como hogar de paso para $name"
            }
            _uiState.update {
                it.copy(
                    sentRequests = it.sentRequests + request,
                    requestResult = RequestResult.Success(message)
                )
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
