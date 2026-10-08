package co.edu.adopcionmascota.features.register.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun TermsCheckbox(
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

                append("Acepto los ")

                pushStyle(
                    SpanStyle(
                        color = Color(0xFF182BE3)
                    )
                )
                append("Términos y Condiciones")
                pop()

                append(", la ")

                pushStyle(
                    SpanStyle(
                        color = Color(0xFF182BE3)
                    )
                )
                append("Política de Privacidad")
                pop()

                append(" y me comprometo formalmente a ")

                pushStyle(
                    SpanStyle(
                        fontWeight = FontWeight.Bold
                    )
                )
                append("no comerciar ni vender ningún animal.")
                pop()
            },
            modifier = Modifier.padding(
                top = 12.dp,
                end = 4.dp
            ),
            style = MaterialTheme.typography.bodyMedium
        )
    }
}