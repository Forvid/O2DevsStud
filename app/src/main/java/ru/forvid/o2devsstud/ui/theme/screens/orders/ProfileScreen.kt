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
import ru.forvid.o2devsstud.domain.model.DriverProfile
import androidx.compose.material3.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ArrowBack
import androidx.hilt.navigation.compose.hiltViewModel
import ru.forvid.o2devsstud.ui.viewmodel.ProfileViewModel
import androidx.compose.runtime.getValue

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    // Если передаётся profile — экран покажет его.
    // Иначе будет использован ProfileViewModel (через Hilt) и содержимое будет взято из uiState.
    profile: DriverProfile? = null,
    profileViewModel: ProfileViewModel? = null,
    onEdit: () -> Unit = {},
    onLogout: () -> Unit = {},
    onBack: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    // получаем VM: если передали — используем его, иначе берём через hilt
    val vm: ProfileViewModel = profileViewModel ?: hiltViewModel()

    // состояние из VM (если profile не передан, будем его брать из vm.uiState)
    val vmState by vm.uiState.collectAsState()

    // финальный профиль для отображения: либо переданный, либо из vm
    val profileToShow: DriverProfile? = profile ?: vmState.profile

    // launcher для выбора изображения — при выборе вызываем vm.saveAvatarUri
    val pickImageLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let { vm.saveAvatarUri(it.toString()) }
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
                IconButton(onClick = onEdit) { Icon(Icons.Default.Edit, contentDescription = "Редактировать") }
            }
        )

        Spacer(modifier = Modifier.height(18.dp))

        Card(modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // avatar
                Box(modifier = Modifier.size(96.dp), contentAlignment = Alignment.BottomEnd) {
                    // если profileToShow?.avatarUrl нулевой, Coil корректно покажет placeholder (если настроен) — иначе пусто
                    val painter = rememberAsyncImagePainter(profileToShow?.avatarUrl)
                    Image(
                        painter = painter,
                        contentDescription = "Аватар",
                        modifier = Modifier
                            .size(96.dp)
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )

                    // если экран использует VM (т.е. мы можем сохранять аватар), показываем кнопку редактирования
                    // если profile был передан извне (readonly), всё равно можно показывать кнопку — она будет сохранять в VM
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
                    Button(onClick = onEdit, modifier = Modifier.weight(1f)) { Text("Редактировать") }
                    OutlinedButton(onClick = {
                        // вызываем logout-логику из VM, а затем callback
                        vm.logout()
                        onLogout()
                    }, modifier = Modifier.weight(1f)) {
                        Text("Выйти")
                    }
                }
            }
        }
    }
}
