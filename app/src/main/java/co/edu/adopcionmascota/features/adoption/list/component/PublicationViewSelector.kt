package co.edu.adopcionmascota.features.adoption.list.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Map
import androidx.compose.material.icons.outlined.NearMe
import androidx.compose.material.icons.outlined.ViewList
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import co.edu.adopcionmascota.core.theme.AppPrimary

enum class ExploreViewMode {
    LIST,
    MAP
}

@Composable
fun ExploreViewSelector(
    selectedMode: ExploreViewMode,
    onModeSelected: (ExploreViewMode) -> Unit
) {

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {

        Row(
            modifier = Modifier
                .background(
                    color = Color(0xFFDCEAFF),
                    shape = RoundedCornerShape(25.dp)
                )
                .padding(4.dp)
        ) {

            ViewOption(
                selected = selectedMode == ExploreViewMode.LIST,
                icon = Icons.Outlined.ViewList,
                text = "Lista",
                onClick = {
                    onModeSelected(ExploreViewMode.LIST)
                }
            )

            ViewOption(
                selected = selectedMode == ExploreViewMode.MAP,
                icon = Icons.Outlined.Map,
                text = "Mapa",
                onClick = {
                    onModeSelected(ExploreViewMode.MAP)
                }
            )
        }

        Surface(
            shape = RoundedCornerShape(20.dp)
        ) {

            Row(
                modifier = Modifier.padding(
                    horizontal = 12.dp,
                    vertical = 8.dp
                ),
                verticalAlignment = Alignment.CenterVertically
            ) {

                Icon(
                    imageVector = Icons.Outlined.NearMe,
                    contentDescription = null,
                    modifier = Modifier.size(14.dp)
                )

                Spacer(modifier = Modifier.width(4.dp))

                Text(
                    text = "< 5 km",
                    fontSize = 12.sp
                )
            }
        }
    }
}

@Composable
private fun ViewOption(
    selected: Boolean,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    text: String,
    onClick: () -> Unit
) {

    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(20.dp),
        color = if (selected) AppPrimary
        else androidx.compose.ui.graphics.Color.Transparent
    ) {

        Row(
            modifier = Modifier.padding(
                horizontal = 15.dp,
                vertical = 7.dp
            ),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(16.dp)
            )

            Spacer(modifier = Modifier.width(5.dp))

            Text(
                text = text,
                color = if (selected)
                    Color.White
                else
                    Color.Black,
                fontSize = 12.sp
            )
        }
    }
}