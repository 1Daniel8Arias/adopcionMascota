package co.edu.adopcionmascota.features.notification

import co.edu.adopcionmascota.R
import androidx.annotation.StringRes
import javax.inject.Inject
import dagger.hilt.android.lifecycle.HiltViewModel
import co.edu.adopcionmascota.core.util.ResourceProvider
import co.edu.adopcionmascota.domain.model.Notification
import co.edu.adopcionmascota.domain.model.NotificationType
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import co.edu.adopcionmascota.domain.usecase.notification.MarkAllNotificationsAsReadUseCase
import co.edu.adopcionmascota.domain.usecase.notification.MarkNotificationAsReadUseCase
import co.edu.adopcionmascota.domain.usecase.notification.ObserveMyNotificationsUseCase
import co.edu.adopcionmascota.domain.usecase.user.ObserveCurrentUserUseCase

// Filtros del Centro de Notificaciones
enum class NotificationFilter(@StringRes val label: Int) {
    ALL(R.string.all),
    UNREAD(R.string.filter_unread),
    COMMENTS(R.string.comments),
    ACHIEVEMENTS(R.string.filter_achievements)
}

data class NotificationsUiState(
    val userName: String = "",
    val userLocation: String = "",
    val notifications: List<Notification> = emptyList(),
    val filter: NotificationFilter = NotificationFilter.ALL,
    val infoMessage: String? = null
) {
    val unreadCount: Int
        get() = notifications.count { !it.isRead }

    val totalCount: Int
        get() = notifications.size

    // La lista visible depende del filtro seleccionado
    val visibleNotifications: List<Notification>
        get() = when (filter) {
            NotificationFilter.ALL -> notifications
            NotificationFilter.UNREAD -> notifications.filter { !it.isRead }
            NotificationFilter.COMMENTS -> notifications.filter { it.type == NotificationType.NEW_COMMENT }
            NotificationFilter.ACHIEVEMENTS -> notifications.filter { it.isAchievement }
        }

    // Cantidad que se muestra junto al filtro; null = no lleva cantidad
    fun countFor(filter: NotificationFilter): Int? = when (filter) {
        NotificationFilter.ALL -> totalCount
        NotificationFilter.UNREAD -> unreadCount
        else -> null
    }
}

@HiltViewModel
class NotificationsViewModel @Inject constructor(
    observeMyNotificationsUseCase: ObserveMyNotificationsUseCase,
    observeCurrentUserUseCase: ObserveCurrentUserUseCase,
    private val markNotificationAsReadUseCase: MarkNotificationAsReadUseCase,
    private val markAllNotificationsAsReadUseCase: MarkAllNotificationsAsReadUseCase,
    private val resources: ResourceProvider
) : ViewModel() {

    private val _uiState = MutableStateFlow(NotificationsUiState())
    val uiState: StateFlow<NotificationsUiState> = _uiState.asStateFlow()

    init {
        // Notificaciones del usuario de la sesión guardadas en Room; se actualizan solas
        viewModelScope.launch {
            observeMyNotificationsUseCase().collect { list ->
                _uiState.update { it.copy(notifications = list) }
            }
        }
        viewModelScope.launch {
            observeCurrentUserUseCase().collect { user ->
                _uiState.update { it.copy(userName = user?.name.orEmpty(), userLocation = user?.city.orEmpty()) }
            }
        }
    }

    fun onFilterChange(filter: NotificationFilter) {
        _uiState.update { it.copy(filter = filter) }
    }

    fun onMarkAllAsRead() {
        if (_uiState.value.unreadCount == 0) {
            _uiState.update { it.copy(infoMessage = resources.getString(R.string.no_pending_notifications)) }
            return
        }
        viewModelScope.launch { runCatching { markAllNotificationsAsReadUseCase() } }
        _uiState.update { it.copy(infoMessage = resources.getString(R.string.all_marked_read)) }
    }

    // Abrir una notificación la marca como leída
    fun onNotificationOpened(id: String) {
        viewModelScope.launch { runCatching { markNotificationAsReadUseCase(id) } }
    }

    fun resetInfoMessage() {
        _uiState.update { it.copy(infoMessage = null) }
    }
}
