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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import co.za.xdcodez.wealthbuilder.common.widgets.CustomTextField
import co.za.xdcodez.wealthbuilder.theme.WealthBuilderTheme
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun TradingRulesContent(
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
                text = "Trading Rules",
                style = MaterialTheme.typography.titleMedium,
                color = Color.White
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Set your discipline rules for consistent trading",
                style = MaterialTheme.typography.bodySmall,
                color = Color(0x99FFFFFF)
            )
        }

        // max trades
        item {
            LabeledField(label = "Max trades per day") {
                CustomTextField(
                    value = state.maxTradesPerDay,
                    onValueChange = { onAction(JournalSetupActions.OnMaxTradesChanged(it)) },
                    filter = { it.filter { c -> c.isDigit() } },
                    placeholder = "Recommended: 4",
                    modifier = Modifier.fillMaxWidth().height(48.dp)
                )
            }
        }

        // loss limit
        item {
            LabeledField(
                label = "Daily loss limit",
                hint = "Percentage of your daily target"
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    CustomTextField(
                        value = state.lossLimitPercent,
                        onValueChange = {onAction(JournalSetupActions.OnLossLimitChanged(it)) },
                        placeholder = "50",
                        filter = { it.filter { c -> c.isDigit()} },
                        modifier = Modifier.weight(1f).height(48.dp)
                    )
                    Text(
                        text = "%",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.White
                    )
                }
            }
        }

        // session times
        item {
            LabeledField(label = "Morning session") {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    CustomTextField(
                        value = state.sessionOneStart,
                        onValueChange = { onAction(JournalSetupActions.OnSessionOneStartChanged(it)) },
                        placeholder = "09:00",
                        filter = { it.filter { c -> c.isDigit() || c == ':' } },
                        modifier = Modifier.weight(1f).height(48.dp)
                    )
                    Text("to", color = Color(0x99FFFFFF))
                    CustomTextField(
                        value = state.sessionOneEnd,
                        onValueChange = { onAction(JournalSetupActions.OnSessionOneEndChanged(it)) },
                        placeholder = "12:00",
                        filter = { it.filter { c -> c.isDigit() || c == ':' } },
                        modifier = Modifier.weight(1f).height(48.dp)
                    )
                }
            }
        }

        item {
            LabeledField(label = "Afternoon session") {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    CustomTextField(
                        value = state.sessionTwoStart,
                        onValueChange = { onAction(JournalSetupActions.OnSessionTwoStartChanged(it)) },
                        placeholder = "15:00",
                        filter = { it.filter { c -> c.isDigit() || c == ':' } },
                        modifier = Modifier.weight(1f).height(48.dp)
                    )
                    Text("to", color = Color(0x99FFFFFF))
                    CustomTextField(
                        value = state.sessionTwoEnd,
                        onValueChange = { onAction(JournalSetupActions.OnSessionTwoEndChanged(it)) },
                        placeholder = "16:30",
                        filter = { it.filter { c -> c.isDigit() || c == ':' } },
                        modifier = Modifier.weight(1f).height(48.dp)
                    )
                }
            }
        }

        // stop conditions info
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.White.copy(alpha = 0.05f))
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Stop Conditions",
                    style = MaterialTheme.typography.labelLarge,
                    color = Color(0x99FFFFFF)
                )
                Text(
                    text = "• Daily target hit → stop trading\n• Loss limit reached → stop trading\n• Max trades reached → stop trading",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White,
                    lineHeight = 20.sp
                )
            }
        }
    }
}

// reusable label wrapper
@Composable
fun LabeledField(
    label: String,
    hint: String? = null,
    content: @Composable () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = Color(0x99FFFFFF)
        )
        if (hint != null) {
            Text(
                text = hint,
                style = MaterialTheme.typography.labelSmall,
                color = Color(0x44FFFFFF)
            )
        }
        content()
    }
}

@Preview
@Composable
fun TradingRulesContentPreview() {
    WealthBuilderTheme {
        TradingRulesContent(
            state = JournalSetupState(),
            onAction = {},
        )
    }
}