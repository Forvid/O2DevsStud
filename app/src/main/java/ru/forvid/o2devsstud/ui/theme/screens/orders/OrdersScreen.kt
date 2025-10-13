package ru.forvid.o2devsstud.ui.theme.screens.orders

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ru.forvid.o2devsstud.ui.theme.components.OrderItem
import ru.forvid.o2devsstud.ui.viewmodel.OrdersViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrdersScreen(
    onOpenOrder: (Long) -> Unit,
    onCreateOrder: () -> Unit,
    onShowMap: (Long) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: OrdersViewModel
) {
    // Получаю полное состояние UI из ViewModel.
    val uiState by viewModel.uiState.collectAsState()
    val orders = uiState.orders

    Scaffold(
        // Убрал TopAppBar отсюда, так как он теперь есть в MainScreen.
        // Это предотвращает появление двух TopAppBar на одном экране.
        floatingActionButton = {
            FloatingActionButton(onClick = onCreateOrder) {
                Icon(Icons.Default.Add, contentDescription = "Создать заявку")
            }
        }
    ) { innerPadding ->
        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Показываю индикатор загрузки, только если список пуст.
            if (uiState.isLoading && orders.isEmpty()) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else if (orders.isEmpty()) {
                // Сообщение, если заказов нет.
                Text(
                    "Нет активных поставок",
                    modifier = Modifier.align(Alignment.Center),
                    style = MaterialTheme.typography.titleMedium
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(items = orders, key = { order -> order.id }) { order ->
                        OrderItem(
                            order = order,
                            onOpenOrder = onOpenOrder,
                            onShowMap = onShowMap,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }

            // Показываю сообщение об ошибке внизу экрана, если оно есть.
            uiState.error?.let { error ->
                Snackbar(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(16.dp)
                ) {
                    Text(text = "Ошибка: $error")
                }
            }
        }
    }
}
