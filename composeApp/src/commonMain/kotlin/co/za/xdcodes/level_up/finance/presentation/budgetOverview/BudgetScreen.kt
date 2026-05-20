package co.za.xdcodes.level_up.finance.presentation.budgetOverview

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import co.za.xdcodes.level_up.common.toMonthName
import co.za.xdcodes.level_up.common.widgets.MonthlyCarouselComposable
import co.za.xdcodes.level_up.finance.domain.dto.BudgetCategoryModel
import co.za.xdcodes.level_up.finance.domain.dto.BudgetMonthModel
import co.za.xdcodes.level_up.finance.presentation.composables.BudgetMonthHeaderSection
import co.za.xdcodes.level_up.finance.presentation.composables.SummaryCategoryCardComposable
import co.za.xdcodes.level_up.theme.LevelUpTheme
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.annotation.KoinExperimentalAPI

@OptIn(KoinExperimentalAPI::class)
@Composable
fun BudgetScreenRoute(
    viewModel: BudgetScreenViewModel = koinViewModel<BudgetScreenViewModel>(),
    onNavigateToTransactions: (monthId: String, categoryId: String) -> Unit,
    onNavigateToBudgetSetup: (monthIndex: Int, year: Int) -> Unit
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.navigationEvent.collect { event ->
            when (event) {
                is BudgetScreenNavigationEvent.ToTransactions -> {
                    onNavigateToTransactions(event.monthId, event.categoryId)
                }

                is BudgetScreenNavigationEvent.ToBudgetSetup -> {
                    onNavigateToBudgetSetup(event.monthIndex, event.year)
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

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        MonthlyCarouselComposable(
            title = "${state.selectedMonthIndex.toMonthName()} ${state.selectedYear}",
            onPrevious = { onAction(BudgetScreenActions.OnPreviousMonth) },
            onNext = { onAction(BudgetScreenActions.OnNextMonth) }
        )
        if (month != null) {
            BudgetMonthHeaderSection(month, Modifier.padding(horizontal = 16.dp, vertical = 8.dp))

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 8.dp),
            ) {
                item {
                    Text(
                        "Categories",
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelLarge.copy(color = Color(0x99FFFFFF))
                    )
                }
                items(categories.size) { index ->
                    val category = categories[index]
                    SummaryCategoryCardComposable(
                        modifier = Modifier
                            .padding(vertical = 4.dp)
                            .clickable {
                                onAction(
                                    BudgetScreenActions.NavigateToTransactions(
                                        month.monthId,
                                        category.id
                                    )
                                )
                            },
                        categoryModel = category
                    )
                }
            }
        } else {
            EmptyBudgetScreen(state.selectedMonthIndex.toMonthName()) {
                onAction(
                    BudgetScreenActions.NavigateToBudgetSetup(
                        state.selectedMonthIndex,
                        state.selectedYear
                    )
                )
            }
        }
    }
}

@Preview
@Composable
fun BudgetScreenPreview() {
    LevelUpTheme {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CircularProgressIndicator(
                modifier = Modifier.size(50.dp)
            )
        }
//        BudgetScreen(
//            BudgetScreenState(
//                selectedMonth = BudgetMonthModel(
//                    moneyIn = 51000.0,
//                    moneyOut = 10000.0,
//                    totalBudget = 21000.0
//                ),
//                budgetCategories = listOf(
//                    BudgetCategoryModel(
//                        id = "",
//                        name = "Personal Spending",
//                        totalPaid = 5000.45,
//                        budget = 8000.00,
//                    ),
//                )
//            ),
//        ) {
//
//        }
    }
}
