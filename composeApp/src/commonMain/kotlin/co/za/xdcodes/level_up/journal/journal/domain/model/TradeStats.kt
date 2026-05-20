package com.trading.journal.domain.model

data class TradeStats(
    val totalTrades: Int,
    val closedTrades: Int,
    val openTrades: Int,
    val wins: Int,
    val losses: Int,
    val winRate: Double,
    val totalPnl: Double,
    val avgWin: Double,
    val avgLoss: Double,
    val profitFactor: Double,
    val avgRiskReward: Double,
    val bestTrade: Double,
    val worstTrade: Double,
    val longestWinStreak: Int,
    val longestLossStreak: Int,
) {
    companion object {
        fun from(trades: List<Trade>): TradeStats {
            val closed = trades.filter { it.status == TradeStatus.CLOSED }
            val pnls = closed.mapNotNull { it.pnl }
            val wins = pnls.filter { it >= 0 }
            val losses = pnls.filter { it < 0 }

            val totalGross = wins.sum()
            val totalLoss = kotlin.math.abs(losses.sum())
            val profitFactor = if (totalLoss == 0.0) Double.MAX_VALUE else totalGross / totalLoss

            val rrs = closed.mapNotNull { it.riskRewardRatio }

            // Streak calculation
            var longestWin = 0; var currentWin = 0
            var longestLoss = 0; var currentLoss = 0
            pnls.forEach { p ->
                if (p >= 0) { currentWin++; currentLoss = 0 }
                else { currentLoss++; currentWin = 0 }
                if (currentWin > longestWin) longestWin = currentWin
                if (currentLoss > longestLoss) longestLoss = currentLoss
            }

            return TradeStats(
                totalTrades = trades.size,
                closedTrades = closed.size,
                openTrades = trades.count { it.status == TradeStatus.OPEN },
                wins = wins.size,
                losses = losses.size,
                winRate = if (closed.isEmpty()) 0.0 else wins.size.toDouble() / closed.size * 100,
                totalPnl = pnls.sum(),
                avgWin = if (wins.isEmpty()) 0.0 else wins.average(),
                avgLoss = if (losses.isEmpty()) 0.0 else losses.average(),
                profitFactor = profitFactor,
                avgRiskReward = if (rrs.isEmpty()) 0.0 else rrs.average(),
                bestTrade = if (pnls.isEmpty()) 0.0 else pnls.max(),
                worstTrade = if (pnls.isEmpty()) 0.0 else pnls.min(),
                longestWinStreak = longestWin,
                longestLossStreak = longestLoss,
            )
        }
    }
}
