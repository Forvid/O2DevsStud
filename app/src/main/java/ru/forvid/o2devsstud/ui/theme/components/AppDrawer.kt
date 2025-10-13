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
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import coil.compose.rememberAsyncImagePainter
import ru.forvid.o2devsstud.ui.theme.navigation.Screen
import ru.forvid.o2devsstud.ui.viewmodel.ProfileViewModel

private data class DrawerItemData(val screen: Screen, val label: String, val icon: ImageVector)

@Composable
fun AppDrawer(
    mainNavController: NavController,
    onSignOut: () -> Unit,
    closeDrawer: () -> Unit,
    profileViewModel: ProfileViewModel
) {
    val profileState by profileViewModel.uiState.collectAsState()
    val profile = profileState.profile

    val currentBackStackEntry by mainNavController.currentBackStackEntryAsState()
    val currentRoute = currentBackStackEntry?.destination?.route

    val onNavigate: (String) -> Unit = { route ->
        mainNavController.navigate(route) {
            launchSingleTop = true
            popUpTo(mainNavController.graph.startDestinationId)
        }
        closeDrawer()
    }

    val menuItems = listOf(
        DrawerItemData(Screen.Home, "Главный экран", Icons.Default.Home),
        DrawerItemData(Screen.Orders, "Текущие поставки", Icons.AutoMirrored.Filled.List),
        DrawerItemData(Screen.History, "История", Icons.Default.History),
        DrawerItemData(Screen.ContactDevelopers, "Связаться с разработчиками", Icons.Default.ContactPhone)
    )

    ModalDrawerSheet {
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

        HorizontalDivider()

        menuItems.forEach { item ->
            NavigationDrawerItem(
                label = { Text(item.label) },
                selected = currentRoute == item.screen.route,
                onClick = { onNavigate(item.screen.route) },
                icon = { Icon(item.icon, contentDescription = item.label) },
                modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        HorizontalDivider()

        NavigationDrawerItem(
            label = { Text("Выйти") },
            selected = false,
            onClick = {
                onSignOut()
                closeDrawer()
            },
            icon = { Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = "Выйти") },
            modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
        )
    }
}