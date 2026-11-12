package co.edu.adopcionmascota.data.repository.remote

import co.edu.adopcionmascota.data.local.dao.UserDao
import co.edu.adopcionmascota.data.local.mapper.toDomain
import co.edu.adopcionmascota.data.remote.dto.Collections
import co.edu.adopcionmascota.data.remote.dto.UserDto
import co.edu.adopcionmascota.domain.exception.EmailAlreadyInUseException
import co.edu.adopcionmascota.domain.exception.UserNotFoundException
import co.edu.adopcionmascota.domain.model.User
import co.edu.adopcionmascota.domain.repository.ImageRepository
import co.edu.adopcionmascota.domain.repository.UserRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

// Usuarios: credenciales en Firebase Authentication y perfil (con el rol) en users/{uid}.
// Room guarda una copia del perfil como caché (la llena FirestoreCacheSync)
@Singleton
class UserRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth,
    private val userDao: UserDao,
    private val imageRepository: ImageRepository
) : UserRepository {

    private val collection = firestore.collection(Collections.USERS)

    override suspend fun save(user: User): String {
        // Firebase Authentication rechaza los correos repetidos (guía 20)
        val result = try {
            auth.createUserWithEmailAndPassword(user.email, user.password).await()
        } catch (e: FirebaseAuthUserCollisionException) {
            throw EmailAlreadyInUseException()
        }
        val newUser = result.user ?: throw Exception("No se obtuvo el UID del usuario creado")
        try {
            val photo = imageRepository.upload(user.profilePictureUrl, "users/${newUser.uid}")
            val dto = UserDto.fromDomain(user.copy(id = newUser.uid, profilePictureUrl = photo))
            collection.document(newUser.uid).set(dto.toMap()).await()
            userDao.insertAll(listOf(dto.toEntity()))
        } catch (e: Exception) {
            // Si el perfil no se pudo guardar se borra la cuenta para no dejarla a medias
            withContext(NonCancellable) { runCatching { newUser.delete().await() } }
            throw e
        }
        return newUser.uid
    }

    override suspend fun update(user: User) {
        val photo = imageRepository.upload(user.profilePictureUrl, "users/${user.id}")
        val dto = UserDto.fromDomain(user.copy(profilePictureUrl = photo))
        // Los puntos se actualizan con incrementos atómicos, por eso no se sobrescriben aquí
        collection.document(user.id).set(dto.toMap() - "points", SetOptions.merge()).await()
        userDao.insertAll(listOf(dto.toEntity()))
    }

    override suspend fun delete(id: String) {
        collection.document(id).delete().await()
        userDao.delete(id)
        // Borrar la cuenta de Auth puede exigir un inicio de sesión reciente; si falla,
        // el perfil ya no existe y el login lo rechazará
        if (auth.currentUser?.uid == id) runCatching { auth.currentUser?.delete()?.await() }
    }

    override suspend fun findById(id: String): User? {
        // Primero la nube; sin conexión (o sin permiso) se usa la caché de Room
        val remote = runCatching { collection.document(id).get().await() }.getOrNull()
        if (remote != null && remote.exists()) {
            val dto = UserDto.fromSnapshot(remote)
            userDao.insertAll(listOf(dto.toEntity()))
            return dto.toDomain()
        }
        if (remote != null && !remote.metadata.isFromCache) return null
        return userDao.findById(id)?.toDomain()
    }

    override fun observeById(id: String): Flow<User?> = userDao.observeById(id).map { it?.toDomain() }

    override suspend fun login(email: String, password: String): User? {
        val response = try {
            auth.signInWithEmailAndPassword(email, password).await()
        } catch (e: FirebaseAuthInvalidCredentialsException) {
            return null // Correo o contraseña incorrectos
        } catch (e: FirebaseAuthInvalidUserException) {
            return null // La cuenta no existe o fue deshabilitada
        }
        val uid = response.user?.uid ?: return null
        // Si la cuenta no tiene perfil en Firestore se cierra la sesión (guía 20)
        val snapshot = collection.document(uid).get().await()
        if (!snapshot.exists()) {
            auth.signOut()
            return null
        }
        val dto = UserDto.fromSnapshot(snapshot)
        userDao.insertAll(listOf(dto.toEntity()))
        return dto.toDomain()
    }

    // Solo se consulta la caché: antes del login no hay permiso para leer Firestore,
    // y el correo repetido ya lo detecta Firebase Authentication en save()
    override suspend fun findByEmail(email: String): User? = userDao.findByEmail(email)?.toDomain()

    override suspend fun addPoints(id: String, points: Int) {
        collection.document(id).update("points", FieldValue.increment(points.toLong())).await()
        userDao.addPoints(id, points)
    }

    override fun observeCount(): Flow<Int> = userDao.observeCount()

    override suspend fun logout() {
        auth.signOut()
    }

    override suspend fun sendPasswordReset(email: String) {
        try {
            auth.sendPasswordResetEmail(email).await()
        } catch (e: FirebaseAuthInvalidUserException) {
            // Solo ocurre si la protección contra enumeración de correos está desactivada
            throw UserNotFoundException()
        }
    }
}
