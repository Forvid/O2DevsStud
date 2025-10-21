package ru.forvid.o2devsstud.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import ru.forvid.o2devsstud.domain.model.DriverProfile
import ru.forvid.o2devsstud.ui.viewmodel.ProfileViewModel
import ru.forvid.o2devsstud.ui.theme.viewmodel.AuthViewModel
import androidx.compose.ui.tooling.preview.Preview
import ru.forvid.o2devsstud.ui.theme.O2DevsStudTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    profileViewModel: ProfileViewModel,
    authViewModel: AuthViewModel,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by profileViewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Мой профиль") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Назад")
                    }
                }
            )
        },
        modifier = modifier
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            when {
                uiState.isLoading -> CircularProgressIndicator()
                uiState.error != null -> Text(
                    text = "Ошибка: ${uiState.error}",
                    modifier = Modifier.padding(innerPadding)
                )
                uiState.profile != null -> ProfileContent(
                    profile = uiState.profile!!,
                    onEdit = { /* навигация */ },
                    onLogout = {
                        profileViewModel.logout()
                        authViewModel.onSignOut()
                    },
                    onBack = onBack,
                    contentPadding = innerPadding
                )
            }
        }
    }
}

/**
 * Чистая UI-функция — подходит для превью и тестов.
 * Для выбора фото в рантайме используйте rememberLauncherForActivityResult в caller (или в ProfileScreen).
 */
@Composable
fun ProfileContent(
    profile: DriverProfile,
    onEdit: () -> Unit,
    onLogout: () -> Unit,
    onBack: () -> Unit,
    contentPadding: PaddingValues = PaddingValues(0.dp)
) {
    // В реальном рантайме можно вынести и использовать launcher в ProfileScreen,
    // а здесь для preview мы показываем статичный UI.
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(contentPadding)
            .padding(horizontal = 16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        Box(contentAlignment = Alignment.BottomEnd) {
            Image(
                painter = rememberAsyncImagePainter(model = profile.avatarUrl),
                contentDescription = "Аватар",
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )
            // В preview / UI-only варианте кнопка просто дергает onEdit
            IconButton(onClick = onEdit) {
                Icon(Icons.Default.Edit, contentDescription = "Изменить фото")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = profile.fullName,
            style = MaterialTheme.typography.headlineSmall,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(24.dp))

        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                ProfileInfoRow(label = "Колонна:", value = profile.column)
                ProfileInfoRow(label = "Тел. водителя:", value = profile.phoneDriver)
                ProfileInfoRow(label = "Тел. колонны:", value = profile.phoneColumn)
                ProfileInfoRow(label = "Тел. логиста:", value = profile.phoneLogist)
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = onLogout,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Выйти")
        }
        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
private fun ProfileInfoRow(label: String, value: String) {
    Row(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.weight(0.4f)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.weight(0.6f)
        )
    }
}

/* ---------------------------
   Preview
   --------------------------- */

@Preview(showBackground = true, widthDp = 360, heightDp = 640, name = "Profile - preview")
@Composable
private fun ProfilePreview() {
    val p = DriverProfile(
        id = 1L,
        fullName = "Иванов Иван",
        column = "Колонна 1",
        phoneDriver = "+7 900 000 00 00",
        phoneColumn = "+7 900 000 00 01",
        phoneLogist = "+7 900 000 00 02",
        email = "ivan@example.com",
        avatarUrl = null
    )

    O2DevsStudTheme {
        ProfileContent(
            profile = p,
            onEdit = {},
            onLogout = {},
            onBack = {}
        )
    }
}
