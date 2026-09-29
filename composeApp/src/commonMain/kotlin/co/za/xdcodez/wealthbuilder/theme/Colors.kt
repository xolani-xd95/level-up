package co.za.xdcodez.wealthbuilder.theme

import androidx.compose.ui.graphics.Color

/**
 * WealthBuilder Design System v2.0 - Color Tokens
 *
 * This file contains the complete color system organized into:
 * 1. Primitive Colors - Base color scales
 * 2. Semantic Tokens - Purpose-based colors for Dark Mode
 *
 * Design Philosophy:
 * - Solid background (#121212) for clean, professional finance UI
 * - Glassmorphic cards with subtle transparency
 * - High contrast for financial data readability
 */

// ══════════════════════════════════════════════════════════════════════════════
// PRIMITIVE COLORS - Base Scales
// ══════════════════════════════════════════════════════════════════════════════

/**
 * Blue Scale - Primary Brand Color
 * Used for: Primary actions, highlights, progress indicators
 */
object Blue {
    val Blue50 = Color(0xFFE3F2FD)
    val Blue100 = Color(0xFFBBDEFB)
    val Blue200 = Color(0xFF90CAF9)
    val Blue300 = Color(0xFF64B5F6)
    val Blue400 = Color(0xFF42A5F5)
    val Blue500 = Color(0xFF1E88E5)  // ⭐ Current Primary
    val Blue600 = Color(0xFF1976D2)
    val Blue700 = Color(0xFF1565C0)
    val Blue800 = Color(0xFF0D47A1)
    val Blue900 = Color(0xFF0A3D91)
}

/**
 * Gold Scale - Accent Color
 * Used for: Selected states, highlights, emphasis
 */
object Gold {
    val Gold50 = Color(0xFFFFF9E5)
    val Gold100 = Color(0xFFFFF3CC)
    val Gold200 = Color(0xFFFFE699)
    val Gold300 = Color(0xFFFFD966)
    val Gold400 = Color(0xFFE5C44A)
    val Gold500 = Color(0xFFD4AF37)  // ⭐ Current Accent
    val Gold600 = Color(0xFFB8992F)
    val Gold700 = Color(0xFF9C8327)
    val Gold800 = Color(0xFF806D1F)
    val Gold900 = Color(0xFF665717)
}

/**
 * Green Scale - Success/Positive States
 * Used for: Success indicators, positive budget states, goals achieved
 */
object Green {
    val Green50 = Color(0xFFE8F5E9)
    val Green100 = Color(0xFFC8E6C9)
    val Green200 = Color(0xFFA5D6A7)
    val Green300 = Color(0xFF81C784)
    val Green400 = Color(0xFF66BB6A)
    val Green500 = Color(0xFF00C853)  // ✓ Success
    val Green600 = Color(0xFF00B248)
    val Green700 = Color(0xFF009C3D)
    val Green800 = Color(0xFF008632)
    val Green900 = Color(0xFF007027)
}

/**
 * Red Scale - Error/Negative States
 * Used for: Errors, over budget, destructive actions
 */
object Red {
    val Red50 = Color(0xFFFFEBEE)
    val Red100 = Color(0xFFFFCDD2)
    val Red200 = Color(0xFFEF9A9A)
    val Red300 = Color(0xFFE57373)
    val Red400 = Color(0xFFEF5350)
    val Red500 = Color(0xFFFF2400)  // ⚠ Error
    val Red600 = Color(0xFFE62000)
    val Red700 = Color(0xFFCC1C00)
    val Red800 = Color(0xFFB31800)
    val Red900 = Color(0xFF991400)
}

/**
 * Orange Scale - Warning States
 * Used for: Warnings, approaching budget limits
 */
object Orange {
    val Orange50 = Color(0xFFFFF3E0)
    val Orange100 = Color(0xFFFFE0B2)
    val Orange200 = Color(0xFFFFCC80)
    val Orange300 = Color(0xFFFFB74D)
    val Orange400 = Color(0xFFFFA726)
    val Orange500 = Color(0xFFFFA500)  // ⚠ Warning
    val Orange600 = Color(0xFFE69500)
    val Orange700 = Color(0xFFCC8500)
    val Orange800 = Color(0xFFB37500)
    val Orange900 = Color(0xFF996500)
}

/**
 * Neutral Scale - Grays and Background Colors
 * Extended scale with special stops for gradients and surfaces
 */
object Neutral {
    val Neutral0 = Color(0xFFFFFFFF)    // Pure White
    val Neutral50 = Color(0xFFFAFAFA)
    val Neutral100 = Color(0xFFF5F5F5)
    val Neutral200 = Color(0xFFEEEEEE)
    val Neutral300 = Color(0xFFE0E0E0)
    val Neutral400 = Color(0xFFBDBDBD)
    val Neutral500 = Color(0xFF9E9E9E)
    val Neutral600 = Color(0xFF757575)
    val Neutral700 = Color(0xFF616161)
    val Neutral800 = Color(0xFF424242)
    val Neutral850 = Color(0xFF363636)  // Background gradient stop
    val Neutral875 = Color(0xFF1C1C1E)  // Bottom sheet container
    val Neutral880 = Color(0xFF1B1B1B)  // Background gradient stop
    val Neutral900 = Color(0xFF121212)  // ⭐ Primary Background (Material Dark)
    // use 22D3EE
    val Neutral950 = Color(0xFF000000)  // Pure Black
}

// ══════════════════════════════════════════════════════════════════════════════
// SEMANTIC TOKENS - Dark Mode (Purpose-Based Colors)
// ══════════════════════════════════════════════════════════════════════════════

/**
 * Background Colors
 * Used for: Screen backgrounds, canvas
 */
object Background {
    val Primary = Neutral.Neutral900      // #121212 - Main screen background (solid)
    val Secondary = Neutral.Neutral950    // #000000 - Alternative/deeper background
}

/**
 * Surface Colors
 * Used for: Cards, containers, elevated components
 */
object Surface {
    val Primary = Color(0xFF1E1E1E)
//    val Primary = Color(0x1AFFFFFF)
    //1E1E1E// White 10% - Main cards, default surfaces (elevated)
    val Secondary = Color(0x26FFFFFF)     // White 15% - Elevated cards
    val Tertiary = Color(0x1FFFFFFF)      // White 12% - Card highlights
    val Input = Color(0x14FFFFFF)         // White 8% - Text field backgrounds
    val Modal = Neutral.Neutral875        // #1C1C1E - Bottom sheets, modals
    val Navigation = Color(0x80808080)    // Gray 50% - Bottom navigation background
    val Selected = Color(0x0DFFFFFF)      // White 5% - Selected state background
}

/**
 * Text Colors
 * Used for: All text content with varying emphasis
 */
object Text {
    val Primary = Neutral.Neutral0        // White 100% - Main text, headings
    val Secondary = Color(0x99FFFFFF)     // White 60% - Labels, secondary info
    val Tertiary = Color(0x66FFFFFF)      // White 40% - Disabled text, tertiary info
    val Placeholder = Color(0x44FFFFFF)   // White 27% - Placeholder text in inputs
    val Disabled = Color(0x33FFFFFF)      // White 20% - Fully disabled text
    val Muted = Color(0x80FFFFFF)         // White 50% - Very muted text
}

/**
 * Border Colors
 * Used for: Borders, dividers, separators
 */
object Border {
    val Subtle = Color(0x1AFFFFFF)        // White 10% - Subtle borders
    val Default = Color(0x33FFFFFF)       // White 20% - Standard borders
    val Strong = Color(0x4DFFFFFF)        // White 30% - Emphasized borders
    val Transparent = Color(0x00FFFFFF)   // Transparent - Unfocused states
}

/**
 * Action Colors
 * Used for: Interactive elements, buttons, links
 */
object Action {
    val Primary = Blue.Blue500                    // #1E88E5 - Primary actions, buttons
    val PrimaryHover = Blue.Blue600               // #1976D2 - Primary hover state
    val PrimaryActive = Blue.Blue700              // #1565C0 - Primary pressed state
    val PrimaryDisabled = Color(0x0D1E88E5)       // Blue 5% - Disabled primary button

    val Accent = Gold.Gold500                     // #D4AF37 - Accent, selected states
    val AccentHover = Gold.Gold600                // #B8992F - Accent hover state

    val Success = Green.Green500                  // #00C853 - Success actions
    val Error = Red.Red500                        // #FF2400 - Error, destructive actions
    val Warning = Orange.Orange500                // #FFA500 - Warning actions
}

/**
 * State Colors
 * Used for: Status indicators, feedback, alerts
 */
object State {
    val Success = Green.Green500                  // #00C853 - Success indicators
    val SuccessBg = Color(0x2600C853)             // Green 15% - Success backgrounds

    val Error = Red.Red500                        // #FF2400 - Error indicators
    val ErrorBg = Color(0x26FF2400)               // Red 15% - Error backgrounds

    val Warning = Orange.Orange500                // #FFA500 - Warning indicators
    val WarningBg = Color(0x26FFA500)             // Orange 15% - Warning backgrounds

    val Info = Blue.Blue500                       // #1E88E5 - Info indicators
    val InfoBg = Color(0x261E88E5)                // Blue 15% - Info backgrounds
}

// ══════════════════════════════════════════════════════════════════════════════
// LEGACY SUPPORT - Backwards Compatibility
// ══════════════════════════════════════════════════════════════════════════════

// These maintain compatibility with existing code during migration
val primary = Blue.Blue500
val surface = Neutral.Neutral900
val background = Neutral.Neutral900
val accentGold = Gold.Gold500
