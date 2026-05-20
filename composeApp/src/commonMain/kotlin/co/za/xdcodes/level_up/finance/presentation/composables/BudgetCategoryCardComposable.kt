package co.za.xdcodes.level_up.finance.presentation.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Icon
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
import androidx.compose.ui.text.capitalize
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import co.za.xdcodes.level_up.common.budgetProgressColor
import co.za.xdcodes.level_up.common.formatCurrency
import co.za.xdcodes.level_up.common.widgets.CardComposable
import co.za.xdcodes.level_up.finance.domain.dto.BudgetCategoryModel
import co.za.xdcodes.level_up.theme.LevelUpTheme
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun BudgetCategoryCardComposable(
    category: BudgetCategoryModel,
    showNavigationIcon: Boolean = false,
    modifier: Modifier = Modifier
) {
    val progress = (category.totalPaid / category.budget)
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
                    category.name.uppercase(),
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
                        formatCurrency(category.totalPaid),
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
                        formatCurrency(category.budgetRemaining),
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
                    formatCurrency(category.budget),
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

@Composable
fun SummaryCategoryCardComposable(
    categoryModel: BudgetCategoryModel,
    modifier: Modifier = Modifier
) {
    val progress = (categoryModel.totalPaid / categoryModel.budget)
        .toFloat()

    val progressColor = budgetProgressColor(progress)

    Column(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(Color.White.copy(alpha = 0.05f))
            .padding(start = 8.dp, end = 8.dp, top = 4.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                categoryModel.name.capitalize(Locale.current),
                style = MaterialTheme.typography.bodyMedium.copy(color = Color.White)
            )

            Icon(
                imageVector = Icons.AutoMirrored.Default.KeyboardArrowRight,
                contentDescription = "",
                tint = Color.White
            )
        }

        Text(buildAnnotatedString {
            withStyle(SpanStyle(fontSize = 15.sp, color = progressColor)) {
                append(formatCurrency(categoryModel.totalPaid))
            }
            withStyle(SpanStyle(fontSize = 13.sp, color = Color(0x99FFFFFF))) {
                append(" / ${formatCurrency(categoryModel.budget)}")
            }
        })
        Row(verticalAlignment = Alignment.CenterVertically) {
            LinearProgressIndicator(
                modifier = Modifier.weight(1f).height(6.dp),
                progress = { progress },
                color = progressColor,
                trackColor = Color(0x33FFFFFF),
                strokeCap = StrokeCap.Round
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                "${(progress * 100).toInt()}%",
                style = MaterialTheme.typography.labelMedium.copy(color = progressColor)
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
    }
}

@Preview
@Composable
fun SummaryCategoryModelComposablePreview() {
    LevelUpTheme {
        SummaryCategoryCardComposable(
            BudgetCategoryModel(
                "",
                "DAY-TO-DAY",
                2000.0,
                5000.0,
            )
        )
    }
}

@Preview
@Composable
fun BudgetCategoryModelComposablePreview() {
    LevelUpTheme {
        BudgetCategoryCardComposable(
            BudgetCategoryModel(
                "",
                "DAY-TO-DAY",
                4000.0,
                5000.0,
            )
        )
    }
}