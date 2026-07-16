package co.za.xdcodez.wealthbuilder.navigation

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import wealthbuilder.composeapp.generated.resources.Res
import wealthbuilder.composeapp.generated.resources.ic_exercise
import wealthbuilder.composeapp.generated.resources.ic_finance
import wealthbuilder.composeapp.generated.resources.ic_gym
import wealthbuilder.composeapp.generated.resources.ic_habits
import wealthbuilder.composeapp.generated.resources.ic_impulse
import wealthbuilder.composeapp.generated.resources.ic_profile

@Composable
fun ModernBottomNavigationBar(
    navController: NavController
) {
    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = currentBackStackEntry?.destination?.route

    // Only show on main screens
    if (currentRoute in bottomNavItems.map { it.route }) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 12.dp, end = 12.dp, bottom = 18.dp )
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color.Gray.copy(alpha = 0.5f))
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                bottomNavItems.forEach { item ->
                    BottomNavItem(
                        item = item,
                        isSelected = currentRoute == item.route,
                        onClick = {
                            navController.navigate(item.route) {
                                launchSingleTop = true
                                restoreState = true
//                                popUpTo(navController.graph.startDestinationId) {
//                                    saveState = true
//                                }
                            }
                        }
                    )
                }
            }

    }
}

@Composable
fun BottomNavItem(
    item: BottomNavDestination,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val scale by animateFloatAsState(
        targetValue = if (isSelected) 1.1f else 1f,
        animationSpec = tween(300)
    )

    val backgroundColor by animateColorAsState(
        targetValue = if (isSelected)
            Color.White.copy(alpha = 0.05f)
        else
            Color.Transparent,
        animationSpec = tween(300)
    )

    val contentColor by animateColorAsState(
        targetValue = if (isSelected)
            Color(0xFFD4AF37)
        else
           Color.White.copy(alpha = 0.5f),
        animationSpec = tween(300)
    )

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .background(backgroundColor)
            .clickable(
                onClick = onClick,
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            )
            .padding(horizontal = 16.dp, vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                painter = painterResource(item.icon),
                contentDescription = item.label,
                tint = contentColor,
                modifier = Modifier
                    .size(24.dp)
                    .scale(scale)
            )

            if (isSelected) {
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = item.label,
                    color = contentColor,
                    style = MaterialTheme.typography.labelMedium,
                    fontSize = 12.sp
                )
            }
        }
    }
}

val bottomNavItems = listOf(
    BottomNavDestination.Finance,
    BottomNavDestination.Habits,
    BottomNavDestination.Journal
)

sealed class BottomNavDestination(
    val route: String,
    val label: String,
    val icon: DrawableResource
) {
    object Habits : BottomNavDestination("habits", "Habits", Res.drawable.ic_impulse)
    object Finance : BottomNavDestination("finance", "Budget", Res.drawable.ic_finance)
    object Journal : BottomNavDestination("journal", "Trading", Res.drawable.ic_gym)
}
