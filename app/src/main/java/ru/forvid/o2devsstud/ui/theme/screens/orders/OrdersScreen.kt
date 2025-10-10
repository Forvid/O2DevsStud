package ru.forvid.o2devsstud.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ru.forvid.o2devsstud.ui.components.OrderItem
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
    val orders by viewModel.orders.collectAsState()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(title = { Text("Текущие поставки") })
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onCreateOrder) {
                Icon(Icons.Default.Add, contentDescription = "Создать")
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 10.dp)
        ) {
            items(orders, key = { it.id }) { order ->
                OrderItem(
                    order = order,
                    onOpenOrder = { id -> onOpenOrder(id) },
                    onShowMap = { trackId -> onShowMap(trackId) },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}
