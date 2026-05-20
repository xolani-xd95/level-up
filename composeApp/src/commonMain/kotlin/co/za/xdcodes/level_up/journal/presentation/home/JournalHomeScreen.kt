package co.za.xdcodes.level_up.journal.presentation.home

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.CircularProgressIndicator
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
import co.za.xdcodes.level_up.common.formatCurrency
import co.za.xdcodes.level_up.common.toMonthName
import co.za.xdcodes.level_up.journal.data.model.MonthlyTradingTarget
import co.za.xdcodes.level_up.journal.domain.model.DailyPoint
import co.za.xdcodes.level_up.journal.domain.model.TradeDirection
import co.za.xdcodes.level_up.journal.domain.model.TradeEntry
import co.za.xdcodes.level_up.journal.domain.model.TradeStatus
import co.za.xdcodes.level_up.journal.domain.model.TradingConfig
import co.za.xdcodes.level_up.theme.LevelUpTheme
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import kotlinx.datetime.plus
import kotlinx.datetime.todayIn
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.annotation.KoinExperimentalAPI


@OptIn(KoinExperimentalAPI::class)
@Composable
fun JournalHomeScreenRoute(
    viewModel: JournalHomeViewModel = koinViewModel(),
    onNavigate: (JournalHomeNavigationEvent) -> Unit
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.navigationEvent.collect { event ->
            onNavigate(event)
        }
    }

    JournalHomeScreen(
        state = state,
        onAction = viewModel::onAction
    )
}

@Composable
fun JournalHomeScreen(
    state: JournalHomeState,
    onAction: (JournalHomeActions) -> Unit
) {
    Box(modifier = Modifier.fillMaxSize()) {

        Column(modifier = Modifier.fillMaxSize()) {

            // ── Month navigation ─────────────────────────────────
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(onClick = { onAction(JournalHomeActions.PreviousMonth) }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Default.KeyboardArrowLeft,
                        contentDescription = "Previous month",
                        tint = Color.White
                    )
                }
                Text(
                    text = if (state.selectedMonthIndex > 0)
                        "${state.selectedMonthIndex.toMonthName()} ${state.selectedYear}"
                    else "",
                    style = MaterialTheme.typography.titleLarge,
                    color = Color.White
                )
                IconButton(onClick = { onAction(JournalHomeActions.NextMonth) }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Default.KeyboardArrowRight,
                        contentDescription = "Next month",
                        tint = Color.White
                    )
                }
            }

            // ── Content ──────────────────────────────────────────
            when {
                state.isLoading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = Color.White)
                    }
                }

                state.isFutureMonth -> {
                    FutureMonthContent()
                }

                !state.isConfigured -> {
                    NotConfiguredContent(
                        monthIndex = state.selectedMonthIndex,
                        year = state.selectedYear,
                        hasGlobalConfig = state.config != null,
                        onSetup = { onAction(JournalHomeActions.OnSetupJournal) }
                    )
                }

                state.isPastMonth -> {
//                    PastMonthContent(
//                        state = state,
//                        onTradeClicked = { onAction(JournalHomeActions.OnTradeClicked(it)) }
//                    )
                }

                else -> {
                    ActiveMonthContent(
                        state = state,
                        onAction = onAction
                    )
                }
            }
        }
    }
}


@Composable
fun ActiveMonthContent(
    state: JournalHomeState,
    onAction: (JournalHomeActions) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 8.dp),
        contentPadding = PaddingValues(bottom = 100.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {

        // ── Monthly progress ─────────────────────────────────────
        item { MonthlyPnlSection(state = state) }

        item {
            MonthlyDisciplineSection(state = state)
        }

        item {
            DaysOfMonthGrid(
                state = state,
                onDayClicked = { dateStr ->
                    val day = state.weekDays.find { it.date == dateStr }
                    day?.let { onAction(JournalHomeActions.OnDayClicked(it)) }
                }
            )
        }

        item { PerformanceSection(state = state) }
    }
}

@Composable
fun MonthlyDisciplineSection(
    state: JournalHomeState
) {
    Column(
        modifier = Modifier.clip(RoundedCornerShape(8.dp))
            .background(Color.White.copy(alpha = 0.05f))
            .padding(vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
//        // ── Header ───────────────────────────────────────────────
        Text(
            text = "Discipline",
            style = MaterialTheme.typography.labelLarge,
            color = Color(0x99FFFFFF)
        )

        // ── Hero numbers ─────────────────────────────────────────
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            // streak
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = if (state.currentStreak > 0) "🔥 ${state.currentStreak}"
                    else "──",
                    style = MaterialTheme.typography.displaySmall,
                    color = if (state.currentStreak > 0) Color(0xFFFFA500)
                    else Color(0x44FFFFFF)
                )
                Text(
                    text = "day streak",
                    style = MaterialTheme.typography.labelMedium,
                    color = Color(0x99FFFFFF)
                )
            }

            // divider
            Box(
                modifier = Modifier
                    .width(1.dp)
                    .height(56.dp)
                    .background(Color(0x1AFFFFFF))
            )

            // points
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(buildAnnotatedString {
                    withStyle(style = SpanStyle(fontSize = 36.sp, color = Color.White)) {
                        append("${state.monthlyPoints}")
                    }
                    withStyle(style = SpanStyle(fontSize = 16.sp, color = Color.White)) {
                        append(" / 20")
                    }
                })
                Text(
                    text = "pts",
                    style = MaterialTheme.typography.labelMedium,
                    color = Color(0x99FFFFFF)
                )
            }
        }
    }
}

@Composable
fun DaysOfMonthGrid(
    state: JournalHomeState,
    onDayClicked: (String) -> Unit
) {
    val today = Clock.System.todayIn(TimeZone.currentSystemDefault())

    val firstDayOfMonth = LocalDate(
        state.selectedYear,
        state.selectedMonthIndex,
        1
    )
    val firstMonday = firstDayOfMonth.minus(
        firstDayOfMonth.dayOfWeek.ordinal,
        DateTimeUnit.DAY
    )

    val weeks = (0..4).map { weekOffset ->
        (0..4).map { dayOffset ->
            firstMonday.plus(
                (weekOffset * 7 + dayOffset),
                DateTimeUnit.DAY
            )
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            listOf("Mon", "Tue", "Wed", "Thu", "Fri").forEach { label ->
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelLarge,
                    color = Color(0x44FFFFFF),
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center
                )
            }
        }

        weeks.forEach { week ->
            val hasMonthDays = week.any {
                it.monthNumber == state.selectedMonthIndex &&
                        it.year == state.selectedYear
            }
            if (!hasMonthDays) return@forEach

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                week.forEach { date ->
                    val isInMonth = date.monthNumber == state.selectedMonthIndex &&
                            date.year == state.selectedYear
                    val dateStr = date.toString()
                    val point = state.dailyPoints.find { it.date == dateStr }
                    val hasTrades = state.allMonthTrades.any { it.date == dateStr }
                    val dayTrades = state.allMonthTrades.filter { it.date == dateStr }
                    val isFuture = date > today
                    val isToday = date == today
                    val pnl = dayTrades
                        .filter { it.status != TradeStatus.OPEN }
                        .sumOf { it.pnl }

                    DaySummary(
                        isInMonth = isInMonth,
                        isFuture = isFuture,
                        isToday = isToday,
                        pointEarned = point?.earned,
                        hasTrades = hasTrades,
                        pnl = pnl,
                        noOfTrades = dayTrades.size,
                        maxTrades = state.config?.maxTradesPerDay ?: 4,
                        onClick = {
                            if (isInMonth && !isFuture) onDayClicked(dateStr)
                        },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@Composable
fun PerformanceSection(state: JournalHomeState) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // ── Title ────────────────────────────────────────────────
        Text(
            text = "Performance",
            style = MaterialTheme.typography.labelLarge,
            color = Color(0x99FFFFFF)
        )

        // ── Grid ─────────────────────────────────────────────────
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Row 1
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                StatCard(
                    label = "Win Rate",
                    value = "${state.winRate.toInt()}%",
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    label = "Total Trades",
                    value = "${state.totalTrades}",
                    modifier = Modifier.weight(1f)
                )
            }

            // Row 2
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                StatCard(
                    label = "Best Day",
                    value = "+${formatCurrency(state.bestDay)}",
                    valueColor = Color(0xFF00C853),
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    label = "Worst Day",
                    value = formatCurrency(state.worstDay),
                    valueColor = Color(0xFFFF2400),
                    modifier = Modifier.weight(1f)
                )
            }

            // Row 3
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                StatCard(
                    label = "Avg Daily",
                    value = if (state.avgDailyPnl >= 0)
                        "+${formatCurrency(state.avgDailyPnl)}"
                    else formatCurrency(state.avgDailyPnl),
                    valueColor = if (state.avgDailyPnl >= 0) Color(0xFF00C853)
                    else Color(0xFFFF2400),
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    label = "Profit Factor",
                    value = "${(state.profitFactor)}x",
                    valueColor = if (state.profitFactor >= 1.0) Color(0xFF00C853)
                    else Color(0xFFFF2400),
                    modifier = Modifier.weight(1f)
                )
            }

            // Row 4
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                StatCard(
                    label = "Biggest Win",
                    value = "+${formatCurrency(state.biggestWin)}",
                    valueColor = Color(0xFF00C853),
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    label = "Biggest Loss",
                    value = formatCurrency(state.biggestLoss),
                    valueColor = Color(0xFFFF2400),
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
fun StatCard(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
    valueColor: Color = Color.White
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(Color.White.copy(alpha = 0.05f))
            .padding(horizontal = 16.dp, vertical = 12.dp),
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
            color = valueColor
        )
    }
}

@Composable
fun DaySummary(
    isInMonth: Boolean,
    isFuture: Boolean,
    isToday: Boolean,
    pointEarned: Boolean?,
    hasTrades: Boolean,
    pnl: Double,
    noOfTrades: Int,
    maxTrades: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val bgColor = when {
        !isInMonth -> Color.Transparent
        isToday -> Color.White.copy(alpha = 0.15f)
        pointEarned == true -> Color(0xFF00C853).copy(alpha = 0.15f)
        pointEarned == false -> Color(0xFFFF2400).copy(alpha = 0.15f)
        hasTrades -> Color(0xFFFFA500).copy(alpha = 0.15f)
        isFuture -> Color.White.copy(alpha = 0.03f)
        else -> Color.White.copy(alpha = 0.05f)
    }

    Column(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(bgColor)
            .clickable(
                enabled = isInMonth && !isFuture,
                onClick = onClick
            )
            .padding(vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        if (!isInMonth) {
            Spacer(modifier = Modifier.height(32.dp))
        } else if (isFuture && !isToday) {
            Text(
                text = "─",
                style = MaterialTheme.typography.labelSmall,
                color = Color(0x44FFFFFF),
                textAlign = TextAlign.Center
            )
            Text(
                text = "─",
                style = MaterialTheme.typography.labelSmall,
                color = Color(0x44FFFFFF),
                textAlign = TextAlign.Center
            )
        } else {
            Text(
                text = if (pnl >= 0) "+${formatCurrency(pnl)}"
                else formatCurrency(pnl),
                style = MaterialTheme.typography.labelSmall,
                color = Color.White,
                textAlign = TextAlign.Center,
                maxLines = 1
            )
            Text(
                text = "$noOfTrades/$maxTrades",
                style = MaterialTheme.typography.labelSmall,
                color = Color.White.copy(alpha = 0.7f),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun MonthlyPnlSection(state: JournalHomeState) {
    val progressColor = when {
        state.monthlyProgress >= 1f -> Color(0xFF00C853)
        state.monthlyProgress >= 0.75f -> Color(0xFFFFA500)
        else -> Color.White
    }

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = "Monthly P&L",
                style = MaterialTheme.typography.labelLarge,
                color = Color(0x99FFFFFF)
            )
            Text(
                buildAnnotatedString {
                    withStyle(
                        SpanStyle(
                            fontSize = 36.sp,
                            color = progressColor
                        )
                    ) {
                        append(formatCurrency(state.monthlyPnl))
                    }
                    withStyle(
                        SpanStyle(
                            fontSize = 16.sp,
                            color = Color(0x99FFFFFF)
                        )
                    ) {
                        append(
                            " / ${formatCurrency(state.monthlyTarget?.monthlyTarget ?: 0.0)}"
                        )
                    }
                },
                textAlign = TextAlign.Center
            )
        }

        LinearProgressIndicator(
            progress = { state.monthlyProgress },
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(RoundedCornerShape(999.dp)),
            color = progressColor,
            trackColor = Color(0x33FFFFFF)
        )
    }
}

@Preview
@Composable
fun JournalHomeScreenPreview() {
    LevelUpTheme {
        JournalHomeScreen(
            state = JournalHomeState(
                currentMonthIndex = 5,
                currentYear = 2026,
                selectedMonthIndex = 5,
                selectedYear = 2026,
                isConfigured = true,
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
                dailyPoints = listOf(
                    DailyPoint(
                        date = "2026-05-12",
                        earned = false,
                        reason = "Target hit"
                    ),
                    DailyPoint(
                        date = "2026-05-13",
                        earned = true,
                        reason = "Target hit"
                    ),
                    DailyPoint(
                        date = "2026-05-14",
                        earned = true,
                        reason = "Override after target"
                    )
                ),
                allMonthTrades = listOf(
                    TradeEntry(
                        date = "2026-05-12",
                        direction = TradeDirection.LONG,
                        positionSize = 0.5,
                        entryPrice = 2310.00,
                        stopLoss = 2305.00,
                        takeProfit = 2320.00,
                        exitPrice = 2320.00,
                        pnl = 800.0,
                        status = TradeStatus.WIN,
                        notes = "Monday trade"
                    ),
                    TradeEntry(
                        date = "2026-05-13",
                        direction = TradeDirection.SHORT,
                        positionSize = 0.3,
                        entryPrice = 2325.00,
                        stopLoss = 2330.00,
                        takeProfit = 2310.00,
                        exitPrice = 2310.00,
                        pnl = 1200.0,
                        status = TradeStatus.WIN,
                        notes = "Tuesday trade"
                    ),
                    TradeEntry(
                        date = "2026-05-14",
                        direction = TradeDirection.LONG,
                        positionSize = 0.5,
                        entryPrice = 2320.50,
                        stopLoss = 2315.00,
                        takeProfit = 2331.00,
                        exitPrice = 2331.00,
                        pnl = 450.0,
                        status = TradeStatus.WIN,
                        notes = "Clean breakout setup"
                    ),
                    TradeEntry(
                        date = "2026-05-15",
                        direction = TradeDirection.SHORT,
                        positionSize = 0.3,
                        entryPrice = 2335.00,
                        stopLoss = 2340.00,
                        takeProfit = 2320.00,
                        exitPrice = 2340.00,
                        pnl = -150.0,
                        status = TradeStatus.LOSS,
                        notes = "Stopped out"
                    )
                )
            ),
            onAction = {}
        )
    }
}

