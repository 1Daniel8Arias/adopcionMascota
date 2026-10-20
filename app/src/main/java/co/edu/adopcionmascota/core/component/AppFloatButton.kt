package co.edu.adopcionmascota.core.component

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AddCircleOutline
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun AppFloatingButton(
    text: String = "Publicar",
    onClick: () -> Unit
) {

    FloatingActionButton(
        onClick = onClick,
        modifier = Modifier
            .width(112.dp)
            .height(52.dp),
        shape = RoundedCornerShape(18.dp),
        containerColor = MaterialTheme.colorScheme.onSurface,
        contentColor = MaterialTheme.colorScheme.surface
    ) {

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {

            Icon(
                imageVector = Icons.Outlined.AddCircleOutline,
                contentDescription = null
            )

            Spacer(modifier = Modifier.width(5.dp))

            Text(text)
        }
    }
}