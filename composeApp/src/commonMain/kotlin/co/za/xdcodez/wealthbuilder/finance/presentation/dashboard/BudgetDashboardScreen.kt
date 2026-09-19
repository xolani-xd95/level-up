package co.za.xdcodez.wealthbuilder.finance.presentation.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
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
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import co.za.xdcodez.wealthbuilder.common.budgetProgressColor
import co.za.xdcodez.wealthbuilder.common.formatCurrency
import co.za.xdcodez.wealthbuilder.common.widgets.CardComposable
import co.za.xdcodez.wealthbuilder.finance.domain.dto.BudgetMonthModel
import co.za.xdcodez.wealthbuilder.finance.domain.dto.Goal
import co.za.xdcodez.wealthbuilder.theme.WealthBuilderTheme
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.annotation.KoinExperimentalAPI

@OptIn(KoinExperimentalAPI::class)
@Composable
fun BudgetDashboardRoute(
    viewModel: BudgetDashboardViewModel = koinViewModel(),
    onNavigateToBudgetDetails: () -> Unit,
    onNavigateToCreateGoal: () -> Unit,
    onNavigateToGoalDetail: (String) -> Unit
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.navigationEvent.collect { event ->
            when (event) {
                BudgetDashboardNavigationEvent.ToBudgetDetails -> onNavigateToBudgetDetails()
                BudgetDashboardNavigationEvent.ToCreateGoal -> onNavigateToCreateGoal()
                is BudgetDashboardNavigationEvent.ToGoalDetail -> onNavigateToGoalDetail(event.goalId)
            }
        }
    }

    if (state.isLoading) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CircularProgressIndicator(modifier = Modifier.size(50.dp))
        }
    } else {
        BudgetDashboardScreen(
            state = state,
            onAction = viewModel::onAction
        )
    }
}

@Composable
fun BudgetDashboardScreen(
    state: BudgetDashboardState,
    onAction: (BudgetDashboardActions) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 12.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Budget Dashboard",
                style = MaterialTheme.typography.headlineSmall,
                color = Color.White
            )
        }

        item {
            state.currentMonthSummary?.let { summary ->
                MonthSummaryCard(
                    summary = summary,
                    onViewDetails = { onAction(BudgetDashboardActions.ViewBudgetDetails) }
                )
            } ?: EmptyBudgetPrompt(
                onViewDetails = { onAction(BudgetDashboardActions.ViewBudgetDetails) }
            )
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Goals & Savings",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White
                )
            }
        }

        items(state.goals) { goal ->
            GoalCard(
                goal = goal,
                onClick = { onAction(BudgetDashboardActions.OnGoalClick(goal.id)) }
            )
        }

        item {
            CreateGoalButton(
                onClick = { onAction(BudgetDashboardActions.CreateNewGoal) }
            )
        }

        item {
            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}

@Composable
fun MonthSummaryCard(
    summary: BudgetMonthModel,
    onViewDetails: () -> Unit
) {
    val expectedMoneyLeft = (summary.moneyIn - summary.totalBudget).coerceAtLeast(0.0)
    val actualMoneyLeft = (summary.moneyIn - summary.moneyOut).coerceAtLeast(0.0)

    val moneyOutProgress = if (summary.totalBudget > 0) {
        (summary.moneyOut / summary.totalBudget).toFloat()
    } else 0f
    val moneyOutPercentage = if (summary.totalBudget > 0) {
        (summary.moneyOut / summary.totalBudget * 100).toInt()
    } else 0

    val progressColor = budgetProgressColor(moneyOutProgress)

    val diff = actualMoneyLeft - expectedMoneyLeft
    val availableBalanceColor = when {
        diff < -0.01 -> Color(0xFFFF2400)
        diff <= 0.01 -> Color(0xFF00C853)
        diff <= expectedMoneyLeft * 0.10 -> Color(0xFFFFA500)
        else ->   Color(0xFF1A1A1A)
    }

    CardComposable(
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = "Available Balance",
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.labelLarge.copy(color = Color(0xFF1A1A1A))
        )
        Text(
            text = formatCurrency(actualMoneyLeft),
            modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.displaySmall.copy(color = availableBalanceColor)
        )

        Text(
            text = "Budget Usage",
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.labelLarge.copy(color =  Color.Black)
        )

        Text(
            buildAnnotatedString {
                withStyle(style = SpanStyle(fontSize = 20.sp, color = progressColor)) {
                    append(formatCurrency(summary.moneyOut))
                }
                withStyle(style = SpanStyle(fontSize = 16.sp, color =  Color.Black)) {
                    append(" / " + formatCurrency(summary.totalBudget) + "  •  ")
                }
                withStyle(SpanStyle(fontSize = 16.sp, color = progressColor)) {
                    append("${moneyOutPercentage}%")
                }
            },
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
        )

        LinearProgressIndicator(
            progress = { moneyOutProgress },
            modifier = Modifier.padding(top = 2.dp).height(6.dp).fillMaxWidth(),
            color = progressColor,
            trackColor = Color(0x33FFFFFF),
            strokeCap = StrokeCap.Round
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onViewDetails() },
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "View Budget Details",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary
            )
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
fun EmptyBudgetPrompt(
    onViewDetails: () -> Unit
) {
    CardComposable {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "No Budget for This Month",
                style = MaterialTheme.typography.titleMedium,
                color = Color.White
            )
            Text(
                text = "Create your first budget to start tracking your finances",
                style = MaterialTheme.typography.bodyMedium,
                color = Color(0x99FFFFFF)
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onViewDetails() },
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Create Budget",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary
                )
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Composable
fun GoalCard(
    goal: Goal,
    onClick: () -> Unit
) {
    CardComposable(
        modifier = Modifier.clickable { onClick() }
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = goal.name,
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White
                )
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = null,
                    tint = Color(0x99FFFFFF)
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = formatCurrency(goal.currentAmount),
                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium),
                    color = Color.White
                )
                Text(
                    text = "of ${formatCurrency(goal.targetAmount)}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0x99FFFFFF)
                )
            }

            LinearProgressIndicator(
                progress = { goal.progressPercentage },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(999.dp)),
                color = MaterialTheme.colorScheme.primary,
                trackColor = Color(0x33FFFFFF),
                strokeCap = StrokeCap.Round
            )

            Text(
                text = "${(goal.progressPercentage * 100).toInt()}% Complete",
                style = MaterialTheme.typography.labelMedium,
                color = Color(0x99FFFFFF)
            )
        }
    }
}

@Composable
fun CreateGoalButton(
    onClick: () -> Unit
) {
    CardComposable(
        modifier = Modifier.clickable { onClick() }
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.padding(4.dp))
            Text(
                text = "Create New Goal",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Preview
@Composable
fun BudgetDashboardPreview() {
    WealthBuilderTheme {
        BudgetDashboardScreen(
            state = BudgetDashboardState(
                isLoading = false,
                currentMonthSummary = BudgetMonthModel(
                    moneyIn = 51000.0,
                    moneyOut = 10000.0,
                    totalBudget = 21000.0
                ),
                goals = listOf(
                    Goal(
                        name = "Vacation Fund",
                        targetAmount = 50000.0,
                        currentAmount = 15000.0
                    ),
                    Goal(
                        name = "Emergency Fund",
                        targetAmount = 30000.0,
                        currentAmount = 8000.0
                    )
                )
            ),
            onAction = {}
        )
    }
}
