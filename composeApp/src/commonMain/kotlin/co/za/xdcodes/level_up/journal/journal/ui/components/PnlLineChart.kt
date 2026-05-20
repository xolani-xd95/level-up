package com.trading.journal.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.trading.journal.ui.theme.TradingColors

@Composable
fun PnlLineChart(
    cumulativePnl: List<Double>,
    modifier: Modifier = Modifier,
) {
    val lineColor = if (cumulativePnl.lastOrNull() ?: 0.0 >= 0) TradingColors.Profit else TradingColors.Loss
    val gridColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)

    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(180.dp),
    ) {
        if (cumulativePnl.size < 2) {
            drawEmptyState(gridColor)
            return@Canvas
        }

        val minY = cumulativePnl.min()
        val maxY = cumulativePnl.max()
        val rangeY = (maxY - minY).coerceAtLeast(1.0)
        val w = size.width
        val h = size.height
        val padding = 16f

        fun xOf(i: Int) = padding + (i.toFloat() / (cumulativePnl.size - 1)) * (w - 2 * padding)
        fun yOf(v: Double) = h - padding - ((v - minY) / rangeY * (h - 2 * padding)).toFloat()

        // Zero line
        val zeroY = yOf(0.0)
        if (zeroY in 0f..h) {
            drawLine(
                color = gridColor,
                start = Offset(padding, zeroY),
                end = Offset(w - padding, zeroY),
                strokeWidth = 1f,
            )
        }

        // Grid lines (3 horizontal)
        for (i in 1..3) {
            val gy = h * i / 4f
            drawLine(gridColor, Offset(padding, gy), Offset(w - padding, gy), strokeWidth = 0.5f)
        }

        // Fill path
        val fillPath = Path().apply {
            moveTo(xOf(0), yOf(cumulativePnl[0]))
            cumulativePnl.forEachIndexed { i, v -> if (i > 0) lineTo(xOf(i), yOf(v)) }
            lineTo(xOf(cumulativePnl.lastIndex), h - padding)
            lineTo(xOf(0), h - padding)
            close()
        }
        drawPath(
            fillPath,
            brush = Brush.verticalGradient(
                colors = listOf(lineColor.copy(alpha = 0.25f), Color.Transparent),
                startY = 0f,
                endY = h,
            ),
        )

        // Line path
        val linePath = Path().apply {
            moveTo(xOf(0), yOf(cumulativePnl[0]))
            cumulativePnl.forEachIndexed { i, v -> if (i > 0) lineTo(xOf(i), yOf(v)) }
        }
        drawPath(linePath, color = lineColor, style = Stroke(width = 2.5f))

        // Last point dot
        val lastX = xOf(cumulativePnl.lastIndex)
        val lastY = yOf(cumulativePnl.last())
        drawCircle(lineColor, radius = 5f, center = Offset(lastX, lastY))
        drawCircle(Color.Transparent, radius = 3f, center = Offset(lastX, lastY))
    }
}

private fun DrawScope.drawEmptyState(color: Color) {
    val h = size.height
    val w = size.width
    for (i in 1..3) {
        drawLine(color, Offset(0f, h * i / 4f), Offset(w, h * i / 4f), strokeWidth = 0.5f)
    }
}
