package com.trading.journal.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.trading.journal.domain.model.TradeDirection
import com.trading.journal.domain.model.TradeStatus
import com.trading.journal.ui.theme.TradingColors

// ── Stat metric card ────────────────────────────────────────────────────────

@Composable
fun StatCard(
    label: String,
    value: String,
    valueColor: Color = MaterialTheme.colorScheme.onSurface,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surfaceVariant,
        tonalElevation = 0.dp,
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                letterSpacing = 0.6.sp,
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Medium,
                    fontSize = 22.sp,
                ),
                color = valueColor,
            )
        }
    }
}

// ── Direction badge ──────────────────────────────────────────────────────────

@Composable
fun DirectionBadge(direction: TradeDirection) {
    val (bg, fg, label) = when (direction) {
        TradeDirection.LONG  -> Triple(TradingColors.ProfitBg, TradingColors.Profit, "LONG")
        TradeDirection.SHORT -> Triple(TradingColors.LossBg,   TradingColors.Loss,   "SHORT")
    }
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(4.dp))
            .background(bg)
            .padding(horizontal = 8.dp, vertical = 3.dp),
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.Medium,
                letterSpacing = 0.8.sp,
                fontSize = 10.sp,
            ),
            color = fg,
        )
    }
}

// ── Result / status chip ─────────────────────────────────────────────────────

@Composable
fun ResultChip(pnl: Double?, status: TradeStatus) {
    val (bg, fg, text) = when {
        status == TradeStatus.OPEN       -> Triple(TradingColors.AmberBg, TradingColors.Amber, "OPEN")
        pnl != null && pnl >= 0         -> Triple(TradingColors.ProfitBg, TradingColors.Profit, "WIN")
        else                             -> Triple(TradingColors.LossBg,  TradingColors.Loss,   "LOSS")
    }
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(4.dp))
            .background(bg)
            .padding(horizontal = 8.dp, vertical = 3.dp),
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.Medium,
                letterSpacing = 0.8.sp,
                fontSize = 10.sp,
            ),
            color = fg,
        )
    }
}

// ── PnL text ─────────────────────────────────────────────────────────────────

@Composable
fun PnlText(pnl: Double?, style: androidx.compose.ui.text.TextStyle = MaterialTheme.typography.bodyMedium) {
    if (pnl == null) {
        Text("—", style = style, color = TradingColors.Amber)
        return
    }
    val sign = if (pnl >= 0) "+" else ""
    val color = if (pnl >= 0) TradingColors.Profit else TradingColors.Loss
    Text(
        text = "$sign$${(pnl)}",
        style = style,
        color = color,
        fontWeight = FontWeight.Medium,
    )
}

// ── Tag chip ──────────────────────────────────────────────────────────────────

@Composable
fun TagChip(tag: String) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(4.dp))
            .border(
                0.5.dp,
                MaterialTheme.colorScheme.outline,
                RoundedCornerShape(4.dp),
            )
            .padding(horizontal = 8.dp, vertical = 3.dp),
    ) {
        Text(
            text = tag,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

// ── Section header ────────────────────────────────────────────────────────────

@Composable
fun SectionHeader(title: String, modifier: Modifier = Modifier) {
    Text(
        text = title.uppercase(),
        style = MaterialTheme.typography.labelSmall.copy(
            fontWeight = FontWeight.Medium,
            letterSpacing = 1.2.sp,
            fontSize = 11.sp,
        ),
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = modifier,
    )
}

// ── Divider ───────────────────────────────────────────────────────────────────

@Composable
fun ThinDivider(modifier: Modifier = Modifier) {
    HorizontalDivider(
        modifier = modifier,
        thickness = 0.5.dp,
        color = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
    )
}
