package co.za.xdcodes.level_up.finance.domain.dto

import kotlinx.serialization.Serializable
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@Serializable
data class BudgetCategoryModel(
    val id: String,
    val name: String,
    val budget : Double,
    val totalPaid: Double,
) {
    val budgetRemaining: Double
        get() = budget - totalPaid
}

@Serializable
data class CategoryTransaction @OptIn(ExperimentalUuidApi::class) constructor(
    val id: String = Uuid.random().toString(),
    val name: String = "",
    val amount: Double = 0.0,
    val isPaid: Boolean = false,
    val date: String = "",
)