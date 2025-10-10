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

private const val TAG = "OrderDetailsScreen"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderDetailsScreen(
    orderId: Long,
    onBack: () -> Unit,
    onConfirm: (Long) -> Unit,
    viewModel: OrdersViewModel,
    modifier: Modifier = Modifier
) {
    val orders by viewModel.orders.collectAsState()
    val order: Order? = orders.find { it.id == orderId }
    val scope = rememberCoroutineScope()
    val scroll = rememberScrollState()

    Scaffold(topBar = {
        TopAppBar(title = { Text("Детали заявки") }, navigationIcon = {
            IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, contentDescription = "Назад") }
        })
    }) { innerPadding ->
        if (order == null) {
            Box(modifier = Modifier.fillMaxSize().padding(innerPadding), contentAlignment = Alignment.Center) {
                Text("Заказ не найден (id = $orderId)")
            }
            return@Scaffold
        }

        Column(modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
            .verticalScroll(scroll)
            .padding(16.dp)) {

            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Откуда: ${order.from}", style = MaterialTheme.typography.bodyLarge)
                    Spacer(Modifier.height(8.dp))
                    Text("Куда: ${order.to}", style = MaterialTheme.typography.bodyLarge)
                    Spacer(Modifier.height(8.dp))
                    Text("Номер: ${order.requestNumber}")
                    Spacer(Modifier.height(8.dp))
                    Text("Статус: ${order.status}")
                    Spacer(Modifier.height(8.dp))
                    Text("Расчетное время: ${order.estimatedDays} дн.")
                }
            }

            Spacer(Modifier.height(16.dp))


            val buttonHeight = 48.dp
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                    Button(modifier = Modifier.weight(1f).height(buttonHeight), onClick = {
                        scope.launch { runCatching { viewModel.changeStatus(order.id, OrderStatus.IN_WORK) }.onFailure { Log.e(TAG, "err", it) } }
                    }) { Text("Взял в работу") }
                    OutlinedButton(modifier = Modifier.weight(1f).height(buttonHeight), onClick = {
                        scope.launch { runCatching { viewModel.changeStatus(order.id, OrderStatus.ON_WAY_TO_LOAD) } }
                    }) { Text("В дорогу к погрузке") }
                }


                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                    Button(modifier = Modifier.weight(1f).height(buttonHeight), onClick = {
                        scope.launch { runCatching { viewModel.changeStatus(order.id, OrderStatus.LOADED) } }
                    }) { Text("Машина загружена") }
                    OutlinedButton(modifier = Modifier.weight(1f).height(buttonHeight), onClick = {
                        scope.launch { runCatching { viewModel.changeStatus(order.id, OrderStatus.ON_WAY_TO_UNLOAD) } }
                    }) { Text("В дорогу на разгрузку") }
                }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                    OutlinedButton(modifier = Modifier.weight(1f).height(buttonHeight), onClick = {
                        scope.launch { runCatching { viewModel.changeStatus(order.id, OrderStatus.PARKED) } }
                    }) { Text("На стоянке") }
                    OutlinedButton(modifier = Modifier.weight(1f).height(buttonHeight), onClick = {
                        scope.launch { runCatching { viewModel.changeStatus(order.id, OrderStatus.ARRIVED_UNLOAD) } }
                    }) { Text("ТС прибыло") }
                }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                    OutlinedButton(modifier = Modifier.weight(1f).height(buttonHeight), onClick = {
                        scope.launch { runCatching { viewModel.changeStatus(order.id, OrderStatus.UNLOADED) } }
                    }) { Text("Машина разгружена") }
                    Button(modifier = Modifier.weight(1f).height(buttonHeight), onClick = {
                        scope.launch { runCatching { viewModel.changeStatus(order.id, OrderStatus.DOCUMENTS_TAKEN) } }
                    }) { Text("Забрал документы") }
                }

                Spacer(Modifier.height(20.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Button(modifier = Modifier.weight(1f), onClick = { onConfirm(order.id) }) { Text("Подтвердить") }
                    OutlinedButton(modifier = Modifier.weight(1f), onClick = onBack) { Text("Назад") }
                }
            }
        }
    }
}
