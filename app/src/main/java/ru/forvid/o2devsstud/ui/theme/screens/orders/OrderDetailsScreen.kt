package ru.forvid.o2devsstud.ui.screens.orders

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import androidx.compose.runtime.rememberCoroutineScope
import ru.forvid.o2devsstud.domain.model.Order
import ru.forvid.o2devsstud.domain.model.OrderStatus
import ru.forvid.o2devsstud.ui.viewmodel.OrdersViewModel
import androidx.compose.material.icons.automirrored.filled.ArrowBack

private const val TAG = "OrderDetailsScreen"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderDetailsScreen(
    orderId: Long,
    onBack: () -> Unit,
    onConfirm: (Long) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: OrdersViewModel // передаётся из NavGraph
) {
    val orders by viewModel.orders.collectAsState()
    val order: Order? = orders.find { it.id == orderId }

    val scope = rememberCoroutineScope()
    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Детали заявки") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(androidx.compose.material.icons.Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Назад")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        }
    ) { innerPadding ->
        if (order == null) {
            // пока заказ не найден — центрируем сообщение (учёт innerPadding)
            Column(
                modifier = modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text("Заказ не найден (id = $orderId)")
                Spacer(modifier = Modifier.height(12.dp))
                Button(onClick = onBack) { Text("Назад") }
            }
            return@Scaffold
        }

        // Основное содержимое — прокручиваемое
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(scrollState)
                .padding(16.dp),
            verticalArrangement = Arrangement.Top
        ) {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Откуда: ${order.from}")
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Куда: ${order.to}")
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Номер: ${order.requestNumber}")
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Статус: ${order.status}")
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Расчетное время: ${order.estimatedDays} дн.")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // --- Кнопки статусов ---
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                // запуск операции в корутине и логирование ошибки
                StatusButton("Взял в работу") {
                    scope.launch {
                        runCatching { viewModel.changeStatus(order.id, OrderStatus.IN_WORK) }
                            .onFailure { Log.e(TAG, "change status error", it) }
                    }
                }
                StatusButton("В дороге к месту погрузки") {
                    scope.launch {
                        runCatching { viewModel.changeStatus(order.id, OrderStatus.ON_WAY_TO_LOAD) }
                            .onFailure { Log.e(TAG, "change status error", it) }
                    }
                }
                StatusButton("Машина загружена") {
                    scope.launch {
                        runCatching { viewModel.changeStatus(order.id, OrderStatus.LOADED) }
                            .onFailure { Log.e(TAG, "change status error", it) }
                    }
                }
                StatusButton("В дороге на место разгрузки") {
                    scope.launch {
                        runCatching { viewModel.changeStatus(order.id, OrderStatus.ON_WAY_TO_UNLOAD) }
                            .onFailure { Log.e(TAG, "change status error", it) }
                    }
                }
                StatusButton("На стоянке") {
                    scope.launch {
                        runCatching { viewModel.changeStatus(order.id, OrderStatus.PARKED) }
                            .onFailure { Log.e(TAG, "change status error", it) }
                    }
                }
                StatusButton("ТС прибыло на место разгрузки") {
                    scope.launch {
                        runCatching { viewModel.changeStatus(order.id, OrderStatus.ARRIVED_UNLOAD) }
                            .onFailure { Log.e(TAG, "change status error", it) }
                    }
                }
                StatusButton("Машина разгружена") {
                    scope.launch {
                        runCatching { viewModel.changeStatus(order.id, OrderStatus.UNLOADED) }
                            .onFailure { Log.e(TAG, "change status error", it) }
                    }
                }
                // кнопка, которая раньше крашила — тоже защищена
                StatusButton("Забрал документы") {
                    scope.launch {
                        runCatching { viewModel.changeStatus(order.id, OrderStatus.DOCUMENTS_TAKEN) }
                            .onFailure { Log.e(TAG, "change status error", it) }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
                    Button(onClick = { onConfirm(order.id) }, modifier = Modifier.weight(1f)) { Text("Подтвердить") }
                    Button(onClick = onBack, modifier = Modifier.weight(1f)) { Text("Назад") }
                }
            }
        }
    }
}

@Composable
fun StatusButton(label: String, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(label)
    }
}
