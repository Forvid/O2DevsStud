package ru.forvid.o2devsstud.ui.theme.screens.orders

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import ru.forvid.o2devsstud.domain.model.Order
import ru.forvid.o2devsstud.domain.model.OrderStatus
import ru.forvid.o2devsstud.ui.viewmodel.OrdersViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderDetailsScreen(
    orderId: Long,
    onBack: () -> Unit,
    onConfirm: (Long) -> Unit,
    onShowTrackOnMap: (Long) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: OrdersViewModel
) {
    val uiState by viewModel.uiState.collectAsState()
    val order = remember(uiState.orders, orderId) {
        uiState.orders.find { it.id == orderId }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Заявка №${order?.requestNumber ?: "..."}") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Назад")
                    }
                }
            )
        },
        modifier = modifier
    ) { innerPadding ->
        Box(
            modifier = Modifier.fillMaxSize().padding(innerPadding),
            contentAlignment = Alignment.Center
        ) {
            if (order != null) {
                OrderContentView(
                    order = order,
                    viewModel = viewModel,
                    onConfirm = onConfirm,
                    onShowTrackOnMap = onShowTrackOnMap
                )
            } else if (uiState.isLoading) {
                CircularProgressIndicator()
            } else {
                Text("Заказ с ID $orderId не найден.")
            }
        }
    }
}

@Composable
private fun OrderContentView(
    order: Order,
    viewModel: OrdersViewModel,
    onConfirm: (Long) -> Unit,
    onShowTrackOnMap: (Long) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        InfoCard(order = order)
        StatusActionButtons(
            order = order,
            viewModel = viewModel,
            onConfirm = onConfirm,
            onShowTrackOnMap = onShowTrackOnMap
        )
    }
}

@Composable
private fun InfoCard(order: Order) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            InfoRow(label = "Откуда:", value = order.from)
            InfoRow(label = "Куда:", value = order.to)
            // Теперь это будет работать, так как displayName есть в OrderStatus
            InfoRow(label = "Статус:", value = order.status.displayName)
            InfoRow(label = "Расчетное время:", value = "${order.estimatedDays} дн.")
        }
    }
}

@Composable
private fun InfoRow(label: String, value: String) {
    Row {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.width(120.dp)
        )
        Text(text = value, style = MaterialTheme.typography.bodyLarge)
    }
}

@Composable
private fun StatusActionButtons(
    order: Order,
    viewModel: OrdersViewModel,
    onConfirm: (Long) -> Unit,
    onShowTrackOnMap: (Long) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth().padding(top = 16.dp)) {
        // --- ИСПРАВЛЕННАЯ ЛОГИКА ПОКАЗА КНОПОК ---
        when (order.status) {
            OrderStatus.PLACED -> ActionButton("Взял в работу") {
                viewModel.changeStatus(order.id, OrderStatus.TAKEN)
            }
            OrderStatus.TAKEN -> ActionButton("В пути к месту погрузки") {
                viewModel.changeStatus(order.id, OrderStatus.IN_TRANSIT_TO_LOAD)
                val trackId = order.id // ЗАГЛУШКА: ID трека к месту погрузки
                viewModel.setActiveTrack(trackId)
                onShowTrackOnMap(trackId)
            }
            OrderStatus.IN_TRANSIT_TO_LOAD -> ActionButton("Машина загружена") {
                viewModel.changeStatus(order.id, OrderStatus.LOADED)
                viewModel.setActiveTrack(null) // Сбрасываем трек
            }
            OrderStatus.LOADED -> ActionButton("В дороге на место разгрузки") {
                viewModel.changeStatus(order.id, OrderStatus.IN_TRANSIT_TO_UNLOAD)
                val trackId = order.id + 1 // ЗАГЛУШКА: ID трека к месту разгрузки
                viewModel.setActiveTrack(trackId)
                onShowTrackOnMap(trackId)
            }
            OrderStatus.IN_TRANSIT_TO_UNLOAD -> ActionButton("На стоянке") {
                viewModel.changeStatus(order.id, OrderStatus.PARKED)
                viewModel.setActiveTrack(null) // Сбрасываем трек
            }
            OrderStatus.PARKED -> ActionButton("Продолжить движение") {
                viewModel.changeStatus(order.id, OrderStatus.IN_TRANSIT_TO_UNLOAD)
                val trackId = order.id + 1 // ЗАГЛУШКА: ID трека к месту разгрузки
                viewModel.setActiveTrack(trackId)
                onShowTrackOnMap(trackId)
            }
            OrderStatus.ARRIVED_FOR_UNLOADING -> ActionButton("Машина разгружена") {
                viewModel.changeStatus(order.id, OrderStatus.UNLOADED)
                viewModel.setActiveTrack(null)
            }
            OrderStatus.UNLOADED -> ActionButton("Забрал документы") {
                viewModel.changeStatus(order.id, OrderStatus.DOCUMENTS_TAKEN)
                onConfirm(order.id)
            }
            else -> {
                // Для статусов DOCUMENTS_TAKEN и других, не предполагающих действий
                Text(
                    "Для статуса \"${order.status.displayName}\" нет доступных действий.",
                    modifier = Modifier.padding(vertical = 16.dp),
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

@Composable
private fun ActionButton(text: String, onClick: () -> Unit) {
    Button(onClick = onClick, modifier = Modifier.fillMaxWidth().height(48.dp)) {
        Text(text)
    }
}
