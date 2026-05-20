package com.trading.journal.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// Brand palette — deep navy + electric teal accent, warm amber for profit
object TradingColors {
    val Navy900   = Color(0xFF0A0F1E)
    val Navy800   = Color(0xFF111827)
    val Navy700   = Color(0xFF1C2940)
    val Navy600   = Color(0xFF263350)
    val Navy400   = Color(0xFF3D5080)

    val Teal400   = Color(0xFF00D4AA)
    val Teal300   = Color(0xFF33DDB8)
    val Teal100   = Color(0xFFCCF7EF)
    val TealDim   = Color(0xFF003D30)

    val Profit    = Color(0xFF00C896)  // green — win
    val ProfitBg  = Color(0xFF003D2A)
    val Loss      = Color(0xFFFF5252)  // red — loss
    val LossBg    = Color(0xFF3D0A0A)
    val Amber     = Color(0xFFF59E0B)  // open / neutral
    val AmberBg   = Color(0xFF3D2A00)

    val Surface   = Color(0xFF151D2E)
    val Card      = Color(0xFF1A2335)
    val CardBorder = Color(0xFF263350)
    val TextPrimary   = Color(0xFFF0F4FF)
    val TextSecondary = Color(0xFF8A9BBE)
    val TextMuted     = Color(0xFF4A5878)

    // Light theme
    val LightBg      = Color(0xFFF5F7FF)
    val LightSurface = Color(0xFFFFFFFF)
    val LightCard    = Color(0xFFF0F4FF)
    val LightBorder  = Color(0xFFDDE3F0)
    val LightText    = Color(0xFF0A0F1E)
    val LightMuted   = Color(0xFF6B7A9A)
}

private val DarkColors = darkColorScheme(
    primary = TradingColors.Teal400,
    onPrimary = TradingColors.Navy900,
    primaryContainer = TradingColors.TealDim,
    onPrimaryContainer = TradingColors.Teal300,
    secondary = TradingColors.Amber,
    background = TradingColors.Navy900,
    surface = TradingColors.Surface,
    surfaceVariant = TradingColors.Card,
    onBackground = TradingColors.TextPrimary,
    onSurface = TradingColors.TextPrimary,
    onSurfaceVariant = TradingColors.TextSecondary,
    outline = TradingColors.CardBorder,
    error = TradingColors.Loss,
)

private val LightColors = lightColorScheme(
    primary = Color(0xFF006B54),
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = TradingColors.Teal100,
    onPrimaryContainer = Color(0xFF002117),
    secondary = Color(0xFF92610A),
    background = TradingColors.LightBg,
    surface = TradingColors.LightSurface,
    surfaceVariant = TradingColors.LightCard,
    onBackground = TradingColors.LightText,
    onSurface = TradingColors.LightText,
    onSurfaceVariant = TradingColors.LightMuted,
    outline = TradingColors.LightBorder,
    error = Color(0xFFBA1A1A),
)

@Composable
fun TradingJournalTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColors else LightColors,
        content = content,
    )
}
