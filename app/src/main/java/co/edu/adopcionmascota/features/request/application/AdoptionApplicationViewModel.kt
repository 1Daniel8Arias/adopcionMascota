package co.edu.adopcionmascota.features.request.application

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.edu.adopcionmascota.R
import co.edu.adopcionmascota.core.util.RequestResult
import co.edu.adopcionmascota.core.util.ResourceProvider
import co.edu.adopcionmascota.domain.exception.AiRateLimitException
import co.edu.adopcionmascota.domain.model.AdoptionApplication
import co.edu.adopcionmascota.domain.model.AdoptionRequestType
import co.edu.adopcionmascota.domain.model.AvailableTime
import co.edu.adopcionmascota.domain.model.HousingType
import co.edu.adopcionmascota.domain.model.OtherPets
import co.edu.adopcionmascota.domain.model.PetExperience
import co.edu.adopcionmascota.domain.usecase.publication.ObservePublicationUseCase
import co.edu.adopcionmascota.domain.usecase.request.GenerateAdoptionLetterUseCase
import co.edu.adopcionmascota.domain.usecase.request.SendAdoptionRequestUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Locale
import javax.inject.Inject

data class AdoptionApplicationUiState(
    val petName: String = "",
    val housing: HousingType? = null,
    val experience: PetExperience? = null,
    val availableTime: AvailableTime? = null,
    val otherPets: OtherPets? = null,
    val extraInfo: String = "",
    val letter: String = "",
    val showLetterEditor: Boolean = false,  // Paso 2: revisar y editar la carta
    val aiGenerated: Boolean = false,
    val isGenerating: Boolean = false,
    val aiMessage: String? = null,          // Aviso amable cuando la IA no está disponible
    val sendResult: RequestResult? = null
) {
    val answersComplete: Boolean
        get() = housing != null && experience != null && availableTime != null && otherPets != null

    val canSend: Boolean
        get() = answersComplete && letter.trim().length >= MIN_LETTER && sendResult != RequestResult.Loading

    companion object { const val MIN_LETTER = 40 }
}

// Solicitud formal de adopción: cuestionario -> carta redactada por la IA -> revisión -> envío
@HiltViewModel
class AdoptionApplicationViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    observePublicationUseCase: ObservePublicationUseCase,
    private val generateAdoptionLetterUseCase: GenerateAdoptionLetterUseCase,
    private val sendAdoptionRequestUseCase: SendAdoptionRequestUseCase,
    private val resources: ResourceProvider
) : ViewModel() {

    private val publicationId: String = checkNotNull(savedStateHandle["publicationId"])

    private val _uiState = MutableStateFlow(AdoptionApplicationUiState())
    val uiState: StateFlow<AdoptionApplicationUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            val publication = observePublicationUseCase(publicationId).first()
            _uiState.update { it.copy(petName = publication?.name.orEmpty()) }
        }
    }

    fun onHousingChange(value: HousingType) = _uiState.update { it.copy(housing = value) }
    fun onExperienceChange(value: PetExperience) = _uiState.update { it.copy(experience = value) }
    fun onTimeChange(value: AvailableTime) = _uiState.update { it.copy(availableTime = value) }
    fun onOtherPetsChange(value: OtherPets) = _uiState.update { it.copy(otherPets = value) }
    fun onExtraInfoChange(value: String) = _uiState.update { it.copy(extraInfo = value.take(500)) }
    fun onLetterChange(value: String) = _uiState.update { it.copy(letter = value) }

    private fun application(): AdoptionApplication? {
        val s = _uiState.value
        return AdoptionApplication(
            housing = s.housing ?: return null,
            experience = s.experience ?: return null,
            availableTime = s.availableTime ?: return null,
            otherPets = s.otherPets ?: return null,
            extraInfo = s.extraInfo.trim()
        )
    }

    // La IA redacta el borrador; el usuario lo revisa y edita antes de enviarlo
    fun onGenerateLetter() {
        val application = application() ?: return
        viewModelScope.launch {
            _uiState.update { it.copy(isGenerating = true, aiMessage = null) }
            try {
                val letter = generateAdoptionLetterUseCase(publicationId, application, Locale.getDefault().language)
                _uiState.update {
                    it.copy(letter = letter, aiGenerated = true, showLetterEditor = true, isGenerating = false)
                }
            } catch (e: AiRateLimitException) {
                // 429: el modelo gratuito está saturado. Se ofrece escribir la carta a mano
                _uiState.update {
                    it.copy(isGenerating = false, showLetterEditor = true, aiMessage = resources.getString(R.string.ai_rate_limited))
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(isGenerating = false, showLetterEditor = true, aiMessage = resources.getString(R.string.ai_unavailable))
                }
            }
        }
    }

    // Escribir la carta sin la IA
    fun onWriteManually() = _uiState.update { it.copy(showLetterEditor = true, aiGenerated = false) }

    fun onBackToQuestions() = _uiState.update { it.copy(showLetterEditor = false) }

    fun onSend() {
        val application = application() ?: return
        if (!_uiState.value.canSend) return
        viewModelScope.launch {
            _uiState.update { it.copy(sendResult = RequestResult.Loading) }
            val state = _uiState.value
            try {
                sendAdoptionRequestUseCase(
                    publicationId = publicationId,
                    type = AdoptionRequestType.ADOPTION,
                    message = state.letter,
                    application = application,
                    aiGenerated = state.aiGenerated
                )
                _uiState.update {
                    it.copy(sendResult = RequestResult.Success(resources.getString(R.string.request_adopt_sent, state.petName)))
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(sendResult = RequestResult.Failure(e.message ?: resources.getString(R.string.network_error)))
                }
            }
        }
    }

    fun resetSendResult() = _uiState.update { it.copy(sendResult = null) }
}
