package co.edu.adopcionmascota.features.register.component

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
 fun NotificationCheckbox(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top
    ) {

        Checkbox(
            checked = checked,
            onCheckedChange = onCheckedChange
        )

        Text(
            text = buildAnnotatedString {

                append("Deseo recibir notificaciones prioritarias de ")

                pushStyle(
                    SpanStyle(
                        fontWeight = FontWeight.Bold
                    )
                )

                append("mascotas perdidas o en riesgo inmediato")

                pop()

                append(" en mi localidad.")
            },
            modifier = Modifier.padding(
                top = 12.dp,
                end = 4.dp
            ),
            style = MaterialTheme.typography.bodyMedium
        )
    }
}


