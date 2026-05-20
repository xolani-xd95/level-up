package co.za.xdcodes.level_up.navigation

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
import co.za.xdcodes.level_up.common.monthId
import co.za.xdcodes.level_up.dashboard.presentation.DashboardScreenRoute
import co.za.xdcodes.level_up.dashboard.presentation.DashboardViewModel
import co.za.xdcodes.level_up.dashboard.presentation.TaskListScreenRoot
import co.za.xdcodes.level_up.finance.presentation.budgetOverview.BudgetScreenRoute
import co.za.xdcodes.level_up.finance.presentation.budgetOverview.BudgetScreenViewModel
import co.za.xdcodes.level_up.finance.presentation.budgetTransactions.BudgetTransactionScreenRoute
import co.za.xdcodes.level_up.finance.presentation.budgetTransactions.TransactionsViewModel
import co.za.xdcodes.level_up.finance.presentation.createbudget.CreateBudgetScreenRoute
import co.za.xdcodes.level_up.journal.presentation.details.DayDetailNavigationEvent
import co.za.xdcodes.level_up.journal.presentation.details.DayDetailScreenRoute
import co.za.xdcodes.level_up.journal.presentation.home.JournalHomeActions
import co.za.xdcodes.level_up.journal.presentation.home.JournalHomeNavigationEvent
import co.za.xdcodes.level_up.journal.presentation.home.JournalHomeScreen
import co.za.xdcodes.level_up.journal.presentation.home.JournalHomeScreenRoute
import co.za.xdcodes.level_up.journal.presentation.home.JournalHomeViewModel
import co.za.xdcodes.level_up.journal.presentation.setup.JournalSetupScreenRoute
import co.za.xdcodes.level_up.journal.presentation.setup.JournalSetupViewModel
import co.za.xdcodes.level_up.navigation.Destination.BudgetTransactionsDestination
import co.za.xdcodes.level_up.navigation.Destination.JournalConfigDestination
import co.za.xdcodes.level_up.navigation.Destination.SetupBudgetDestination
import org.koin.compose.KoinContext
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.annotation.KoinExperimentalAPI

@Composable
fun RootNavigationGraph(navController: NavHostController, paddingValues: PaddingValues) {
    KoinContext {
        NavHost(
            navController = navController,
            startDestination = BottomNavDestination.Dashboard.route,
            modifier = Modifier.padding(paddingValues)
        ) {
            /**
             * Dashboard navigation screens
             * */
            composable(BottomNavDestination.Dashboard.route) { entry ->
                val viewModel: DashboardViewModel =
                    entry.sharedViewModel(
                        navController,
                        BottomNavDestination.Dashboard.route
                    )
                DashboardScreenRoute(viewModel) {
                    navController.navigate(Destination.TaskListDestination.route)
                }
            }
            composable(Destination.TaskListDestination.route) { entry ->
                val viewModel: DashboardViewModel =
                    entry.sharedViewModel(
                        navController,
                        BottomNavDestination.Dashboard.route
                    )
                TaskListScreenRoot(viewModel) {
                    navController.navigateUp()
                }
            }

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
                    onNavigateToBudgetSetup = { monthIndex, year ->
                        navController.navigate(SetupBudgetDestination.createRoute(monthIndex, year))
                    }
                )
            }

            composable(
                route = SetupBudgetDestination.route,
                arguments = listOf(
                    navArgument("monthIndex") { type = NavType.IntType },
                    navArgument("year") { type = NavType.IntType }
                )) { entry ->
                CreateBudgetScreenRoute(
                    monthIndex = entry.arguments?.getInt("monthIndex") ?: 1,
                    year = entry.arguments?.getInt("year") ?: 2026,
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
             * Profile navigation screens
             * */
            composable(BottomNavDestination.Profile.route) {
                DashboardScreenRoute() { }
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
