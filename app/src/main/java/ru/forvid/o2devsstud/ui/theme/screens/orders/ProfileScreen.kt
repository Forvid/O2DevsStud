package ru.forvid.o2devsstud.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import coil.compose.rememberAsyncImagePainter
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ArrowBack
import ru.forvid.o2devsstud.ui.viewmodel.ProfileViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    // Теперь я получаю ViewModel напрямую из графа навигации.
    // Это делает код чище и предсказуемее.
    profileViewModel: ProfileViewModel,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Состояние теперь берется только из переданной ViewModel.
    val vmState by profileViewModel.uiState.collectAsState()
    val profileToShow = vmState.profile

    // launcher для выбора изображения остаётся таким же.
    val pickImageLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let { profileViewModel.saveAvatarUri(it.toString()) }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        TopAppBar(
            title = { Text("Мой профиль") },
            navigationIcon = {
                IconButton(onClick = onBack) {
                    Icon(Icons.Filled.ArrowBack, contentDescription = "Назад")
                }
            },
            actions = {
                // TODO: Реализовать логику редактирования
                IconButton(onClick = { /* onEdit() */ }) { Icon(Icons.Default.Edit, contentDescription = "Редактировать") }
            }
        )

        Spacer(modifier = Modifier.height(18.dp))

        Card(modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(modifier = Modifier.size(96.dp), contentAlignment = Alignment.BottomEnd) {
                    val painter = rememberAsyncImagePainter(profileToShow?.avatarUrl)
                    Image(
                        painter = painter,
                        contentDescription = "Аватар",
                        modifier = Modifier
                            .size(96.dp)
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                    IconButton(onClick = { pickImageLauncher.launch("image/*") }) {
                        Icon(Icons.Default.Edit, contentDescription = "Изменить фото")
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = profileToShow?.fullName ?: "—",
                    style = MaterialTheme.typography.titleMedium,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = "Колонна: ${profileToShow?.column ?: "—"}")
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = "Тел. водителя: ${profileToShow?.phoneDriver ?: "—"}")
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = "Тел. колонны: ${profileToShow?.phoneColumn ?: "—"}")
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = "Тел. логиста: ${profileToShow?.phoneLogist ?: "—"}")

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // TODO: Реализовать логику редактирования
                    Button(onClick = { /* onEdit() */ }, modifier = Modifier.weight(1f)) { Text("Редактировать") }
                    OutlinedButton(onClick = {
                        // TODO: Логика выхода должна быть централизована в MainScreen/RootNavigation
                        // profileViewModel.logout()
                        // onLogout()
                    }, modifier = Modifier.weight(1f)) {
                        Text("Выйти")
                    }
                }
            }
        }
    }
}
