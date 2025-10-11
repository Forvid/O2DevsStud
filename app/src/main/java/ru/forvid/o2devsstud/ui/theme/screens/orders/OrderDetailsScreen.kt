package ru.forvid.o2devsstud.ui.screens.orders

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import ru.forvid.o2devsstud.domain.model.Order
import ru.forvid.o2devsstud.domain.model.OrderStatus
import ru.forvid.o2devsstud.ui.viewmodel.OrdersViewModel
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.foundation.layout.RowScope

private const val TAG = "OrderDetailsScreen"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderDetailsScreen(
    orderId: Long,
    onBack: () -> Unit,
    onConfirm: (Long) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: OrdersViewModel
) {
    val orders by viewModel.orders.collectAsState()
    val order = orders.find { it.id == orderId }

    val scope = rememberCoroutineScope()
    val scroll = rememberScrollState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Детали заявки") },
                navigationIcon = {
                    IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, contentDescription = "Назад") }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
            )
        }
    ) { innerPadding ->
        if (order == null) {
            Box(modifier = modifier.fillMaxSize().padding(innerPadding), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Заказ не найден (id = $orderId)")
                    Spacer(Modifier.height(12.dp))
                    Button(onClick = onBack) { Text("Назад") }
                }
            }
            return@Scaffold
        }

        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(scroll)
                .padding(16.dp),
            verticalArrangement = Arrangement.Top
        ) {
            Card(modifier = Modifier.fillMaxWidth(), elevation = CardDefaults.elevatedCardElevation(6.dp)) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(text = "${order.from} → ${order.to}", style = MaterialTheme.typography.titleMedium)
                    Spacer(Modifier.height(8.dp))
                    Text("Заявка: ${order.requestNumber}", style = MaterialTheme.typography.bodyMedium)
                    Spacer(Modifier.height(8.dp))
                    Text("Статус: ${order.status}", style = MaterialTheme.typography.bodyMedium)
                    Spacer(Modifier.height(8.dp))
                    Text("Расчетное время: ${order.estimatedDays} дн.", style = MaterialTheme.typography.bodySmall)
                }
            }

            Spacer(Modifier.height(16.dp))

            // Actions area — кнопки статусов (одинаковая ширина)
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                // Сгруппируем кнопки по рядам (2 в ряд) для компактности
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                    ActionButtonFull("Взял в работу") {
                        scope.launch { runCatching { viewModel.changeStatus(order.id, OrderStatus.IN_WORK) }.onFailure { Log.e(TAG,"err",it) } }
                    }
                    ActionButtonOutlined("В дороге к месту погрузки") {
                        scope.launch { runCatching { viewModel.changeStatus(order.id, OrderStatus.ON_WAY_TO_LOAD) }.onFailure { Log.e(TAG,"err",it) } }
                    }
                }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                    ActionButtonFull("Машина загружена") {
                        scope.launch { runCatching { viewModel.changeStatus(order.id, OrderStatus.LOADED) }.onFailure { Log.e(TAG,"err",it) } }
                    }
                    ActionButtonOutlined("В дороге на место разгрузки") {
                        scope.launch { runCatching { viewModel.changeStatus(order.id, OrderStatus.ON_WAY_TO_UNLOAD) }.onFailure { Log.e(TAG,"err",it) } }
                    }
                }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                    ActionButtonOutlined("На стоянке") {
                        scope.launch { runCatching { viewModel.changeStatus(order.id, OrderStatus.PARKED) }.onFailure { Log.e(TAG,"err",it) } }
                    }
                    ActionButtonOutlined("ТС прибыло") {
                        scope.launch { runCatching { viewModel.changeStatus(order.id, OrderStatus.ARRIVED_UNLOAD) }.onFailure { Log.e(TAG,"err",it) } }
                    }
                }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                    ActionButtonOutlined("Машина разгружена") {
                        scope.launch { runCatching { viewModel.changeStatus(order.id, OrderStatus.UNLOADED) }.onFailure { Log.e(TAG,"err",it) } }
                    }
                    ActionButtonFull("Забрал документы") {
                        scope.launch { runCatching { viewModel.changeStatus(order.id, OrderStatus.DOCUMENTS_TAKEN) }.onFailure { Log.e(TAG,"err",it) } }
                    }
                }
            }

            Spacer(Modifier.height(20.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
                Button(onClick = { onConfirm(order.id) }, modifier = Modifier.weight(1f)) { Text("Подтвердить") }
                OutlinedButton(onClick = onBack, modifier = Modifier.weight(1f)) { Text("Назад") }
            }
        }
    }
}

@Composable
private fun RowScope.ActionButtonFull(text: String, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .weight(1f)
            .heightIn(min = 48.dp)
    ) {
        Text(text)
    }
}

@Composable
private fun RowScope.ActionButtonOutlined(text: String, onClick: () -> Unit) {
    OutlinedButton(
        onClick = onClick,
        modifier = Modifier
            .weight(1f)
            .heightIn(min = 48.dp)
    ) {
        Text(text)
    }
}
