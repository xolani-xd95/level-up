package co.za.xdcodez.wealthbuilder

import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.navigation.compose.rememberNavController
import co.za.xdcodez.wealthbuilder.navigation.ModernBottomNavigationBar
import co.za.xdcodez.wealthbuilder.navigation.RootNavigationGraph
import co.za.xdcodez.wealthbuilder.theme.WealthBuilderTheme
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.core.annotation.KoinExperimentalAPI

@OptIn(KoinExperimentalAPI::class)
@Composable
@Preview
fun App() {
    val navController = rememberNavController()

    WealthBuilderTheme {
        Scaffold(
            containerColor = Color.Transparent,
            bottomBar = { ModernBottomNavigationBar(navController) }
        ) { paddingValues ->
            RootNavigationGraph(navController, paddingValues)
        }
    }
}