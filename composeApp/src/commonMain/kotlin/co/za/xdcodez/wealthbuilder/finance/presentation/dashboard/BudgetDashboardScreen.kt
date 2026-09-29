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
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import co.za.xdcodez.wealthbuilder.common.budgetProgressColor
import co.za.xdcodez.wealthbuilder.common.formatCurrency
import co.za.xdcodez.wealthbuilder.common.widgets.CardComposable
import co.za.xdcodez.wealthbuilder.finance.domain.dto.BudgetMonthModel
import co.za.xdcodez.wealthbuilder.finance.domain.dto.Goal
import co.za.xdcodez.wealthbuilder.home.HomeNavigationEvent
import co.za.xdcodez.wealthbuilder.theme.WealthBuilderTheme
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.annotation.KoinExperimentalAPI

@OptIn(KoinExperimentalAPI::class)
@Composable
fun BudgetDashboardRoute(
    // Avoid evaluating koinViewModel() during Layoutlib Compose Previews where Koin is not started
    viewModel: BudgetDashboardViewModel? = if (LocalInspectionMode.current) null else koinViewModel(),
    onNavigate: (HomeNavigationEvent) -> Unit,
) {
    // If running inside Android Studio Compose Preview or if ViewModel is null, render preview state directly
    if (LocalInspectionMode.current || viewModel == null) {
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
            onAction = { onNavigate(it) }
        )
        return
    }

    val state by viewModel.state.collectAsState()

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
            onAction = { onNavigate(it) }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BudgetDashboardScreen(
    state: BudgetDashboardState,
    onAction: (HomeNavigationEvent) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .padding(horizontal = 12.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            state.currentMonthSummary?.let { summary ->
                MonthSummaryCard(
                    summary = summary,
                    onViewDetails = { onAction(HomeNavigationEvent.NavigateToBudgetDetails(summary.monthId)) }
                )
            } ?: EmptyBudgetPrompt(
                onViewDetails = { onAction(HomeNavigationEvent.NavigateToCreateBudget(state.currentPeriod.orEmpty())) }
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
                onClick = { /* TODO: Navigate to goal detail */ }
            )
        }

        item {
            CreateGoalButton(
                onClick = { onAction(HomeNavigationEvent.NavigateToCreateGoal("")) }
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

    val progressColor = budgetProgressColor(moneyOutProgress)

    val diff = actualMoneyLeft - expectedMoneyLeft
    val availableBalanceColor = when {
        diff < -0.01 -> Color(0xFFFF2400)
        diff <= 0.01 -> Color(0xFF00C853)
        diff <= expectedMoneyLeft * 0.10 -> Color(0xFFFFA500)
        else -> Color.White
    }

    CardComposable(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            Text(
                text = "September 2026",
                style = MaterialTheme.typography.labelLarge.copy(color = MaterialTheme.colorScheme.primary)
            )
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                modifier = Modifier.clickable { onViewDetails() },
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "Available Balance",
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Start,
            style = MaterialTheme.typography.labelMedium.copy(color = Color.White.copy(alpha = 0.5f))
        )
        Text(
            text = formatCurrency(actualMoneyLeft),
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Start,
            style = MaterialTheme.typography.headlineMedium.copy(color = availableBalanceColor)
        )

        Spacer(modifier = Modifier.height(8.dp))
        LinearProgressIndicator(
            progress = { moneyOutProgress },
            modifier = Modifier.padding(top = 2.dp).height(6.dp).fillMaxWidth(),
            color = progressColor,
            trackColor = Color(0x33FFFFFF),
            strokeCap = StrokeCap.Round
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    "budget used",
                    style = MaterialTheme.typography.labelLarge.copy(color = Color.White.copy(alpha = 0.5f))
                )

                Text(
                    formatCurrency(summary.moneyOut),
                    style = MaterialTheme.typography.bodyLarge.copy(color = progressColor)
                )
            }
            Column(
                horizontalAlignment = Alignment.End
            ) {
                Text(
                    "budget total",
                    style = MaterialTheme.typography.labelLarge.copy(color = Color.White.copy(alpha = 0.5f))
                )

                Text(
                    formatCurrency(summary.totalBudget),
                    style = MaterialTheme.typography.bodyLarge.copy(color = Color.White)
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmptyBudgetPrompt(
    onViewDetails: () -> Unit
) {
    CardComposable(
        modifier = Modifier.clickable(
            onClick = onViewDetails,
        )
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 32.dp)
        ) {

            // Icon
            Icon(
                imageVector = Icons.Default.AddCircle,
                contentDescription = null,
                tint = Color.White.copy(alpha = 0.2f),
                modifier = Modifier.size(64.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Title
            Text(
                text = "No budget yet",
                style = MaterialTheme.typography.titleLarge.copy(
                    color = Color.White
                ),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Subtitle
            Text(
                text = "Tap to set up your budget and start tracking your spending",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = Color(0x99FFFFFF),
                    lineHeight = 22.sp
                ),
                textAlign = TextAlign.Center
            )


            // CTA Button
//            Button(
//                onClick = onViewDetails,
//                shape = RoundedCornerShape(12.dp),
//                colors = ButtonDefaults.buttonColors(
//                    containerColor = Color.White,
//                    contentColor = Color.Black
//                ),
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .height(52.dp)
//            ) {
//                Text(
//                    text = "Set Up Budget",
//                    style = MaterialTheme.typography.titleSmall
//                )
//            }
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
    Row(
        modifier = Modifier   .fillMaxWidth()
            .padding(horizontal = 68.dp, vertical = 16.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.primary)
            .clickable {}
            .padding(vertical = 12.dp)
            .clickable { onClick() }
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
                text = "CREATE NEW GOAL",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White
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
