package co.za.xdcodes.level_up.finance.domain.dto

import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

data class BudgetCategoryInput @OptIn(ExperimentalUuidApi::class) constructor(
    val id: String = Uuid.random().toString(),
    val name: String = "",
    val amount: String = ""
)