package co.za.xdcodez.wealthbuilder.finance.presentation.createbudget

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import co.za.xdcodez.wealthbuilder.common.formatCurrency

@Composable
fun ReviewBudgetContent(
    state: CreateBudgetUiState,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(bottom = 80.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text(
                text = "Review Budget",
                style = MaterialTheme.typography.titleMedium,
                color = Color.White
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Make sure everything looks correct before saving",
                style = MaterialTheme.typography.bodySmall,
                color = Color(0x99FFFFFF)
            )
            Spacer(modifier = Modifier.height(16.dp))
        }

        // ── Income summary ───────────────────────────────────────
        item {
            ReviewSection(title = "Income Sources") {
                state.incomeSources.forEach { source ->
                    ReviewRow(
                        label = source.name,
                        value = formatCurrency(source.amount.toDoubleOrNull() ?: 0.0)
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                ReviewRow(
                    label = "Total Income",
                    value = formatCurrency(state.totalIncome),
                    isTotal = true
                )
            }
        }

        // ── Categories summary ───────────────────────────────────
        item {
            ReviewSection(title = "Budget Categories") {
                state.categories.forEach { category ->
                    ReviewRow(
                        label = category.name,
                        value = formatCurrency(category.amount.toDoubleOrNull() ?: 0.0)
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                ReviewRow(
                    label = "Total Budget",
                    value = formatCurrency(state.totalAllocated),
                    isTotal = true
                )
            }
        }

        // ── Buffer ───────────────────────────────────────────────
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.White.copy(alpha = 0.05f))
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Month End Goal",
                        style = MaterialTheme.typography.labelLarge,
                        color = Color(0x99FFFFFF)
                    )
                    Text(
                        text = "Money left after budget",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0x44FFFFFF)
                    )
                }
                Text(
                    text = formatCurrency(state.unallocated.coerceAtLeast(0.0)),
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White
                )
            }
        }
    }
}

@Composable
fun ReviewSection(
    title: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(Color.White.copy(alpha = 0.05f))
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.labelLarge,
            color = Color(0x99FFFFFF)
        )
        Spacer(modifier = Modifier.height(4.dp))
        content()
    }
}

@Composable
fun ReviewRow(
    label: String,
    value: String,
    isTotal: Boolean = false
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = if (isTotal) MaterialTheme.typography.titleSmall
            else MaterialTheme.typography.bodyMedium,
            color = if (isTotal) Color.White else Color(0x99FFFFFF)
        )
        Text(
            text = value,
            style = if (isTotal) MaterialTheme.typography.titleSmall
            else MaterialTheme.typography.bodyMedium,
            color = if (isTotal) Color.White else Color(0x99FFFFFF)
        )
    }
}