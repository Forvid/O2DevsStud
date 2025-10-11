package ru.forvid.o2devsstud.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConfirmedScreen(orderId: Long, onBack: () -> Unit) {
    Scaffold(
        topBar = { TopAppBar(title = { Text("Подтверждение") }) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Card(modifier = Modifier.fillMaxWidth(), elevation = CardDefaults.elevatedCardElevation(8.dp)) {
                Column(modifier = Modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Surface(shape = CircleShape, tonalElevation = 6.dp, modifier = Modifier.size(72.dp)) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(Icons.Default.Check, contentDescription = "OK", modifier = Modifier.size(36.dp))
                        }
                    }

                    Spacer(Modifier.height(16.dp))
                    Text("Заявка подтверждена", style = MaterialTheme.typography.titleLarge)
                    Spacer(Modifier.height(8.dp))
                    Text("ID: $orderId", style = MaterialTheme.typography.bodyMedium)
                    Spacer(Modifier.height(20.dp))
                    Button(onClick = onBack, modifier = Modifier.fillMaxWidth()) { Text("Вернуться к заявкам") }
                }
            }
        }
    }
}
