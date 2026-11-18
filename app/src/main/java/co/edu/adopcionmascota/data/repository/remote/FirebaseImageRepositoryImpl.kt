package co.edu.adopcionmascota.data.repository.remote

import android.util.Log
import androidx.core.net.toUri
import co.edu.adopcionmascota.domain.repository.ImageRepository
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirebaseImageRepositoryImpl @Inject constructor(
    private val storage: FirebaseStorage
) : ImageRepository {

    override suspend fun upload(image: String, folder: String): String {
        // Solo se suben las imágenes locales; las URL y los drawables de ejemplo se dejan igual
        if (!image.startsWith("file://") && !image.startsWith("content://")) return image
        return try {
            val ref = storage.reference.child("$folder/${UUID.randomUUID()}.jpg")
            ref.putFile(image.toUri()).await()
            ref.downloadUrl.await().toString()
        } catch (e: Exception) {
            // Si Storage no está activo (por ejemplo, el bucket exige el plan Blaze), la app
            // sigue funcionando con la imagen local en este dispositivo
            Log.w(TAG, "No se pudo subir la imagen a Firebase Storage: ${e.message}", e)
            image
        }
    }

    private companion object { const val TAG = "ImageRepository" }
}
