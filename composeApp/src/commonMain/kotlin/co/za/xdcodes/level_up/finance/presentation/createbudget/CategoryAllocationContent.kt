package co.za.xdcodes.level_up.finance.presentation.createbudget

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import co.za.xdcodes.level_up.common.formatCurrency
import co.za.xdcodes.level_up.common.widgets.CustomTextField
import co.za.xdcodes.level_up.finance.domain.dto.BudgetCategoryInput
import co.za.xdcodes.level_up.theme.LevelUpTheme
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun CategoryAllocationContent(
    state: CreateBudgetUiState,
    onAction: (CreateBudgetActions) -> Unit,
    modifier: Modifier = Modifier
) {
    val progressColor = if (state.isOverAllocated) Color(0xFFFF2400)
    else if (state.allocationProgress == 1f) Color(0xFF00C853)
    else MaterialTheme.colorScheme.primary

    val heroText = if (state.isOverAllocated) "Over Allocated"
    else if (state.allocationProgress == 1f) "Fully Allocated"
    else "Allocated"

    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(bottom = 80.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text(
                text = "Budget Allocation",
                style = MaterialTheme.typography.titleMedium,
                color = Color.White
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Allocate your income across categories",
                style = MaterialTheme.typography.bodySmall,
                color = Color(0x99FFFFFF)
            )
            Spacer(modifier = Modifier.height(16.dp))

            // ── Unallocated hero ─────────────────────────────────
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = heroText,
                    style = MaterialTheme.typography.labelLarge,
                    color = if (state.isOverAllocated) Color(0xFFFF2400)
                    else if (state.allocationProgress == 1f) Color(0xFF00C853)
                    else Color(0x99FFFFFF)
                )

                Text(
                    buildAnnotatedString {
                        withStyle(
                            SpanStyle(
                                fontSize = 20.sp,
                                color = progressColor
                            )
                        ) {
                            append(formatCurrency(state.totalAllocated))
                        }
                        withStyle(
                            SpanStyle(
                                fontSize = 16.sp,
                                color = Color(0x99FFFFFF)
                            )
                        ) {
                            append(" / ${formatCurrency(state.totalIncome)}  •  ")
                        }
                        withStyle(SpanStyle(fontSize = 16.sp, color = progressColor)) {
                            append("${(state.allocationProgress * 100).toInt()}%")
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            // ── Allocation progress bar ──────────────────────────
            LinearProgressIndicator(
                progress = { state.allocationProgress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(999.dp)),
                color = progressColor,
                trackColor = Color(0x33FFFFFF)
            )

            Spacer(modifier = Modifier.height(16.dp))
        }

        items(state.categories, key = { it.id }) { category ->
            CategoryInputRow(
                category = category,
                showRemove = state.categories.size > 1,
                onNameChange = {
                    onAction(CreateBudgetActions.UpdateCategoryName(category.id, it))
                },
                onAmountChange = {
                    onAction(CreateBudgetActions.UpdateCategoryAmount(category.id, it))
                },
                onRemove = { onAction(CreateBudgetActions.RemoveCategory(category.id)) }
            )
        }

        item {
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.White.copy(alpha = 0.05f))
                    .clickable { onAction(CreateBudgetActions.AddCategory) }
                    .padding(horizontal = 16.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "",
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Add Category",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Composable
fun CategoryInputRow(
    category: BudgetCategoryInput,
    showRemove: Boolean,
    onNameChange: (String) -> Unit,
    onAmountChange: (String) -> Unit,
    onRemove: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        CustomTextField(
            value = category.name,
            onValueChange = onNameChange,
            placeholder = "e.g. Essentials",
            modifier = Modifier.weight(0.55f).height(48.dp)
        )

        CustomTextField(
            value = category.amount,
            onValueChange = onAmountChange,
            placeholder = "0.00",
            prefix = "R ",
            filter = { it.filter { c -> c.isDigit() || c == '.' } },
            modifier = Modifier.weight(0.35f).height(48.dp),
        )

        if (showRemove) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Remove",
                tint = Color(0x99FFFFFF),
                modifier = Modifier.size(20.dp).clickable { onRemove() }
            )
        } else {
            Spacer(modifier = Modifier.size(20.dp))
        }
    }
}

@Preview
@Composable
fun CategoryAllocationContentPreview() {
    LevelUpTheme {
        CategoryAllocationContent(
            state = CreateBudgetUiState(),
            modifier = Modifier,
            onAction = {}
        )
    }
}