package co.edu.adopcionmascota.features.publication

import androidx.compose.ui.res.stringResource
import co.edu.adopcionmascota.R
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import co.edu.adopcionmascota.core.component.MapBox
import co.edu.adopcionmascota.domain.model.Location
import com.mapbox.geojson.Point

// Mapa a pantalla completa para elegir el punto de la publicación tocándolo
@Composable
fun LocationPickerDialog(
    initialLocation: Location,
    onConfirm: (Location) -> Unit,
    onDismiss: () -> Unit
) {
    // Point se crea con (longitud, latitud); Location guarda primero la latitud
    var pickedPoint by remember {
        mutableStateOf(Point.fromLngLat(initialLocation.longitude, initialLocation.latitude))
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            MapBox(
                modifier = Modifier.fillMaxSize(),
                center = pickedPoint,
                zoom = 16.0,
                selectedPoint = pickedPoint,
                onMapClick = { pickedPoint = it }
            )

            Row(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = onDismiss,
                    modifier = Modifier.weight(1f),
                    // Fondo sólido para que se lea encima del mapa
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                ) {
                    Text(text = stringResource(R.string.cancel))
                }
                Button(
                    onClick = {
                        onConfirm(
                            Location(latitude = pickedPoint.latitude(), longitude = pickedPoint.longitude())
                        )
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Text(text = stringResource(R.string.confirm_location))
                }
            }
        }
    }
}
