package ru.forvid.o2devsstud.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ru.forvid.o2devsstud.ui.viewmodel.HistoryViewModel

// Модель данных можно оставить здесь или вынести в domain/model
data class HistoryItem(
    val id: Long,
    val from: String,
    val to: String,
    val timeOnRoute: String,
    val contractorName: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(
    // Теперь получаю ViewModel напрямую из графа навигации.
    viewModel: HistoryViewModel,
    onOpen: (Long) -> Unit,
    onBack: () -> Unit, // Кнопка "назад" теперь обязательна
    modifier: Modifier = Modifier
) {
    // Беру состояние из ViewModel
    val state by viewModel.uiState.collectAsState()
    val items = state.items

    Column(modifier = modifier.fillMaxSize()) {
        CenterAlignedTopAppBar(
            title = { Text("История поставок") },
            colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            )
            // TODO: Добавить кнопку "назад", если она нужна в TopAppBar
        )

        LazyColumn(modifier = Modifier.fillMaxSize().padding(12.dp)) {
            items(items) { item ->
                Card(modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp)) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text("${item.from} → ${item.to}")
                        Spacer(modifier = Modifier.height(6.dp))
                        Text("Время в дороге: ${item.timeOnRoute}")
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("Контрагент: ${item.contractorName}")
                    }
                }
            }
        }
    }
}
