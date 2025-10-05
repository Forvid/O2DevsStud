package ru.forvid.o2devsstud.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.PolylineOptions
import ru.forvid.o2devsstud.data.remote.dto.TrackDto
import ru.forvid.o2devsstud.ui.viewmodel.OrdersViewModel
import androidx.compose.material3.*
import androidx.compose.foundation.layout.*
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapScreen(
    modifier: Modifier = Modifier,
    trackIdToShow: Long? = null,
    viewModel: OrdersViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    // permission launcher
    var locationGranted by remember { mutableStateOf(false) }
    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted -> locationGranted = granted }

    // запрос разрешения при старте
    LaunchedEffect(Unit) {
        val ok = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        if (!ok) launcher.launch(Manifest.permission.ACCESS_FINE_LOCATION) else locationGranted = true
    }

    // MapView with lifecycle
    val mapView = rememberMapViewWithLifecycle(lifecycleOwner)

    Column(modifier = modifier.fillMaxSize().padding(8.dp)) {
        TopAppBar(title = { Text("Карта") })
        Spacer(modifier = Modifier.height(8.dp))

        AndroidView(factory = { mapView }, modifier = Modifier.fillMaxSize()) { mv ->
            mv.getMapAsync { googleMap ->
                googleMap.uiSettings.isZoomControlsEnabled = true

                if (locationGranted &&
                    ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED
                ) {
                    try {
                        googleMap.isMyLocationEnabled = true
                    } catch (_: Throwable) { /* ignore */ }
                }

                // Если указан id — запрашиваем трек и рисуем
                trackIdToShow?.let { id ->
                    viewModel.fetchTrackAndLog(id) { track: TrackDto? ->
                        track?.let { t ->
                            // безопасно: если points == null — используем пустой список
                            val safePoints = (t.points ?: emptyList()).mapNotNull { p ->
                                // p.lat или lon могут быть nullable — пропускаем точку если нет координат
                                val lat = when (val l = p.lat) {
                                    null -> return@mapNotNull null
                                    else -> l
                                }
                                val lon = when {
                                    p.lon != null -> p.lon
                                    p.lng != null -> p.lng
                                    else -> return@mapNotNull null
                                }
                                LatLng(lat, lon)
                            }

                            if (safePoints.isNotEmpty()) {
                                googleMap.addPolyline(PolylineOptions().addAll(safePoints))
                                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(safePoints.first(), 12f))
                            }
                        }
                    }
                }
            }
        }
    }
}

/** Хелпер: MapView + lifecycle */
@Composable
fun rememberMapViewWithLifecycle(lifecycleOwner: LifecycleOwner): MapView {
    val context = LocalContext.current
    val mapView = remember { MapView(context) }

    DisposableEffect(lifecycleOwner) {
        val lifecycle = lifecycleOwner.lifecycle
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_CREATE -> mapView.onCreate(null)
                Lifecycle.Event.ON_START -> mapView.onStart()
                Lifecycle.Event.ON_RESUME -> mapView.onResume()
                Lifecycle.Event.ON_PAUSE -> mapView.onPause()
                Lifecycle.Event.ON_STOP -> mapView.onStop()
                Lifecycle.Event.ON_DESTROY -> mapView.onDestroy()
                else -> {}
            }
        }
        lifecycle.addObserver(observer)
        onDispose { lifecycle.removeObserver(observer) }
    }

    return mapView
}
