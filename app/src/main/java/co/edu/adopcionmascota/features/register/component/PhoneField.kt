package co.edu.adopcionmascota.features.register.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp

@Composable
fun PhoneField(
    phone: String,
    onPhoneChange: (String) -> Unit,
    function: @Composable () -> Unit
) {

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {

        Surface(
            modifier = Modifier.height(56.dp),
            shape = RoundedCornerShape(14.dp),
            color = Color(0xFFEAF1FF)
        ) {

            Row(
                modifier = Modifier.padding(horizontal = 14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {

                Text(
                    text = "🇨🇴",
                    style = MaterialTheme.typography.titleMedium
                )

                Spacer(modifier = Modifier.width(6.dp))

                Text(
                    text = "+57",
                    fontWeight = FontWeight.Medium
                )
            }
        }


    }
}