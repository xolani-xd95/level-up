package co.za.xdcodez.wealthbuilder.finance.presentation.dashboard

sealed interface BudgetDashboardActions {
    object ViewBudgetDetails : BudgetDashboardActions
    object CreateNewGoal : BudgetDashboardActions
    data class OnGoalClick(val goalId: String) : BudgetDashboardActions
    object Refresh : BudgetDashboardActions
}
