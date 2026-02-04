package co.za.xdcodes.level_up.finance.presentation.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Icon
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
import co.za.xdcodes.level_up.finance.domain.dto.BudgetCategoryModel
import co.za.xdcodes.level_up.theme.LevelUpTheme
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun BudgetCategoryCardComposable(
    categoryModel: BudgetCategoryModel,
    showNavigationIcon: Boolean = false,
    modifier: Modifier = Modifier
) {
    val progress = (categoryModel.totalPaid / categoryModel.totalBudget)
        .coerceIn(0.0, 1.0)
        .toFloat()

    CardComposable(
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    categoryModel.category.uppercase(),
                    style = MaterialTheme.typography.titleMedium.copy(color = MaterialTheme.colorScheme.primary)
                )

                if (showNavigationIcon)
                    Icon(
                        imageVector = Icons.AutoMirrored.Default.KeyboardArrowRight,
                        contentDescription = "",
                        tint = MaterialTheme.colorScheme.primary
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
                        formatCurrency(categoryModel.totalPaid),
                        style = MaterialTheme.typography.labelLarge.copy(color = Color.White)
                    )
                    Text(
                        "paid",
                        style = MaterialTheme.typography.labelMedium.copy(color = Color(0x99FFFFFF))
                    )
                }

                Column(
                    horizontalAlignment = Alignment.End
                ) {
                    Text(
                        formatCurrency(categoryModel.budgetRemaining),
                        style = MaterialTheme.typography.titleMedium.copy(color = Color.White)
                    )
                    Text(
                        "of your budget remaining",
                        style = MaterialTheme.typography.labelMedium.copy(color = Color(0x99FFFFFF))
                    )
                }
            }

            LinearProgressIndicator(
                modifier = Modifier
                    .padding(top = 6.dp)
                    .fillMaxWidth()
                    .height(6.dp),
                progress = { progress },
                color = MaterialTheme.colorScheme.primary,
                trackColor = Color(0x99FFFFFF),
                strokeCap = StrokeCap.Round
            )

            Column(
                modifier = Modifier.padding(top = 16.dp),
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    formatCurrency(categoryModel.totalBudget),
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
fun BudgetCategoryModelComposablePreview() {
    LevelUpTheme {
        BudgetCategoryCardComposable(
            BudgetCategoryModel(
                "DAY-TO-DAY",
                4000.0,
                5000.0,
                month = ""
            )
        )
    }
}