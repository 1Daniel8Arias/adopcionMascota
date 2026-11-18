package co.edu.adopcionmascota.core.component

import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import java.io.File
import java.util.UUID

// Abre la galería, que no requiere permiso: el usuario elige la imagen y la aplicación
// recibe acceso solo a ese archivo. Devuelve la función que abre el selector.
// El acceso al URI de la galería termina al cerrar la app, por eso la imagen se copia
// a la carpeta privada de la aplicación y se entrega un URI file:// que se puede guardar en Room.
@Composable
fun rememberGalleryPicker(onImageSelected: (Uri) -> Unit): () -> Unit {
    val context = LocalContext.current
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { onImageSelected(copyToAppStorage(context, it) ?: it) }
    }
    return { galleryLauncher.launch("image/*") }
}

private fun copyToAppStorage(context: Context, uri: Uri): Uri? = try {
    val folder = File(context.filesDir, "images").apply { mkdirs() }
    val file = File(folder, "${UUID.randomUUID()}.jpg")
    context.contentResolver.openInputStream(uri)?.use { input ->
        file.outputStream().use { output -> input.copyTo(output) }
    }
    Uri.fromFile(file)
} catch (e: Exception) {
    null
}
