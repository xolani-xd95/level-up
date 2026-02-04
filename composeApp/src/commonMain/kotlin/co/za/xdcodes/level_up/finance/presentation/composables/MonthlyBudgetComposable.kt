package co.za.xdcodes.level_up.finance.presentation.composables

import androidx.annotation.ColorInt
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.unit.dp
import co.za.xdcodes.level_up.common.formatCurrency
import co.za.xdcodes.level_up.common.widgets.CardComposable
import co.za.xdcodes.level_up.finance.domain.dto.BudgetOverviewModel
import co.za.xdcodes.level_up.theme.LevelUpTheme
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun MonthlyBudgetComposable(budget: BudgetOverviewModel) {
    val expectedMoneyLeft = (budget.moneyIn - budget.totalBudget).coerceAtLeast(0.0)
    val actualMoneyLeft = (budget.moneyIn - budget.moneyOut).coerceAtLeast(0.0)

    val moneyOutProgress = (budget.moneyOut / budget.totalBudget)
        .coerceIn(0.0, 1.0)
        .toFloat()

    CardComposable {
        Row(
            modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    formatCurrency(budget.moneyIn),
                    maxLines = 1,
                    style = MaterialTheme.typography.titleMedium.copy(color = Color.White)
                )
                Text(
                    "money in",
                    style = MaterialTheme.typography.labelMedium.copy(color = Color(0x99FFFFFF))
                )
            }

            Column(
                horizontalAlignment = Alignment.End
            ) {
                Text(
                    formatCurrency(budget.savings),
                    maxLines = 1,
                    style = MaterialTheme.typography.titleLarge.copy(color = Color.White)
                )
                Text(
                    "savings",
                    style = MaterialTheme.typography.labelMedium.copy(color = Color(0x99FFFFFF))
                )
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "budget used",
                style = MaterialTheme.typography.labelMedium.copy(color = Color(0x99FFFFFF))
            )

            LinearProgressIndicator(
                progress = { moneyOutProgress },
                modifier = Modifier.padding(horizontal = 8.dp).height(6.dp).weight(1f),
                trackColor = Color.Transparent,
                color = Color(0xFFFF2400),
                strokeCap = StrokeCap.Round
            )

            Text(
                formatCurrency(budget.moneyOut),
                maxLines = 1,
                style = MaterialTheme.typography.titleMedium.copy(color = Color(0xFFFF2400))
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth().padding(top = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    formatCurrency(actualMoneyLeft),
                    style = MaterialTheme.typography.titleLarge.copy(color = Color.White)
                )
                Text(
                    "money left: ${formatCurrency(expectedMoneyLeft)}",
                    style = MaterialTheme.typography.labelMedium.copy(color = Color(0x99FFFFFF))
                )
            }

            Column(
                horizontalAlignment = Alignment.End
            ) {
                Text(
                    formatCurrency(budget.totalBudget),
                    style = MaterialTheme.typography.titleMedium.copy(color = Color.White)
                )
                Text(
                    "total budget",
                    style = MaterialTheme.typography.labelMedium.copy(color = Color(0x99FFFFFF))
                )
            }
        }
    }
}

@Preview
@Composable
fun MonthlyBudgetComposablePreview() {
    LevelUpTheme {
        MonthlyBudgetComposable(
            BudgetOverviewModel(
                month = "FEBRUARY",
                moneyIn = 54000.0,
                moneyOut = 15000.0,
                savings = 0.0,
                totalBudget = 30000.0
            ),
        )
    }
}