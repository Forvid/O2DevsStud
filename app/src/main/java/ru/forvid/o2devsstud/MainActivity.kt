package ru.forvid.o2devsstud

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import dagger.hilt.android.AndroidEntryPoint
import ru.forvid.o2devsstud.ui.navigation.AppNavHost
import ru.forvid.o2devsstud.ui.theme.O2DevsStudTheme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AppRoot()
        }
    }
}

@Composable
fun AppRoot() {
    O2DevsStudTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            AppNavHost()
        }
    }
}
