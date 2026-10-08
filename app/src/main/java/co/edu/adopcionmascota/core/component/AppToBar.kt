package co.edu.adopcionmascota.core.component


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Pets
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import co.edu.adopcionmascota.core.theme.AppPrimary

@Composable
fun AppTopBar(
    title: String = "Patitas Con...",
    location: String = "Bogotá, Colombia",
    onNotificationClick: () -> Unit = {},
    onProfileClick: () -> Unit = {}
) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(52.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(Color.White),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Outlined.Pets,
                contentDescription = "Logo",
                tint = AppPrimary
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        Column(
            modifier = Modifier.weight(1f)
        ) {

            Text(
                text = title,
                fontSize = 15.sp,
                style = MaterialTheme.typography.titleSmall
            )

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {

                Icon(
                    imageVector = Icons.Outlined.LocationOn,
                    contentDescription = null,
                    modifier = Modifier.size(13.dp)
                )

                Spacer(modifier = Modifier.width(2.dp))

                Text(
                    text = location,
                    fontSize = 11.sp
                )
            }
        }

        IconButton(
            onClick = onNotificationClick
        ) {
            Icon(
                imageVector = Icons.Outlined.Notifications,
                contentDescription = "Notificaciones"
            )
        }

        IconButton(
            onClick = onProfileClick
        ) {
            Box(
                modifier = Modifier
                    .size(34.dp)
                    .clip(CircleShape)
                    .background(Color.LightGray),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Outlined.Pets,
                    contentDescription = "Perfil"
                )
            }
        }
    }
}