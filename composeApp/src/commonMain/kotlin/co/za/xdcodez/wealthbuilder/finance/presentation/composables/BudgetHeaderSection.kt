package co.za.xdcodez.wealthbuilder.finance.presentation.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import co.za.xdcodez.wealthbuilder.common.budgetProgressColor
import co.za.xdcodez.wealthbuilder.common.formatCurrency

/**
 * Generic budget header section with automatic color computation
 *
 * @param primaryMetricLabel Label for the main metric (e.g., "Available Balance", "Budget Remaining")
 * @param primaryMetricValue The main metric value to display
 * @param usedAmount Amount used/spent
 * @param totalAmount Total budget/allocation
 * @param colorStrategy Strategy for computing the primary metric color
 * @param bottomRowContent Optional composable for bottom row (e.g., money in / month end goal)
 */
@Composable
fun BudgetHeaderSection(
    primaryMetricLabel: String,
    primaryMetricValue: Double,
    usedAmount: Double,
    totalAmount: Double,
    colorStrategy: BudgetColorStrategy,
    modifier: Modifier = Modifier,
    bottomRowContent: (@Composable () -> Unit)? = null
) {
    val progress = if (totalAmount > 0) (usedAmount / totalAmount).toFloat() else 0f
    val percentage = if (totalAmount > 0) (usedAmount / totalAmount * 100).toInt() else 0
    val progressColor = budgetProgressColor(progress)
    val primaryMetricColor = colorStrategy.computeColor(primaryMetricValue)

    Column(
        modifier = modifier.fillMaxWidth().padding(horizontal = 8.dp)
    ) {
        // Primary metric
        Text(
            text = primaryMetricLabel,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.labelLarge.copy(color = Color(0x99FFFFFF))
        )
        Text(
            text = formatCurrency(primaryMetricValue),
            modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.displaySmall.copy(color = primaryMetricColor)
        )

        // Budget Usage
        Text(
            text = "Budget Usage",
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.labelLarge.copy(color = Color(0x99FFFFFF))
        )

        Text(
            buildAnnotatedString {
                withStyle(style = SpanStyle(fontSize = 20.sp, color = progressColor)) {
                    append(formatCurrency(usedAmount))
                }
                withStyle(style = SpanStyle(fontSize = 16.sp, color = Color(0x99FFFFFF))) {
                    append(" / " + formatCurrency(totalAmount) + "  •  ")
                }
                withStyle(SpanStyle(fontSize = 16.sp, color = progressColor)) {
                    append("${percentage}%")
                }
            },
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
        )

        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier.padding(top = 2.dp).height(6.dp).fillMaxWidth(),
            color = progressColor,
            trackColor = Color(0x33FFFFFF),
            strokeCap = StrokeCap.Round
        )

        // Optional bottom row
        bottomRowContent?.invoke()
    }
}

/**
 * Standard bottom row showing two labeled amounts side by side
 */
@Composable
fun BudgetBottomRow(
    leftLabel: String,
    leftAmount: Double,
    rightLabel: String,
    rightAmount: Double,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(horizontalAlignment = Alignment.Start) {
            Text(
                leftLabel,
                style = MaterialTheme.typography.labelLarge.copy(color = Color(0x99FFFFFF))
            )
            Text(
                formatCurrency(leftAmount),
                maxLines = 1,
                style = MaterialTheme.typography.bodyLarge.copy(color = Color.White)
            )
        }
        Column(horizontalAlignment = Alignment.End) {
            Text(
                rightLabel,
                style = MaterialTheme.typography.labelLarge.copy(color = Color(0x99FFFFFF))
            )
            Text(
                formatCurrency(rightAmount),
                maxLines = 1,
                style = MaterialTheme.typography.bodyLarge.copy(color = Color.White)
            )
        }
    }
}

/**
 * Strategy for computing the color of the primary metric based on its value
 */
sealed class BudgetColorStrategy {
    abstract fun computeColor(value: Double): Color

    /**
     * Color strategy for "Budget Remaining" metric
     * - Red if over budget (negative)
     * - Green if nearly depleted (close to 0)
     * - Amber if less than 25% remaining
     * - White if healthy
     */
    data class BudgetRemaining(val totalBudget: Double) : BudgetColorStrategy() {
        override fun computeColor(value: Double): Color {
            val percentRemaining = value / totalBudget
            return when {
                value < 0 -> Color(0xFFFF2400)
                value <= 0.1 -> Color(0xFF00C853)
                percentRemaining <= 0.25 -> Color(0xFFFFA500)
                else -> Color.White
            }
        }
    }

    /**
     * Color strategy for "Available Balance" metric
     * - Red if below expected goal
     * - Green if hit goal exactly
     * - Amber if within 10% of goal
     * - White if comfortably above goal
     */
    data class AvailableBalance(val expectedBalance: Double) : BudgetColorStrategy() {
        override fun computeColor(value: Double): Color {
            val diff = value - expectedBalance
            return when {
                diff < -0.01 -> Color(0xFFFF2400)
                diff <= 0.01 -> Color(0xFF00C853)
                diff <= expectedBalance * 0.10 -> Color(0xFFFFA500)
                else -> Color.White
            }
        }
    }
}