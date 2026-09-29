package co.za.xdcodez.wealthbuilder.finance.presentation.goals

import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import co.za.xdcodez.wealthbuilder.common.formatCurrency
import co.za.xdcodez.wealthbuilder.common.widgets.CardComposable
import co.za.xdcodez.wealthbuilder.common.widgets.CustomTextField
import co.za.xdcodez.wealthbuilder.navigation.WealthBuilderBaseScreen
import co.za.xdcodez.wealthbuilder.theme.WealthBuilderTheme
import kotlinx.coroutines.flow.collectLatest
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun GoalDetailScreenRoute(
    goalId: String,
    onNavigateBack: () -> Unit,
    viewModel: GoalDetailViewModel = koinViewModel { parametersOf(goalId) }
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.navigationEvents.collectLatest { event ->
            when (event) {
                GoalDetailNavigationEvent.NavigateBack -> onNavigateBack()
            }
        }
    }

    GoalDetailScreen(
        state = state,
        onAction = viewModel::onAction,
        onNavigateBack = onNavigateBack
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun GoalDetailScreen(
    state: GoalDetailState,
    onAction: (GoalDetailAction) -> Unit,
    onNavigateBack: () -> Unit
) {
    Scaffold(
        containerColor = Color.Transparent,
        topBar = {
            WealthBuilderBaseScreen(
                title = state.goal?.name ?: "Goal Details",
                onBackClick = onNavigateBack,
            ) {
                IconButton(onClick = { onAction(GoalDetailAction.ToggleDeleteConfirmation) }) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete Goal",
                        tint = Color.White
                    )
                }
            }
        },
        floatingActionButton = {
            if (state.goal != null && !state.goal.isCompleted) {
                FloatingActionButton(
                    onClick = { onAction(GoalDetailAction.ToggleContributionDialog) },
                    containerColor = MaterialTheme.colorScheme.primary
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add Contribution",
                        tint = Color.White
                    )
                }
            }
        }
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            when {
                state.isLoading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                state.goal != null -> {
                    GoalDetailContent(
                        goal = state.goal,
                        modifier = Modifier.fillMaxSize()
                    )
                }

                else -> {
                    Text(
                        text = "Goal not found",
                        color = Color.White,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            }

            // Contribution Dialog
            if (state.showContributionDialog) {
                ContributionDialog(
                    amount = state.contributionAmount,
                    isValid = state.isContributionValid,
                    onAmountChange = { onAction(GoalDetailAction.UpdateContributionAmount(it)) },
                    onConfirm = { onAction(GoalDetailAction.AddContribution) },
                    onDismiss = { onAction(GoalDetailAction.ToggleContributionDialog) }
                )
            }

            // Delete Confirmation Dialog
            if (state.showDeleteConfirmation) {
                DeleteConfirmationDialog(
                    goalName = state.goal?.name ?: "",
                    onConfirm = { onAction(GoalDetailAction.DeleteGoal) },
                    onDismiss = { onAction(GoalDetailAction.ToggleDeleteConfirmation) }
                )
            }

            // Error Snackbar
            state.error?.let { error ->
                Snackbar(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(16.dp),
                    action = {
                        TextButton(onClick = { onAction(GoalDetailAction.DismissError) }) {
                            Text("Dismiss")
                        }
                    }
                ) {
                    Text(error)
                }
            }
        }
    }
}

@Composable
private fun GoalDetailContent(
    goal: co.za.xdcodez.wealthbuilder.finance.domain.dto.Goal,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Progress Card
        item {
            CardComposable {
                Column(
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Progress Circle/Bar
                    LinearProgressIndicator(
                        progress = { goal.progressPercentage },
                        modifier = Modifier.fillMaxWidth().height(8.dp),
                        color = if (goal.isCompleted) Color(0xFF4CAF50) else MaterialTheme.colorScheme.primary,
                        trackColor = Color(0x33FFFFFF),
                        strokeCap = StrokeCap.Round
                    )

                    // Percentage
                    Text(
                        text = "${(goal.progressPercentage * 100).toInt()}% Complete",
                        style = MaterialTheme.typography.titleLarge,
                        color = if (goal.isCompleted) Color(0xFF4CAF50) else MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )

                    // Current vs Target
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = "CURRENT",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color(0x99FFFFFF),
                                fontSize = 10.sp
                            )
                            Text(
                                text = formatCurrency(goal.currentAmount),
                                style = MaterialTheme.typography.titleMedium,
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = "TARGET",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color(0x99FFFFFF),
                                fontSize = 10.sp
                            )
                            Text(
                                text = formatCurrency(goal.targetAmount),
                                style = MaterialTheme.typography.titleMedium,
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    if (!goal.isCompleted) {
                        HorizontalDivider(color = Color.White.copy(alpha = 0.1f))

                        // Remaining Amount
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Remaining",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color(0x99FFFFFF)
                            )
                            Text(
                                text = formatCurrency(goal.remainingAmount),
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    } else {
                        // Completion Message
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFF4CAF50).copy(alpha = 0.2f))
                                .padding(12.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "🎉 Goal Completed!",
                                style = MaterialTheme.typography.titleMedium,
                                color = Color(0xFF4CAF50),
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }

        // Timeline Card
        item {
            CardComposable {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        text = "TIMELINE",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.primary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = "Start Date",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color(0x99FFFFFF),
                                fontSize = 11.sp
                            )
                            Text(
                                text = goal.startDate,
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.White
                            )
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = "End Date",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color(0x99FFFFFF),
                                fontSize = 11.sp
                            )
                            Text(
                                text = goal.endDate,
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.White
                            )
                        }
                    }

                    goal.daysRemaining?.let { days ->
                        HorizontalDivider(color = Color.White.copy(alpha = 0.1f))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Days Remaining",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color(0x99FFFFFF)
                            )
                            Text(
                                text = "$days days",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }

        // Monthly Contribution Card
        if (!goal.isCompleted && goal.estimatedMonthlyContribution > 0) {
            item {
                CardComposable {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            text = "ESTIMATED MONTHLY CONTRIBUTION",
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.primary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = formatCurrency(goal.estimatedMonthlyContribution),
                            style = MaterialTheme.typography.headlineMedium,
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Based on your timeline, this is the recommended monthly savings to reach your goal.",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0x99FFFFFF)
                        )
                    }
                }
            }
        }

        // Linked Category Card
        goal.linkedCategoryId?.let {
            item {
                CardComposable {
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(
                            text = "LINKED BUDGET CATEGORY",
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.primary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Category ID: $it",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.White
                        )
                        Text(
                            text = "Track your contributions in your monthly budget",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0x99FFFFFF),
                            fontSize = 11.sp
                        )
                    }
                }
            }
        }

        // Bottom spacing for FAB
        item {
            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}

@Composable
private fun ContributionDialog(
    amount: String,
    isValid: Boolean,
    onAmountChange: (String) -> Unit,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        CardComposable(modifier = Modifier.fillMaxWidth()) {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "Add Contribution",
                    style = MaterialTheme.typography.titleLarge,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )

                CustomTextField(
                    value = amount,
                    onValueChange = onAmountChange,
                    placeholder = "0.00",
                    prefix = "R",
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                        keyboardType = KeyboardType.Decimal
                    )
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Cancel", color = Color.White)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = onConfirm,
                        enabled = isValid,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Text("Add")
                    }
                }
            }
        }
    }
}

@Composable
private fun DeleteConfirmationDialog(
    goalName: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        CardComposable(modifier = Modifier.fillMaxWidth()) {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "Delete Goal?",
                    style = MaterialTheme.typography.titleLarge,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "Are you sure you want to delete \"$goalName\"? This action cannot be undone.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0x99FFFFFF)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Cancel", color = Color.White)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = onConfirm,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFE53935)
                        )
                    ) {
                        Text("Delete")
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun GoalDetailScreenPreview() {
    WealthBuilderTheme {
        GoalDetailScreen(
            state = GoalDetailState(
                goal = co.za.xdcodez.wealthbuilder.finance.domain.dto.Goal(
                    name = "Emergency Fund",
                    targetAmount = 50000.0,
                    currentAmount = 20000.0,
                    startDate = "2026-01-01",
                    endDate = "2026-12-31"
                ),
                isLoading = false
            ),
            onAction = {},
            onNavigateBack = {}
        )
    }
}
