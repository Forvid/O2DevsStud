package ru.forvid.o2devsstud.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.Icons

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
        TopAppBar(title = { Text("Связаться с разработчиками") }, navigationIcon = {
            if (onBack != null) IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, contentDescription = "Назад") }
        })

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(value = text, onValueChange = { text = it }, modifier = Modifier.fillMaxWidth(), label = { Text("Сообщение") })
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
