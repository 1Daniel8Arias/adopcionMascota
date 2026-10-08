package co.edu.adopcionmascota.core.component

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Tune
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.shape.RoundedCornerShape
import co.edu.adopcionmascota.core.theme.AppLightBlue
import co.edu.adopcionmascota.core.theme.AppPrimary
import co.edu.adopcionmascota.core.theme.AppSecondaryText

@Composable
fun AppSearchBar(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String = "Buscar...",
    onFilterClick: () -> Unit = {}
) {

    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,

        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),

        placeholder = {
            Text(
                text = placeholder,
                fontSize = 13.sp,
                color = AppSecondaryText
            )
        },

        leadingIcon = {
            Icon(
                imageVector = Icons.Outlined.Search,
                contentDescription = "Buscar"
            )
        },

        trailingIcon = {
            IconButton(
                onClick = onFilterClick
            ) {
                Icon(
                    imageVector = Icons.Outlined.Tune,
                    contentDescription = "Filtros"
                )
            }
        },

        singleLine = true,

        shape = RoundedCornerShape(30.dp),

        colors = OutlinedTextFieldDefaults.colors(
            unfocusedContainerColor = AppLightBlue,
            focusedContainerColor = AppLightBlue,
            unfocusedBorderColor = androidx.compose.ui.graphics.Color.Transparent,
            focusedBorderColor = AppPrimary
        )
    )
}