package co.za.xdcodez.wealthbuilder.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
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
import co.za.xdcodez.wealthbuilder.habits.presentation.today.TodayCheckInScreenRoute
import co.za.xdcodez.wealthbuilder.journal.presentation.details.DayDetailNavigationEvent
import co.za.xdcodez.wealthbuilder.journal.presentation.details.DayDetailScreenRoute
import co.za.xdcodez.wealthbuilder.journal.presentation.home.JournalHomeActions
import co.za.xdcodez.wealthbuilder.journal.presentation.home.JournalHomeNavigationEvent
import co.za.xdcodez.wealthbuilder.journal.presentation.home.JournalHomeScreenRoute
import co.za.xdcodez.wealthbuilder.journal.presentation.home.JournalHomeViewModel
import co.za.xdcodez.wealthbuilder.journal.presentation.setup.JournalSetupScreenRoute
import co.za.xdcodez.wealthbuilder.journal.presentation.setup.JournalSetupViewModel
import co.za.xdcodez.wealthbuilder.navigation.Destination.BudgetTransactionsDestination
import co.za.xdcodez.wealthbuilder.navigation.Destination.JournalConfigDestination
import co.za.xdcodez.wealthbuilder.navigation.Destination.SetupBudgetDestination
import org.koin.compose.KoinContext
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.annotation.KoinExperimentalAPI

@Composable
fun RootNavigationGraph(navController: NavHostController, paddingValues: PaddingValues) {
    KoinContext {
        NavHost(
            navController = navController,
            startDestination = BottomNavDestination.Habits.route,
            modifier = Modifier.padding(paddingValues)
        ) {
            /**
             * Finance navigation screens
             * */
            composable(BottomNavDestination.Finance.route) { entry ->
                val viewModel: BudgetScreenViewModel = entry.sharedViewModel(
                    navController,
                    BottomNavDestination.Finance.route
                )
                val navBackStack by navController.currentBackStackEntryAsState()
                LaunchedEffect(navBackStack?.destination?.route) {
                    if (navBackStack?.destination?.route == BottomNavDestination.Finance.route) {
                        viewModel.onReturnFromSetup()
                    }
                }

                BudgetScreenRoute(
                    onNavigateToTransactions = { monthId, categoryId ->
                        navController.navigate(BudgetTransactionsDestination.createRoute(monthId, categoryId))
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
                        BottomNavDestination.Finance.route
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

            /**
             * Journal navigation screens
             * */
            composable(BottomNavDestination.Journal.route) { entry ->
                val viewModel: JournalHomeViewModel = entry.sharedViewModel(
                    navController,
                    BottomNavDestination.Journal.route
                )
                val state by viewModel.state.collectAsState()

                val navBackStack by navController.currentBackStackEntryAsState()
                LaunchedEffect(navBackStack?.destination?.route) {
                    if (navBackStack?.destination?.route == BottomNavDestination.Journal.route) {
                        viewModel.onAction(JournalHomeActions.Refresh)
                    }
                }

                JournalHomeScreenRoute(
                    viewModel,
                    onNavigate = { event ->
                        when (event) {
                            is JournalHomeNavigationEvent.ToSetup -> {
                                navController.navigate(
                                    JournalConfigDestination.route
                                )
                            }
                            is JournalHomeNavigationEvent.ToDayDetail -> {
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
                )
            }

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
            composable(JournalConfigDestination.route) { entry ->
                val viewModel: JournalSetupViewModel = entry.sharedViewModel(
                    navController,
                    BottomNavDestination.Journal.route
                )
                JournalSetupScreenRoute(
                    monthIndex = 1,
                    year = 2026,
                    journalSetupViewModel = viewModel,
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            /**
             * Habits navigation screens
             * */
            composable(BottomNavDestination.Habits.route) {
                TodayCheckInScreenRoute()
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
