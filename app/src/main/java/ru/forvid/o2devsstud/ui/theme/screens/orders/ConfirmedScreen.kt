package ru.forvid.o2devsstud.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import ru.forvid.o2devsstud.domain.model.Order
import ru.forvid.o2devsstud.domain.model.OrderStatus
import ru.forvid.o2devsstud.ui.theme.O2DevsStudTheme
import ru.forvid.o2devsstud.ui.viewmodel.OrdersViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConfirmedScreen(
    orderId: Long,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: OrdersViewModel = hiltViewModel()
) {
    val orders by viewModel.orders.collectAsState()
    val order: Order? = orders.find { it.id == orderId }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TopAppBar(
            title = { Text("Заказ подтвержден") },
            navigationIcon = {
                IconButton(onClick = onBack) {
                    Icon(Icons.Filled.ArrowBack, contentDescription = "Назад")
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            )
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Визуальный индикатор подтверждения
        Icon(
            imageVector = Icons.Filled.CheckCircle,
            contentDescription = "Подтвержден",
            tint = Color(0xFF4CAF50),
            modifier = Modifier.size(80.dp)
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = "Заказ успешно подтвержден!",
            style = MaterialTheme.typography.headlineSmall,
            color = Color(0xFF4CAF50)
        )

        Spacer(modifier = Modifier.height(24.dp))

        if (order != null) {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(text = "Номер заказа: ${order.requestNumber}")
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = "Откуда: ${order.from}")
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = "Куда: ${order.to}")
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = "Статус: ${order.status}")
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = "Расчетное время: ${order.estimatedDays} дн.")
                }
            }
        } else {
            Text("Заказ не найден (id = $orderId)")
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(onClick = onBack) {
            Text("Вернуться к списку заказов")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ConfirmedScreenPreview() {
    val fakeOrder = Order(
        id = 1L,
        from = "ул. Пример, 1",
        to = "г. Примерск",
        requestNumber = "AP-001",
        status = OrderStatus.PLACED,
        estimatedDays = 3
    )

    O2DevsStudTheme {
        ConfirmedScreen(orderId = fakeOrder.id, onBack = {})
    }
}
