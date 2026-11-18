package co.edu.adopcionmascota.core.component

import android.Manifest
import android.content.Context
import android.content.Intent
import android.location.LocationManager
import android.net.Uri
import android.provider.Settings
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.core.location.LocationManagerCompat
import androidx.lifecycle.compose.LifecycleResumeEffect
import co.edu.adopcionmascota.R
import co.edu.adopcionmascota.domain.model.Publication
import com.google.gson.JsonPrimitive
import com.mapbox.geojson.Point
import com.mapbox.maps.extension.compose.MapEffect
import com.mapbox.maps.extension.compose.MapboxMap
import com.mapbox.maps.extension.compose.animation.viewport.rememberMapViewportState
import com.mapbox.maps.extension.compose.annotation.generated.PointAnnotation
import com.mapbox.maps.extension.compose.annotation.generated.PointAnnotationGroup
import com.mapbox.maps.extension.compose.annotation.rememberIconImage
import com.mapbox.maps.plugin.PuckBearing
import com.mapbox.maps.plugin.annotation.AnnotationConfig
import com.mapbox.maps.plugin.annotation.AnnotationSourceOptions
import com.mapbox.maps.plugin.annotation.ClusterOptions
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationOptions
import com.mapbox.maps.plugin.locationcomponent.createDefault2DPuck
import com.mapbox.maps.plugin.locationcomponent.location
import com.mapbox.maps.plugin.viewport.data.DefaultViewportTransitionOptions

// Centro de Armenia, Quindío. Mapbox usa el orden (longitud, latitud)
private val ARMENIA = Point.fromLngLat(-75.6725, 4.5389)

@Composable
fun MapBox(
    modifier: Modifier = Modifier,
    center: Point = ARMENIA,                    // Punto en el que arranca la cámara
    zoom: Double = 9.5,                         // Entre más alto, más cerca del suelo
    publications: List<Publication> = emptyList(), // Publicaciones que se dibujan como marcadores
    selectedPoint: Point? = null,               // Marcador suelto que controla la pantalla
    interactive: Boolean = true,                // Si es false, el mapa no responde a gestos ni toques
    showMyLocationButton: Boolean = true,
    locationButtonTopPadding: Dp = 12.dp,       // Para no quedar debajo de lo que la pantalla ponga encima
    onMarkerClick: (Publication) -> Unit = {},  // Se llama al tocar el marcador de una publicación
    onMapClick: (Point) -> Unit = {}            // Se llama al tocar una zona del mapa sin marcador
) {
    // Sin token el SDK no puede dibujar el mapa: se avisa en lugar de cerrar la aplicación
    if (stringResource(R.string.mapbox_access_token).isBlank()) {
        Box(
            modifier = modifier.background(MaterialTheme.colorScheme.surfaceVariant),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = stringResource(R.string.map_token_missing),
                modifier = Modifier.padding(16.dp),
                textAlign = TextAlign.Center
            )
        }
        return
    }

    val context = LocalContext.current

    // Cada incremento es una petición para centrar la cámara en el usuario
    var followUserRequest by remember { mutableIntStateOf(0) }

    // Qué le falta al botón "Mi ubicación" para funcionar; null = no hay nada que avisar
    var locationProblem by remember { mutableStateOf<LocationProblem?>(null) }

    // El usuario pidió su ubicación y falta algo: se retoma al volver de Ajustes
    var pendingFollow by remember { mutableStateOf(false) }

    // Con el permiso listo, centra el mapa si el GPS está encendido o avisa que debe activarlo
    val followUserIfPossible = {
        if (context.isLocationEnabled()) {
            pendingFollow = false
            locationProblem = null
            followUserRequest++
        } else {
            pendingFollow = true
            locationProblem = LocationProblem.LOCATION_OFF
        }
    }

    // Si niega el permiso se avisa; el mapa sigue funcionando sin la ubicación
    val permissionState = rememberLocationPermissionState { granted ->
        if (granted) {
            followUserIfPossible()
        } else {
            pendingFollow = true
            locationProblem = LocationProblem.PERMISSION_DENIED
        }
    }

    // Al volver de Ajustes se retoma la petición si ya está todo listo
    LifecycleResumeEffect(pendingFollow) {
        if (pendingFollow && context.hasLocationPermission() && context.isLocationEnabled()) {
            followUserIfPossible()
        }
        onPauseOrDispose { }
    }

    // Estado inicial del mapa: center y zoom solo se usan la primera vez que se compone
    val mapViewportState = rememberMapViewportState {
        setCameraOptions {
            zoom(zoom)
            center(center)
        }
    }

    // Ícono común a todos los marcadores
    val marker = rememberIconImage(
        key = R.drawable.red_marker,
        painter = painterResource(R.drawable.red_marker)
    )

    // Se guarda el id en cada marcador para saber a qué publicación corresponde si lo tocan
    val publicationsById = remember(publications) { publications.associateBy { it.id } }
    val annotations = remember(publications) {
        publications.map { publication ->
            PointAnnotationOptions()
                .withPoint(
                    Point.fromLngLat(publication.coordinates.longitude, publication.coordinates.latitude)
                )
                .withData(JsonPrimitive(publication.id))
        }
    }

    // Agrupar los marcadores que están a menos de 60 píxeles entre sí
    val clusterConfig = remember {
        AnnotationConfig(
            annotationSourceOptions = AnnotationSourceOptions(
                clusterOptions = ClusterOptions(clusterRadius = 60L)
            )
        )
    }

    Box(modifier = modifier) {
        MapboxMap(
            modifier = Modifier.matchParentSize(),
            mapViewportState = mapViewportState,
            onMapClickListener = { point ->
                onMapClick(point) // Se avisa a la pantalla el punto que se tocó
                true
            }
        ) {
            // La clave cambia en cada petición, así el efecto se vuelve a ejecutar
            if (permissionState.hasPermission && followUserRequest > 0) {
                MapEffect(followUserRequest) { mapView ->
                    mapView.location.updateSettings {
                        locationPuck = createDefault2DPuck(withBearing = true)
                        enabled = true
                        puckBearing = PuckBearing.COURSE
                        puckBearingEnabled = true
                    }
                    mapViewportState.transitionToFollowPuckState(
                        defaultTransitionOptions = DefaultViewportTransitionOptions.Builder()
                            .maxDurationMs(1500)
                            .build()
                    )
                }
            }

            // Marcadores de las publicaciones, todos en una sola capa
            if (annotations.isNotEmpty()) {
                PointAnnotationGroup(
                    annotations = annotations,
                    annotationConfig = clusterConfig
                ) {
                    iconImage = marker
                    // Se recupera la publicación a partir del id guardado en el marcador
                    interactionsState.onClicked { annotation ->
                        val publication = annotation.getData()?.asString?.let(publicationsById::get)
                        publication?.let(onMarkerClick)
                        publication != null // true = el toque no llega al mapa
                    }
                }
            }

            // Marcador del punto elegido, solo si no es nulo
            selectedPoint?.let { point ->
                PointAnnotation(point = point) {
                    iconImage = marker
                }
            }
        }

        // Capa transparente: recibe los toques para que el mapa no los procese.
        // El arrastre no se consume, así que el scroll de la pantalla sigue funcionando
        if (!interactive) {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .pointerInput(Unit) {}
            )
        }

        // El botón de ubicación no tiene sentido en un mapa de solo lectura
        if (showMyLocationButton && interactive) {
            SmallFloatingActionButton(
                onClick = {
                    if (permissionState.hasPermission) {
                        followUserIfPossible()
                    } else {
                        permissionState.requestPermission()
                    }
                },
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(top = locationButtonTopPadding, end = 12.dp),
                containerColor = MaterialTheme.colorScheme.surfaceContainerLowest,
                contentColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(
                    imageVector = Icons.Filled.MyLocation,
                    contentDescription = stringResource(R.string.map_my_location),
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        // Explica qué falta y lleva al ajuste correspondiente
        locationProblem?.let { problem ->
            ConfirmAlertDialog(
                title = when (problem) {
                    LocationProblem.PERMISSION_DENIED -> stringResource(R.string.map_permission_denied_title)
                    LocationProblem.LOCATION_OFF -> stringResource(R.string.map_location_off_title)
                },
                message = when (problem) {
                    LocationProblem.PERMISSION_DENIED -> stringResource(R.string.map_permission_denied_message)
                    LocationProblem.LOCATION_OFF -> stringResource(R.string.map_location_off_message)
                },
                confirmText = stringResource(R.string.open_settings),
                onConfirm = {
                    locationProblem = null
                    context.startActivity(problem.settingsIntent(context))
                },
                onDismiss = {
                    locationProblem = null
                    pendingFollow = false
                }
            )
        }
    }
}

// Lo que puede impedir centrar el mapa en el usuario
private enum class LocationProblem { PERMISSION_DENIED, LOCATION_OFF }

private fun LocationProblem.settingsIntent(context: Context): Intent = when (this) {
    LocationProblem.PERMISSION_DENIED -> Intent(
        Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
        Uri.fromParts("package", context.packageName, null)
    )
    LocationProblem.LOCATION_OFF -> Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
}

// Interruptor general de ubicación del teléfono: con él apagado el permiso no basta
private fun Context.isLocationEnabled(): Boolean {
    val manager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
    return LocationManagerCompat.isLocationEnabled(manager)
}

private val locationPermissions = arrayOf(
    Manifest.permission.ACCESS_FINE_LOCATION,
    Manifest.permission.ACCESS_COARSE_LOCATION
)

// Basta con la ubicación aproximada: desde Android 12 el usuario puede negar la precisa
private fun Context.hasLocationPermission(): Boolean =
    locationPermissions.any {
        ContextCompat.checkSelfPermission(this, it) == PackageManager.PERMISSION_GRANTED
    }

// Estado para manejar el permiso de ubicación de forma controlada
class LocationPermissionState(
    hasPermission: Boolean = false
) {
    var hasPermission by mutableStateOf(hasPermission)
        internal set

    // Se asigna al crear el estado en el composable; lanza la solicitud de permiso
    var requestPermission: () -> Unit = {}
        internal set
}

// onPermissionResult se llama después del diálogo del sistema; granted es true
// si se concedió la ubicación precisa o la aproximada
@Composable
fun rememberLocationPermissionState(
    onPermissionResult: (granted: Boolean) -> Unit = {}
): LocationPermissionState {
    val context = LocalContext.current
    val state = remember { LocationPermissionState(context.hasLocationPermission()) }

    // Lanzador para solicitar ambos permisos en un solo diálogo
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { results ->
        val granted = results.values.any { it }
        state.hasPermission = granted
        onPermissionResult(granted)
    }

    state.requestPermission = { launcher.launch(locationPermissions) }

    // El permiso puede cambiar desde Ajustes: se vuelve a leer al regresar a primer plano
    LifecycleResumeEffect(state) {
        state.hasPermission = context.hasLocationPermission()
        onPauseOrDispose { }
    }

    return state
}
