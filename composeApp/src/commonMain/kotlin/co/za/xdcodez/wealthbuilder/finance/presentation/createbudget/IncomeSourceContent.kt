package co.za.xdcodez.wealthbuilder.finance.presentation.createbudget

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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import co.za.xdcodez.wealthbuilder.common.formatCurrency
import co.za.xdcodez.wealthbuilder.common.widgets.CustomTextField
import co.za.xdcodez.wealthbuilder.finance.domain.dto.IncomeSourceInput
import co.za.xdcodez.wealthbuilder.theme.WealthBuilderTheme
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun IncomeSourceContent(
    state: CreateBudgetUiState,
    onAction: (CreateBudgetActions) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(bottom = 80.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text(
                text = "Income Sources",
                style = MaterialTheme.typography.titleMedium,
                color = Color.White
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Add all your income sources for this month",
                style = MaterialTheme.typography.bodySmall,
                color = Color(0x99FFFFFF)
            )
            Spacer(modifier = Modifier.height(16.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Total Income",
                    style = MaterialTheme.typography.labelLarge,
                    color = Color(0x99FFFFFF)
                )
                Text(
                    text = formatCurrency(state.totalIncome),
                    style = MaterialTheme.typography.displaySmall,
                    color = Color.White
                )
            }

        }

        items(state.incomeSources, key = { it.id }) { source ->
            IncomeSourceRow(
                source = source,
                showRemove = state.incomeSources.size > 1,
                onNameChange = {
                    onAction(
                        CreateBudgetActions.UpdateIncomeSourceName(
                            source.id,
                            it
                        )
                    )
                },
                onAmountChange = {
                    onAction(
                        CreateBudgetActions.UpdateIncomeSourceAmount(
                            source.id,
                            it
                        )
                    )
                },
                onRemove = { onAction(CreateBudgetActions.RemoveIncomeSource(source.id)) }
            )
        }

        item {
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.White.copy(alpha = 0.05f))
                    .clickable { onAction(CreateBudgetActions.AddIncomeSource) }
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
                    text = "Add Income Source",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Composable
fun IncomeSourceRow(
    source: IncomeSourceInput,
    showRemove: Boolean,
    onNameChange: (String) -> Unit,
    onAmountChange: (String) -> Unit,
    onRemove: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        CustomTextField(
            value = source.name,
            onValueChange = onNameChange,
            modifier = Modifier.weight(0.6f).height(40.dp),
            placeholder = "e.g. Salary"
        )

        CustomTextField(
            value = source.amount,
            placeholder = "0.00",
            onValueChange = onAmountChange,
            modifier = Modifier.weight(0.4f).height(40.dp),
            prefix = "R ",
            filter = { it.filter { c -> c.isDigit() || c == '.' } }
        )

        if (showRemove) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Remove",
                tint = Color(0x99FFFFFF),
                modifier = Modifier
                    .size(18.dp)
                    .clickable { onRemove() }
            )
        }
    }

}

@Preview
@Composable
fun IncomeSourceContentPreview() {
    WealthBuilderTheme {
        IncomeSourceContent(
            state = CreateBudgetUiState(),
            modifier = Modifier,
            onAction = {}
        )
    }
}