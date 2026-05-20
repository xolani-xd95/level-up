package com.trading.journal.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.trading.journal.domain.model.Trade
import com.trading.journal.ui.theme.TradingColors

@Composable
fun TradeCard(
    trade: Trade,
    onClick: () -> Unit = {},
    onDelete: (() -> Unit)? = null,
    modifier: Modifier = Modifier,
) {
    var showDeleteConfirm by remember { mutableStateOf(false) }

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surfaceVariant,
        tonalElevation = 0.dp,
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // ── Top row ──
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                // Symbol + direction
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = trade.symbol,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Medium,
                            fontSize = 16.sp,
                        ),
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                    Spacer(Modifier.height(4.dp))
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        DirectionBadge(trade.direction)
                        ResultChip(trade.pnl, trade.status)
                        Text(
                            text = trade.date.toString(),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
                // PnL
                Column(horizontalAlignment = Alignment.End) {
                    PnlText(
                        pnl = trade.pnl,
                        style = MaterialTheme.typography.titleMedium.copy(fontSize = 17.sp),
                    )
                    if (trade.pnlPercent != null) {
                        val pct = trade.pnlPercent!!
                        Text(
                            text = "${(pct)}%",
                            style = MaterialTheme.typography.labelSmall,
                            color = if (pct >= 0) TradingColors.Profit else TradingColors.Loss,
                        )
                    }
                }
            }

            // ── Middle detail row ──
            Spacer(Modifier.height(10.dp))
            ThinDivider()
            Spacer(Modifier.height(10.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(20.dp),
            ) {
                PriceDetail("Entry", "${(trade.entryPrice)}")
                PriceDetail("Exit", trade.exitPrice?.let { "${(it)}" } ?: "Open")
                PriceDetail("Qty", "${(trade.quantity)}")
                if (trade.riskRewardRatio != null)
                    PriceDetail("R:R", "${(trade.riskRewardRatio!!)}")
            }

            // ── Strategy + tags ──
            if (trade.strategy.isNotBlank() || trade.tags.isNotEmpty()) {
                Spacer(Modifier.height(10.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    if (trade.strategy.isNotBlank()) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(MaterialTheme.colorScheme.primaryContainer)
                                .padding(horizontal = 8.dp, vertical = 3.dp),
                        ) {
                            Text(
                                text = trade.strategy,
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                            )
                        }
                    }
                    trade.tags.take(3).forEach { TagChip(it) }
                }
            }

            // ── Delete action ──
            if (onDelete != null) {
                Spacer(Modifier.height(10.dp))
                if (showDeleteConfirm) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        Text(
                            "Delete this trade?",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                        TextButton(
                            onClick = { onDelete(); showDeleteConfirm = false },
                            colors = ButtonDefaults.textButtonColors(contentColor = TradingColors.Loss),
                        ) { Text("Delete") }
                        TextButton(onClick = { showDeleteConfirm = false }) { Text("Cancel") }
                    }
                } else {
                    TextButton(
                        onClick = { showDeleteConfirm = true },
                        contentPadding = PaddingValues(0.dp),
                        colors = ButtonDefaults.textButtonColors(
                            contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        ),
                    ) {
                        Icon(
                            Icons.Outlined.Delete,
                            contentDescription = "Delete trade",
                            modifier = Modifier.size(14.dp),
                        )
                        Spacer(Modifier.width(4.dp))
                        Text("Delete", style = MaterialTheme.typography.labelSmall)
                    }
                }
            }
        }
    }
}

@Composable
private fun PriceDetail(label: String, value: String) {
    Column {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
            color = MaterialTheme.colorScheme.onSurface,
        )
    }
}
