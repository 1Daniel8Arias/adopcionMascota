package co.edu.adopcionmascota.core.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.ui.Alignment


@Composable
fun IconoTextField(
    titulo: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholderText: String,
    icono: ImageVector,
    modifier: Modifier = Modifier,
    visualTransformation: VisualTransformation = VisualTransformation.None // Valor por defecto: texto normal
) {
    Column(modifier = modifier) {
        // Título dinámico
        Text(
            text = titulo,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = Color(0xFF4A5568),
            modifier = Modifier.padding(bottom = 6.dp)
        )

        // Campo de entrada dinámico
        TextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            visualTransformation = visualTransformation, // Aplica la transformación si se recibe una
            placeholder = {
                Text(
                    text = placeholderText,
                    color = Color(0xFF9AA5B1),
                    fontSize = 14.sp
                )
            },
            leadingIcon = {
                Icon(
                    imageVector = icono,
                    contentDescription = null,
                    tint = Color(0xFF4A5568)
                )
            },
            shape = RoundedCornerShape(16.dp),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color(0xFFF0F4FE),
                unfocusedContainerColor = Color(0xFFF0F4FE),
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent
            )
        )
    }
}

@Composable
fun IconoText(
    texto: String,
    icono: ImageVector,
    modifier: Modifier = Modifier,
    iconoALaDerecha: Boolean = false
){
Row(
    modifier = Modifier
        .fillMaxWidth() // Ocupa todo el ancho disponible
        .background(
            color = Color(0x4586A6FF), // Azul claro de fondo para buen contraste[cite: 2]
            shape = RoundedCornerShape(16.dp))
        .padding(horizontal = 16.dp, vertical = 12.dp),
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.spacedBy(10.dp)
) {

    if (!iconoALaDerecha) {
        Icon(
            imageVector = icono,
            contentDescription = null,
            tint = Color(0xFF4A5CDE)
        )
    }

    Text(
        modifier = Modifier.weight(1f),
        text = texto,
        color = Color(0xFF4A5568),
        fontSize = 13.sp
    )

    // Muestra el icono a la derecha si iconoALaDerecha es true
    if (iconoALaDerecha) {
        Icon(
            imageVector = icono,
            contentDescription = null,
            tint = Color(0xFF4A5CDE)
        )
    }

}
}

