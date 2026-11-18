package co.edu.adopcionmascota.data.remote.sync

import android.util.Log
import androidx.room.withTransaction
import co.edu.adopcionmascota.data.local.database.AppDatabase
import co.edu.adopcionmascota.data.local.entity.FavoriteEntity
import co.edu.adopcionmascota.data.remote.request.AdoptionRequestDto
import co.edu.adopcionmascota.data.remote.Collections
import co.edu.adopcionmascota.data.remote.comment.CommentDto
import co.edu.adopcionmascota.data.remote.notification.NotificationDto
import co.edu.adopcionmascota.data.remote.publication.PublicationDto
import co.edu.adopcionmascota.data.remote.user.UserDto
import co.edu.adopcionmascota.di.ApplicationScope
import co.edu.adopcionmascota.domain.repository.SessionRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

// Mantiene Room como caché de Firestore.
// Mientras haya un usuario autenticado, escucha las colecciones con addSnapshotListener
// (guía 19) y reemplaza las tablas de Room con cada snapshot. Las pantallas siguen leyendo
// de Room, así que funcionan igual que antes y también sin conexión.
@Singleton
class FirestoreCacheSync @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
    private val database: AppDatabase,
    private val sessionRepository: SessionRepository,
    @param:ApplicationScope private val scope: CoroutineScope
) {
    // Las escrituras en Room se hacen de a una, en el orden en que llegan los snapshots
    @OptIn(ExperimentalCoroutinesApi::class)
    private val writer = Dispatchers.IO.limitedParallelism(1)

    private val registrations = mutableListOf<ListenerRegistration>()
    private var listeningUid: String? = null
    private var started = false
    private var firstEvent = true

    // Último error de Firestore (por ejemplo PERMISSION_DENIED), útil para diagnosticar
    private val _lastError = MutableStateFlow<String?>(null)
    val lastError: StateFlow<String?> = _lastError.asStateFlow()

    fun start() {
        if (started) return
        started = true
        auth.addAuthStateListener { firebaseAuth ->
            val uid = firebaseAuth.currentUser?.uid
            // El primer evento siempre se procesa: así una sesión vieja de DataStore sin
            // usuario de Firebase (por ejemplo, de la versión solo con Room) se cierra
            if (!firstEvent && uid == listeningUid) return@addAuthStateListener
            firstEvent = false
            stopListeners()
            listeningUid = uid
            if (uid != null) {
                startListeners(uid)
            } else {
                // Sin usuario de Firebase no hay sesión válida: se limpia la caché y DataStore
                scope.launch(writer) {
                    database.clearAllTables()
                    if (sessionRepository.session.first() != null) sessionRepository.clearSession()
                }
            }
        }
    }

    private fun startListeners(uid: String) {
        registrations += firestore.collection(Collections.USERS).addSnapshotListener { snap, error ->
            handle(Collections.USERS, snap, error) {
                val users = it.documents.map { doc -> UserDto.fromSnapshot(doc).toEntity() }
                database.withTransaction {
                    database.userDao().clear()
                    database.userDao().insertAll(users)
                }
            }
        }
        registrations += firestore.collection(Collections.PUBLICATIONS).addSnapshotListener { snap, error ->
            handle(Collections.PUBLICATIONS, snap, error) {
                val items = it.documents.map { doc -> PublicationDto.fromSnapshot(doc).toEntity() }
                database.withTransaction {
                    database.publicationDao().clear()
                    database.publicationDao().insertAll(items)
                }
            }
        }
        registrations += firestore.collection(Collections.COMMENTS).addSnapshotListener { snap, error ->
            handle(Collections.COMMENTS, snap, error) {
                val items = it.documents.map { doc -> CommentDto.fromSnapshot(doc).toEntity(uid) }
                database.withTransaction {
                    database.commentDao().clear()
                    database.commentDao().insertAll(items)
                }
            }
        }
        // Cada usuario solo descarga sus notificaciones
        registrations += firestore.collection(Collections.NOTIFICATIONS)
            .whereEqualTo("userId", uid)
            .addSnapshotListener { snap, error ->
                handle(Collections.NOTIFICATIONS, snap, error) {
                    val items = it.documents.map { doc -> NotificationDto.fromSnapshot(doc).toEntity() }
                    database.withTransaction {
                        database.notificationDao().clear()
                        database.notificationDao().insertAll(items)
                    }
                }
            }
        registrations += firestore.collection(Collections.ADOPTION_REQUESTS).addSnapshotListener { snap, error ->
            handle(Collections.ADOPTION_REQUESTS, snap, error) {
                val items = it.documents.map { doc -> AdoptionRequestDto.fromSnapshot(doc).toEntity() }
                database.withTransaction {
                    database.adoptionRequestDao().clear()
                    database.adoptionRequestDao().insertAll(items)
                }
            }
        }
        registrations += firestore.collection(Collections.USERS).document(uid)
            .collection(Collections.FAVORITES)
            .addSnapshotListener { snap, error ->
                handle(Collections.FAVORITES, snap, error) {
                    val items = it.documents.map { doc ->
                        FavoriteEntity(uid, doc.id, doc.getLong("createdAt") ?: 0L)
                    }
                    database.withTransaction {
                        database.favoriteDao().clear()
                        database.favoriteDao().insertAll(items)
                    }
                }
            }
    }

    private fun handle(
        name: String,
        snapshot: QuerySnapshot?,
        error: FirebaseFirestoreException?,
        write: suspend (QuerySnapshot) -> Unit
    ) {
        if (error != null) {
            // PERMISSION_DENIED: revisar las reglas de Firestore en la consola
            Log.e(TAG, "Error escuchando '$name': ${error.code} ${error.message}")
            _lastError.value = "$name: ${error.code}"
            return
        }
        snapshot ?: return
        scope.launch(writer) {
            runCatching { write(snapshot) }.onFailure { Log.e(TAG, "Error guardando '$name' en Room", it) }
        }
    }

    private fun stopListeners() {
        registrations.forEach { it.remove() }
        registrations.clear()
    }

    private companion object { const val TAG = "FirestoreCacheSync" }
}
