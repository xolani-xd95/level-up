package co.za.xdcodes.level_up.finance.presentation.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import co.za.xdcodes.level_up.common.budgetProgressColor
import co.za.xdcodes.level_up.common.formatCurrency
import co.za.xdcodes.level_up.finance.domain.dto.BudgetCategoryModel
import co.za.xdcodes.level_up.finance.domain.dto.BudgetMonthModel
import co.za.xdcodes.level_up.theme.LevelUpTheme
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun BudgetMonthHeaderSection(
    budget: BudgetMonthModel,
    modifier: Modifier = Modifier
) {
    val expectedMoneyLeft = (budget.moneyIn - budget.totalBudget).coerceAtLeast(0.0)
    val actualMoneyLeft = (budget.moneyIn - budget.moneyOut).coerceAtLeast(0.0)

    val moneyOutProgress = (budget.moneyOut / budget.totalBudget)
        .toFloat()
    val moneyOutPercentage = (budget.moneyOut / budget.totalBudget * 100).toInt()

    val progressColor = budgetProgressColor(moneyOutProgress)

    val diff = actualMoneyLeft - expectedMoneyLeft
    val availableBalanceColor = when {
        diff < -0.01 -> Color(0xFFFF2400) // below goal → red
        diff <= 0.01 -> Color(0xFF00C853) // hit goal → green
        diff <= expectedMoneyLeft * 0.10 -> Color(0xFFFFA500) // within 10% of goal → amber
        else -> Color.White       // comfortably above → white
    }
    Column(
        modifier = modifier.fillMaxWidth(),
    ) {
        Text(
            text = "Available Balance",
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.labelLarge.copy(color = Color(0x99FFFFFF))
        )
        Text(
            text = formatCurrency(actualMoneyLeft),
            modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.displaySmall.copy(color = availableBalanceColor)
        )

        Text(
            text = "Budget Usage",
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.labelLarge.copy(color = Color(0x99FFFFFF))
        )

        Text(
            buildAnnotatedString {
                withStyle(style = SpanStyle(fontSize = 20.sp, color = progressColor)) {
                    append(formatCurrency(budget.moneyOut))
                }
                withStyle(style = SpanStyle(fontSize = 16.sp, color = Color(0x99FFFFFF))) {
                    append(" / " + formatCurrency(budget.totalBudget) + "  •  ")
                }
                withStyle(SpanStyle(fontSize = 16.sp, color = progressColor)) {
                    append("${moneyOutPercentage}%")
                }
            },
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
        )

        LinearProgressIndicator(
            progress = { moneyOutProgress },
            modifier = Modifier.padding(top = 8.dp, bottom = 8.dp).height(6.dp).fillMaxWidth(),
            color = progressColor,
            trackColor = Color(0x33FFFFFF),
            strokeCap = StrokeCap.Round
        )

        Row(
            modifier = Modifier
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    "money in",
                    style = MaterialTheme.typography.labelLarge.copy(color = Color(0x99FFFFFF))
                )
                Text(
                    formatCurrency(budget.moneyIn),
                    maxLines = 1,
                    style = MaterialTheme.typography.bodyLarge.copy(color = Color.White)
                )
            }
            Column(
                horizontalAlignment = Alignment.End
            ) {
                Text(
                    "month end goal",
                    style = MaterialTheme.typography.labelLarge.copy(color = Color(0x99FFFFFF))
                )
                Text(
                    formatCurrency(expectedMoneyLeft),
                    maxLines = 1,
                    style = MaterialTheme.typography.bodyLarge.copy(color = Color.White)
                )
            }
        }
    }
}

@Composable
fun BudgetCategoryHeaderSection(
    category: BudgetCategoryModel,
    modifier: Modifier = Modifier
) {

    val categoryBudgetProgress = (category.totalPaid / category.budget)
        .toFloat()

    val categoryBudgetPercentage = (category.totalPaid / category.budget * 100).toInt()
    val progressColor = budgetProgressColor(categoryBudgetProgress)
    val percentRemaining = category.budgetRemaining / category.budget

    val remainingColor = when {
        category.budgetRemaining < 0 -> Color(0xFFFF2400) // over budget → red
        category.budgetRemaining <= 0.1 -> Color(0xFF00C853) // perfect → green
        percentRemaining <= 0.25 -> Color(0xFFFFA500) // under 25% left → amber
        else -> Color.White        // healthy → white
    }
    Column(
        modifier = modifier.fillMaxWidth()
    ) {
        Text(
            text = "Budget Remaining",
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.labelLarge.copy(color = Color(0x99FFFFFF))
        )
        Text(
            text = formatCurrency(category.budgetRemaining),
            modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.displaySmall.copy(color = remainingColor)
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Budget Usage",
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.labelLarge.copy(color = Color(0x99FFFFFF))
        )

        Text(
            buildAnnotatedString {
                withStyle(style = SpanStyle(fontSize = 20.sp, color = progressColor)) {
                    append(formatCurrency(category.totalPaid))
                }
                withStyle(style = SpanStyle(fontSize = 16.sp, color = Color(0x99FFFFFF))) {
                    append(" / " + formatCurrency(category.budget) + "  •  ")
                }
                withStyle(SpanStyle(fontSize = 16.sp, color = progressColor)) {
                    append("${categoryBudgetPercentage}%")
                }
            },
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
        )

        LinearProgressIndicator(
            progress = { categoryBudgetProgress },
            modifier = Modifier.padding(vertical = 8.dp).height(6.dp).fillMaxWidth(),
            color = progressColor,
            trackColor = Color(0x33FFFFFF),
            strokeCap = StrokeCap.Round
        )

    }
}

@Preview
@Composable
fun MonthlyBudgetComposablePreview() {
    LevelUpTheme {
        BudgetMonthHeaderSection(
            BudgetMonthModel(
                monthId = "",
                monthIndex = 1,
                year = 1,
                moneyIn = 50000.0,
                moneyOut = 4500.0,
                totalBudget = 5000.0,
            ),
        )
    }
}

@Preview
@Composable
fun BudgetCategoryComposablePreview() {
    LevelUpTheme {
        BudgetCategoryHeaderSection(
            BudgetCategoryModel(
                id = "",
                name = "Events",
                totalPaid = 1000.00,
                budget = 8000.00,
            ),
        )
    }
}