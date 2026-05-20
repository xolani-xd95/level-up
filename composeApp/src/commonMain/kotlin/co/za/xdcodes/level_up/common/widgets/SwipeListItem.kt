package co.za.xdcodes.level_up.common.widgets

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import co.za.xdcodes.level_up.theme.LevelUpTheme
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun SwipeListItem(
    primaryText: String,
    secondaryText: String? = "",
    isComplete: Boolean,
    listOfActions: @Composable () -> Unit
) {
    val maxSwipe = 300f
    var offsetX by remember { mutableStateOf(0f) }

    val animatedOffset by animateFloatAsState(
        targetValue = offsetX,
        label = "swipe"
    )

    Box(
        modifier = Modifier
            .height(40.dp)
            .fillMaxWidth(),
        contentAlignment = Alignment.BottomCenter
    ) {
        if (animatedOffset < -250f) listOfActions()

        Box(
            modifier = Modifier
                .offset { IntOffset(animatedOffset.toInt(), 0) }
                .fillMaxWidth()
                .pointerInput(Unit) {
                    detectHorizontalDragGestures(
                        onHorizontalDrag = { _, dragAmount ->
                            offsetX = (offsetX + dragAmount)
                                .coerceIn(-maxSwipe, 0f) // left only
                        },
                        onDragEnd = {
                            // snap open or closed
                            offsetX = if (offsetX < -maxSwipe / 3) -maxSwipe else 0f
                        },
                        onDragCancel = { offsetX = 0f }
                    )
                }

        ) {
            Column(
                modifier = Modifier.padding(horizontal = 2.dp),
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = primaryText,
                        modifier = Modifier
                            .weight(1f),
                        style = MaterialTheme.typography.bodyMedium.copy(
                            textDecoration = if (isComplete) TextDecoration.LineThrough else TextDecoration.None,
                            color = if (isComplete) Color.Gray else Color.White
                        )
                    )

                    Text(
                        text = secondaryText.orEmpty(),
                        style = MaterialTheme.typography.bodyLarge.copy(
                            color = Color.White
                        )
                    )
                }
                HorizontalDivider(color = Color(0x1AFFFFFF))
            }
        }
    }
}

@Composable
fun SwipeActionBtn(icon: ImageVector, color: Color, backgroundColor: Color, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxHeight()
            .width(50.dp)
//            .background(color = backgroundColor)
            .clickable { onClick() },
        contentAlignment = Alignment.BottomCenter
    ) {
        Icon(
            imageVector = icon,
            tint = backgroundColor,
            contentDescription = "",
        )
    }
}

@Preview
@Composable
fun SwipeListItemPreview() {
    LevelUpTheme {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            SwipeListItem(
                primaryText = "task.content",
                secondaryText = "50min",
                isComplete = false,
                listOfActions =
                    {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End,
                            verticalAlignment = Alignment.CenterVertically
                        ) {

                            SwipeActionBtn(Icons.Default.Delete, Color.Red, Color.White) { }
                            SwipeActionBtn(Icons.Default.Delete, Color.Red, Color.White) { }

                        }
                    }
            )
            SwipeListItem(
                primaryText = "task. somehitn gto tell hmcontent",
                secondaryText = "5min",
                isComplete = false,
                listOfActions =
                    {
                        Row(
                            modifier = Modifier.fillMaxSize(),
                            horizontalArrangement = Arrangement.End,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            SwipeActionBtn(Icons.Default.Delete, Color.Red, Color.Red) { }
                        }
                    }
            )
        }
    }
}
