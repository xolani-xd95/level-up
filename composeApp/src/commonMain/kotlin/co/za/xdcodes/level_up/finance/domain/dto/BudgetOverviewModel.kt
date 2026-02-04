package co.za.xdcodes.level_up.finance.domain.dto

import kotlinx.serialization.Serializable

@Serializable
data class BudgetOverviewModel(
    val month: String,
    val monthIndex: Int = 0,
    val moneyIn: Double,
    val moneyOut: Double,
    val savings: Double,
    val totalBudget: Double
)