package co.za.xdcodes.level_up.dashboard.presentation

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import co.za.xdcodes.level_up.common.dayLetter
import co.za.xdcodes.level_up.common.widgets.CardComposable
import co.za.xdcodes.level_up.dashboard.domain.dto.DailySummaryModel
import co.za.xdcodes.level_up.dashboard.domain.dto.TaskCategory
import co.za.xdcodes.level_up.dashboard.domain.dto.TaskModel
import co.za.xdcodes.level_up.dashboard.domain.dto.TaskPriority
import co.za.xdcodes.level_up.dashboard.domain.dto.TaskState
import co.za.xdcodes.level_up.theme.LevelUpTheme
import level_up.composeapp.generated.resources.D1_consistency
import level_up.composeapp.generated.resources.Res
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun ArcProgressBarComposable(
    total: Int,
    completed: Int
) {
    val progress = (completed * 100f) / total

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .size(180.dp, 105.dp)
            .padding(8.dp)
            .drawBehind {
                drawArc(
                    color = Color(0xFF111111),
                    size = Size(size.width, size.height * 2),
                    startAngle = 180f,
                    sweepAngle = 180f,
                    useCenter = false,
                    style = Stroke(width = 20f)
                )
                drawArc(
                    color = Color(0xFFD3AF37),
                    size = Size(size.width, size.height * 2),
                    startAngle = 180f,
                    sweepAngle = progress * 180 / 100,
                    useCenter = false,
                    style = Stroke(width = 20f, cap = StrokeCap.Square)
                )
            }
            .clipToBounds()
    ) {
        Text(buildAnnotatedString {
            withStyle(style = SpanStyle(fontSize = 60.sp, color = Color.White)) {
                append(completed.toString())
            }
            withStyle(style = SpanStyle(fontSize = 16.sp, color = Color.White)) {
                append("/$total")
            }
        },
            modifier = Modifier.padding(top = 30.dp))
    }
}

@Preview
@Composable
fun ArcProgressBarPreview() {
    LevelUpTheme {
        ArcProgressBarComposable(total = 90, completed = 5)
    }
}
@Composable
fun WeekDayPicker(
    days: List<DailySummaryModel>,
    selectedDate: String,
    onDaySelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    CardComposable(
        modifier = Modifier.padding(top = 8.dp)
    ) {
        Row(
            modifier = modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            days.forEach { day ->
                WeekDayItem(
                    day = day,
                    isSelected = day.isoDate == selectedDate,
                    onClick = {
                        if (!day.isFuture) {
                            onDaySelected(day.isoDate)
                        }
                    }
                )
            }
        }
    }
}


@Composable
fun WeekDayItem(
    day: DailySummaryModel,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val borderColor = when {
        isSelected -> MaterialTheme.colorScheme.primary
        day.isToday -> MaterialTheme.colorScheme.secondary
        else -> Color.White
    }

    Column(
        modifier = Modifier
            .width(60.dp)
            .height(60.dp)
            .clip(RoundedCornerShape(12.dp))
            .drawWithContent {
                drawContent()
                drawRect(
                    brush = Brush.verticalGradient(
                        listOf(Color.White.copy(0.12f), Color.Transparent)
                    )
                )
            }
            .background(color = Color(0xff363636).copy(alpha = 0.05f))
//            .border(
//                width = 1.dp,
//                color = Color.Gray,
//                shape = RoundedCornerShape(12.dp)
//            )
//            .clickable(enabled = !day.isFuture, onClick = onClick)
            .padding(vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = day.dayLetter(),
            style = MaterialTheme.typography.labelLarge,
            color = if (day.isFuture) Color.Gray else Color.White
        )

        Spacer(modifier = Modifier.height(4.dp))

        if (day.success)
            Icon(
                imageVector = Icons.Default.Check,
                tint = Color(0xFF2ECC71),
                contentDescription = ""
            )
        else
            Icon(
                imageVector = Icons.Default.Close,
                tint = Color(0xFFE74C3C),
                contentDescription = ""
            )
    }
}

