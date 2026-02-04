package co.za.xdcodes.level_up.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import co.za.xdcodes.level_up.dashboard.presentation.DashboardScreenRoute
import co.za.xdcodes.level_up.dashboard.presentation.DashboardViewModel
import co.za.xdcodes.level_up.dashboard.presentation.TaskListComposable
import co.za.xdcodes.level_up.dashboard.presentation.TaskListScreenRoot
import co.za.xdcodes.level_up.finance.presentation.budget.BudgetCategoryScreenRoute
import co.za.xdcodes.level_up.finance.presentation.budget.BudgetScreenNavigation
import co.za.xdcodes.level_up.finance.presentation.budget.BudgetScreenRoute
import co.za.xdcodes.level_up.finance.presentation.budget.BudgetScreenViewModel
import co.za.xdcodes.level_up.finance.presentation.budget.BudgetTransactionScreenRoute
import co.za.xdcodes.level_up.navigation.Destination.BudgetCategoryDestination
import co.za.xdcodes.level_up.navigation.Destination.BudgetTransactionsDestination
import co.za.xdcodes.level_up.navigation.Destination.CreateWorkoutStepper
import co.za.xdcodes.level_up.workout.presentation.WorkoutListScreenRoot
import co.za.xdcodes.level_up.workout.presentation.create.CreateWorkoutScreenRoot
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
                val viewModel: BudgetScreenViewModel =
                    entry.sharedViewModel(
                        navController,
                        BottomNavDestination.Finance.route
                    )

                BudgetScreenRoute(viewModel) {
                    navController.navigate(BudgetCategoryDestination.route)
                }
            }
            composable(BudgetCategoryDestination.route) { entry ->
                val viewModel: BudgetScreenViewModel =
                    entry.sharedViewModel(
                        navController,
                        BottomNavDestination.Finance.route
                    )
                BudgetCategoryScreenRoute(viewModel) { destination ->
                    when (destination) {
                        BudgetScreenNavigation.NavigateToTransactions -> navController.navigate(
                            BudgetTransactionsDestination.route
                        )

                        BudgetScreenNavigation.NavigateUp -> navController.navigateUp()
                    }
                }
            }
            composable(BudgetTransactionsDestination.route) { entry ->
                val viewModel: BudgetScreenViewModel =
                    entry.sharedViewModel(
                        navController,
                        BottomNavDestination.Finance.route
                    )
                BudgetTransactionScreenRoute(viewModel) {
                    navController.navigateUp()
                }
            }

            /**
             * Workout navigation screens
             * */
            composable(BottomNavDestination.Workout.route) {
                WorkoutListScreenRoot() {
                    navController.navigate(CreateWorkoutStepper.route)
                }
            }
            composable(CreateWorkoutStepper.route) {
                CreateWorkoutScreenRoot(onNavigate = {
                    navController.navigateUp()
                })
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
