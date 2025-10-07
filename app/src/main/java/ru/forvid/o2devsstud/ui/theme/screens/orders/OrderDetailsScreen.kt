package ru.forvid.o2devsstud.ui.screens.orders

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.ui.tooling.preview.Preview
import ru.forvid.o2devsstud.domain.model.Order
import ru.forvid.o2devsstud.domain.model.OrderStatus
import ru.forvid.o2devsstud.ui.theme.O2DevsStudTheme
import ru.forvid.o2devsstud.ui.viewmodel.OrdersViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderDetailsScreen(
    orderId: Long,
    onBack: () -> Unit,
    onConfirm: (Long) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: OrdersViewModel = hiltViewModel()
) {
    val orders by viewModel.orders.collectAsState()
    val order: Order? = orders.find { it.id == orderId }

    Column(modifier = modifier.fillMaxSize()) {
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

        if (order == null) {
            Column(
                modifier = Modifier.fillMaxSize().padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text("Заказ не найден (id = $orderId)")
                Spacer(modifier = Modifier.height(12.dp))
                Button(onClick = onBack) { Text("Назад") }
            }
            return@Column
        }

        Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
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
                // safe id handling
                val id = order.id
                StatusButton("Взял в работу") { id?.let { viewModel.changeStatus(it, OrderStatus.IN_WORK) } }
                StatusButton("В дороге к месту погрузки") { id?.let { viewModel.changeStatus(it, OrderStatus.ON_WAY_TO_LOAD) } }
                StatusButton("Машина загружена") { id?.let { viewModel.changeStatus(it, OrderStatus.LOADED) } }
                StatusButton("В дороге на место разгрузки") { id?.let { viewModel.changeStatus(it, OrderStatus.ON_WAY_TO_UNLOAD) } }
                StatusButton("На стоянке") { id?.let { viewModel.changeStatus(it, OrderStatus.PARKED) } }
                StatusButton("ТС прибыло на место разгрузки") { id?.let { viewModel.changeStatus(it, OrderStatus.ARRIVED_UNLOAD) } }
                StatusButton("Машина разгружена") { id?.let { viewModel.changeStatus(it, OrderStatus.UNLOADED) } }
                StatusButton("Забрал документы") { id?.let { viewModel.changeStatus(it, OrderStatus.DOCUMENTS_TAKEN) } }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
                Button(onClick = { order.id?.let { onConfirm(it) } }, modifier = Modifier.weight(1f)) { Text("Подтвердить") }
                Button(onClick = onBack, modifier = Modifier.weight(1f)) { Text("Назад") }
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
