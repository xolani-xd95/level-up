package co.za.xdcodez.wealthbuilder.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

val DarkColorTheme = darkColorScheme(
    primary = Action.Primary,
    secondary = Action.Accent,
    tertiary = Action.Success,
    background = Background.Primary,
    surface = Surface.Primary,
    error = State.Error,
    onPrimary = Text.Primary,
    onSecondary = Text.Primary,
    onTertiary = Text.Primary,
    onBackground = Text.Primary,
    onSurface = Text.Primary,
    onError = Text.Primary
)

@Composable
fun WealthBuilderTheme(
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = DarkColorTheme
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(color = Background.Primary),
            content = { content() }
        )
    }
}
