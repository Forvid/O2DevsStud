package ru.forvid.o2devsstud.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import ru.forvid.o2devsstud.domain.model.DriverProfile
import ru.forvid.o2devsstud.ui.theme.navigation.Screen
import ru.forvid.o2devsstud.ui.viewmodel.ProfileViewModel
import androidx.compose.ui.tooling.preview.Preview
import ru.forvid.o2devsstud.ui.theme.O2DevsStudTheme

private data class DrawerItemData(val route: String, val label: String, val icon: ImageVector)

/**
 * Runtime wrapper: использует ProfileViewModel и передаёт данные в AppDrawerContent.
 * Не меняет навигационной логики — делает thin adapter.
 */
@Composable
fun AppDrawer(
    currentRoute: String?,
    onNavigate: (String) -> Unit,
    onSignOut: () -> Unit,
    closeDrawer: () -> Unit,
    profileViewModel: ProfileViewModel
) {
    val profileState by profileViewModel.uiState.collectAsState()
    val profile = profileState.profile

    AppDrawerContent(
        currentRoute = currentRoute,
        profile = profile,
        onNavigate = { route ->
            onNavigate(route)
            closeDrawer()
        },
        onSignOut = {
            profileViewModel.logout()
            onSignOut()
            closeDrawer()
        }
    )
}

/**
 * Чистый UI-дровер — принимает profile и лямбды. Подходит для preview и тестов.
 */
@Composable
fun AppDrawerContent(
    currentRoute: String?,
    profile: DriverProfile?,
    onNavigate: (String) -> Unit,
    onSignOut: () -> Unit
) {
    val menuItems = listOf(
        DrawerItemData(Screen.Home.route, "Главный экран", Icons.Default.Home),
        DrawerItemData(Screen.Orders.route, "Текущие поставки", Icons.AutoMirrored.Filled.List),
        DrawerItemData(Screen.History.route, "История", Icons.Default.History),
        DrawerItemData(Screen.ContactDevelopers.route, "Связаться с разработчиками", Icons.Default.ContactPhone)
    )

    ModalDrawerSheet {
        // Header (кликабельный — переходит в профиль)
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onNavigate(Screen.Profile.route) },
            tonalElevation = 1.dp
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = rememberAsyncImagePainter(model = profile?.avatarUrl),
                    contentDescription = "Аватар",
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
                Spacer(Modifier.width(12.dp))
                Column {
                    Text(text = profile?.fullName ?: "Водитель", style = MaterialTheme.typography.titleMedium)
                    Text(
                        text = profile?.column ?: "Компания",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        Divider()

        menuItems.forEach { item ->
            NavigationDrawerItem(
                label = { Text(item.label) },
                selected = currentRoute == item.route,
                onClick = { onNavigate(item.route) },
                icon = { Icon(item.icon, contentDescription = item.label) },
                modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        Divider()

        NavigationDrawerItem(
            label = { Text("Выйти") },
            selected = false,
            onClick = onSignOut,
            icon = { Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = "Выйти") },
            modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
        )
    }
}

/* ---------------------------
   Preview
   --------------------------- */

@Preview(showBackground = true, widthDp = 320, heightDp = 640, name = "Drawer Preview")
@Composable
private fun AppDrawerPreview() {
    val p = DriverProfile(
        id = 1L,
        fullName = "Иван Иванов",
        column = "Колонна 1",
        phoneDriver = "+7 900 000 00 00",
        phoneColumn = "+7 900 000 00 01",
        phoneLogist = "+7 900 000 00 02",
        email = "ivan@example.com",
        avatarUrl = null
    )
    O2DevsStudTheme {
        AppDrawerContent(
            currentRoute = Screen.Orders.route,
            profile = p,
            onNavigate = {},
            onSignOut = {}
        )
    }
}
