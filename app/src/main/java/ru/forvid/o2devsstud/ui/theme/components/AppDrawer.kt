package ru.forvid.o2devsstud.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ru.forvid.o2devsstud.domain.model.DriverProfile
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import coil.compose.rememberAsyncImagePainter
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
    profile: DriverProfile? = null,
    onProfileClick: () -> Unit = {}
) {
    Column(modifier = Modifier.fillMaxSize()) {
        // header: surface to ensure opaque background, clickable to open profile
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    onProfileClick()
                },
            tonalElevation = 2.dp,
            color = MaterialTheme.colorScheme.surface
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                val painter = rememberAsyncImagePainter(profile?.avatarUrl)
                Image(
                    painter = painter,
                    contentDescription = "avatar",
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
                Spacer(Modifier.width(12.dp))
                Column {
                    Text(text = profile?.fullName ?: "Иванов Иван", style = MaterialTheme.typography.titleMedium)
                    Text(
                        text = profile?.column ?: "Колонна 1",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        Divider()

        // navigation items
        DrawerItem(label = "Текущие поставки") {
            onNavigate("orders"); closeDrawer()
        }
        DrawerItem(label = "Карта", icon = { Icon(Icons.Default.Map, contentDescription = null) }) {
            onNavigate("map"); closeDrawer()
        }
        DrawerItem(label = "История", icon = { Icon(Icons.Default.History, contentDescription = null) }) {
            onNavigate("history"); closeDrawer()
        }
        DrawerItem(label = "Связаться с разработчиками", icon = { Icon(Icons.Default.ContactPhone, contentDescription = null) }) {
            onNavigate("contact_developers"); closeDrawer()
        }

        Spacer(modifier = Modifier.weight(1f))

        Divider()

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
private fun DrawerItem(label: String, icon: (@Composable () -> Unit)? = null, onClick: () -> Unit) {
    NavigationDrawerItem(
        label = { Text(label) },
        selected = false,
        onClick = onClick,
        icon = icon,
        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
    )
}
