package co.za.xdcodes.level_up.dashboard.presentation

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.KeyboardArrowRight
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.text.toUpperCase
import androidx.compose.ui.unit.dp
import co.za.xdcodes.level_up.dashboard.domain.dto.CategoryMedalTier
import co.za.xdcodes.level_up.dashboard.domain.dto.TaskCategory
import co.za.xdcodes.level_up.dashboard.domain.dto.TaskModel
import co.za.xdcodes.level_up.dashboard.domain.dto.TaskPriority
import co.za.xdcodes.level_up.dashboard.domain.dto.TaskState
import co.za.xdcodes.level_up.theme.LevelUpTheme
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn
import level_up.composeapp.generated.resources.D1_consistency
import level_up.composeapp.generated.resources.Res
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.annotation.KoinExperimentalAPI
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(KoinExperimentalAPI::class)
@Composable
fun DashboardScreenRoute(
    viewModel: DashboardViewModel = koinViewModel<DashboardViewModel>(),
    onNavigate: () -> Unit
) {
    val state by viewModel.state.collectAsState()
    DashboardScreen(state) {
        onNavigate()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    state: DashboardState,
    onNavigate: () -> Unit
) {
    val GoldGradient = Brush.linearGradient(
        colors = listOf(
            Color(0xFF3A2E00), // deep shadow gold
            Color(0xFFBFA14A), // muted gold
            Color(0xFFD4AF37), // classic gold highlight
            Color(0xFFFFE27A)  // soft shine
        )
    )

    Column(modifier = Modifier.fillMaxSize().padding(horizontal = 12.dp)) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxSize()
        ) {
            state.badgeStreaks?.let { tier ->
                val (currentTier, nextTier) = tier

                currentTier?.let { currentTier ->
                    Text(
                        currentTier.description.toUpperCase(Locale.current),
                        modifier = Modifier.padding(vertical = 6.dp),
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.primary
                    )
//                    ArcProgressBarComposable(total = 90, completed = 5)
                    Image(
                        painter = painterResource(currentTier.drawable),
                        modifier = Modifier.size(130.dp),
                        contentDescription = ""
                    )

                    Text(
                        currentTier.title,
                        modifier = Modifier.padding(vertical = 6.dp),
                        style = MaterialTheme.typography.titleMedium,
                        color = Color(0xFFD4AF37)
                    )
                }
                nextTier?.let { nextTier ->
                    val remainingDays =
                        nextTier.daysRequired - (state.dashboardStreaks?.goalDaysCompleted ?: 0)

                    Column(
                        modifier = Modifier.fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color.White.copy(alpha = 0.05f))
                            .fillMaxWidth()
                            .drawWithContent {
                                drawContent()
                                drawRect(
                                    brush = Brush.verticalGradient(
                                        listOf(Color.White.copy(0.12f), Color.Transparent)
                                    )
                                )
                            }
                            .padding(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.Bottom
                        ) {
                            Text(
                                "${nextTier.daysRequired - remainingDays}d",
                                style = MaterialTheme.typography.labelMedium,
                                color = Color.LightGray
                            )

                            Text(
                                "next milestone: ${nextTier.title}",
                                style = MaterialTheme.typography.labelLarge,
                                color = Color.LightGray
                            )
                            Text(
                                "${nextTier.daysRequired}d",
                                style = MaterialTheme.typography.labelMedium,
                                color = Color.LightGray
                            )
                        }

                        LinearProgressIndicator(
                            progress = { remainingDays / nextTier.daysRequired.toFloat() },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(4.dp)
                                .clip(RoundedCornerShape(2.dp)),
                            color = Color(0xFFD4AF37),
                            trackColor = Color.White.copy(alpha = 0.1f)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color.White.copy(alpha = 0.05f))
                    .fillMaxWidth()
//                        .drawWithContent {
//                            drawContent()
//                            drawRect(
//                                brush = Brush.verticalGradient(
//                                    listOf(Color.White.copy(0.12f), Color.Transparent)
//                                )
//                            )
//                        }
                    .padding(6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                state.workoutStreaks?.first?.let {
                    CategoryMedalItem(it)
                }

                state.tradingStreaks?.first?.let {
                    CategoryMedalItem(it)
                }
                state.runningStreaks?.first?.let {
                    CategoryMedalItem(it)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            TaskOverviewCard(
                tasks = state.taskForToday,
                onNavigate = onNavigate
            )
        }
    }
}

@Composable
fun TaskOverviewCard(
    tasks: List<TaskModel>,
    onNavigate: () -> Unit
) {
    val totalTasks = tasks.size.coerceAtLeast(1)
    val countsByState = tasks.groupingBy { it.state }.eachCount()

    val totalTodo = countsByState[TaskState.TODO] ?: 0
    val totalStarted = countsByState[TaskState.STARTED] ?: 0
    val totalDone = countsByState[TaskState.DONE] ?: 0

    val todoRemainingProgress = (totalTodo / totalTasks.toFloat())
    val startedRemainingProgress = (totalStarted / totalTasks.toFloat())
    val doneProgress = totalDone / totalTasks.toFloat()

    Column(
        modifier = Modifier.fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(Color.White.copy(alpha = 0.05f))
            .fillMaxWidth()
//            .drawWithContent {
//                drawContent()
//                drawRect(
//                    brush = Brush.verticalGradient(
//                        listOf(Color.White.copy(0.12f), Color.Transparent)
//                    )
//                )
//            }
            .padding(bottom = 12.dp, start = 8.dp)
            .clickable {
                onNavigate()
            },
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {

        Row(
            modifier = Modifier.fillMaxWidth().padding(top = 8.dp, bottom = 2.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                "Today's Execution",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary
            )
            Icon(
                imageVector = Icons.AutoMirrored.Outlined.KeyboardArrowRight,
                contentDescription = "",
                tint = MaterialTheme.colorScheme.primary
            )
        }

        Row(
            modifier = Modifier.padding(end = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "DONE",
                modifier = Modifier.padding(end = 30.dp),
                style = MaterialTheme.typography.labelMedium,
                color = Color.Green.copy(alpha = 0.5f)
            )

            LinearProgressIndicator(
                progress = { doneProgress },
                modifier = Modifier
                    .weight(1f)
                    .height(8.dp)
                    .clip(RoundedCornerShape(2.dp)),
                color = Color.Green.copy(alpha = 0.5f),
                strokeCap = StrokeCap.Round,
                trackColor = Color.Transparent
            )

            Text(
                "$totalDone/$totalTasks completed",
                style = MaterialTheme.typography.labelMedium,
                color = Color.Green.copy(alpha = 0.5f)
            )
        }

        Row(
            modifier = Modifier.padding(end = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "STARTED",
                modifier = Modifier.padding(end = 8.dp),
                style = MaterialTheme.typography.labelMedium,
                color = Color.LightGray
            )
            LinearProgressIndicator(
                progress = { startedRemainingProgress },
                modifier = Modifier
                    .weight(1f)
                    .height(8.dp)
                    .padding(end = 8.dp)
                    .clip(RoundedCornerShape(2.dp)),
                color = MaterialTheme.colorScheme.primary,
                strokeCap = StrokeCap.Round,
                trackColor = Color.Transparent
            )

            Text(
                "$totalStarted in-progress",
                style = MaterialTheme.typography.labelMedium,
                color = Color.LightGray
            )
        }
        Row(
            modifier = Modifier.padding(end = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "TODO",
                modifier = Modifier.padding(end = 30.dp),
                style = MaterialTheme.typography.labelMedium,
                color = Color.LightGray
            )
            LinearProgressIndicator(
                progress = { todoRemainingProgress },
                modifier = Modifier
                    .weight(1f)
                    .height(8.dp)
                    .padding(end = 8.dp)
                    .clip(RoundedCornerShape(2.dp)),
                color = Color(0xFFD4AF37),
                trackColor = Color.Transparent,
                strokeCap = StrokeCap.Round
            )

            Text(
                "$totalTodo remaining",
                style = MaterialTheme.typography.labelMedium,
                color = Color.LightGray
            )
        }
    }
}

@Composable
fun CategoryMedalItem(medal: CategoryMedalTier) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Image(
            painter = painterResource(medal.drawable),
            contentDescription = "",
            modifier = Modifier.size(90.dp)
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            medal.title,
            style = MaterialTheme.typography.labelSmall.copy(
                color = Color(0xFFD4AF37) // gold tier text
            )
        )
    }
}


@OptIn(ExperimentalMaterial3Api::class, ExperimentalUuidApi::class)
@Composable
fun AddTaskBottomSheet(
    onAddTask: (TaskModel) -> Unit
) {
    var taskName by remember { mutableStateOf("") }

    var categoryExpanded by remember { mutableStateOf(false) }
    var durationExpanded by remember { mutableStateOf(false) }
    var priorityExpanded by remember { mutableStateOf(false) }

    var selectedCategory by remember { mutableStateOf(TaskCategory.WORKOUT) }
    var selectedDuration by remember { mutableStateOf("15min") }
    var selectedPriority by remember { mutableStateOf(TaskPriority.LOW) }

    val timeZone = TimeZone.currentSystemDefault()
    val currentDate = Clock.System.todayIn(timeZone).toString()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        // Title
        Text(
            text = "Add Task",
            style = MaterialTheme.typography.titleLarge,
            color = Color.White,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        // Task name
        OutlinedTextField(
            value = taskName,
            textStyle = TextStyle(color = Color.White),
            onValueChange = { taskName = it },
            label = { Text("Task name") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        // CATEGORY
        ExposedDropdownMenuBox(
            expanded = priorityExpanded,
            onExpandedChange = { priorityExpanded = !priorityExpanded },
            modifier = Modifier.fillMaxWidth()
        ) {
            OutlinedTextField(
                value = selectedPriority.priority,
                onValueChange = {},
                textStyle = TextStyle(color = Color.White),
                readOnly = true,
                label = { Text("Priority") },
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = priorityExpanded)
                },
                modifier = Modifier.menuAnchor().fillMaxWidth()
            )

            ExposedDropdownMenu(
                expanded = priorityExpanded,
                onDismissRequest = { priorityExpanded = false }
            ) {
                TaskPriority.entries.forEach {
                    DropdownMenuItem(
                        text = { Text(it.priority, style = TextStyle(color = Color.White)) },
                        onClick = {
                            selectedPriority = it
                            priorityExpanded = false
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        // Dropdowns
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            // CATEGORY
            ExposedDropdownMenuBox(
                expanded = categoryExpanded,
                onExpandedChange = { categoryExpanded = !categoryExpanded },
                modifier = Modifier.weight(1f)
            ) {
                OutlinedTextField(
                    value = selectedCategory.category,
                    onValueChange = {},
                    textStyle = TextStyle(color = Color.White),
                    readOnly = true,
                    label = { Text("Category") },
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = categoryExpanded)
                    },
                    modifier = Modifier.menuAnchor().fillMaxWidth()
                )

                ExposedDropdownMenu(
                    expanded = categoryExpanded,
                    onDismissRequest = { categoryExpanded = false }
                ) {
                    TaskCategory.entries.forEach {
                        DropdownMenuItem(
                            text = { Text(it.category, style = TextStyle(color = Color.White)) },
                            onClick = {
                                selectedCategory = it
                                categoryExpanded = false
                            }
                        )
                    }
                }
            }

            // DURATION
            ExposedDropdownMenuBox(
                expanded = durationExpanded,
                onExpandedChange = { durationExpanded = !durationExpanded },
                modifier = Modifier.weight(1f)
            ) {
                OutlinedTextField(
                    value = selectedDuration,
                    onValueChange = {},
                    readOnly = true,
                    textStyle = TextStyle(color = Color.White),
                    label = { Text("Duration") },
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = durationExpanded)
                    },
                    modifier = Modifier.menuAnchor().fillMaxWidth()
                )

                ExposedDropdownMenu(
                    expanded = durationExpanded,
                    onDismissRequest = { durationExpanded = false }
                ) {
                    listOf("15min", "30min", "45min", "1hr", "1hr30min").forEach {
                        DropdownMenuItem(
                            text = { Text(it, style = TextStyle(color = Color.White)) },
                            onClick = {
                                selectedDuration = it
                                durationExpanded = false
                            }
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Add Button
        Button(
            onClick = {
                onAddTask(
                    TaskModel(
                        id = Uuid.random().toString(),
                        content = taskName,
                        duration = selectedDuration,
                        category = selectedCategory,
                        date = currentDate,
                        state = TaskState.TODO,
                        priority = selectedPriority
                    )
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("Add Task")
        }

        Spacer(modifier = Modifier.height(8.dp))
    }
}

@Composable
@Preview
fun DashboardScreenPreview() {
    LevelUpTheme {
        TaskOverviewCard(
            tasks =
                listOf(
                    TaskModel(
                        id = "",
                        content = "text number",
                        duration = "5hr",
                        category = TaskCategory.WORKOUT,
                        date = "",
                        state = TaskState.DONE,
                        priority = TaskPriority.LOW
                    ),
                    TaskModel(
                        id = "",
                        content = "text number",
                        duration = "5hr",
                        category = TaskCategory.WORKOUT,
                        date = "",
                        state = TaskState.STARTED,
                        priority = TaskPriority.LOW
                    ),
                    TaskModel(
                        id = "",
                        content = "text number",
                        duration = "5hr",
                        category = TaskCategory.WORKOUT,
                        date = "",
                        state = TaskState.TODO,
                        priority = TaskPriority.LOW
                    ),
                    TaskModel(
                        id = "",
                        content = "text number",
                        duration = "5hr",
                        category = TaskCategory.WORKOUT,
                        date = "",
                        state = TaskState.TODO,
                        priority = TaskPriority.LOW
                    )
                ),
            onNavigate = { }
        )
    }
}