package co.za.xdcodez.wealthbuilder.home

sealed interface HomeNavigationEvent {

    data class NavigateToCreateBudget(val monthId: String) : HomeNavigationEvent
    data class NavigateToBudgetDetails(val monthId: String) : HomeNavigationEvent
    data class NavigateToCreateGoal(val monthId: String) : HomeNavigationEvent
    data class NavigateToDayDetails(
        val date: String,
        val monthIndex: Int,
        val year: Int
    ) : HomeNavigationEvent
}