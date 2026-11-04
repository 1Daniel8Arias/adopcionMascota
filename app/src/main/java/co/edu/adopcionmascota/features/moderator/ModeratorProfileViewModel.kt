package co.edu.adopcionmascota.features.moderator

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.edu.adopcionmascota.domain.model.Publication
import co.edu.adopcionmascota.domain.model.PublicationStatus
import co.edu.adopcionmascota.domain.model.User
import co.edu.adopcionmascota.domain.usecase.auth.LogoutUseCase
import co.edu.adopcionmascota.domain.usecase.publication.ObserveReportsUseCase
import co.edu.adopcionmascota.domain.usecase.user.GetCurrentUserUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch


data class ProfileUiState(
    val user: User?=null,
    val myPublications: List<Publication> = emptyList(),
    val allPublications: List<Publication> = emptyList(),
    val poinst: Int = 0

)

@HiltViewModel
class ModeratorProfileViewModel @Inject constructor(
    private val getCurrentUserUseCase: GetCurrentUserUseCase,
    observerReportUseCase: ObserveReportsUseCase,
    private val logotUseCase: LogoutUseCase,
    ) : ViewModel(){

    private val user = MutableStateFlow<User?>(null)

    // El estado se recalcula cada vez que cambia el usuario o la lista de reportes
    val uiState: StateFlow<ProfileUiState> = combine(user, observerReportUseCase()) { user, publications ->
       val myPublications = publications.filter { it.status != PublicationStatus.DELETED }

        ProfileUiState(
            user = user,
            myPublications = myPublications,
            allPublications = publications ,

        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = ProfileUiState()
    )

    init {
        viewModelScope.launch {
            user.value = getCurrentUserUseCase()
        }
    }

    fun logout(){
        viewModelScope.launch {
            logotUseCase()
        }
    }

    }

