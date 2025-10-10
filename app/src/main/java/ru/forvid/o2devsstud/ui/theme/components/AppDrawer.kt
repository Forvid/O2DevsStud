package ru.forvid.o2devsstud.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ru.forvid.o2devsstud.domain.model.DriverProfile
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import coil.compose.rememberAsyncImagePainter
import androidx.compose.material3.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.ContactPhone
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Map

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppDrawer(
    currentRoute: String?,
    onNavigate: (String) -> Unit,
    onSignOut: () -> Unit,
    closeDrawer: () -> Unit,
    profile: DriverProfile? = null
) {
    // обёртка – у ModalDrawerSheet уже есть background, но на уровне содержимого удобно явно задать padding
    Column(modifier = Modifier.fillMaxSize()) {
        // header
        Box(modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                // avatar (можно заменить на локальный ресурс, если нет)
                val painter = rememberAsyncImagePainter(profile?.avatarUrl)
                Image(
                    painter = painter,
                    contentDescription = "avatar",
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
                Spacer(Modifier.width(12.dp))
                Column {
                    Text(text = profile?.fullName ?: "Иванов Иван", style = MaterialTheme.typography.titleMedium)
                    Text(text = profile?.column ?: "Колонна 1", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }

        Divider()

        // nav items
        DrawerItem(label = "Текущие поставки", onClick = { onNavigate("orders"); closeDrawer() })
        DrawerItem(label = "Карта", onClick = { onNavigate("map"); closeDrawer() }, icon = { Icon(Icons.Default.Map, contentDescription = null) })
        DrawerItem(label = "История", onClick = { onNavigate("history"); closeDrawer() }, icon = { Icon(Icons.Default.History, contentDescription = null) })
        DrawerItem(label = "Связаться с разработчиками", onClick = { onNavigate("contact_developers"); closeDrawer()}, icon = { Icon(Icons.Default.ContactPhone, contentDescription = null) })

        Spacer(modifier = Modifier.weight(1f))

        Divider()

        // sign out
        NavigationDrawerItem(
            label = { Text("Выйти") },
            selected = false,
            onClick = { onSignOut() },
            icon = { Icon(Icons.Default.ExitToApp, contentDescription = null) },
            modifier = Modifier.padding(8.dp)
        )
    }
}

@Composable
private fun DrawerItem(label: String, onClick: () -> Unit, icon: (@Composable ()->Unit)? = null) {
    NavigationDrawerItem(
        label = { Text(label) },
        selected = false,
        onClick = onClick,
        icon = if (icon != null) { { icon() } } else null,
        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
    )
}
