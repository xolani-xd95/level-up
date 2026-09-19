package co.za.xdcodez.wealthbuilder.journal.domain.model

data class AccountBalance(
    val accountNumber: Int = 0,
    val balance: Double = 0.0,
    val updatedAt: String = ""
)
