package co.za.xdcodez.wealthbuilder.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.lifecycle.ViewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.navArgument
import co.za.xdcodez.wealthbuilder.finance.presentation.budgetOverview.BudgetScreenRoute
import co.za.xdcodez.wealthbuilder.finance.presentation.budgetOverview.BudgetScreenViewModel
import co.za.xdcodez.wealthbuilder.finance.presentation.budgetTransactions.BudgetTransactionScreenRoute
import co.za.xdcodez.wealthbuilder.finance.presentation.budgetTransactions.TransactionsViewModel
import co.za.xdcodez.wealthbuilder.finance.presentation.createbudget.CreateBudgetScreenRoute
import co.za.xdcodez.wealthbuilder.finance.presentation.goals.CreateGoalScreenRoute
import co.za.xdcodez.wealthbuilder.finance.presentation.goals.GoalDetailScreenRoute
import co.za.xdcodez.wealthbuilder.home.HomeNavigationEvent
import co.za.xdcodez.wealthbuilder.home.HomeScreenRoute
import co.za.xdcodez.wealthbuilder.journal.presentation.details.DayDetailNavigationEvent
import co.za.xdcodez.wealthbuilder.journal.presentation.details.DayDetailScreenRoute
import co.za.xdcodez.wealthbuilder.navigation.Destination.BudgetOverviewDestination
import co.za.xdcodez.wealthbuilder.navigation.Destination.BudgetTransactionsDestination
import co.za.xdcodez.wealthbuilder.navigation.Destination.CreateGoalDestination
import co.za.xdcodez.wealthbuilder.navigation.Destination.GoalDetailDestination
import co.za.xdcodez.wealthbuilder.navigation.Destination.SetupBudgetDestination
import org.koin.compose.KoinContext
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.annotation.KoinExperimentalAPI

@Composable
fun RootNavigationGraph(navController: NavHostController) {
    KoinContext {
        NavHost(
            navController = navController,
            startDestination = Destination.HomeDestination.route
        ) {

            composable(Destination.HomeDestination.route) {
                HomeScreenRoute { event ->
                    when (event) {
                        is HomeNavigationEvent.NavigateToBudgetDetails -> {
                            navController.navigate(BudgetOverviewDestination.route)
                        }

                        is HomeNavigationEvent.NavigateToCreateGoal -> {
                            navController.navigate(CreateGoalDestination.route)
                        }

                        is HomeNavigationEvent.NavigateToCreateBudget -> {
                            navController.navigate(SetupBudgetDestination.createRoute(event.monthId))
                        }
                        is HomeNavigationEvent.NavigateToDayDetails -> {
                            navController.navigate(
                                Destination.JournalDayDetailDestination.createRoute(
                                    date = event.date,
                                    monthIndex = event.monthIndex,
                                    year = event.year
                                )
                            )
                        }
                    }
                }
            }
            /**
             * Finance navigation screens
             * */

            composable(BudgetOverviewDestination.route) { entry ->
                val viewModel: BudgetScreenViewModel = entry.sharedViewModel(
                    navController,
                    Destination.HomeDestination.route
                )
                val navBackStack by navController.currentBackStackEntryAsState()
                LaunchedEffect(navBackStack?.destination?.route) {
                    if (navBackStack?.destination?.route == BudgetOverviewDestination.route) {
                        viewModel.onReturnFromSetup()
                    }
                }

                BudgetScreenRoute(
                    onNavigateBack = {
                        navController.popBackStack()
                    },
                    onNavigateToTransactions = { monthId, categoryId ->
                        navController.navigate(
                            BudgetTransactionsDestination.createRoute(
                                monthId,
                                categoryId
                            )
                        )
                    },
                    onNavigateToBudgetSetup = { monthId ->
                        navController.navigate(SetupBudgetDestination.createRoute(monthId))
                    }
                )
            }

            composable(
                route = SetupBudgetDestination.route,
                arguments = listOf(
                    navArgument("monthId") { type = NavType.StringType }
                )) { entry ->
                CreateBudgetScreenRoute(
                    monthId = entry.arguments?.getString("monthId") ?: "",
                ) {
                    navController.popBackStack()
                }
            }

            composable(
                route = BudgetTransactionsDestination.route,
                arguments = listOf(
                    navArgument("monthId") { type = NavType.StringType },
                    navArgument("categoryId") { type = NavType.StringType })
            ) { entry ->
                val viewModel: TransactionsViewModel =
                    entry.sharedViewModel(
                        navController,
                        BudgetOverviewDestination.route
                    )

                val monthId = entry.arguments?.getString("monthId") ?: ""
                val categoryId = entry.arguments?.getString("categoryId") ?: ""

                BudgetTransactionScreenRoute(
                    viewModel = viewModel,
                    monthId = monthId,
                    categoryId = categoryId
                ) {
                    navController.navigateUp()
                }
            }

            // Goal screens
            composable(CreateGoalDestination.route) {
                CreateGoalScreenRoute(
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToGoalDetail = { goalId ->
                        navController.navigate(GoalDetailDestination.createRoute(goalId)) {
                            popUpTo(CreateGoalDestination.route) { inclusive = true }
                        }
                    }
                )
            }

            composable(
                route = GoalDetailDestination.route,
                arguments = listOf(
                    navArgument("goalId") { type = NavType.StringType }
                )
            ) { entry ->
                val goalId = entry.arguments?.getString("goalId") ?: ""
                GoalDetailScreenRoute(
                    goalId = goalId,
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            /**
             * Journal navigation screens
             * */

            // journal day detail
            composable(
                route = Destination.JournalDayDetailDestination.route,
                arguments = listOf(
                    navArgument("date") { type = NavType.StringType },
                    navArgument("monthIndex") { type = NavType.IntType },
                    navArgument("year") { type = NavType.IntType }
                )
            ) { entry ->
                val date = entry.arguments?.getString("date") ?: ""
                val monthIndex = entry.arguments?.getInt("monthIndex") ?: 1
                val year = entry.arguments?.getInt("year") ?: 2026

                DayDetailScreenRoute(
                    date = date,
                    monthIndex = monthIndex,
                    year = year,
                    onNavigate = { event ->
                        when (event) {
                            DayDetailNavigationEvent.NavigateBack -> navController.popBackStack()
                            DayDetailNavigationEvent.ToAddTrade -> {
                                // TODO: navigate to add trade screen
                            }

                            is DayDetailNavigationEvent.ToTradeDetail -> {
                                // TODO: navigate to trade detail screen
                            }
                        }
                    }
                )
            }
        }
    }
}

@OptIn(KoinExperimentalAPI::class)
@Composable
inline fun <reified T : ViewModel> NavBackStackEntry.sharedViewModel(
    navController: NavController,
    parentRoute: String
): T {
    val parentEntry = remember(this) {
        navController.getBackStackEntry(parentRoute)
    }
    return koinViewModel(viewModelStoreOwner = parentEntry)
}
