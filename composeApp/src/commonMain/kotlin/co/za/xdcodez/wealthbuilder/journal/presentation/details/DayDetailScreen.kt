package co.za.xdcodez.wealthbuilder.journal.presentation.details

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import co.za.xdcodez.wealthbuilder.common.formatCurrency
import co.za.xdcodez.wealthbuilder.journal.data.model.MonthlyTradingTarget
import co.za.xdcodez.wealthbuilder.journal.domain.model.StopReason
import co.za.xdcodez.wealthbuilder.journal.domain.model.TradeDirection
import co.za.xdcodez.wealthbuilder.journal.domain.model.TradeEntry
import co.za.xdcodez.wealthbuilder.journal.domain.model.TradeStatus
import co.za.xdcodez.wealthbuilder.journal.domain.model.TradingConfig
import co.za.xdcodez.wealthbuilder.theme.WealthBuilderTheme
import kotlinx.datetime.LocalDate
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.annotation.KoinExperimentalAPI

@OptIn(KoinExperimentalAPI::class)
@Composable
fun DayDetailScreenRoute(
    date: String,
    monthIndex: Int,
    year: Int,
    viewModel: DayDetailViewModel = koinViewModel(),
    onNavigate: (DayDetailNavigationEvent) -> Unit
) {
    LaunchedEffect(date) {
        viewModel.init(date, monthIndex, year)
    }

    LaunchedEffect(Unit) {
        viewModel.navigationEvent.collect { onNavigate(it) }
    }

    val state by viewModel.state.collectAsState()

    DayDetailScreen(
        state = state,
        onAction = viewModel::onAction
    )
}

@Composable
fun DayDetailScreen(
    state: DayDetailState,
    onAction: (DayDetailActions) -> Unit
) {
    Box(modifier = Modifier.fillMaxSize()) {

        Column(modifier = Modifier.fillMaxSize()) {

            // ── Top bar ──────────────────────────────────────────
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(
                    onClick = { onAction(DayDetailActions.NavigateBack) }
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White
                    )
                }
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = formatDayTitle(state.date),
                        style = MaterialTheme.typography.titleLarge,
                        color = Color.White
                    )
                    Text(
                        text = formatDaySubTitle(state.date),
                        style = MaterialTheme.typography.titleSmall,
                        color = Color(0x99FFFFFF)
                    )
                }

                Spacer(modifier = Modifier.size(44.dp))
            }

            when {
                state.isLoading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = Color.White)
                    }
                }

                else -> {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp),
                        contentPadding = PaddingValues(bottom = 100.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {

                        // ── Daily summary ────────────────────────
                        item { DailySummarySection(state = state) }

                        // ── Stop banner ──────────────────────────
//                        if (state.isDoneForToday && state.isToday) {
                        item { StopBanner(stopReason = state.stopReason) }
//                        }
                        // ── Stop conditions grid ─────────────────

                        item { DayStopConditionsGrid(state = state) }


                        // ── Trades ───────────────────────────────
                        // ── Morning session ──────────────────────
                        if (state.morningTrades.isNotEmpty()) {
                            item {
                                SessionHeader(
                                    title = "Morning Session",
                                    time = "09:00 - 12:00",
                                    pnl = state.morningPnl,
                                    tradeCount = state.morningTrades.size
                                )
                            }
                            items(state.morningTrades) { trade ->
                                TradeCardDetail(
                                    trade = trade,
                                    onClick = {
                                        onAction(DayDetailActions.OnTradeClicked(trade))
                                    }
                                )
                            }
                        }

                        // ── Afternoon session ────────────────────
                        if (state.afternoonTrades.isNotEmpty()) {
                            item {
                                SessionHeader(
                                    title = "Afternoon Session",
                                    time = "15:00 - 16:30",
                                    pnl = state.afternoonPnl,
                                    tradeCount = state.afternoonTrades.size
                                )
                            }
                            items(state.afternoonTrades) { trade ->
                                TradeCardDetail(
                                    trade = trade,
                                    onClick = {
                                        onAction(DayDetailActions.OnTradeClicked(trade))
                                    }
                                )
                            }
                        }
                        if (state.trades.isEmpty()) {
                            item {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 32.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = if (state.isToday) "No trades yet today"
                                        else "No trades on this day",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = Color(0x44FFFFFF)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // ── FAB — today only, not done ───────────────────────────
        if (state.isToday && !state.isDoneForToday && !state.isLoading) {
            FloatingActionButton(
                onClick = { onAction(DayDetailActions.OnAddTrade) },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(24.dp),
                containerColor = Color.White,
                contentColor = Color.Black
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Trade")
            }
        }
    }
}

@Composable
fun SessionHeader(
    title: String,
    time: String,
    pnl: Double,
    tradeCount: Int
) {
    val pnlColor = when {
        pnl > 0 -> Color(0xFF00C853)
        pnl < 0 -> Color(0xFFFF2400)
        else -> Color(0x99FFFFFF)
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelLarge,
                color = Color(0x99FFFFFF)
            )
            Text(
                text = "$time  •  $tradeCount ${if (tradeCount == 1) "trade" else "trades"}",
                style = MaterialTheme.typography.labelSmall,
                color = Color(0x44FFFFFF)
            )
        }

        Text(
            text = if (pnl >= 0) "+${formatCurrency(pnl)}"
            else formatCurrency(pnl),
            style = MaterialTheme.typography.titleSmall,
            color = pnlColor
        )
    }
}

@Composable
fun TradeCardDetail(
    trade: TradeEntry,
    onClick: () -> Unit
) {
    val (statusColor, statusLabel) = when (trade.status) {
        TradeStatus.WIN -> Color(0xFF00C853) to "Win"
        TradeStatus.LOSS -> Color(0xFFFF2400) to "Loss"
        TradeStatus.OPEN -> Color(0xFFFFA500) to "Open"
        TradeStatus.BREAKEVEN -> Color(0x99FFFFFF) to "B/E"
    }

    val directionIcon = if (trade.direction == TradeDirection.LONG) "↑" else "↓"
    val directionColor = if (trade.direction == TradeDirection.LONG)
        Color(0xFF00C853) else Color(0xFFFF2400)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color.White.copy(alpha = 0.05f))
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // ── Left ─────────────────────────────────────────────────
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = directionIcon,
                fontSize = 20.sp,
                color = directionColor
            )

            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(
                    text = "GOLD  ${trade.positionSize} lots",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White
                )
                // show times if available
                if (trade.openTime.isNotEmpty() && trade.closeTime.isNotEmpty()) {
                    Text(
                        text = "${trade.openTime} - ${trade.closeTime}",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color(0x99FFFFFF)
                    )
                }
                Text(
                    text = "@ ${trade.entryPrice}  •  RR ${(trade.riskRewardRatio)}",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color(0x44FFFFFF)
                )
            }
        }

        // ── Right ────────────────────────────────────────────────
        Column(
            horizontalAlignment = Alignment.End,
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Text(
                text = when (trade.status) {
                    TradeStatus.OPEN -> "Open"
                    else -> if (trade.pnl >= 0) "+${formatCurrency(trade.pnl)}"
                    else formatCurrency(trade.pnl)
                },
                style = MaterialTheme.typography.titleSmall,
                color = statusColor
            )
            Text(
                text = statusLabel,
                style = MaterialTheme.typography.labelSmall,
                color = statusColor.copy(alpha = 0.7f)
            )
        }
    }
}

@Composable
fun DailySummarySection(state: DayDetailState) {
    val progressColor = when {
        state.isTargetHit -> Color(0xFF00C853)
        state.todayProgress >= 0.75f -> Color(0xFFFFA500)
        state.isLossLimitHit -> Color(0xFFFF2400)
        else -> Color.White
    }

    Column(
        modifier = Modifier.fillMaxWidth().padding(top = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = "Day P&L",
            style = MaterialTheme.typography.labelLarge,
            color = Color(0x99FFFFFF)
        )

        Text(
            buildAnnotatedString {
                withStyle(SpanStyle(fontSize = 36.sp, color = progressColor)) {
                    append(formatCurrency(state.todayPnl))
                }
                withStyle(SpanStyle(fontSize = 16.sp, color = Color(0x99FFFFFF))) {
                    append(" / ${formatCurrency(state.dailyTarget)}")
                }
            },
            textAlign = TextAlign.Center
        )

        LinearProgressIndicator(
            progress = { state.todayProgress },
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(RoundedCornerShape(999.dp)),
            color = progressColor,
            trackColor = Color(0x33FFFFFF)
        )
    }
}

@Composable
fun StopConditionCard(
    label: String,
    value: String,
    isWarning: Boolean,
    isHit: Boolean,
    modifier: Modifier = Modifier
) {
    val color = when {
        isHit && label == "Loss" -> Color(0xFFFF2400)  // loss hit → red
        isHit -> Color(0xFF00C853)  // target/trades hit → green
        isWarning -> Color(0xFFFFA500)  // approaching → amber
        else -> Color(0x99FFFFFF)  // safe → muted
    }

    Column(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(Color.White.copy(alpha = 0.05f))
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = Color(0x99FFFFFF)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium,
            color = color,
            textAlign = TextAlign.Center,
            maxLines = 2
        )
    }
}

@Composable
fun DayStopConditionsGrid(state: DayDetailState) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        StopConditionCard(
            label = "Trades",
            value = "${state.tradesUsed} / ${state.config?.maxTradesPerDay ?: 4}",
            isWarning = state.tradesUsed >= (state.config?.maxTradesPerDay ?: 4) - 1,
            isHit = state.isMaxTradesHit,
            modifier = Modifier.weight(1f)
        )
        StopConditionCard(
            label = "Won",
            value = formatCurrency(state.todayProfit),
            isWarning = false,
            isHit = state.isTargetHit,
            modifier = Modifier.weight(1f)
        )
        StopConditionCard(
            label = "Lost",
            value = formatCurrency((state.todayLoss)),
            isWarning = (state.todayLoss) >= state.lossLimitAmount * 0.75,
            isHit = state.isLossLimitHit,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
fun StopBanner(stopReason: StopReason?) {
    val (icon, message, color) = when (stopReason) {
        StopReason.TARGET_HIT -> Triple(
            "🎯",
            "Daily target reached! You're done for today.",
            Color(0xFF00C853)
        )

        StopReason.LOSS_LIMIT_HIT -> Triple(
            "⛔",
            "Loss limit reached. Protect your capital.",
            Color(0xFFFF2400)
        )

        StopReason.MAX_TRADES_HIT -> Triple(
            "🔄",
            "Max trades reached for today.",
            Color(0xFFFFA500)
        )

        null -> return
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(color.copy(alpha = 0.15f))
            .padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = icon, fontSize = 24.sp)
        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            color = color
        )
    }
}

fun formatDayTitle(date: String): String {
    return try {
        val localDate = LocalDate.parse(date)
        val day = localDate.dayOfWeek.name
            .lowercase()
            .replaceFirstChar { it.uppercase() }
        day
    } catch (e: Exception) {
        date
    }
}

fun formatDaySubTitle(date: String): String {
    return try {
        val localDate = LocalDate.parse(date)
        val dayOfMonth = localDate.dayOfMonth
        val month = localDate.month.name
            .lowercase()
            .replaceFirstChar { it.uppercase() }
        val year = localDate.year
        "$dayOfMonth $month $year"
    } catch (e: Exception) {
        date
    }
}

@Preview
@Composable
fun DayDetailScreenPreview() {
    WealthBuilderTheme {
        DayDetailScreen(
            state = DayDetailState(
                date = "2026-05-14",
                isToday = true,
                isLoading = false,
                config = TradingConfig(
                    maxTradesPerDay = 4,
                    lossLimitPercent = 0.5,
                    sessionOneStart = "09:00",
                    sessionOneEnd = "12:00",
                    sessionTwoStart = "15:00",
                    sessionTwoEnd = "16:30",
                    isConfigured = true
                ),
                monthlyTarget = MonthlyTradingTarget(
                    monthId = "2026-05",
                    monthlyTarget = 20000.0
                ),
                monthlyPnlSoFar = 8450.0,
                trades = listOf(
                    // ── Morning trades ───────────────────────────
                    TradeEntry(
                        id = "1",
                        date = "2026-05-14",
                        direction = TradeDirection.LONG,
                        positionSize = 0.5,
                        entryPrice = 2320.50,
                        exitPrice = 2331.00,
                        pnl = 450.0,
                        status = TradeStatus.WIN,
                        openTime = "09:45",
                        closeTime = "11:20",
                        session = "MORNING",
                        notes = "Clean breakout setup"
                    ),
                    TradeEntry(
                        id = "2",
                        date = "2026-05-14",
                        direction = TradeDirection.SHORT,
                        positionSize = 0.3,
                        entryPrice = 2335.00,
                        exitPrice = 2340.00,
                        pnl = -150.0,
                        status = TradeStatus.LOSS,
                        openTime = "10:15",
                        closeTime = "10:45",
                        session = "MORNING",
                        notes = "Stopped out, wrong direction"
                    ),
                    // ── Afternoon trades ─────────────────────────
                    TradeEntry(
                        id = "3",
                        date = "2026-05-14",
                        direction = TradeDirection.LONG,
                        positionSize = 0.3,
                        entryPrice = 2318.00,
                        exitPrice = 2328.00,
                        pnl = 300.0,
                        status = TradeStatus.WIN,
                        openTime = "15:10",
                        closeTime = "15:55",
                        session = "AFTERNOON",
                        notes = "Good RR setup"
                    ),
                    TradeEntry(
                        id = "4",
                        date = "2026-05-14",
                        direction = TradeDirection.SHORT,
                        positionSize = 0.2,
                        entryPrice = 2325.00,
                        exitPrice = 2322.00,
                        pnl = 120.0,
                        status = TradeStatus.WIN,
                        openTime = "16:00",
                        closeTime = "16:25",
                        session = "AFTERNOON",
                        notes = ""
                    )
                )
            ),
            onAction = {}
        )
    }
}

@Preview
@Composable
fun DayDetailScreenPastDayPreview() {
    WealthBuilderTheme {
        DayDetailScreen(
            state = DayDetailState(
                date = "2026-05-12",
                isToday = false,
                isLoading = false,
                config = TradingConfig(
                    maxTradesPerDay = 4,
                    lossLimitPercent = 0.5,
                    sessionOneStart = "09:00",
                    sessionOneEnd = "12:00",
                    sessionTwoStart = "15:00",
                    sessionTwoEnd = "16:30",
                    isConfigured = true
                ),
                monthlyTarget = MonthlyTradingTarget(
                    monthId = "2026-05",
                    monthlyTarget = 20000.0
                ),
                monthlyPnlSoFar = 6200.0,
                trades = listOf(
                    TradeEntry(
                        id = "5",
                        date = "2026-05-12",
                        direction = TradeDirection.LONG,
                        positionSize = 0.5,
                        entryPrice = 2310.00,
                        exitPrice = 2320.00,
                        pnl = 800.0,
                        status = TradeStatus.WIN,
                        openTime = "09:30",
                        closeTime = "10:45",
                        session = "MORNING",
                        notes = "Monday breakout"
                    ),
                    TradeEntry(
                        id = "6",
                        date = "2026-05-12",
                        direction = TradeDirection.SHORT,
                        positionSize = 0.3,
                        entryPrice = 2325.00,
                        exitPrice = 2315.00,
                        pnl = 400.0,
                        status = TradeStatus.WIN,
                        openTime = "11:00",
                        closeTime = "11:50",
                        session = "MORNING",
                        notes = "Reversal at resistance"
                    ),
                    TradeEntry(
                        id = "7",
                        date = "2026-05-12",
                        direction = TradeDirection.LONG,
                        positionSize = 0.2,
                        entryPrice = 2318.00,
                        exitPrice = 2313.00,
                        pnl = -250.0,
                        status = TradeStatus.LOSS,
                        openTime = "15:05",
                        closeTime = "15:30",
                        session = "AFTERNOON",
                        notes = "Bad entry, chased price"
                    )
                )
            ),
            onAction = {}
        )
    }
}

@Preview
@Composable
fun DayDetailScreenEmptyPreview() {
    WealthBuilderTheme {
        DayDetailScreen(
            state = DayDetailState(
                date = "2026-05-14",
                isToday = true,
                isLoading = false,
                config = TradingConfig(
                    maxTradesPerDay = 4,
                    lossLimitPercent = 0.5,
                    sessionOneStart = "09:00",
                    sessionOneEnd = "12:00",
                    sessionTwoStart = "15:00",
                    sessionTwoEnd = "16:30",
                    isConfigured = true
                ),
                monthlyTarget = MonthlyTradingTarget(
                    monthId = "2026-05",
                    monthlyTarget = 20000.0
                ),
                monthlyPnlSoFar = 8450.0,
                trades = emptyList()
            ),
            onAction = {}
        )
    }
}

@Preview
@Composable
fun DayDetailScreenStopBannerPreview() {
    WealthBuilderTheme {
        DayDetailScreen(
            state = DayDetailState(
                date = "2026-05-14",
                isToday = true,
                isLoading = false,
                config = TradingConfig(
                    maxTradesPerDay = 4,
                    lossLimitPercent = 0.5,
                    sessionOneStart = "09:00",
                    sessionOneEnd = "12:00",
                    sessionTwoStart = "15:00",
                    sessionTwoEnd = "16:30",
                    isConfigured = true
                ),
                monthlyTarget = MonthlyTradingTarget(
                    monthId = "2026-05",
                    monthlyTarget = 20000.0
                ),
                monthlyPnlSoFar = 8450.0,
                trades = listOf(
                    TradeEntry(
                        id = "8",
                        date = "2026-05-14",
                        direction = TradeDirection.LONG,
                        positionSize = 0.5,
                        entryPrice = 2320.50,
                        exitPrice = 2331.00,
                        pnl = 1050.0,  // ← over daily target → stop banner shows
                        status = TradeStatus.WIN,
                        openTime = "09:45",
                        closeTime = "11:20",
                        session = "MORNING",
                        notes = "Hit target"
                    )
                )
            ),
            onAction = {}
        )
    }
}

//@Preview
//@Composable
//fun DayDetailScreenPastDayPreview() {
//    LevelUpTheme {
//        DayDetailScreen(
//            state = DayDetailState(
//                date = "2026-05-12",
//                isToday = false,
//                isLoading = false,
//                config = TradingConfig(
//                    maxTradesPerDay = 4,
//                    lossLimitPercent = 0.5,
//                    sessionOneStart = "09:00",
//                    sessionOneEnd = "12:00",
//                    sessionTwoStart = "15:00",
//                    sessionTwoEnd = "16:30",
//                    isConfigured = true
//                ),
//                monthlyTarget = MonthlyTradingTarget(
//                    monthId = "2026-05",
//                    monthlyTarget = 20000.0
//                ),
//                monthlyPnlSoFar = 6200.0,
//                trades = listOf(
//                    TradeEntry(
//                        id = "4",
//                        date = "2026-05-12",
//                        direction = TradeDirection.LONG,
//                        positionSize = 0.5,
//                        entryPrice = 2310.00,
//                        stopLoss = 2305.00,
//                        takeProfit = 2320.00,
//                        exitPrice = 2320.00,
//                        pnl = 800.0,
//                        status = TradeStatus.WIN,
//                        notes = "Monday breakout"
//                    ),
//                    TradeEntry(
//                        id = "5",
//                        date = "2026-05-12",
//                        direction = TradeDirection.SHORT,
//                        positionSize = 0.3,
//                        entryPrice = 2325.00,
//                        stopLoss = 2328.00,
//                        takeProfit = 2315.00,
//                        exitPrice = 2315.00,
//                        pnl = 400.0,
//                        status = TradeStatus.WIN,
//                        notes = "Reversal at resistance"
//                    )
//                )
//            ),
//            onAction = {}
//        )
//    }
//}

//@Preview
//@Composable
//fun DayDetailScreenEmptyPreview() {
//    LevelUpTheme {
//        DayDetailScreen(
//            state = DayDetailState(
//                date = "2026-05-14",
//                isToday = true,
//                isLoading = false,
//                config = TradingConfig(
//                    maxTradesPerDay = 4,
//                    lossLimitPercent = 0.5,
//                    sessionOneStart = "09:00",
//                    sessionOneEnd = "12:00",
//                    sessionTwoStart = "15:00",
//                    sessionTwoEnd = "16:30",
//                    isConfigured = true
//                ),
//                monthlyTarget = MonthlyTradingTarget(
//                    monthId = "2026-05",
//                    monthlyTarget = 20000.0
//                ),
//                monthlyPnlSoFar = 8450.0,
//                trades = emptyList()
//            ),
//            onAction = {}
//        )
//    }
//}