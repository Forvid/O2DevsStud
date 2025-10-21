package ru.forvid.o2devsstud.ui.theme.screens.orders

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ru.forvid.o2devsstud.domain.model.Order
import ru.forvid.o2devsstud.domain.model.OrderStatus
import ru.forvid.o2devsstud.ui.viewmodel.OrdersViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrdersScreen(
    onOpenOrder: (Long) -> Unit,
    onCreateOrder: () -> Unit,
    onDrawerOpen: () -> Unit, // Добавил параметр для открытия бокового меню
    modifier: Modifier = Modifier,
    viewModel: OrdersViewModel
) {
    val uiState by viewModel.uiState.collectAsState()
    val orders = uiState.orders

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Текущие поставки") },
                navigationIcon = {
                    IconButton(onClick = onDrawerOpen) {
                        Icon(Icons.Default.Menu, contentDescription = "Меню")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onCreateOrder) {
                Icon(Icons.Default.Add, contentDescription = "Создать заявку")
            }
        },
        modifier = modifier
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            if (uiState.isLoading && orders.isEmpty()) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else if (orders.isEmpty()) {
                Text(
                    "Нет активных поставок",
                    modifier = Modifier.align(Alignment.Center),
                    style = MaterialTheme.typography.titleMedium
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(items = orders, key = { order -> order.id }) { order ->
                        OrderItemNew(
                            order = order,
                            onClick = { onOpenOrder(order.id) }
                        )
                    }
                }
            }

            uiState.error?.let { error ->
                Box(modifier = Modifier.align(Alignment.BottomCenter).padding(16.dp)) {
                    Text(
                        text = "Ошибка: $error",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}

@Composable
fun OrderItemNew(
    order: Order,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.LocalShipping,
                contentDescription = "Заказ",
                modifier = Modifier.size(40.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Заявка №${order.requestNumber}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = order.date,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Text(
                    text = "${order.from} → ${order.to}",
                    style = MaterialTheme.typography.bodyLarge
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        // Ваш индикатор статуса сохранен
                        Canvas(modifier = Modifier.size(8.dp)) {
                            drawCircle(color = if (order.status == OrderStatus.PLACED) Color.Green else Color.Red)
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = order.status.displayName, // Использует displayName для консистентности
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                    order.codAmount?.let {
                        if (it > 0) {
                            Text(
                                text = "Нал. платеж: $it",
                                style = MaterialTheme.typography.bodyMedium,
                                fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            }
        }
    }
}