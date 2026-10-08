package co.edu.adopcionmascota.features.register.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp

@Composable
 fun PasswordField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    error: String?
) {

    var visible by remember {
        mutableStateOf(false)
    }

    Column {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(Color(0xFFEAF1FF))
                .border(
                    width = if (error != null) 1.dp else 0.dp,
                    color = if (error != null)
                        MaterialTheme.colorScheme.error
                    else
                        Color.Transparent,
                    shape = RoundedCornerShape(14.dp)
                )
                .padding(start = 16.dp, end = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Icon(
                imageVector = Icons.Default.Lock,
                contentDescription = null,
                tint = Color(0xFF3B3B3B)
            )

            Spacer(modifier = Modifier.width(14.dp))

            androidx.compose.foundation.text.BasicTextField(
                value = value,
                onValueChange = onValueChange,
                modifier = Modifier.weight(1f),
                singleLine = true,
                visualTransformation =
                    if (visible)
                        VisualTransformation.None
                    else
                        PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password
                ),
                textStyle = MaterialTheme.typography.bodyLarge.copy(
                    color = Color.Black
                ),
                decorationBox = { innerTextField ->

                    Box {

                        if (value.isEmpty()) {

                            Text(
                                text = placeholder,
                                color = Color(0xFF777777)
                            )
                        }

                        innerTextField()
                    }
                }
            )

            IconButton(
                onClick = {
                    visible = !visible
                }
            ) {

                Icon(
                    imageVector =
                        if (visible)
                            Icons.Default.VisibilityOff
                        else
                            Icons.Default.Visibility,
                    contentDescription =
                        if (visible)
                            "Ocultar contraseña"
                        else
                            "Mostrar contraseña"
                )
            }
        }

        if (error != null) {

            Text(
                text = error,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(
                    start = 8.dp,
                    top = 4.dp
                )
            )
        }
    }
}