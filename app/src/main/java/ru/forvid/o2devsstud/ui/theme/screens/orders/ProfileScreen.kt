package ru.forvid.o2devsstud.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ru.forvid.o2devsstud.domain.model.DriverProfile

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    profile: DriverProfile?,
    onEdit: () -> Unit,
    onLogout: () -> Unit,
    onBack: () -> Unit
) {
    Column(modifier = Modifier
        .fillMaxSize()
        .padding(16.dp)
    ) {
        TopAppBar(
            title = { Text("Мой профиль") },
            navigationIcon = {
                IconButton(onClick = onBack) {
                    Icon(Icons.Filled.ArrowBack, contentDescription = "Назад")
                }
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(text = "ФИО: ${profile?.fullName ?: "—"}")
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = "Колонна: ${profile?.column ?: "—"}")
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = "Тел. водителя: ${profile?.phoneDriver ?: "—"}")
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = "Тел. колонны: ${profile?.phoneColumn ?: "—"}")
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = "Тел. логиста: ${profile?.phoneLogist ?: "—"}")
                Spacer(modifier = Modifier.height(16.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(onClick = onEdit, modifier = Modifier.weight(1f)) { Text("Редактировать") }
                    Button(onClick = onLogout, modifier = Modifier.weight(1f)) { Text("Выйти") }
                }
            }
        }
    }
}
