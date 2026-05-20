package co.za.xdcodes.level_up.finance.domain.dto


data class BudgetMonthModel(
    val monthId: String = "",
    val monthIndex: Int = 0,
    val year: Int = 0,
    val incomeSources: List<IncomeSourceInput> = emptyList(),
    val categories: List<BudgetCategoryInput> = emptyList(),
    val moneyIn: Double = 0.0,
    val moneyOut: Double = 0.0,
    val totalBudget: Double = 0.0
)