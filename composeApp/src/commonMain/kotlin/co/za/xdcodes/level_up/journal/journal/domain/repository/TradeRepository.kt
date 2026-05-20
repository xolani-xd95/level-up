package com.trading.journal.domain.repository

import com.trading.journal.domain.model.Trade
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.datetime.LocalDate

interface TradeRepository {
    fun observeAll(): Flow<List<Trade>>
    suspend fun upsert(trade: Trade)
    suspend fun delete(id: String)
    suspend fun getById(id: String): Trade?
}

class InMemoryTradeRepository : TradeRepository {
    private val _trades = MutableStateFlow<List<Trade>>(emptyList())

    override fun observeAll(): Flow<List<Trade>> =
        _trades.map { it.sortedByDescending { t -> t.date } }

    override suspend fun upsert(trade: Trade) {
        _trades.update { current ->
            val idx = current.indexOfFirst { it.id == trade.id }
            if (idx >= 0) current.toMutableList().also { it[idx] = trade }
            else current + trade
        }
    }

    override suspend fun delete(id: String) {
        _trades.update { it.filter { t -> t.id != id } }
    }

    override suspend fun getById(id: String): Trade? =
        _trades.value.firstOrNull { it.id == id }
}
