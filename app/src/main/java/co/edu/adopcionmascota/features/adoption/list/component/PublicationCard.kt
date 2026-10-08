package co.edu.adopcionmascota.features.adoption.list.component

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import co.edu.adopcionmascota.core.theme.AppSecondaryText
import coil3.compose.AsyncImage
import co.edu.adopcionmascota.domain.Publications
import co.edu.adopcionmascota.core.component.AppTag
import co.edu.adopcionmascota.core.theme.*
import co.edu.adopcionmascota.domain.PetType


@Composable
fun PublicationCard(
    publication: Publications,
    onActionClick: () -> Unit = {},
    onShareClick: () -> Unit = {}

) {

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = AppCardBlue
        )
    ) {

        Column {

            // -----------------------------------------
            // IMAGEN
            // -----------------------------------------

            AsyncImage(
                model = publication.image,
                contentDescription = publication.name,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(185.dp),
                contentScale = ContentScale.Crop
            )

            // -----------------------------------------
            // INFORMACIÓN
            // -----------------------------------------

            Column(
                modifier = Modifier.padding(12.dp)
            ) {

                Row(
                    modifier = Modifier.fillMaxWidth()
                ) {

                    Text(
                        text = publication.name,
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.weight(1f)
                    )

                    if (publication.distancia.isNotEmpty()) {

                        Text(
                            text = "⌖ ${publication.distancia}",
                            fontSize = 10.sp
                        )
                    }
                }

                Spacer(
                    modifier = Modifier.height(3.dp)
                )

                Text(
                    text = buildDescription(publication),
                    fontSize = 12.sp,
                    color = AppSecondaryText
                )

                Spacer(
                    modifier = Modifier.height(8.dp)
                )

                // -----------------------------------------
                // INFORMACIÓN SEGÚN ESTADO
                // -----------------------------------------

                when (publication.type) {

                    PetType.ADOPCION -> {

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {

                            AppTag("✓ Vacunado")
                            AppTag("✓ Esterilizado")
                            AppTag("Apto con niños")

                            var liked by remember {
                                mutableStateOf(false)
                            }

                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {

                                IconButton(
                                    onClick = {
                                        liked = !liked
                                    }
                                ) {

                                    Icon(
                                        imageVector =
                                            if (liked)
                                                Icons.Filled.Favorite
                                            else
                                                Icons.Outlined.FavoriteBorder,
                                        contentDescription = "Like",
                                        tint =
                                            if (liked)
                                                Color.Red
                                            else
                                                Color.Gray
                                    )
                                }

                                Text(
                                    text = publication.likes
                                )
                            }
                        }
                    }

                    PetType.PERDIDO -> {

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {

                            Text(
                                text = "◉ ${publication.likes} personas alertas en la zona",
                                fontSize = 11.sp
                            )

                            Text(
                                text = "□ 14 pistas",
                                fontSize = 11.sp,
                                color = AppSecondaryText
                            )
                        }
                    }

                    PetType.ENCONTRADO -> {

                        Text(
                            text = "Encontrado desorientado. Se busca a sus dueños o adoptante responsable...",
                            fontSize = 11.sp,
                            color = AppSecondaryText
                        )
                    }

                }

                Spacer(
                    modifier = Modifier.height(10.dp)
                )

                // -----------------------------------------
                // BOTÓN
                // -----------------------------------------

                PublicationActionButton(
                    publication = publication,
                    onClick = onActionClick
                )
            }
        }
    }
}
private fun buildDescription(
    publication: Publications
): String {

    val parts = mutableListOf<String>()

    if (publication.description.isNotEmpty()) {
        parts.add(publication.description)
    }

    if (publication.gender.isNotEmpty()) {
        parts.add(publication.gender)
    }

    if (publication.age.isNotEmpty()) {
        parts.add(publication.age)
    }

    return parts.joinToString(" • ")
}

@Composable
private fun PublicationActionButton(
    publication: Publications,
    onClick: () -> Unit
) {

    val buttonColor = when (publication.type) {

        PetType.ADOPCION ->
            AppDarkButton

        PetType.PERDIDO  ->
            AppDarkButton

        PetType.ENCONTRADO ->
            AppPrimary
    }

    val buttonText = when (publication.type) {

        PetType.ADOPCION ->
            "Me interesa adoptar →"

        PetType.PERDIDO ->
            "He visto a ${publication.name}"

       PetType.ENCONTRADO ->
            "Ver detalles y fotos →"

    }

    Button(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(25.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = buttonColor
        )
    ) {

        Text(
            text = buttonText,
            fontSize = 12.sp
        )
    }
}
