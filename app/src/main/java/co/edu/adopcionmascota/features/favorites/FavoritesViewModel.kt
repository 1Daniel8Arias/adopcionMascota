package co.edu.adopcionmascota.features.favorites

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.edu.adopcionmascota.domain.model.Publication
import co.edu.adopcionmascota.domain.usecase.favorite.ObserveFavoritePublicationsUseCase
import co.edu.adopcionmascota.domain.usecase.favorite.ToggleFavoriteUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class FavoritesUiState(
    val isLoading: Boolean = true,
    val publications: List<Publication> = emptyList()
)

// "Mis favoritos": publicaciones a las que el usuario les dio "Me encanta" (Room)
@HiltViewModel
class FavoritesViewModel @Inject constructor(
    observeFavoritePublicationsUseCase: ObserveFavoritePublicationsUseCase,
    private val toggleFavoriteUseCase: ToggleFavoriteUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(FavoritesUiState())
    val uiState: StateFlow<FavoritesUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            observeFavoritePublicationsUseCase().collect { publications ->
                _uiState.update { it.copy(isLoading = false, publications = publications) }
            }
        }
    }

    // Quitar el corazón la saca de la lista
    fun onLikeClick(publicationId: String) {
        viewModelScope.launch { toggleFavoriteUseCase(publicationId) }
    }
}
