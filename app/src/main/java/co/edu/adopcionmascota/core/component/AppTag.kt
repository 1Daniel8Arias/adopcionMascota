package co.edu.adopcionmascota.core.component

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun AppTag(
    text: String,
    modifier: Modifier = Modifier,
    backgroundColor: Color = Color(0xFFDDEEFF),
    textColor: Color = Color(0xFF344054)
) {

    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(10.dp),
        color = backgroundColor
    ) {

        Text(
            text = text,
            modifier = Modifier.padding(
                horizontal = 8.dp,
                vertical = 5.dp
            ),
            fontSize = 9.sp,
            color = textColor
        )
    }
}