package co.za.xdcodes.level_up

import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.navigation.compose.rememberNavController
import co.za.xdcodes.level_up.navigation.CustomBottomNavigationBar
import co.za.xdcodes.level_up.navigation.RootNavigationGraph
import co.za.xdcodes.level_up.theme.LevelUpTheme
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.core.annotation.KoinExperimentalAPI

@OptIn(KoinExperimentalAPI::class)
@Composable
@Preview
fun App() {
    val navController = rememberNavController()

    LevelUpTheme {
        Scaffold(
            containerColor = Color.Transparent,
            bottomBar = { CustomBottomNavigationBar(navController) }
        ) { paddingValues ->
            RootNavigationGraph(navController, paddingValues)
        }
    }
}
