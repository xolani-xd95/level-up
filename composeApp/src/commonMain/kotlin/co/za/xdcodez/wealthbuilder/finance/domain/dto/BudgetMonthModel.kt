package co.za.xdcodez.wealthbuilder.finance.domain.dto


data class BudgetMonthModel(
    val monthId: String = "",
    val startDate: String = "",  // Format: "YYYY-MM-DD" (payday start, e.g., "2026-05-27")
    val endDate: String = "",    // Format: "YYYY-MM-DD" (day before next payday, e.g., "2026-06-26")
    val incomeSources: List<IncomeSourceInput> = emptyList(),
    val categories: List<BudgetCategoryInput> = emptyList(),
    val moneyIn: Double = 0.0,
    val moneyOut: Double = 0.0,
    val totalBudget: Double = 0.0
)