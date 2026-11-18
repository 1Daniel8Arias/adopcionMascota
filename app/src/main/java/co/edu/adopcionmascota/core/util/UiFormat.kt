package co.edu.adopcionmascota.core.util

import android.annotation.SuppressLint
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import co.edu.adopcionmascota.R

// La imagen de una publicación se guarda en Room como texto:
// - una URI (content://, file://, http...) cuando la eligió el usuario en la galería
// - el nombre de un drawable ("imagen_max") en los datos de ejemplo
// Esta función devuelve el modelo que entiende Coil (String o id del recurso)
@SuppressLint("DiscouragedApi")
@Composable
fun rememberImageModel(image: String): Any {
    val context = LocalContext.current
    return remember(image) {
        if (image.contains("://")) image
        else context.resources.getIdentifier(image, "drawable", context.packageName)
            .takeIf { it != 0 } ?: R.drawable.icono
    }
}

// "Hace 5 min", "Hace 2 h", "Hace 3 d"
@Composable
fun relativeTime(millis: Long): String {
    val minutes = ((System.currentTimeMillis() - millis) / 60_000).coerceAtLeast(0)
    return when {
        minutes < 1 -> stringResource(R.string.time_now)
        minutes < 60 -> stringResource(R.string.time_minutes, minutes.toInt())
        minutes < 60 * 24 -> stringResource(R.string.time_hours, (minutes / 60).toInt())
        else -> stringResource(R.string.time_days, (minutes / (60 * 24)).toInt())
    }
}
