package co.za.xdcodes.level_up.navigation

sealed class Destination(val route: String) {
    object TaskListDestination: Destination("task_list")
    object BudgetCategoryDestination: Destination("budget_category")
    object BudgetTransactionsDestination: Destination("budget_transaction")
    object CreateWorkoutStepper: Destination("create_workout_stepper")
}