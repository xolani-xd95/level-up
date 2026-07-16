package co.za.xdcodez.wealthbuilder.journal.presentation.setup

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import co.za.xdcodez.wealthbuilder.common.formatCurrency
import co.za.xdcodez.wealthbuilder.common.toMonthName
import co.za.xdcodez.wealthbuilder.common.widgets.CustomTextField
import co.za.xdcodez.wealthbuilder.journal.presentation.setup.MonthlyTargetContent
import co.za.xdcodez.wealthbuilder.theme.WealthBuilderTheme
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun MonthlyTargetContent(
    state: JournalSetupState,
    onAction: (JournalSetupActions) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(bottom = 80.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = "Monthly Target",
                style = MaterialTheme.typography.titleMedium,
                color = Color.White
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "What's your profit target for ${state.monthIndex.toMonthName()} ${state.year}?",
                style = MaterialTheme.typography.bodySmall,
                color = Color(0x99FFFFFF)
            )
        }

        // ── Target input ─────────────────────────────────────────
        item {
            LabeledField(label = "Monthly target") {
                CustomTextField(
                    value = state.monthlyTarget,
                    onValueChange = {
                        onAction(
                            JournalSetupActions.OnMonthlyTargetChanged(
                                it.filter { c -> c.isDigit() }
                            )
                        )
                    },
                    placeholder = "0",
                    prefix = "R ",
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
            }
        }

        // ── Breakdown — only show when target > 0 ────────────────
        if ((state.monthlyTarget.toDoubleOrNull() ?: 0.0) > 0.0) {
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color.White.copy(alpha = 0.05f))
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Breakdown",
                        style = MaterialTheme.typography.labelLarge,
                        color = Color(0x99FFFFFF)
                    )

                    BreakdownRow(
                        label = "Daily target",
                        value = formatCurrency(state.dailyTarget),
                        hint = "Based on 20 trading days"
                    )

                    BreakdownRow(
                        label = "Weekly target",
                        value = formatCurrency(state.weeklyTarget)
                    )

                    BreakdownRow(
                        label = "Daily loss limit",
                        value = formatCurrency(state.lossLimitAmount),
                        hint = "${state.lossLimitPercent}% of daily target",
                        valueColor = Color(0xFFFF2400)
                    )
                }
            }
        }
    }
}

@Composable
fun BreakdownRow(
    label: String,
    value: String,
    hint: String? = null,
    valueColor: Color = Color.White
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White
            )
            if (hint != null) {
                Text(
                    text = hint,
                    style = MaterialTheme.typography.labelSmall,
                    color = Color(0x44FFFFFF)
                )
            }
        }
        Text(
            text = value,
            style = MaterialTheme.typography.titleSmall,
            color = valueColor
        )
    }
}

@Preview
@Composable
fun MonthlyTargetContentPreview() {
    WealthBuilderTheme {
        MonthlyTargetContent(
            state = JournalSetupState(),
            onAction = {}
        )
    }
}