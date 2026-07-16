package co.za.xdcodez.wealthbuilder.journal.presentation.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import co.za.xdcodez.wealthbuilder.common.formatCurrency
import co.za.xdcodez.wealthbuilder.journal.domain.model.TradeDirection
import co.za.xdcodez.wealthbuilder.journal.domain.model.TradeEntry
import co.za.xdcodez.wealthbuilder.journal.domain.model.TradeStatus


//@Composable
//fun StopBanner(state: JournalHomeState) {
//    val (icon, message, color) = when (state.stopReason) {
//        StopReason.TARGET_HIT -> Triple(
//            "🎯",
//            "Daily target reached! You're done for today.",
//            Color(0xFF00C853)
//        )
//
//        StopReason.LOSS_LIMIT_HIT -> Triple(
//            "⛔",
//            "Loss limit reached. Protect your capital.",
//            Color(0xFFFF2400)
//        )
//
//        StopReason.MAX_TRADES_HIT -> Triple(
//            "🔄",
//            "Max trades reached for today.",
//            Color(0xFFFFA500)
//        )
//
//        null -> Triple("", "", Color.Transparent)
//    }
//
//    Row(
//        modifier = Modifier
//            .fillMaxWidth()
//            .clip(RoundedCornerShape(12.dp))
//            .background(color.copy(alpha = 0.15f))
//            .padding(16.dp),
//        horizontalArrangement = Arrangement.spacedBy(12.dp),
//        verticalAlignment = Alignment.CenterVertically
//    ) {
//        Text(text = icon, fontSize = 24.sp)
//        Text(
//            text = message,
//            style = MaterialTheme.typography.bodyMedium,
//            color = color
//        )
//    }
//}

//@Composable
//fun TodayDashboard(state: JournalHomeState) {
//    val progressColor = when {
//        state.isTargetHit -> Color(0xFF00C853)
//        state.todayProgress >= 0.75f -> Color(0xFFFFA500)
//        state.isLossLimitHit -> Color(0xFFFF2400)
//        else -> Color.White
//    }
//
//    Column(
//        modifier = Modifier.fillMaxWidth(),
//        horizontalAlignment = Alignment.CenterHorizontally,
//        verticalArrangement = Arrangement.spacedBy(4.dp)
//    ) {
//        Text(
//            text = "Today's P&L",
//            style = MaterialTheme.typography.labelLarge,
//            color = Color(0x99FFFFFF)
//        )
//        Text(
//            text = buildAnnotatedString {
//                withStyle(SpanStyle(fontSize = 36.sp, color = progressColor)) {
//                    append(formatCurrency(state.todayPnl))
//                }
//                withStyle(SpanStyle(fontSize = 16.sp, color = Color(0x99FFFFFF))) {
//                    append(" / ${formatCurrency(state.dailyTarget)}")
//                }
//            },
//            textAlign = TextAlign.Center
//        )
//
//        Spacer(modifier = Modifier.height(8.dp))
//
//        LinearProgressIndicator(
//            progress = { state.todayProgress },
//            modifier = Modifier
//                .fillMaxWidth()
//                .height(6.dp)
//                .clip(RoundedCornerShape(999.dp)),
//            color = progressColor,
//            trackColor = Color(0x33FFFFFF)
//        )
//    }
//}

//@Composable
//fun StopConditionsGrid(state: JournalHomeState) {
//    Row(
//        modifier = Modifier.fillMaxWidth(),
//        horizontalArrangement = Arrangement.spacedBy(8.dp)
//    ) {
//        StopConditionCard(
//            label = "Trades",
//            value = "${state.tradesUsedToday} / ${state.config?.maxTradesPerDay ?: 4}",
//            isWarning = state.tradesUsedToday >= (state.config?.maxTradesPerDay ?: 4) - 1,
//            isHit = state.isMaxTradesHit,
//            modifier = Modifier.weight(1f)
//        )
//        StopConditionCard(
//            label = "Loss",
//            value = "${formatCurrency(state.todayLoss)} / ${formatCurrency(state.lossLimitAmount)}",
//            isWarning = state.todayLoss >= state.lossLimitAmount * 0.75,
//            isHit = state.isLossLimitHit,
//            modifier = Modifier.weight(1f)
//        )
//        StopConditionCard(
//            label = "Target",
//            value = formatCurrency(state.dailyTarget),
//            isWarning = false,
//            isHit = state.isTargetHit,
//            modifier = Modifier.weight(1f)
//        )
//    }
//}
//
//@Composable
//fun StopConditionCard(
//    label: String,
//    value: String,
//    isWarning: Boolean,
//    isHit: Boolean,
//    modifier: Modifier = Modifier
//) {
//    val color = when {
//        isHit && label == "Loss" -> Color(0xFFFF2400)
//        isHit -> Color(0xFF00C853)
//        isWarning -> Color(0xFFFFA500)
//        else -> Color(0x99FFFFFF)
//    }
//
//    Column(
//        modifier = modifier
//            .clip(RoundedCornerShape(12.dp))
//            .background(Color.White.copy(alpha = 0.05f))
//            .padding(12.dp),
//        horizontalAlignment = Alignment.CenterHorizontally,
//        verticalArrangement = Arrangement.spacedBy(4.dp)
//    ) {
//        Text(
//            text = label,
//            style = MaterialTheme.typography.labelSmall,
//            color = Color(0x99FFFFFF)
//        )
//        Text(
//            text = value,
//            style = MaterialTheme.typography.labelMedium,
//            color = color,
//            textAlign = TextAlign.Center
//        )
//        Text(
//            text = when {
//                isHit && label == "Loss" -> "⛔"
//                isHit -> "✅"
//                isWarning -> "🟡"
//                else -> "🟢"
//            },
//            fontSize = 14.sp
//        )
//    }
//}
//
//@Composable
//fun TradeCard(
//    trade: TradeEntry,
//    onClick: () -> Unit
//) {
//    val pnlColor = when {
//        trade.status == TradeStatus.WIN -> Color(0xFF00C853)
//        trade.status == TradeStatus.LOSS -> Color(0xFFFF2400)
//        trade.status == TradeStatus.OPEN -> Color(0xFFFFA500)
//        else -> Color.White
//    }
//
//    Row(
//        modifier = Modifier
//            .fillMaxWidth()
//            .clip(RoundedCornerShape(12.dp))
//            .background(Color.White.copy(alpha = 0.05f))
//            .clickable { onClick() }
//            .padding(horizontal = 12.dp, vertical = 10.dp),
//        horizontalArrangement = Arrangement.SpaceBetween,
//        verticalAlignment = Alignment.CenterVertically
//    ) {
//        Row(
//            horizontalArrangement = Arrangement.spacedBy(8.dp),
//            verticalAlignment = Alignment.CenterVertically
//        ) {
//            Text(
//                text = if (trade.direction == TradeDirection.LONG) "↑" else "↓",
//                fontSize = 18.sp,
//                color = if (trade.direction == TradeDirection.LONG)
//                    Color(0xFF00C853) else Color(0xFFFF2400)
//            )
//            Column {
//                Text(
//                    text = "GOLD",
//                    style = MaterialTheme.typography.bodyMedium,
//                    color = Color.White
//                )
//                Text(
//                    text = "${trade.positionSize} lots  •  RR ${trade.riskRewardRatio}",
//                    style = MaterialTheme.typography.labelSmall,
//                    color = Color(0x99FFFFFF)
//                )
//            }
//        }
//
//        Column(horizontalAlignment = Alignment.End) {
//            Text(
//                text = if (trade.status == TradeStatus.OPEN) "Open"
//                else formatCurrency(trade.pnl),
//                style = MaterialTheme.typography.titleSmall,
//                color = pnlColor
//            )
//            Text(
//                text = trade.status.name.lowercase()
//                    .replaceFirstChar { it.uppercase() },
//                style = MaterialTheme.typography.labelSmall,
//                color = pnlColor.copy(alpha = 0.7f)
//            )
//        }
//    }
//}
