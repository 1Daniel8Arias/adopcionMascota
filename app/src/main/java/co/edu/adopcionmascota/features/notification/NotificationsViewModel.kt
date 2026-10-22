package co.edu.adopcionmascota.features.notification

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// Filtros del Centro de Notificaciones
enum class NotificationFilter(val label: String) {
    ALL("Todas"),
    UNREAD("No leídas"),
    COMMENTS("Comentarios"),
    ACHIEVEMENTS("Logros")
}

data class NotificationsUiState(
    val userName: String = "Camila Morales",
    val userLocation: String = "Bogotá D.C.",
    val notifications: List<AppNotification> = emptyList(),
    val filter: NotificationFilter = NotificationFilter.ALL,
    val infoMessage: String? = null
) {
    val unreadCount: Int
        get() = notifications.count { !it.isRead }

    val totalCount: Int
        get() = notifications.size

    // La lista visible depende del filtro seleccionado
    val visibleNotifications: List<AppNotification>
        get() = when (filter) {
            NotificationFilter.ALL -> notifications
            NotificationFilter.UNREAD -> notifications.filter { !it.isRead }
            NotificationFilter.COMMENTS -> notifications.filter { it.type == NotificationType.NEW_COMMENT }
            NotificationFilter.ACHIEVEMENTS -> notifications.filter { it.isAchievement }
        }

    fun labelFor(filter: NotificationFilter): String = when (filter) {
        NotificationFilter.ALL -> "${filter.label} ($totalCount)"
        NotificationFilter.UNREAD -> "${filter.label} ($unreadCount)"
        else -> filter.label
    }
}

class NotificationsViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(NotificationsUiState())
    val uiState: StateFlow<NotificationsUiState> = _uiState.asStateFlow()

    init {
        // Al leer una notificación en el detalle, la lista se actualiza sola
        viewModelScope.launch {
            NotificationStore.notifications.collect { list ->
                _uiState.update { it.copy(notifications = list) }
            }
        }
    }

    fun onFilterChange(filter: NotificationFilter) {
        _uiState.update { it.copy(filter = filter) }
    }

    fun onMarkAllAsRead() {
        if (_uiState.value.unreadCount == 0) {
            _uiState.update { it.copy(infoMessage = "No tienes notificaciones pendientes") }
            return
        }
        NotificationStore.markAllAsRead()
        _uiState.update { it.copy(infoMessage = "Todas las notificaciones quedaron como leídas") }
    }

    // Abrir una notificación la marca como leída
    fun onNotificationOpened(id: String) {
        NotificationStore.markAsRead(id)
    }

    fun resetInfoMessage() {
        _uiState.update { it.copy(infoMessage = null) }
    }
}
