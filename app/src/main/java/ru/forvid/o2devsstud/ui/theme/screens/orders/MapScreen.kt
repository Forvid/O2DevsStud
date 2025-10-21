package ru.forvid.o2devsstud.ui.theme.screens.orders

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.location.Location
import android.os.Looper
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions
import ru.forvid.o2devsstud.ui.viewmodel.OrdersViewModel
import ru.forvid.o2devsstud.ui.viewmodel.TrackState

@SuppressLint("MissingPermission")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapScreen(
    modifier: Modifier = Modifier,
    viewModel: OrdersViewModel,
    trackIdToShow: Long? = null,
    showTopBar: Boolean = true,
    onBack: (() -> Unit)? = null
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    var locationGranted by remember { mutableStateOf(false) }
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted -> locationGranted = granted }

    LaunchedEffect(Unit) {
        val ok = ContextCompat.checkSelfPermission(
            context, Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        if (!ok) permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION) else locationGranted = true
    }

    val trackState by viewModel.trackState.collectAsState()

    LaunchedEffect(trackIdToShow) {
        if (trackIdToShow != null) {
            viewModel.fetchTrack(trackIdToShow)
        } else {
            viewModel.clearTrackState()
        }
    }

    val mapView = rememberMapViewWithLifecycle(lifecycleOwner)
    val googleMapRef = remember { mutableStateOf<GoogleMap?>(null) }
    val mapObjects = remember { mutableStateListOf<MapObject>() }
    val fused = remember { LocationServices.getFusedLocationProviderClient(context) }
    var lastLocation by remember { mutableStateOf<Location?>(null) }

    val locationCallback = remember {
        object : LocationCallback() {
            override fun onLocationResult(result: LocationResult) {
                lastLocation = result.lastLocation
            }
        }
    }

    LaunchedEffect(locationGranted) {
        if (locationGranted) {
            val request = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 5_000L)
                .setMinUpdateDistanceMeters(5f)
                .setMaxUpdateDelayMillis(10_000L)
                .build()
            fused.requestLocationUpdates(request, locationCallback, Looper.getMainLooper())
        } else {
            fused.removeLocationUpdates(locationCallback)
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            fused.removeLocationUpdates(locationCallback)
        }
    }

    Column(modifier = modifier.fillMaxSize()) {
        if (showTopBar) {
            TopAppBar(
                title = { Text("Карта") },
                navigationIcon = {
                    if (onBack != null) {
                        IconButton(onClick = {
                            viewModel.clearTrackState()
                            onBack()
                        }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Назад")
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        }

        Box(modifier = Modifier.fillMaxSize()) {
            AndroidView(factory = { mapView }, modifier = Modifier.fillMaxSize()) { mv ->
                mv.getMapAsync { googleMap ->
                    googleMapRef.value = googleMap
                    googleMap.uiSettings.isZoomControlsEnabled = true
                    if (locationGranted) {
                        try {
                            googleMap.isMyLocationEnabled = true
                        } catch (_: SecurityException) { /* ignore */ }
                    }
                    googleMap.clear()
                    mapObjects.forEach { it.draw(googleMap) }
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(12.dp),
                verticalArrangement = Arrangement.Bottom,
                horizontalAlignment = Alignment.End
            ) {
                FloatingActionButton(
                    onClick = {
                        val g = googleMapRef.value
                        val loc = lastLocation
                        if (g != null && loc != null) {
                            g.animateCamera(CameraUpdateFactory.newLatLngZoom(LatLng(loc.latitude, loc.longitude), 14f))
                        } else {
                            fused.lastLocation.addOnSuccessListener { l: Location? ->
                                l?.let {
                                    googleMapRef.value?.animateCamera(
                                        CameraUpdateFactory.newLatLngZoom(LatLng(it.latitude, it.longitude), 14f)
                                    )
                                }
                            }
                        }
                    },
                    shape = CircleShape,
                    containerColor = MaterialTheme.colorScheme.primary
                ) {
                    Icon(Icons.Default.MyLocation, contentDescription = "Центр")
                }
            }

            // Этот when обрабатывает UI-элементы (загрузчик, снекбар)
            val currentTrackState = trackState
            when (currentTrackState) {
                is TrackState.Loading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
                is TrackState.Error -> {
                    val msg = currentTrackState.message ?: "Ошибка"
                    Snackbar(modifier = Modifier.align(Alignment.BottomCenter).padding(16.dp)) {
                        Text(text = "Ошибка загрузки трека: $msg")
                    }
                }
                is TrackState.Idle, is TrackState.Success -> {
                    // Ничего не показывает поверх карты в этих состояниях
                }
            }
        }
    }

    // Этот LaunchedEffect отвечает за отрисовку объектов на карте
    LaunchedEffect(trackState) {
        val gMap = googleMapRef.value ?: return@LaunchedEffect
        mapObjects.clear()

        val currentTrackState = trackState
        when (currentTrackState) {
            is TrackState.Success -> {
                val points = currentTrackState.track.points.mapNotNull { point ->
                    if (point.lat != null && point.lng != null) {
                        LatLng(point.lat, point.lng)
                    } else {
                        null
                    }
                }
                if (points.isNotEmpty()) {
                    mapObjects.add(MapObject.PolylineObject(points))
                    points.firstOrNull()?.let { mapObjects.add(
                        MapObject.MarkerObject(
                            it,
                            "Начало маршрута"
                        )
                    ) }
                    points.lastOrNull()?.let { mapObjects.add(
                        MapObject.MarkerObject(
                            it,
                            "Конец маршрута"
                        )
                    ) }

                    val boundsBuilder = LatLngBounds.builder()
                    points.forEach { boundsBuilder.include(it) }
                    gMap.animateCamera(CameraUpdateFactory.newLatLngBounds(boundsBuilder.build(), 150))
                }
            }
            is TrackState.Error, is TrackState.Idle, is TrackState.Loading -> {
                // В этих состояниях карта должна быть чистой от старого трека.
                // mapObjects уже очищен, так что здесь ничего делать не нужно.
            }
        }
        gMap.clear()
        mapObjects.forEach { it.draw(gMap) }
    }
}

private sealed class MapObject {
    abstract fun draw(googleMap: GoogleMap)

    data class MarkerObject(val position: LatLng, val title: String, val snippet: String? = null) : MapObject() {
        override fun draw(googleMap: GoogleMap) {
            googleMap.addMarker(MarkerOptions().position(position).title(title).snippet(snippet))
        }
    }

    data class PolylineObject(val points: List<LatLng>) : MapObject() {
        override fun draw(googleMap: GoogleMap) {
            googleMap.addPolyline(
                PolylineOptions()
                    .addAll(points)
                    .color(android.graphics.Color.BLUE)
                    .width(15f)
            )
        }
    }
}

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

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true, widthDp = 360, heightDp = 640, name = "Map - preview")
@Composable
private fun MapScreenPreview() {
    ru.forvid.o2devsstud.ui.theme.O2DevsStudTheme {
        Column(modifier = Modifier.fillMaxSize()) {
            TopAppBar(title = { Text("Карта (Preview)") })
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Map preview — AndroidView(MapView) не отображается в Preview")
            }
        }
    }
}