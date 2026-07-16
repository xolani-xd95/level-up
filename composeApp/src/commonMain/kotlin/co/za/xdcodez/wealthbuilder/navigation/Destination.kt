package co.za.xdcodez.wealthbuilder.navigation

sealed class Destination(val route: String) {
    object BudgetTransactionsDestination: Destination("budget_transaction/{monthId}/{categoryId}") {
        fun createRoute(monthId: String, categoryId: String) = "budget_transaction/$monthId/$categoryId"
    }
    object SetupBudgetDestination: Destination("setup_budget/{monthId}") {
        fun createRoute(monthId: String) = "setup_budget/$monthId"
    }
    object JournalConfigDestination: Destination("journal_config")
    object JournalDayDetailDestination : Destination("journal_day/{date}/{monthIndex}/{year}") {
        fun createRoute(date: String, monthIndex: Int, year: Int) =
            "journal_day/$date/$monthIndex/$year"
    }
    object CreateWorkoutStepper: Destination("create_workout_stepper")
    object HabitsTodayDestination: Destination("habits_today")
    object HabitsQuarterlyGoalsDestination: Destination("habits_quarterly_goals")
}