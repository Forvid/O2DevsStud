package ru.forvid.o2devsstud.ui.screens

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import ru.forvid.o2devsstud.ui.viewmodel.OrdersViewModel

@Composable
fun HomeScreen(
    viewModel: OrdersViewModel,
    onOrderClick: (Long) -> Unit
) {
    // Подписываемся на ID активного трека из ViewModel
    val activeOrderTrackId by viewModel.activeOrderTrackId.collectAsState()

    // Этот LaunchedEffect будет автоматически запрашивать трек,
    // как только activeOrderTrackId изменится в ViewModel.
    LaunchedEffect(activeOrderTrackId) {
        activeOrderTrackId?.let { trackId ->
            viewModel.fetchTrack(trackId)
        }
    }

    MapScreen(
        viewModel = viewModel,
        trackIdToShow = activeOrderTrackId,
        showTopBar = false, // <-- Вот ключ к чистому интерфейсу!
        onBack = null
    )
}