package co.za.xdcodez.wealthbuilder.finance.presentation.dashboard

import co.za.xdcodez.wealthbuilder.finance.domain.dto.BudgetMonthModel
import co.za.xdcodez.wealthbuilder.finance.domain.dto.Goal

data class BudgetDashboardState(
    val isLoading: Boolean = true,
    val currentMonthSummary: BudgetMonthModel? = null,
    val goals: List<Goal> = emptyList(),
    val error: String? = null
)
