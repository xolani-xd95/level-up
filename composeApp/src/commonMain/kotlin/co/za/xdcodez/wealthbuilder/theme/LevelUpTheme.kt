package co.za.xdcodez.wealthbuilder.theme

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
fun WealthBuilderTheme(
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = LightColorTheme
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = gradientOption0() // Change this to try different options
                ),
            content = { content() }
        )
    }
}

// ── Gradient Options ─────────────────────────────────────────────
// Change the gradient in WealthBuilderTheme above to try each option

private fun gradientOption0() = Brush.linearGradient(
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
// Option 1: Subtle Blue Tint (Dark with slight blue atmosphere)
private fun gradientOption1() = Brush.linearGradient(
    colors = listOf(
        Color(0xff000000),
        Color(0xff0A1520), // Very dark blue-black
        Color(0xff1A2F3F), // Dark blue-gray
        Color(0xff162838), // Medium dark blue-gray
        Color(0xff162838),
        Color(0xff0A1520),
        Color(0xff000000),
    ),
    start = Offset(1000f, 0f),
    end = Offset(0f, 1000f)
)

// Option 2: Primary Accent Highlights (Original with blue in lighter areas)
private fun gradientOption2() = Brush.linearGradient(
    colors = listOf(
        Color(0xff000000),
        Color(0xff1B1B1B),
        Color(0xff2B4A5E), // Blue-tinted highlight
        Color(0xff1E3A4F), // Blue-tinted mid
        Color(0xff1E3A4F),
        Color(0xff1B1B1B),
        Color(0xff000000),
    ),
    start = Offset(1000f, 0f),
    end = Offset(0f, 1000f)
)

// Option 3: Deep Blue Gradient (Black to darker primary)
private fun gradientOption3() = Brush.linearGradient(
    colors = listOf(
        Color(0xff000000),
        Color(0xff0D1821),
        Color(0xff0F2537), // Darkened primary
        Color(0xff11304D),
        Color(0xff0F2537),
        Color(0xff0D1821),
        Color(0xff000000),
    ),
    start = Offset(1000f, 0f),
    end = Offset(0f, 1000f)
)

// Option 4: Minimal Blue Hint (Very subtle, keeps mostly original)
private fun gradientOption4() = Brush.linearGradient(
    colors = listOf(
        Color(0xff000000),
        Color(0xff1B1B1B),
        Color(0xff3D4A52), // Slight blue tint in gray
        Color(0xff2F3539), // Slight blue tint
        Color(0xff2F3539),
        Color(0xff1B1B1B),
        Color(0xff000000),
    ),
    start = Offset(1000f, 0f),
    end = Offset(0f, 1000f)
)