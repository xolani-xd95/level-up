package co.za.xdcodes.level_up.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import level_up.composeapp.generated.resources.Res
import level_up.composeapp.generated.resources.ic_exercise
import level_up.composeapp.generated.resources.ic_finance
import level_up.composeapp.generated.resources.ic_home
import level_up.composeapp.generated.resources.ic_profile
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource


@Composable
fun CustomBottomNavigationBar(
    navController: NavController
) {
    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = currentBackStackEntry?.destination?.route

    if (currentRoute == BottomNavDestination.Dashboard.route ||
        currentRoute == BottomNavDestination.Finance.route ||
        currentRoute == BottomNavDestination.Workout.route ||
        currentRoute == BottomNavDestination.Profile.route
    )
        Row(
            modifier = Modifier
                .padding(start = 12.dp, end = 12.dp, top = 8.dp, bottom = 30.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(color = White.copy(alpha = 0.2f))
                .drawWithContent {
                    drawContent()
                    drawRect(
                        brush = Brush.verticalGradient(
                            listOf(Color.White.copy(0.12f), Color.Transparent)
                        )
                    )
                }
                .fillMaxWidth()
                .padding(vertical = 15.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            bottomNavItems.forEach { item ->
                Icon(
                    painter = painterResource(item.icon),
                    contentDescription = "",
                    tint = if (currentRoute == item.route) MaterialTheme.colorScheme.primary else White,
                    modifier = Modifier
                        .padding(horizontal = 34.dp)
                        .clickable {
                            navController.navigate(item.route) {
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                )
            }
        }
}

val bottomNavItems = listOf(
    BottomNavDestination.Dashboard,
    BottomNavDestination.Finance,
    BottomNavDestination.Workout,
    BottomNavDestination.Profile
)

sealed class BottomNavDestination(
    val route: String,
    val icon: DrawableResource
) {
    object Dashboard : BottomNavDestination("dashboard", Res.drawable.ic_home)
    object Finance : BottomNavDestination("finance", Res.drawable.ic_finance)
    object Workout : BottomNavDestination("workout", Res.drawable.ic_exercise)
    object Profile : BottomNavDestination("profile", Res.drawable.ic_profile)
}