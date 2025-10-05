package ru.forvid.o2devsstud.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.List
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import ru.forvid.o2devsstud.ui.navigation.Screen

data class DrawerMenuItem(val screen: Screen, val title: String, val icon: ImageVector)

@Composable
fun AppDrawer(
    currentRoute: String?,
    onNavigate: (String) -> Unit,
    onSignOut: () -> Unit,
    closeDrawer: () -> Unit
) {
    val menuItems = listOf(
        DrawerMenuItem(Screen.Orders, "Текущие поставки", Icons.Filled.List),
        DrawerMenuItem(Screen.History, "История поставок", Icons.Filled.History),
        DrawerMenuItem(Screen.Profile, "Мой профиль", Icons.Filled.AccountCircle),
        DrawerMenuItem(Screen.ContactDevelopers, "Связаться с разработчиками", Icons.Filled.Call)
    )

    ModalDrawerSheet {
        Text(
            "O2RUS",
            modifier = Modifier.padding(16.dp),
            style = MaterialTheme.typography.titleLarge
        )
        HorizontalDivider()

        Column(Modifier.padding(8.dp)) {
            menuItems.forEach { item ->
                NavigationDrawerItem(
                    label = { Text(item.title) },
                    icon = { Icon(item.icon, contentDescription = null) },
                    selected = currentRoute == item.screen.route,
                    onClick = {
                        onNavigate(item.screen.route)
                        closeDrawer()
                    },
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                )
            }
        }

        HorizontalDivider()

        NavigationDrawerItem(
            label = { Text("Выход") },
            icon = { Icon(Icons.Filled.ExitToApp, contentDescription = null) },
            selected = false,
            onClick = { onSignOut(); closeDrawer() },
            modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
        )
    }
}
