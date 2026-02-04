package co.za.xdcodes.level_up.finance.presentation.budget

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import co.za.xdcodes.level_up.common.widgets.CustomAppTopBar
import co.za.xdcodes.level_up.finance.presentation.composables.BudgetCategoryCardComposable
import co.za.xdcodes.level_up.finance.presentation.composables.MonthlyBudgetComposable
import co.za.xdcodes.level_up.theme.LevelUpTheme
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.annotation.KoinExperimentalAPI

@OptIn(KoinExperimentalAPI::class)
@Composable
fun BudgetCategoryScreenRoute(
    viewModel: BudgetScreenViewModel = koinViewModel<BudgetScreenViewModel>(),
    onNavigate: (BudgetScreenNavigation) -> Unit
) {
    val state by viewModel.state.collectAsState()
    CustomAppTopBar(
        title = state.selectedMonth?.month.orEmpty(),
        showNavigationIcon = true,
        onNavigate = {
            onNavigate(BudgetScreenNavigation.NavigateUp)
        }
    ) {
        BudgetCategoryScreen(state) {
            viewModel.onAction(it)
            onNavigate(BudgetScreenNavigation.NavigateToTransactions)
        }
    }
}

@Composable
fun BudgetCategoryScreen(state: BudgetScreenState, onAction: (BudgetScreenActions) -> Unit) {
    val month = state.selectedMonth
    val categories = state.budgetCategories

    Column(
        verticalArrangement = Arrangement.Top

    ) {
        month?.let {
            MonthlyBudgetComposable(month)
        }

        Text(
            "EXPENSES",
            modifier = Modifier.padding(start = 10.dp, top = 12.dp),
            style = MaterialTheme.typography.labelLarge.copy(color = MaterialTheme.colorScheme.primary)
        )

        LazyColumn {
            items(categories.size) { index ->
                val category = categories[index]
                BudgetCategoryCardComposable(
                    modifier = Modifier
                        .padding(top = 12.dp)
                        .clickable {
                            onAction(BudgetScreenActions.OnCategoryCardClicked(category = category.category))
                        },
                    showNavigationIcon = true,
                    categoryModel = category
                )
            }
        }
    }
}

@Preview
@Composable
fun BudgetCategoryScreenPreview() {
    LevelUpTheme {
        BudgetCategoryScreen(BudgetScreenState()) { }
    }
}

