package co.za.xdcodes.level_up.common

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

@Composable
fun budgetProgressColor(progress: Float): Color = when {
    progress > 1f     -> Color(0xFFFF2400)
    progress == 1f    -> Color(0xFF00C853)
    progress >= 0.75f -> Color(0xFFFFA500)
    else              -> MaterialTheme.colorScheme.primary
}

fun monthId(monthIndex: Int, year: Int): String {
    return "$year-${monthIndex.toString().padStart(2, '0')}"
}

