package co.za.xdcodez.wealthbuilder.finance.domain.dto

import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

data class IncomeSourceInput @OptIn(ExperimentalUuidApi::class) constructor(
    val id: String = Uuid.random().toString(),
    val name: String = "",
    val amount: String = ""
)