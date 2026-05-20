package com.trading.journal.ui.screens.logtrade

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.trading.journal.domain.model.AssetClass
import com.trading.journal.domain.model.Trade
import com.trading.journal.domain.model.TradeDirection
import com.trading.journal.domain.repository.TradeRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn

data class LogTradeUiState(
    val symbol: String = "",
    val direction: TradeDirection = TradeDirection.LONG,
    val assetClass: AssetClass = AssetClass.STOCKS,
    val dateText: String = Clock.System.todayIn(TimeZone.currentSystemDefault()).toString(),
    val entryPrice: String = "",
    val exitPrice: String = "",
    val quantity: String = "1",
    val stopLoss: String = "",
    val takeProfit: String = "",
    val strategy: String = "",
    val notes: String = "",
    val tagsText: String = "",
    val isSaved: Boolean = false,
    val errors: Map<String, String> = emptyMap(),
)

class LogTradeViewModel(
    private val repository: TradeRepository,
    private val editId: String? = null,
) : ViewModel() {

    private val _uiState = MutableStateFlow(LogTradeUiState())
    val uiState = _uiState.asStateFlow()

    init {
        if (editId != null) {
            viewModelScope.launch {
                repository.getById(editId)?.let { trade ->
                    _uiState.update {
                        LogTradeUiState(
                            symbol = trade.symbol,
                            direction = trade.direction,
                            assetClass = trade.assetClass,
                            dateText = trade.date.toString(),
                            entryPrice = trade.entryPrice.toString(),
                            exitPrice = trade.exitPrice?.toString() ?: "",
                            quantity = trade.quantity.toString(),
                            stopLoss = trade.stopLoss?.toString() ?: "",
                            takeProfit = trade.takeProfit?.toString() ?: "",
                            strategy = trade.strategy,
                            notes = trade.notes,
                            tagsText = trade.tags.joinToString(", "),
                        )
                    }
                }
            }
        }
    }

    fun update(block: LogTradeUiState.() -> LogTradeUiState) = _uiState.update(block)

    fun save() {
        val s = _uiState.value
        val errors = mutableMapOf<String, String>()
        if (s.symbol.isBlank()) errors["symbol"] = "Symbol is required"
        val entry = s.entryPrice.toDoubleOrNull()
        if (entry == null) errors["entry"] = "Valid entry price required"
        if (errors.isNotEmpty()) { _uiState.update { it.copy(errors = errors) }; return }

        val date = try {
            kotlinx.datetime.LocalDate.parse(s.dateText)
        } catch (e: Exception) {
            kotlinx.datetime.Clock.System.todayIn(kotlinx.datetime.TimeZone.currentSystemDefault())
        }

        val trade = Trade(
            id = editId ?: "uuid4().toString()",
            symbol = s.symbol.uppercase().trim(),
            direction = s.direction,
            assetClass = s.assetClass,
            date = date,
            entryPrice = entry!!,
            exitPrice = s.exitPrice.toDoubleOrNull(),
            quantity = s.quantity.toDoubleOrNull() ?: 1.0,
            stopLoss = s.stopLoss.toDoubleOrNull(),
            takeProfit = s.takeProfit.toDoubleOrNull(),
            strategy = s.strategy.trim(),
            notes = s.notes.trim(),
            tags = s.tagsText.split(",").map { it.trim() }.filter { it.isNotBlank() },
        )
        viewModelScope.launch {
            repository.upsert(trade)
            _uiState.update { it.copy(isSaved = true) }
        }
    }
}
