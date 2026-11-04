package co.edu.adopcionmascota.core.component

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable

// Abre la galería, que no requiere permiso: el usuario elige la imagen y la aplicación
// recibe acceso solo a ese archivo. Devuelve la función que abre el selector.
// El URI solo sirve mientras la aplicación esté abierta; para conservarlo hay que subirlo a un servidor.
@Composable
fun rememberGalleryPicker(onImageSelected: (Uri) -> Unit): () -> Unit {
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let(onImageSelected)
    }
    return { galleryLauncher.launch("image/*") }
}
