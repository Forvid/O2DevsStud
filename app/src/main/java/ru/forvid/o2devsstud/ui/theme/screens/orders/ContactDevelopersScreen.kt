package ru.forvid.o2devsstud.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContactDevelopersScreen(
    onSendMessage: (String) -> Unit,
    phoneToCall: String?,
    onBack: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var text by remember { mutableStateOf("") }

    Column(modifier = modifier.fillMaxSize().padding(16.dp)) {
        CenterAlignedTopAppBar(
            title = { Text("Связаться с разработчиками") },
            colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        TextField(value = text, onValueChange = { text = it }, modifier = Modifier.fillMaxWidth(), placeholder = { Text("Сообщение") })
        Spacer(modifier = Modifier.height(12.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Button(onClick = { onSendMessage(text); text = "" }, modifier = Modifier.weight(1f)) { Text("Отправить") }
            Button(onClick = {
                phoneToCall?.let {
                    val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$it"))
                    context.startActivity(intent)
                }
            }, modifier = Modifier.weight(1f)) {
                Text("Позвонить")
            }
        }
    }
}
