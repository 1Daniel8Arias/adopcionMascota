package co.edu.adopcionmascota.domain.usecase.notification

import co.edu.adopcionmascota.domain.model.Notification
import co.edu.adopcionmascota.domain.repository.NotificationRepository
import co.edu.adopcionmascota.domain.repository.SessionRepository
import co.edu.adopcionmascota.domain.usecase.user.GetCurrentUserUseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject

// Notificaciones del usuario de la sesión
class ObserveMyNotificationsUseCase @Inject constructor(
    private val sessionRepository: SessionRepository,
    private val notificationRepository: NotificationRepository
) {
    @OptIn(ExperimentalCoroutinesApi::class)
    operator fun invoke(): Flow<List<Notification>> = sessionRepository.session.flatMapLatest { session ->
        if (session == null) flowOf(emptyList()) else notificationRepository.observeByUser(session.userId)
    }
}

class GetNotificationByIdUseCase @Inject constructor(
    private val notificationRepository: NotificationRepository
) {
    suspend operator fun invoke(id: String): Notification? = notificationRepository.findById(id)
}

class MarkNotificationAsReadUseCase @Inject constructor(
    private val notificationRepository: NotificationRepository
) {
    suspend operator fun invoke(id: String) = notificationRepository.markAsRead(id)
}

class MarkAllNotificationsAsReadUseCase @Inject constructor(
    private val getCurrentUserUseCase: GetCurrentUserUseCase,
    private val notificationRepository: NotificationRepository
) {
    suspend operator fun invoke() {
        val user = getCurrentUserUseCase() ?: return
        notificationRepository.markAllAsRead(user.id)
    }
}

class UpdateNotificationUseCase @Inject constructor(
    private val notificationRepository: NotificationRepository
) {
    suspend operator fun invoke(notification: Notification) = notificationRepository.update(notification)
}
