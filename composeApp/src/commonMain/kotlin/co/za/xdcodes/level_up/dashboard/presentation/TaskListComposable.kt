package co.za.xdcodes.level_up.dashboard.presentation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.outlined.KeyboardArrowDown
import androidx.compose.material.icons.outlined.KeyboardArrowUp
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import co.za.xdcodes.level_up.common.widgets.CustomAppTopBar
import co.za.xdcodes.level_up.dashboard.domain.dto.TaskCategory
import co.za.xdcodes.level_up.dashboard.domain.dto.TaskModel
import co.za.xdcodes.level_up.dashboard.domain.dto.TaskPriority
import co.za.xdcodes.level_up.dashboard.domain.dto.TaskState
import co.za.xdcodes.level_up.theme.LevelUpTheme
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn
import me.saket.swipe.SwipeAction
import me.saket.swipe.SwipeableActionsBox
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.annotation.KoinExperimentalAPI
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid


@OptIn(KoinExperimentalAPI::class)
@Composable
fun TaskListScreenRoot(
    viewModel: DashboardViewModel = koinViewModel<DashboardViewModel>(),
    onNavigateUp: () -> Unit
) {
    val state by viewModel.state.collectAsState()

    TaskListComposable(
        state = state,
        onNavigateUp = onNavigateUp
    ) {
        viewModel.onAction(it)
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TaskListComposable(
    state: DashboardState,
    onNavigateUp: () -> Unit,
    onTaskAction: (DashboardActions) -> Unit
) {
    val scope = rememberCoroutineScope()
    val pagerState = rememberPagerState(pageCount = { TaskState.entries.size }, initialPage = 0)
    val selectedTabIndex by remember {
        derivedStateOf { pagerState.currentPage }
    }

    CustomAppTopBar(
        title = "non-negotiables",
        modifier = Modifier.fillMaxSize().padding(horizontal = 12.dp),
        showNavigationIcon = true,
        onNavigate = onNavigateUp
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Tab Navigation
                TabRow(
                    modifier = Modifier.fillMaxWidth(),
                    selectedTabIndex = selectedTabIndex,
                    containerColor = Color.Transparent,
                    divider = { }
                ) {
                    TaskState.entries.forEachIndexed { index, taskState ->
                        val taskCount = state.taskForToday.count { it.state == taskState }
                        Tab(
                            modifier = Modifier.fillMaxWidth(),
                            selected = selectedTabIndex == index,
                            onClick = {
                                scope.launch {
                                    pagerState.animateScrollToPage(taskState.ordinal)
                                }
                            },
                            text = {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        taskState.text,
                                        style = TextStyle(color = Color.White)
                                    )
                                    if (taskCount > 0) {
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Box(
                                            modifier = Modifier
                                                .size(20.dp)
                                                .background(
                                                    color = when (taskState) {
                                                        TaskState.TODO -> Color(0xFFD4AF37)
                                                        TaskState.STARTED -> MaterialTheme.colorScheme.primary
                                                        TaskState.DONE -> Color.Green.copy(alpha = 0.7f)
                                                    },
                                                    shape = CircleShape
                                                ),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = taskCount.toString(),
                                                style = MaterialTheme.typography.labelSmall,
                                                color = Color.White
                                            )
                                        }
                                    }
                                }
                            }
                        )
                    }
                }

                // Task List Pager
                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier.weight(1f)
                ) { page ->
                    val filteredTasks =
                        state.taskForToday.filter { it.state == TaskState.entries[page] }

                    if (filteredTasks.isEmpty()) {
                        EmptyTaskState(taskState = TaskState.entries[page])
                    } else {
                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(top = 8.dp, bottom = 100.dp) // Extra padding for bottom bar
                        ) {
                            items(filteredTasks, key = { it.id }) { task ->
                                SwipeableTaskItem(
                                    task = task,
                                    onStart = { onTaskAction(DashboardActions.OnStartTask(it)) },
                                    onComplete = { onTaskAction(DashboardActions.OnCompleteTask(it)) },
                                    onDelete = { onTaskAction(DashboardActions.OnDeleteTask(it)) },
                                    modifier = Modifier.animateItemPlacement()
                                )
                            }
                        }
                    }
                }
            }

            // Quick Add Bar anchored at the bottom
            QuickAddTaskBar(
                onAddTask = { taskContent ->
                    onTaskAction(DashboardActions.OnTaskAdded(taskContent))
                },
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 8.dp)
            )
        }
    }
}

@OptIn(ExperimentalUuidApi::class)
@Composable
fun QuickAddTaskBar(
    onAddTask: (TaskModel) -> Unit,
    modifier: Modifier = Modifier
) {
    var taskText by remember { mutableStateOf("") }
    var showAdvanced by remember { mutableStateOf(false) }

    val timeZone = TimeZone.currentSystemDefault()
    val currentDate = Clock.System.todayIn(timeZone).toString()

    Column(
        modifier = modifier
            .fillMaxWidth()
            .drawWithContent {
                drawContent()
                // Top shadow for elevation effect
                drawRect(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color.Black.copy(alpha = 0.3f),
                            Color.Transparent
                        ),
                        startY = 0f,
                        endY = 20f
                    )
                )
            }
            .clip(RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp))
            .background(Color(0xFF1B1B1B)) // Solid dark background
            .padding(12.dp)
    ) {
        // Quick add row
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedTextField(
                value = taskText,
                onValueChange = { taskText = it },
                modifier = Modifier.weight(1f),
                placeholder = { Text("Add a task...", color = Color.White.copy(alpha = 0.5f)) },
                textStyle = TextStyle(color = Color.White),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = Color.White.copy(alpha = 0.3f),
                    focusedBorderColor = MaterialTheme.colorScheme.primary
                ),
                singleLine = true,
                shape = RoundedCornerShape(8.dp)
            )

            IconButton(
                onClick = {
                    if (taskText.isNotBlank()) {
                        val inferredCategory = inferCategory(taskText)
                        val inferredDuration = inferDuration(taskText, inferredCategory)
                        val inferredPriority = inferPriority(taskText)

                        val task = TaskModel(
                            id = Uuid.random().toString(),
                            content = taskText,
                            duration = inferredDuration,
                            category = inferredCategory,
                            date = currentDate,
                            state = TaskState.TODO,
                            priority = inferredPriority
                        )
                        onAddTask(task)
                        taskText = ""
                    }
                },
                modifier = Modifier
                    .size(48.dp)
                    .background(
                        color = if (taskText.isNotBlank()) MaterialTheme.colorScheme.primary
                        else Color.White.copy(alpha = 0.1f),
                        shape = CircleShape
                    )
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add task",
                    tint = Color.White
                )
            }
        }

        // Quick hint text
        if (taskText.isNotBlank()) {
            val category = inferCategory(taskText)
            val duration = inferDuration(taskText, category)
            val priority = inferPriority(taskText)

            Text(
                text = "${category.category} • $duration • ${priority.priority}",
                style = MaterialTheme.typography.labelSmall,
                color = Color.White.copy(alpha = 0.5f),
                modifier = Modifier.padding(top = 4.dp, start = 4.dp)
            )
        }
    }
}

/**
 * Smart category inference based on keywords in task text
 */
fun inferCategory(text: String): TaskCategory {
    val lowerText = text.lowercase()
    return when {
        lowerText.contains("workout") || lowerText.contains("gym") || lowerText.contains("exercise") -> TaskCategory.WORKOUT
        lowerText.contains("run") || lowerText.contains("jog") || lowerText.contains("cardio") -> TaskCategory.RUNNING
        lowerText.contains("trade") || lowerText.contains("invest") || lowerText.contains("stock") -> TaskCategory.TRADING
        lowerText.contains("focus") || lowerText.contains("study") || lowerText.contains("work") -> TaskCategory.FOCUS
        lowerText.contains("health") || lowerText.contains("meditate") || lowerText.contains("sleep") -> TaskCategory.HEALTH
        else -> TaskCategory.FOCUS // default
    }
}

/**
 * Smart duration inference based on keywords and category
 */
fun inferDuration(text: String, category: TaskCategory): String {
    val lowerText = text.lowercase()

    // Explicit duration keywords
    return when {
        lowerText.contains("quick") || lowerText.contains("fast") || lowerText.contains("brief") -> "15min"
        lowerText.contains("short") -> "30min"
        lowerText.contains("long") || lowerText.contains("detailed") || lowerText.contains("deep") -> "2hr"
        lowerText.contains("extended") -> "3hr"
        // Category-based defaults
        else -> when (category) {
            TaskCategory.WORKOUT -> "1hr"
            TaskCategory.RUNNING -> "45min"
            TaskCategory.TRADING -> "2hr"
            TaskCategory.FOCUS -> "1hr"
            TaskCategory.HEALTH -> "30min"
        }
    }
}

/**
 * Smart priority inference based on keywords and punctuation
 */
fun inferPriority(text: String): TaskPriority {
    val lowerText = text.lowercase()

    return when {
        // High priority indicators
        text.contains("!") ||
        lowerText.contains("urgent") ||
        lowerText.contains("asap") ||
        lowerText.contains("critical") ||
        lowerText.contains("important") ||
        lowerText.contains("must") ||
        lowerText.contains("need to") -> TaskPriority.HIGH

        // Low priority indicators
        lowerText.contains("maybe") ||
        lowerText.contains("optional") ||
        lowerText.contains("consider") ||
        lowerText.contains("think about") ||
        lowerText.contains("sometime") ||
        lowerText.contains("eventually") -> TaskPriority.LOW

        // Default to medium
        else -> TaskPriority.MEDIUM
    }
}

@Composable
fun EmptyTaskState(taskState: TaskState) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = when (taskState) {
                    TaskState.TODO -> "No tasks to do"
                    TaskState.STARTED -> "No tasks in progress"
                    TaskState.DONE -> "No completed tasks"
                },
                style = MaterialTheme.typography.bodyLarge,
                color = Color.White.copy(alpha = 0.5f)
            )
            Text(
                text = when (taskState) {
                    TaskState.TODO -> "Add a new task above"
                    TaskState.STARTED -> "Start a task from TODO"
                    TaskState.DONE -> "Complete tasks to see them here"
                },
                style = MaterialTheme.typography.bodySmall,
                color = Color.White.copy(alpha = 0.3f),
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}

@Composable
fun SwipeableTaskItem(
    task: TaskModel,
    onStart: (String) -> Unit,
    onComplete: (String) -> Unit,
    onDelete: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var isExpanded by remember { mutableStateOf(false) }

    val color = when (task.priority) {
        TaskPriority.LOW -> Color.Yellow
        TaskPriority.MEDIUM -> Color(0xFF1E88E5)
        TaskPriority.HIGH -> Color.Red
    }

    // Define swipe actions
    val deleteAction = SwipeAction(
        icon = {
            Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = "Delete",
                tint = Color.White,
                modifier = Modifier.padding(16.dp)
            )
        },
        background = Color.Red,
        onSwipe = { onDelete(task.id) }
    )

    val progressAction = when (task.state) {
        TaskState.TODO -> SwipeAction(
            icon = {
                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = "Start",
                    tint = Color.White,
                    modifier = Modifier.padding(16.dp)
                )
            },
            background = MaterialTheme.colorScheme.primary,
            onSwipe = { onStart(task.id) }
        )
        TaskState.STARTED -> SwipeAction(
            icon = {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Complete",
                    tint = Color.White,
                    modifier = Modifier.padding(16.dp)
                )
            },
            background = Color.Green,
            onSwipe = { onComplete(task.id) }
        )
        TaskState.DONE -> null
    }

    SwipeableActionsBox(
        modifier = modifier,
        startActions = listOf(deleteAction),
        endActions = if (progressAction != null) listOf(progressAction) else emptyList()
    ) {
        // Task card content
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(color.copy(alpha = 0.05f))
                .drawWithContent {
                    drawContent()
                    drawRect(
                        brush = Brush.verticalGradient(
                            listOf(Color.White.copy(0.12f), Color.Transparent)
                        )
                    )
                    val strokeWidth = 4.dp.toPx()
                    drawLine(
                        color = color,
                        start = Offset(0f, 0f),
                        end = Offset(0f, size.height),
                        strokeWidth = strokeWidth
                    )
                }
                .padding(start = 4.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .padding(horizontal = 12.dp, vertical = 12.dp)
                    .fillMaxWidth()
                    .clickable { isExpanded = !isExpanded }
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        task.content,
                        style = MaterialTheme.typography.bodyLarge.copy(color = Color.White),
                        maxLines = if (isExpanded) Int.MAX_VALUE else 1
                    )

                    Row(
                        modifier = Modifier.padding(top = 6.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        TaskMetaChip(
                            text = task.category.category,
                            color = color
                        )
                        TaskMetaChip(
                            text = task.duration,
                            color = Color.White.copy(alpha = 0.3f)
                        )
                    }
                }

                Icon(
                    imageVector = if (isExpanded) Icons.Outlined.KeyboardArrowUp else Icons.Outlined.KeyboardArrowDown,
                    tint = Color.White.copy(alpha = 0.5f),
                    contentDescription = if (isExpanded) "Collapse" else "Expand"
                )
            }

            AnimatedVisibility(visible = isExpanded) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    TextButton(onClick = { onDelete(task.id) }) {
                        Text("DELETE", color = Color.Red)
                    }

                    when (task.state) {
                        TaskState.TODO -> TextButton(onClick = { onStart(task.id) }) {
                            Text("START", color = MaterialTheme.colorScheme.primary)
                        }
                        TaskState.STARTED -> TextButton(onClick = { onComplete(task.id) }) {
                            Text("COMPLETE", color = Color.Green)
                        }
                        TaskState.DONE -> Text(
                            "✓ Completed",
                            style = MaterialTheme.typography.labelMedium,
                            color = Color.Green.copy(alpha = 0.7f),
                            modifier = Modifier.padding(8.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun TaskMetaChip(text: String, color: Color) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(color.copy(alpha = 0.2f))
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall,
            color = color
        )
    }
}


@Preview
@Composable
fun TaskItemSwipePreview() {
    LevelUpTheme {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            SwipeableTaskItem(
                task = TaskModel(
                    id = "1",
                    content = "Morning workout session",
                    duration = "1hr",
                    category = TaskCategory.WORKOUT,
                    date = "",
                    state = TaskState.TODO,
                    priority = TaskPriority.HIGH
                ),
                onStart = {},
                onComplete = {},
                onDelete = {}
            )

            SwipeableTaskItem(
                task = TaskModel(
                    id = "2",
                    content = "Running 5km",
                    duration = "30min",
                    category = TaskCategory.RUNNING,
                    date = "",
                    state = TaskState.STARTED,
                    priority = TaskPriority.MEDIUM
                ),
                onStart = {},
                onComplete = {},
                onDelete = {}
            )
        }
    }
}

@Preview
@Composable
fun QuickAddBarPreview() {
    LevelUpTheme {
        Box(modifier = Modifier.fillMaxSize().background(Color.Black)) {
            QuickAddTaskBar(
                onAddTask = {},
                modifier = Modifier.align(Alignment.BottomCenter).padding(16.dp)
            )
        }
    }
}

@Preview
@Composable
fun TaskListComposablePreview() {
    LevelUpTheme {
        TaskListComposable(
            state = DashboardState(
                taskForToday = listOf(
                    TaskModel(
                        id = "1",
                        content = "Morning workout",
                        duration = "1hr",
                        category = TaskCategory.WORKOUT,
                        date = "",
                        state = TaskState.TODO,
                        priority = TaskPriority.MEDIUM
                    ),
                    TaskModel(
                        id = "2",
                        content = "Trading session - review and execute",
                        duration = "2hr",
                        category = TaskCategory.TRADING,
                        date = "",
                        state = TaskState.STARTED,
                        priority = TaskPriority.HIGH
                    ),
                    TaskModel(
                        id = "3",
                        content = "Evening run",
                        duration = "45min",
                        category = TaskCategory.RUNNING,
                        date = "",
                        state = TaskState.DONE,
                        priority = TaskPriority.LOW
                    ),
                )
            ),
            onNavigateUp = {},
            onTaskAction = {}
        )
    }
}
