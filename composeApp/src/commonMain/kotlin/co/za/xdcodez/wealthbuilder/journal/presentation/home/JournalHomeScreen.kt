package co.za.xdcodez.wealthbuilder.journal.presentation.home

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import co.za.xdcodez.wealthbuilder.common.formatCurrency
import co.za.xdcodez.wealthbuilder.common.getRemainingMessage
import co.za.xdcodez.wealthbuilder.common.toMonthName
import co.za.xdcodez.wealthbuilder.habits.domain.getDisciplineMessage
import co.za.xdcodez.wealthbuilder.journal.data.model.MonthlyTradingTarget
import co.za.xdcodez.wealthbuilder.journal.domain.model.TradeDirection
import co.za.xdcodez.wealthbuilder.journal.domain.model.TradeEntry
import co.za.xdcodez.wealthbuilder.journal.domain.model.TradeStatus
import co.za.xdcodez.wealthbuilder.journal.domain.model.TradingConfig
import co.za.xdcodez.wealthbuilder.theme.WealthBuilderTheme
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
            ActiveMonthContent(
                state = state,
                onAction = onAction
            )
        }
    }
}


@Composable
fun ActiveMonthContent(
    state: JournalHomeState,
    onAction: (JournalHomeActions) -> Unit
) {

    val remaining = (state.target - state.todaysProfit).coerceAtLeast(0.0)

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 8.dp),
        contentPadding = PaddingValues(bottom = 100.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        item {
            Row(
                modifier = Modifier.fillMaxWidth()
                    .padding(bottom = 12.dp),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier.padding(top = 16.dp),
                    horizontalAlignment = CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                ) {
                    Text(
                        formatCurrency(state.dayStart),
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        "day's start",
                        color = Color.White.copy(alpha = 0.5f),
                        style = MaterialTheme.typography.labelSmall
                    )
                }

                GaugeArcProgress(
                    current = state.todaysProfit,
                    target = state.target
                )

                Column(
                    modifier = Modifier.padding(top = 16.dp),
                    horizontalAlignment = CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                ) {
                    Text(
                        formatCurrency(state.accountBalance?.balance ?: state.liveBalance),
                        color = Color(0xFF00C853),
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        "live balance",
                        color = Color.White.copy(alpha = 0.5f),
                        style = MaterialTheme.typography.labelSmall
                    )
                }

            }
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .border(width = 1.dp, color = Color(0xFF00C853), shape = RoundedCornerShape(8.dp))
                    .background(Color(0xFF00C853).copy(alpha = 0.15f))
                    .padding(vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = getRemainingMessage(current = state.todaysProfit, target = state.target),
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White.copy(alpha = 0.8f)
                )
            }
        }

        item {
            Column {

                // ── Month navigation ─────────────────────────────────
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
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
                        style = MaterialTheme.typography.titleMedium,
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
                DaysOfMonthGrid(
                    state = state,
                    onDayClicked = { dateStr ->
                        val day = state.weekDays.find { it.date == dateStr }
                        day?.let { onAction(JournalHomeActions.OnDayClicked(it)) }
                    }
                )
            }
        }

        item { PerformanceSection(state = state) }
    }
}

@Composable
fun GaugeArcProgress(
    current: Double,
    target: Double,
    modifier: Modifier = Modifier,
    size: Dp = 130.dp,
    strokeWidth: Dp = 8.dp,
    startAngle: Float = 150f,
    sweepAngle: Float = 245f,
) {
    val progress =
        if (target <= 0.0) 0f else (current.toFloat() / target.toFloat()).coerceIn(0f, 1f)

    Box(
        modifier = modifier.size(130.dp, 100.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val stroke = Stroke(width = strokeWidth.toPx(), cap = StrokeCap.Round)
            val arcSize = Size(
                width = size.toPx() - strokeWidth.toPx(),
                height = size.toPx() - strokeWidth.toPx()
            )
            val topLeft = Offset(strokeWidth.toPx() / 2, strokeWidth.toPx() / 2)

            // Track (full gauge range, dim)
            drawArc(
                color = Color.White.copy(alpha = 0.08f),
                startAngle = startAngle,
                sweepAngle = sweepAngle,
                useCenter = false,
                topLeft = topLeft,
                size = arcSize,
                style = stroke
            )

            // Progress (fills proportionally from startAngle)
            drawArc(
                color = Color(0xFF00C853).copy(alpha = 0.5f),
                startAngle = startAngle,
                sweepAngle = sweepAngle * progress,
                useCenter = false,
                topLeft = topLeft,
                size = arcSize,
                style = stroke
            )
        }

        Column(
            horizontalAlignment = CenterHorizontally,
            modifier = Modifier.padding(top = 16.dp)
        ) {
            Text(
                text = formatCurrency(current),
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Text(
                text = "target ${formatCurrency(target)}",
                style = MaterialTheme.typography.labelSmall,
                color = Color.White.copy(alpha = 0.5f)
            )
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
                    val hasTrades = state.allMonthTrades.any { it.date == dateStr }
                    val dayTrades = state.allMonthTrades.filter { it.date == dateStr }
                    val isFuture = date > today
                    val isToday = date == today
                    val pnl = dayTrades
                        .filter { it.status != TradeStatus.OPEN }
                        .sumOf { it.pnl }
                    // Calculate percentage relative to starting balance
                    val startingBalance = state.config?.startingBalance ?: 1.0
                    val pnlPercent = if (startingBalance > 0) (pnl / startingBalance) * 100 else 0.0

                    DaySummary(
                        isInMonth = isInMonth,
                        isFuture = isFuture,
                        isToday = isToday,
                        hasTrades = hasTrades,
                        pnl = pnl,
                        pnlPercent = kotlin.math.abs(pnlPercent),
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
        modifier = Modifier.fillMaxWidth().padding(top = 12.dp),
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

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                StatCard(
                    label = "Estimated Goal Balance",
                    value = formatCurrency(state.estimatedGoalBalance),
                    valueColor = Color(0xFF00C853),
                    modifier = Modifier.weight(1f)
                )

                StatCard(
                    label = "Days Left",
                    value = state.tradingDaysLeft.toString(),
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
            .clip(RoundedCornerShape(8.dp))
            .background(Color.White.copy(alpha = 0.05f))
            .padding(horizontal = 8.dp, vertical = 6.dp)
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
    hasTrades: Boolean,
    pnl: Double,
    pnlPercent: Double,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val bgColor = when {
        !isInMonth -> Color.Transparent
        isFuture && !hasTrades-> Color.White.copy(alpha = 0.03f)
        pnlPercent > 5.0 -> Color(0xFF00C853).copy(alpha = 0.15f)
        pnlPercent < 0.0 -> Color(0xFFFF2400).copy(alpha = 0.15f)
        pnlPercent < 5.0 && pnlPercent > 0.0 -> Color(0xFFFFA500).copy(alpha = 0.15f)
        isToday -> Color.White.copy(alpha = 0.15f)
        else -> Color.White.copy(alpha = 0.05f)
    }

    val rounded = (pnlPercent * 10).toInt() / 10.0
    val percentText = if (pnlPercent >= 0) " ${rounded}%" else "${rounded}%"

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
        verticalArrangement = Arrangement.spacedBy(3.dp)
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
                text = formatCurrency(kotlin.math.abs(pnl)),
                style = MaterialTheme.typography.labelSmall,
                color = Color.White,
                textAlign = TextAlign.Center,
                maxLines = 1
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                if (hasTrades)
                    Icon(
                        imageVector = if (pnlPercent > 0.0) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = if (pnlPercent >= 0) Color(0xFF00C853) else Color(0xFFFF2400),
                    )
                Text(
                    text = if (hasTrades) percentText else "─",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.White,
                    textAlign = TextAlign.Center
                )
            }

        }
    }
}

@Preview
@Composable
fun JournalHomeScreenPreview() {
    WealthBuilderTheme {
        JournalHomeScreen(
            state = JournalHomeState(
                currentMonthIndex = 5,
                currentYear = 2026,
                selectedMonthIndex = 5,
                selectedYear = 2026,
                isConfigured = true,
                config = TradingConfig(
                    startingBalance = 3000.0,
                    cycleStartDate = "2026-05-01",
                    profitCeilingPercent = 0.05,
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

