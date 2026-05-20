package co.za.xdcodes.level_up.navigation

sealed class Destination(val route: String) {
    object TaskListDestination: Destination("task_list")
    object BudgetTransactionsDestination: Destination("budget_transaction/{monthId}/{categoryId}") {
        fun createRoute(monthId: String, categoryId: String) = "budget_transaction/$monthId/$categoryId"
    }
    object SetupBudgetDestination: Destination("setup_budget/{monthIndex}/{year}") {
        fun createRoute(monthIndex: Int, year: Int) = "setup_budget/$monthIndex/$year"
    }
    object JournalConfigDestination: Destination("journal_config")
    object JournalDayDetailDestination : Destination("journal_day/{date}/{monthIndex}/{year}") {
        fun createRoute(date: String, monthIndex: Int, year: Int) =
            "journal_day/$date/$monthIndex/$year"
    }
    object CreateWorkoutStepper: Destination("create_workout_stepper")
}