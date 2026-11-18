package co.edu.adopcionmascota.domain.repository

// Subida de imágenes a la nube. Está aislada en su propio repositorio para poder cambiar
// de proveedor (Firebase Storage hoy, Cloudinary u otro mañana) sin tocar el resto de la app
interface ImageRepository {
    // Sube la imagen local (file:// o content://) y retorna la URL pública.
    // Si la imagen ya es una URL o un recurso de ejemplo, la retorna sin cambios
    suspend fun upload(image: String, folder: String): String
}
