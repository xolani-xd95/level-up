package co.za.xdcodes.level_up.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

val LightColorTheme = lightColorScheme(
    primary = primary,
    surface = surface,
    background = background
)

@Composable
fun LevelUpTheme(
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = LightColorTheme
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            Color(0xff000000),
                            Color(0xff1B1B1B),
                            Color(0xff495057),
                            Color(0xff363636),
                            Color(0xff363636),
                            Color(0xff1B1B1B),
                            Color(0xff000000),
                        ),
                        start = Offset.Infinite.copy(x = Float.POSITIVE_INFINITY, y = 0f),
                        end = Offset(0f, Float.POSITIVE_INFINITY)
                    )
                ),
            content = { content() }
        )
    }
}