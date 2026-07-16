package co.za.xdcodez.wealthbuilder.finance.presentation.budgetOverview

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import co.za.xdcodez.wealthbuilder.common.widgets.MonthlyCarouselComposable
import co.za.xdcodez.wealthbuilder.finance.domain.dto.BudgetCategoryModel
import co.za.xdcodez.wealthbuilder.finance.domain.dto.BudgetMonthModel
import co.za.xdcodez.wealthbuilder.finance.presentation.composables.BudgetMonthHeaderSection
import co.za.xdcodez.wealthbuilder.finance.presentation.composables.SummaryCategoryCardComposable
import co.za.xdcodez.wealthbuilder.theme.WealthBuilderTheme
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.annotation.KoinExperimentalAPI

@OptIn(KoinExperimentalAPI::class)
@Composable
fun BudgetScreenRoute(
    viewModel: BudgetScreenViewModel = koinViewModel<BudgetScreenViewModel>(),
    onNavigateToTransactions: (monthId: String, categoryId: String) -> Unit,
    onNavigateToBudgetSetup: (monthId: String) -> Unit
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.navigationEvent.collect { event ->
            when (event) {
                is BudgetScreenNavigationEvent.ToTransactions -> {
                    onNavigateToTransactions(event.monthId, event.categoryId)
                }

                is BudgetScreenNavigationEvent.ToBudgetSetup -> {
                    onNavigateToBudgetSetup(event.monthId)
                }
            }
        }
    }


    if (state.isLoading) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CircularProgressIndicator(
                modifier = Modifier.size(50.dp)
            )
        }
    } else {
        BudgetScreen(
            state = state,
            onAction = viewModel::onAction
        )
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BudgetScreen(
    state: BudgetScreenState,
    onAction: (BudgetScreenActions) -> Unit
) {
    val month = state.selectedMonth
    val categories = state.budgetCategories
    val selectedPeriod = state.selectedPeriod

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        MonthlyCarouselComposable(
            title =  state.currentPeriod?.displayTitle?: "",
            onPrevious = { onAction(BudgetScreenActions.OnPreviousPeriod) },
            onNext = { onAction(BudgetScreenActions.OnNextPeriod) }
        )
        if (month != null && selectedPeriod != null) {
            Column(
                modifier = Modifier.padding(horizontal = 12.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                BudgetMonthHeaderSection(month, Modifier.padding(horizontal = 8.dp))

                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize(),
                ) {
                    item {
                        Text(
                            "Categories",
                            modifier = Modifier.padding(horizontal = 8.dp),
                            style = MaterialTheme.typography.labelLarge.copy(
                                color = Color(
                                    0x99FFFFFF
                                )
                            )
                        )
                    }
                    items(categories.size) { index ->
                        val category = categories[index]
                        SummaryCategoryCardComposable(
                            modifier = Modifier
                                .padding(vertical = 5.dp)
                                .clickable {
                                    onAction(
                                        BudgetScreenActions.NavigateToTransactions(
                                            selectedPeriod.monthId,
                                            category.id
                                        )
                                    )
                                },
                            categoryModel = category
                        )
                    }
                }
            }
        } else if (selectedPeriod != null) {
            EmptyBudgetScreen(selectedPeriod.displayTitle) {
                onAction(
                    BudgetScreenActions.NavigateToBudgetSetup(
                        selectedPeriod.monthId
                    )
                )
            }
        }
    }
}

@Preview
@Composable
fun BudgetScreenPreview() {
    WealthBuilderTheme {
        BudgetScreen(
            BudgetScreenState(
                selectedMonth = BudgetMonthModel(
                    moneyIn = 51000.0,
                    moneyOut = 10000.0,
                    totalBudget = 21000.0
                ),
                budgetCategories = listOf(
                    BudgetCategoryModel(
                        id = "",
                        name = "Personal Spending",
                        totalPaid = 5000.45,
                        budget = 8000.00,
                    ),
                    BudgetCategoryModel(
                        id = "",
                        name = "Personal Spending",
                        totalPaid = 5000.45,
                        budget = 8000.00,
                    ),
                )
            ),
        ) { }
    }
}
