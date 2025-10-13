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
    val activeOrderTrackId by viewModel.activeOrderTrackId.collectAsState()

    LaunchedEffect(activeOrderTrackId) {
        // Эта логика может быть доработана, если нужно загружать трек
    }

    MapScreen(
        trackIdToShow = activeOrderTrackId,
        viewModel = viewModel,
        onBack = null // На главном экране кнопка "назад" не нужна
    )
}