package ru.forvid.o2devsstud.ui.theme.screens.orders

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
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
        if (order != null) {
            OrderContentView(
                order = order,
                viewModel = viewModel,
                onConfirm = onConfirm,
                onShowTrackOnMap = onShowTrackOnMap,
                contentPadding = innerPadding
            )
        } else {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                if (uiState.isLoading) {
                    CircularProgressIndicator()
                } else {
                    Text("Заказ с ID $orderId не найден.")
                }
            }
        }
    }
}

@Composable
private fun OrderContentView(
    order: Order,
    viewModel: OrdersViewModel,
    onConfirm: (Long) -> Unit,
    onShowTrackOnMap: (Long) -> Unit,
    contentPadding: PaddingValues
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(contentPadding)
            .padding(horizontal = 16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.Top
    ) {
        Spacer(modifier = Modifier.height(16.dp))
        InfoCard(order = order)
        Spacer(modifier = Modifier.height(24.dp))
        StatusActionButtons(
            order = order,
            viewModel = viewModel,
            onConfirm = onConfirm,
            onShowTrackOnMap = onShowTrackOnMap
        )
        Spacer(modifier = Modifier.height(16.dp))
    }
}


@Composable
private fun InfoCard(order: Order) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            InfoRow(label = "Откуда:", value = order.from)
            InfoRow(label = "Куда:", value = order.to)
            InfoRow(label = "Статус:", value = order.status.displayName)
            InfoRow(label = "Расчетное время:", value = "${order.estimatedDays} дн.")
        }
    }
}

@Composable
private fun InfoRow(label: String, value: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.width(150.dp)
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
    var showConfirmDialog by remember { mutableStateOf(false) }
    var actionToConfirm by remember { mutableStateOf<(() -> Unit)?>(null) }
    var dialogText by remember { mutableStateOf("") }

    val onActionClick = { text: String, action: () -> Unit ->
        dialogText = "Вы уверены, что хотите изменить статус на \"$text\"?"
        actionToConfirm = action
        showConfirmDialog = true
    }

    if (showConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showConfirmDialog = false },
            title = { Text("Подтверждение действия") },
            text = { Text(dialogText) },
            confirmButton = {
                Button(
                    onClick = {
                        actionToConfirm?.invoke()
                        showConfirmDialog = false
                    }
                ) {
                    Text("Да")
                }
            },
            dismissButton = {
                TextButton(onClick = { showConfirmDialog = false }) {
                    Text("Отмена")
                }
            }
        )
    }

    Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        when (order.status) {
            OrderStatus.PLACED -> ActionButton("Взял в работу") {
                onActionClick("Взял в работу") {
                    viewModel.changeStatus(order.id, OrderStatus.TAKEN)
                }
            }
            OrderStatus.TAKEN -> ActionButton("В пути к месту погрузки") {
                onActionClick("В пути к месту погрузки") {
                    viewModel.changeStatus(order.id, OrderStatus.IN_TRANSIT_TO_LOAD)
                    order.trackId?.let { trackId ->
                        viewModel.setActiveTrack(trackId)
                        onShowTrackOnMap(trackId)
                    }
                }
            }
            OrderStatus.IN_TRANSIT_TO_LOAD -> ActionButton("Машина загружена") {
                onActionClick("Машина загружена") {
                    viewModel.changeStatus(order.id, OrderStatus.LOADED)
                    viewModel.setActiveTrack(null)
                }
            }
            OrderStatus.LOADED -> ActionButton("В дороге на место разгрузки") {
                onActionClick("В дороге на место разгрузки") {
                    viewModel.changeStatus(order.id, OrderStatus.IN_TRANSIT_TO_UNLOAD)
                    order.trackId?.let { trackId ->
                        viewModel.setActiveTrack(trackId)
                        onShowTrackOnMap(trackId)
                    }
                }
            }
            OrderStatus.IN_TRANSIT_TO_UNLOAD -> ActionButton("На стоянке") {
                onActionClick("На стоянке") {
                    viewModel.changeStatus(order.id, OrderStatus.PARKED)
                    viewModel.setActiveTrack(null)
                }
            }
            OrderStatus.PARKED -> ActionButton("Продолжить движение") {
                onActionClick("Продолжить движение") {
                    viewModel.changeStatus(order.id, OrderStatus.IN_TRANSIT_TO_UNLOAD)
                    order.trackId?.let { trackId ->
                        viewModel.setActiveTrack(trackId)
                        onShowTrackOnMap(trackId)
                    }
                }
            }
            OrderStatus.ARRIVED_FOR_UNLOADING -> ActionButton("Машина разгружена") {
                onActionClick("Машина разгружена") {
                    viewModel.changeStatus(order.id, OrderStatus.UNLOADED)
                    viewModel.setActiveTrack(null)
                }
            }
            OrderStatus.UNLOADED -> ActionButton("Забрал документы") {
                onActionClick("Забрал документы") {
                    viewModel.changeStatus(order.id, OrderStatus.DOCUMENTS_TAKEN)
                    onConfirm(order.id)
                }
            }
            else -> {
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
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
    ) {
        Text(text)
    }
}