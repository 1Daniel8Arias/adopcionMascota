package co.edu.adopcionmascota.features.adoption.detail

import android.annotation.SuppressLint
import android.content.ClipData
import androidx.benchmark.traceprocessor.Row
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import co.edu.adopcionmascota.R
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButtonDefaults.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import co.edu.adopcionmascota.core.component.AppTag
import co.edu.adopcionmascota.core.component.AppTopBar
import co.edu.adopcionmascota.navigation.MainRoutes
import coil3.compose.AsyncImage


@Composable
fun PublicationDetailScreen(
    pubblicationId: String,
    padding: PaddingValues= PaddingValues()
) {
    Scaffold(
        modifier = Modifier.statusBarsPadding(),
        topBar = {
            AppTopBar(
                title = "Patitas Conectadas",
                location = "Armenia, Quindío"
            )
        },
        bottomBar = {
            PublicationBottomBar()
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(12.dp)
        ) {

            item {
                Card(
                    shape = RoundedCornerShape(20.dp)
                ) {

                    Box {

                        AsyncImage(
                            model = R.drawable.imagen_max,
                            contentDescription = null,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(260.dp),
                            contentScale = ContentScale.Crop
                        )

                        Card(
                            modifier = Modifier
                                .padding(12.dp)
                                .align(Alignment.TopEnd)
                        ) {

                            Row(
                                modifier = Modifier.padding(
                                    horizontal = 8.dp,
                                    vertical = 4.dp
                                )
                            ) {

                                Icon(
                                    Icons.Default.FavoriteBorder,
                                    null,
                                    tint = Color.Blue
                                )

                                Spacer(
                                    Modifier.width(4.dp)
                                )

                                Text("142")
                            }
                        }
                    }
                }
            }
            item{
                PetInfoCard()
            }

            item {
                HealthCard()
            }

            item {
                HistoryCard()
            }

            item {
                PublisherCard()
            }

            item {
                MapCard()
            }

            item {
                CommentsCard()
            }




        }
    }
}

@Composable
fun PetInfoCard() {

    ElevatedCard {

        Column(
            modifier = Modifier.padding(16.dp)
        ) {

            Row {

                Text(
                    text = "Max",
                    style = MaterialTheme.typography.headlineSmall
                )

                Spacer(
                    Modifier.width(8.dp)
                )

                Text("♂")
            }

            Text(
                "Golden Mestizo · 3 meses · Tamaño Mediano"
            )

            Spacer(
                Modifier.height(12.dp)
            )

            FilledTonalButton(
                onClick = {}
            ) {
                Text("Busca Hogar")
            }
        }
    }
}
@Composable
fun HealthCard() {

    ElevatedCard {

        Column(
            modifier = Modifier.padding(16.dp)
        ) {

            Text(
                "Salud y Cuidados",
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(
                Modifier.height(12.dp)
            )

            FlowRow {

                AppTag("Vacunación al día")
                AppTag("Esterilizado")
                AppTag("Desparasitado")
                AppTag("Chip ID")
                AppTag("Apto para gatos")
            }
        }
    }
}
@Composable
fun HistoryCard() {

    ElevatedCard {

        Column(
            modifier = Modifier.padding(16.dp)
        ) {

            Text(
                "Historia de Max",
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(
                Modifier.height(8.dp)
            )

            Text(
                "Max fue rescatado de una quebrada cerca de La Calera..."
            )
        }
    }
}
@Composable
fun PublisherCard() {

    ElevatedCard {

        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Icon(
                Icons.Default.Person,
                null
            )

            Spacer(
                Modifier.width(12.dp)
            )

            Column(
                modifier = Modifier.weight(1f)
            ) {

                Text(
                    "Fundación Rescate Bogotá"
                )

                Text(
                    "Nivel 3 · Guardián"
                )
            }

            Button(
                onClick = {}
            ) {
                Text("WhatsApp")
            }
        }
    }
}
@Composable
fun MapCard() {

    ElevatedCard(
        modifier = Modifier.fillMaxWidth()
    ) {

        Column(
            modifier = Modifier.padding(16.dp)
        ) {

            Text(
                text = "Punto de Referencia"
            )

            Spacer(
                modifier = Modifier.height(12.dp)
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
            ) {

                Image(
                    painter = painterResource(
                        id = R.drawable.icono_mapa
                    ),
                    contentDescription = "Mapa de referencia",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )

                // Aquí podrías colocar un marcador
                Text(
                    text = "📍",
                    modifier = Modifier
                        .align(Alignment.Center)
                )
            }
        }
    }
}
@Composable
fun CommentsCard() {

    ElevatedCard {

        Column(
            modifier = Modifier.padding(16.dp)
        ) {

            Text(
                "Comentarios y Avances"
            )

            Spacer(
                Modifier.height(12.dp)
            )

            OutlinedTextField(
                value = "",
                onValueChange = {},
                modifier = Modifier.fillMaxWidth(),
                placeholder = {
                    Text("Aportar información...")
                }
            )

            Spacer(
                Modifier.height(8.dp)
            )

            Button(
                onClick = {},
                modifier = Modifier.align(
                    Alignment.End
                )
            ) {
                Text("Comentar")
            }
        }
    }
}
@Composable
fun PublicationBottomBar() {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {

        FilledTonalButton(
            onClick = {},
            modifier = Modifier.weight(1f)
        ) {
            Text("Hogar de Paso")
        }

        Button(
            onClick = {},
            modifier = Modifier.weight(1f)
        ) {
            Text("Adoptar a Milo")
        }
    }
}










