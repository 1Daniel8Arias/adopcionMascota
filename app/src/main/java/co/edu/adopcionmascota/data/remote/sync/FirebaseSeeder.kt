package co.edu.adopcionmascota.data.remote.sync

import android.content.Context
import android.util.Log
import co.edu.adopcionmascota.data.local.database.SeedData
import co.edu.adopcionmascota.data.remote.dto.Collections
import co.edu.adopcionmascota.data.remote.dto.CommentDto
import co.edu.adopcionmascota.data.remote.dto.NotificationDto
import co.edu.adopcionmascota.data.remote.dto.PublicationDto
import co.edu.adopcionmascota.data.remote.dto.UserDto
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

// Crea en Firebase las cuentas de prueba y los datos de ejemplo (solo en compilaciones debug).
// Usa una instancia secundaria de FirebaseApp para no tocar la sesión del usuario de la app.
// Las cuentas y sus contraseñas están documentadas en docs/CUENTAS_PRUEBA.md
@Singleton
class FirebaseSeeder @Inject constructor(
    @param:ApplicationContext private val context: Context
) {
    private val prefs by lazy { context.getSharedPreferences("firebase_seed", Context.MODE_PRIVATE) }

    suspend fun seedIfNeeded() {
        if (prefs.getBoolean(KEY_DONE, false)) return
        try {
            seed()
            prefs.edit().putBoolean(KEY_DONE, true).apply()
            Log.i(TAG, "Datos de ejemplo listos en Firebase")
        } catch (e: Exception) {
            // Lo más común: Authentication o Firestore sin activar, o reglas que lo impiden
            Log.e(TAG, "No se pudieron crear los datos de ejemplo: ${e.message}", e)
        }
    }

    private suspend fun seed() {
        val main = FirebaseApp.getInstance()
        val app = FirebaseApp.getApps(context).firstOrNull { it.name == APP_NAME }
            ?: FirebaseApp.initializeApp(context, main.options, APP_NAME)
        val auth = FirebaseAuth.getInstance(app)
        val firestore = FirebaseFirestore.getInstance(app)
        val now = System.currentTimeMillis()

        // 1. Cuentas: se crean en Authentication (o se inicia sesión si ya existen)
        //    y el perfil con su rol se guarda en users/{uid}
        val uidBySeedId = mutableMapOf<String, String>()
        SeedData.users(now).forEach { user ->
            val result = try {
                auth.createUserWithEmailAndPassword(user.email, user.password).await()
            } catch (e: FirebaseAuthUserCollisionException) {
                auth.signInWithEmailAndPassword(user.email, user.password).await()
            }
            val uid = result.user?.uid ?: error("Sin UID para ${user.email}")
            uidBySeedId[user.id] = uid
            val doc = firestore.collection(Collections.USERS).document(uid)
            if (!doc.get().await().exists()) {
                doc.set(UserDto.fromDomain(user.copy(id = uid)).toMap(), SetOptions.merge()).await()
            }
        }

        // 2. Publicaciones, comentarios y notificaciones de ejemplo (una sola vez)
        val meta = firestore.collection(Collections.META).document("seed")
        if (!meta.get().await().exists()) {
            fun uid(seedId: String) = uidBySeedId[seedId] ?: seedId
            val batch = firestore.batch()
            SeedData.publications(now).forEach { p ->
                val dto = PublicationDto.fromDomain(p.copy(ownerId = uid(p.ownerId)))
                batch.set(firestore.collection(Collections.PUBLICATIONS).document(dto.id), dto.toMap())
            }
            SeedData.comments(now).forEach { c ->
                val dto = CommentDto.fromDomain(c.copy(authorId = uid(c.authorId), thankedByMe = false), null)
                batch.set(firestore.collection(Collections.COMMENTS).document(dto.id), dto.toMap())
            }
            SeedData.notifications(now).forEach { n ->
                val dto = NotificationDto.fromDomain(n.copy(userId = uid(n.userId)))
                batch.set(firestore.collection(Collections.NOTIFICATIONS).document(dto.id), dto.toMap())
            }
            batch.set(meta, mapOf("createdAt" to now))
            batch.commit().await()
        }
        auth.signOut()
    }

    private companion object {
        const val TAG = "FirebaseSeeder"
        const val APP_NAME = "seeder"
        const val KEY_DONE = "seed_v1_done"
    }
}
