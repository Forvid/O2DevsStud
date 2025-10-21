package ru.forvid.o2devsstud.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ru.forvid.o2devsstud.domain.model.HistoryItem
import ru.forvid.o2devsstud.ui.viewmodel.HistoryViewModel
import androidx.compose.ui.tooling.preview.Preview


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(
    viewModel: HistoryViewModel,
    onOpen: (Long) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val state by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("История поставок") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Назад")
                    }
                }
            )
        },
        modifier = modifier
    ) { innerPadding ->
        // --- Применяет системный padding к Box ---
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentAlignment = Alignment.Center
        ) {
            when {
                state.isLoading && state.items.isEmpty() -> {
                    CircularProgressIndicator()
                }
                state.error != null -> {
                    Text(text = "Ошибка: ${state.error}")
                }
                state.items.isEmpty() -> {
                    Text(text = "История поставок пуста")
                }
                else -> {
                    // --- Убираем передачу padding в HistoryList ---
                    HistoryList(items = state.items)
                }
            }
        }
    }
}

@Composable
private fun HistoryList(items: List<HistoryItem>) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        // --- Использует простой padding для всего списка ---
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(items, key = { it.id }) { item ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "${item.from} → ${item.to}",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = "Время в дороге: ${item.timeOnRoute}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        text = "Контрагент: ${item.contractorName}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true, widthDp = 360, heightDp = 640, name = "History - preview")
@Composable
private fun HistoryListPreview() {
    val sample = listOf(
        HistoryItem(id = 1, from = "Москва Т1", to = "Тверь", timeOnRoute = "3ч 12м", contractorName = "ООО Пример"),
        HistoryItem(id = 2, from = "Казань", to = "Уфа", timeOnRoute = "5ч 33м", contractorName = "ООО Контрагент")
    )
    ru.forvid.o2devsstud.ui.theme.O2DevsStudTheme {
        HistoryList(items = sample)
    }
}
