package co.za.xdcodes.level_up.finance.presentation.budget

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.automirrored.outlined.KeyboardArrowRight
import androidx.compose.material.icons.outlined.KeyboardArrowRight
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.toUpperCase
import androidx.compose.ui.unit.dp
import co.za.xdcodes.level_up.common.widgets.CustomAppTopBar
import co.za.xdcodes.level_up.finance.domain.dto.BudgetOverviewModel
import co.za.xdcodes.level_up.finance.presentation.composables.MonthlyBudgetComposable
import co.za.xdcodes.level_up.theme.LevelUpTheme
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.annotation.KoinExperimentalAPI
import kotlin.collections.get

@OptIn(KoinExperimentalAPI::class)
@Composable
fun BudgetScreenRoute(
    viewModel: BudgetScreenViewModel = koinViewModel<BudgetScreenViewModel>(),
    onNavigate: () -> Unit
) {
    val state by viewModel.state.collectAsState()
    BudgetScreen(state.budgetMonths) { action ->
        viewModel.onAction(action)
        onNavigate()
    }
}



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BudgetScreen(
    budget: List<BudgetOverviewModel>,
    onAction: (BudgetScreenActions) -> Unit
) {
    CustomAppTopBar(
        title = "Yearly Overview",
        modifier = Modifier.fillMaxSize().padding(horizontal = 12.dp)
    ) {
        LazyColumn{
            items(budget.size) { index ->
                val month = budget[index]

                Column(
                    modifier = Modifier
                        .padding(vertical = 8.dp)
                        .clickable {
                            onAction(BudgetScreenActions.OnBudgetCardClicked(month.month))
                        }
                ) {
                    Text(
                        month.month.uppercase(),
                        modifier = Modifier.padding(start = 10.dp, bottom = 4.dp),
                        style = MaterialTheme.typography.labelLarge.copy(color = MaterialTheme.colorScheme.primary)
                    )

                    MonthlyBudgetComposable(month)
                }
            }
        }
    }

}

@Preview
@Composable
fun BudgetScreenPreview() {
    LevelUpTheme {
        BudgetScreen(
            listOf(
                BudgetOverviewModel(
                    month = "JANUARY",
                    moneyIn = 0.0,
                    moneyOut = 0.0,
                    savings = 0.0,
                    totalBudget = 0.0
                ),
                BudgetOverviewModel(
                    month = "FEBRUARY",
                    moneyIn = 0.0,
                    moneyOut = 0.0,
                    savings = 0.0,
                    totalBudget = 0.0
                ),
                BudgetOverviewModel(
                    month = "MARCH",
                    moneyIn = 0.0,
                    moneyOut = 0.0,
                    savings = 0.0,
                    totalBudget = 0.0
                ),
                BudgetOverviewModel(
                    month = "APRIL",
                    moneyIn = 0.0,
                    moneyOut = 0.0,
                    savings = 0.0,
                    totalBudget = 0.0
                ),
                BudgetOverviewModel(
                    month = "MAY",
                    moneyIn = 0.0,
                    moneyOut = 0.0,
                    savings = 0.0,
                    totalBudget = 0.0
                ),

                )
        ) {

        }
    }
}
