package ru.forvid.o2devsstud.ui.screens

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
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.model.*
import kotlinx.coroutines.launch
import ru.forvid.o2devsstud.data.remote.dto.TrackDto
import ru.forvid.o2devsstud.ui.viewmodel.OrdersViewModel
import ru.forvid.o2devsstud.ui.viewmodel.TrackState

@SuppressLint("MissingPermission")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapScreen(
    modifier: Modifier = Modifier,
    trackIdToShow: Long? = null,
    viewModel: OrdersViewModel = hiltViewModel(),
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

    // подписка на состояние трека
    val trackState by viewModel.trackState.collectAsState()

    // если передали id — один раз запросить трек
    LaunchedEffect(trackIdToShow) {
        trackIdToShow?.let { viewModel.fetchTrack(it) }
    }

    // MapView + lifecycle
    val mapView = rememberMapViewWithLifecycle(lifecycleOwner)
    val googleMapRef = remember { mutableStateOf<GoogleMap?>(null) }
    val polylineRef = remember { mutableStateOf<Polyline?>(null) }
    val coroutineScope = rememberCoroutineScope()

    // Fused location для центрирования / обновлений (не обязателен, но полезен)
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
            try {
                val request = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 5_000L)
                    .setMinUpdateDistanceMeters(5f)
                    .setMaxUpdateDelayMillis(10_000L)
                    .build()
                fused.requestLocationUpdates(request, locationCallback, Looper.getMainLooper())
            } catch (_: Throwable) { /* ignore */ }
        } else {
            fused.removeLocationUpdates(locationCallback)
        }
    }

    Column(modifier = modifier.fillMaxSize()) {
        TopAppBar(
            title = { Text("Карта") },
            navigationIcon = {
                if (onBack != null) {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Назад")
                    }
                }
            }
        )

        Spacer(modifier = Modifier.height(8.dp))

        Box(modifier = Modifier.fillMaxSize()) {
            AndroidView(factory = { mapView }, modifier = Modifier.fillMaxSize()) { mv ->
                mv.getMapAsync { g ->
                    googleMapRef.value = g
                    g.uiSettings.isZoomControlsEnabled = true

                    // включаем my-location только после проверки разрешения
                    if (locationGranted &&
                        ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED
                    ) {
                        try {
                            g.isMyLocationEnabled = true
                        } catch (_: SecurityException) { /* ignore */ }
                    }

                    // если уже есть трек — нарисовать
                    when (val s = trackState) {
                        is TrackState.Success -> drawTrackOnMap(g, s.track, polylineRef)
                        else -> {}
                    }
                }
            }

            // кнопка центрирования (нижний правый угол)
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
                            try {
                                fused.lastLocation.addOnSuccessListener { l ->
                                    l?.let {
                                        googleMapRef.value?.animateCamera(
                                            CameraUpdateFactory.newLatLngZoom(LatLng(it.latitude, it.longitude), 14f)
                                        )
                                    }
                                }
                            } catch (_: Throwable) { }
                        }
                    },
                    shape = CircleShape,
                    containerColor = MaterialTheme.colorScheme.primary
                ) {
                    Icon(Icons.Default.MyLocation, contentDescription = "Центр")
                }
            }

            // индикатор загрузки/ошибки
            when (trackState) {
                is TrackState.Loading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
                is TrackState.Error -> {
                    val msg = (trackState as TrackState.Error).message ?: "Ошибка"
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.TopCenter) {
                        Text(text = "Ошибка загрузки трека: $msg", modifier = Modifier.padding(12.dp))
                    }
                }
                else -> {}
            }
        }
    }

    // при изменении состояния трека — если googleMap готов, перерисуем
    LaunchedEffect(googleMapRef.value, trackState) {
        val g = googleMapRef.value ?: return@LaunchedEffect
        when (val s = trackState) {
            is TrackState.Success -> drawTrackOnMap(g, s.track, polylineRef)
            else -> {}
        }
    }
}

/** рисует трек, удаляя пред. polyline и центруя камеру */
private fun drawTrackOnMap(g: GoogleMap, track: TrackDto, polylineRef: MutableState<Polyline?>) {
    val safePoints = (track.points ?: emptyList()).mapNotNull { p ->
        val lat = p.lat ?: return@mapNotNull null
        val lon = p.lon ?: p.lng ?: return@mapNotNull null
        LatLng(lat, lon)
    }

    try { polylineRef.value?.remove() } catch (_: Throwable) { /* ignore */ }

    if (safePoints.isEmpty()) return

    val polyline = g.addPolyline(PolylineOptions().addAll(safePoints).width(8f))
    polylineRef.value = polyline

    try {
        val builder = LatLngBounds.builder()
        safePoints.forEach { builder.include(it) }
        val bounds = builder.build()
        val padding = 150
        g.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, padding))
    } catch (_: Throwable) {
        g.moveCamera(CameraUpdateFactory.newLatLngZoom(safePoints.first(), 12f))
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
