package co.za.xdcodes.level_up.journal.presentation.setup

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import co.za.xdcodes.level_up.common.formatCurrency
import co.za.xdcodes.level_up.common.toMonthName
import co.za.xdcodes.level_up.finance.presentation.createbudget.ReviewSection
import co.za.xdcodes.level_up.theme.LevelUpTheme
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun ReviewSetupContent(
    state: JournalSetupState,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(bottom = 80.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text(
                text = "Review Setup",
                style = MaterialTheme.typography.titleMedium,
                color = Color.White
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Make sure everything looks correct before saving",
                style = MaterialTheme.typography.bodySmall,
                color = Color(0x99FFFFFF)
            )
            Spacer(modifier = Modifier.height(8.dp))
        }

        // ── Monthly target ───────────────────────────────────────
        item {
            ReviewSection(title = "${state.monthIndex.toMonthName()} ${state.year}") {
                BreakdownRow(
                    label = "Monthly target",
                    value = formatCurrency(state.monthlyTarget.toDoubleOrNull() ?: 0.0)
                )
                BreakdownRow(
                    label = "Weekly target",
                    value = formatCurrency(state.weeklyTarget)
                )
                BreakdownRow(
                    label = "Daily target",
                    value = formatCurrency(state.dailyTarget),
                    hint = "Based on 20 trading days"
                )
                BreakdownRow(
                    label = "Daily loss limit",
                    value = formatCurrency(state.lossLimitAmount),
                    hint = "${state.lossLimitPercent}% of daily target",
                    valueColor = Color(0xFFFF2400)
                )
            }
        }

        // ── Rules — only show on first time setup ────────────────
        if (state.isFirstTimeSetup) {
            item {
                ReviewSection(title = "Trading Rules") {
                    BreakdownRow(
                        label = "Max trades per day",
                        value = state.maxTradesPerDay
                    )
                    BreakdownRow(
                        label = "Loss limit",
                        value = "${state.lossLimitPercent}%"
                    )
                }
            }

            item {
                ReviewSection(title = "Trading Sessions") {
                    BreakdownRow(
                        label = "Morning",
                        value = "${state.sessionOneStart} - ${state.sessionOneEnd}"
                    )
                    BreakdownRow(
                        label = "Afternoon",
                        value = "${state.sessionTwoStart} - ${state.sessionTwoEnd}"
                    )
                }
            }
        }

        // ── Stop conditions reminder ─────────────────────────────
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

        // ── Error ────────────────────────────────────────────────
        if (state.error != null) {
            item {
                Text(
                    text = state.error,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFFFF2400),
                    modifier = Modifier.padding(horizontal = 8.dp)
                )
            }
        }

        // ── Loading ──────────────────────────────────────────────
        if (state.isLoading) {
            item {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        color = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }
    }
}

@Preview
@Composable
fun ReviewSetupContentPreview() {
    LevelUpTheme {
        ReviewSetupContent(
            state = JournalSetupState()
        )
    }
}