package co.edu.adopcionmascota.features.adoption.list.component

import androidx.compose.ui.res.stringResource
import co.edu.adopcionmascota.R
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
import co.edu.adopcionmascota.core.util.rememberImageModel
import co.edu.adopcionmascota.domain.model.Publication
import co.edu.adopcionmascota.core.component.AppTag
import co.edu.adopcionmascota.core.theme.*
import co.edu.adopcionmascota.domain.model.PetType


@Composable
fun PublicationCard(
    publication: Publication,
    isLiked: Boolean = false,               // "Me encanta" del usuario (Room)
    onLikeClick: () -> Unit = {},
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
                model = rememberImageModel(publication.image),
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
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
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

                    // "Me encanta": se guarda por usuario y aparece en "Mis favoritos"
                    IconButton(onClick = onLikeClick) {
                        Icon(
                            imageVector = if (isLiked) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                            contentDescription = stringResource(R.string.like),
                            tint = if (isLiked) Color.Red else Color.Gray
                        )
                    }

                    Text(text = publication.likes.toString(), fontSize = 12.sp)
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

                            // Las etiquetas salen de los datos de salud de la publicación
                            if (publication.isVaccinated) AppTag(stringResource(R.string.tag_vaccinated))
                            if (publication.isSterilized) AppTag(stringResource(R.string.tag_sterilized))
                            if (publication.breed.isNotBlank()) AppTag(publication.breed)
                        }
                    }

                    PetType.PERDIDO -> {

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {

                            Text(
                                text = stringResource(R.string.people_alert, publication.likes),
                                fontSize = 11.sp
                            )

                            Text(
                                text = stringResource(R.string.clues),
                                fontSize = 11.sp,
                                color = AppSecondaryText
                            )
                        }
                    }

                    PetType.ENCONTRADO -> {

                        Text(
                            text = stringResource(R.string.found_card_text),
                            fontSize = 11.sp,
                            color = AppSecondaryText
                        )
                    }

                    PetType.VETERINARIA -> {

                        AppTag(stringResource(R.string.detail_status_vet))
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
    publication: Publication
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
    publication: Publication,
    onClick: () -> Unit
) {

    val buttonColor = when (publication.type) {

        PetType.ADOPCION ->
            AppDarkButton

        PetType.PERDIDO  ->
            AppDarkButton

        PetType.ENCONTRADO ->
            AppPrimary

        PetType.VETERINARIA ->
            AppPrimary
    }

    val buttonText = when (publication.type) {

        PetType.ADOPCION ->
            stringResource(R.string.interested_adopt)

        PetType.PERDIDO ->
            stringResource(R.string.seen_pet, publication.name)

       PetType.ENCONTRADO ->
            stringResource(R.string.view_details)

        PetType.VETERINARIA ->
            stringResource(R.string.detail_action_vet)

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
