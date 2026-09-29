package co.za.xdcodez.wealthbuilder.finance.presentation.goals

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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Snackbar
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import co.za.xdcodez.wealthbuilder.common.formatCurrency
import co.za.xdcodez.wealthbuilder.common.widgets.CardComposable
import co.za.xdcodez.wealthbuilder.common.widgets.CustomTextField
import co.za.xdcodez.wealthbuilder.navigation.WealthBuilderBaseScreen
import co.za.xdcodez.wealthbuilder.theme.WealthBuilderTheme
import kotlinx.coroutines.flow.collectLatest
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun CreateGoalScreenRoute(
    onNavigateBack: () -> Unit,
    onNavigateToGoalDetail: (String) -> Unit,
    viewModel: CreateGoalViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.navigationEvents.collectLatest { event ->
            when (event) {
                CreateGoalNavigationEvent.NavigateBack -> onNavigateBack()
                is CreateGoalNavigationEvent.NavigateToGoalDetail -> onNavigateToGoalDetail(event.goalId)
            }
        }
    }

    CreateGoalScreen(
        state = state,
        onAction = viewModel::onAction,
        onNavigateBack = onNavigateBack
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CreateGoalScreen(
    state: CreateGoalState,
    onAction: (CreateGoalAction) -> Unit,
    onNavigateBack: () -> Unit
) {
    WealthBuilderBaseScreen(
        title = "Setup Goal",
        onBackClick = onNavigateBack,
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Goal Name
                item {
                    CardComposable {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text(
                                text = "GOAL NAME",
                                style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.primary,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                            CustomTextField(
                                value = state.name,
                                onValueChange = { onAction(CreateGoalAction.UpdateName(it)) },
                                placeholder = "e.g., Emergency Fund, Vacation, New Car",
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }

                // Target Amount
                item {
                    CardComposable {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text(
                                text = "TARGET AMOUNT",
                                style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.primary,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                            CustomTextField(
                                value = state.targetAmount,
                                onValueChange = { onAction(CreateGoalAction.UpdateTargetAmount(it)) },
                                placeholder = "0.00",
                                prefix = "R ",
                                modifier = Modifier.fillMaxWidth(),
                                keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                                    keyboardType = KeyboardType.Decimal
                                )
                            )
                        }
                    }
                }

                // Date Range
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

                            // Start Date
                            DatePickerField(
                                label = "Start Date",
                                value = state.startDate,
                                onValueChange = { onAction(CreateGoalAction.UpdateStartDate(it)) }
                            )

                            // End Date
                            DatePickerField(
                                label = "End Date",
                                value = state.endDate,
                                onValueChange = { onAction(CreateGoalAction.UpdateEndDate(it)) }
                            )
                        }
                    }
                }

                // Monthly Contribution Estimate
                if (state.estimatedMonthlyContribution > 0) {
                    item {
                        CardComposable {
                            Column(
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Text(
                                    text = "ESTIMATED MONTHLY CONTRIBUTION",
                                    style = MaterialTheme.typography.labelLarge,
                                    color = MaterialTheme.colorScheme.primary,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = formatCurrency(state.estimatedMonthlyContribution),
                                    style = MaterialTheme.typography.headlineMedium,
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "Recommended monthly savings to reach your goal",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color(0x99FFFFFF)
                                )
                            }
                        }
                    }
                }

                // Link to Budget Category
                item {
                    CardComposable {
                        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = "LINK TO BUDGET CATEGORY",
                                        style = MaterialTheme.typography.labelLarge,
                                        color = MaterialTheme.colorScheme.primary,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = "Optional - Track contributions",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = Color(0x99FFFFFF),
                                        fontSize = 11.sp
                                    )
                                }
                                TextButton(
                                    onClick = { onAction(CreateGoalAction.ToggleCategorySelector) }
                                ) {
                                    Text(
                                        text = if (state.selectedCategoryId != null) "Change" else "Select",
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }

                            if (state.selectedCategoryId != null) {
                                val selectedCategory =
                                    state.availableCategories.find { it.id == state.selectedCategoryId }
                                selectedCategory?.let { category ->
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(Color.White.copy(alpha = 0.05f))
                                            .padding(12.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(
                                            text = category.name,
                                            color = Color.White,
                                            style = MaterialTheme.typography.bodyMedium
                                        )
                                        Text(
                                            text = formatCurrency(category.budget),
                                            color = Color(0x99FFFFFF),
                                            style = MaterialTheme.typography.bodyMedium
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // Create Button
                item {
                    Button(
                        onClick = { onAction(CreateGoalAction.CreateGoal) },
                        enabled = state.isValid && !state.isLoading,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            disabledContainerColor = Color.Gray.copy(alpha = 0.3f)
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        if (state.isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                color = Color.White
                            )
                        } else {
                            Text(
                                text = "CREATE GOAL",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                // Bottom spacing
                item {
                    Spacer(modifier = Modifier.height(32.dp))
                }
            }

            // Category Selector Bottom Sheet
            if (state.showCategorySelector) {
                CategorySelectorBottomSheet(
                    categories = state.availableCategories,
                    selectedCategoryId = state.selectedCategoryId,
                    onSelect = { onAction(CreateGoalAction.SelectCategory(it)) },
                    onDismiss = { onAction(CreateGoalAction.ToggleCategorySelector) }
                )
            }

            // Error Snackbar
            state.error?.let { error ->
                Snackbar(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(16.dp),
                    action = {
                        TextButton(onClick = { onAction(CreateGoalAction.DismissError) }) {
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DatePickerField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit
) {
    var showDatePicker by remember { mutableStateOf(false) }

    // Get today's date at midnight
    val today = remember {
        // Get current time and subtract to get start of today
        val nowMillis = kotlinx.datetime.Clock.System.now().toEpochMilliseconds()
        val todayStart = nowMillis - (nowMillis % 86400000L) // Start of day in UTC
        todayStart
    }

    val datePickerState = rememberDatePickerState(
        selectableDates = object : SelectableDates {
            override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                return utcTimeMillis >= today
            }
        }
    )

    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = Color(0x99FFFFFF),
            fontSize = 11.sp
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(Color.White.copy(alpha = 0.08f))
                .clickable { showDatePicker = true }
                .padding(horizontal = 10.dp, vertical = 12.dp)
        ) {
            Text(
                text = if (value.isEmpty()) "YYYY-MM-DD" else value,
                color = if (value.isEmpty()) Color(0x44FFFFFF) else Color.White,
                fontSize = 15.sp
            )
        }
    }

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let { millis ->
                            val instant = kotlinx.datetime.Instant.fromEpochMilliseconds(millis)
                            val date = instant.toString().substringBefore('T')
                            onValueChange(date)
                        }
                        showDatePicker = false
                    }
                ) {
                    Text("OK", color = MaterialTheme.colorScheme.primary)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("Cancel", color = Color.White.copy(alpha = 0.6f))
                }
            },
            colors = DatePickerDefaults.colors(
                containerColor = Color(0xFF1B1B1B)
            )
        ) {
            DatePicker(
                state = datePickerState,
                colors = DatePickerDefaults.colors(
                    containerColor = Color(0xFF1B1B1B),
                    titleContentColor = Color.White,
                    headlineContentColor = Color.White,
                    weekdayContentColor = Color(0x99FFFFFF),
                    subheadContentColor = Color.White,
                    yearContentColor = Color.White,
                    currentYearContentColor = MaterialTheme.colorScheme.primary,
                    selectedYearContentColor = Color.White,
                    selectedYearContainerColor = MaterialTheme.colorScheme.primary,
                    dayContentColor = Color.White,
                    selectedDayContentColor = Color.White,
                    selectedDayContainerColor = MaterialTheme.colorScheme.primary,
                    todayContentColor = MaterialTheme.colorScheme.primary,
                    todayDateBorderColor = MaterialTheme.colorScheme.primary,
                    disabledDayContentColor = Color.White.copy(alpha = 0.3f)
                )
            )
        }
    }
}

@Composable
private fun CategorySelectorBottomSheet(
    categories: List<co.za.xdcodez.wealthbuilder.finance.domain.dto.BudgetCategoryModel>,
    selectedCategoryId: String?,
    onSelect: (String?) -> Unit,
    onDismiss: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.5f))
            .clickable(onClick = onDismiss)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                .background(Color(0xFF1B1B1B))
                .padding(16.dp)
                .clickable(enabled = false) { }
        ) {
            Text(
                text = "Select Budget Category",
                style = MaterialTheme.typography.titleLarge,
                color = Color.White,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            if (categories.isEmpty()) {
                Text(
                    text = "No categories available. Create a budget first.",
                    color = Color(0x99FFFFFF),
                    modifier = Modifier.padding(vertical = 32.dp)
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    item {
                        CategoryItem(
                            name = "None",
                            budget = null,
                            isSelected = selectedCategoryId == null,
                            onClick = { onSelect(null) }
                        )
                    }

                    items(categories) { category ->
                        CategoryItem(
                            name = category.name,
                            budget = category.budget,
                            isSelected = category.id == selectedCategoryId,
                            onClick = { onSelect(category.id) }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
private fun CategoryItem(
    name: String,
    budget: Double?,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(
                if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
                else Color.White.copy(alpha = 0.05f)
            )
            .clickable(onClick = onClick)
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = name,
            color = if (isSelected) MaterialTheme.colorScheme.primary else Color.White,
            style = MaterialTheme.typography.bodyLarge
        )
        budget?.let {
            Text(
                text = formatCurrency(it),
                color = if (isSelected) MaterialTheme.colorScheme.primary else Color(0x99FFFFFF),
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Preview
@Composable
fun CreateGoalScreenPreview() {
    WealthBuilderTheme {
        CreateGoalScreen(
            state = CreateGoalState(
                name = "Emergency Fund",
                targetAmount = "50000",
                startDate = "2026-01-01",
                endDate = "2026-12-31",
                estimatedMonthlyContribution = 4166.67
            ),
            onAction = {},
            onNavigateBack = {}
        )
    }
}
