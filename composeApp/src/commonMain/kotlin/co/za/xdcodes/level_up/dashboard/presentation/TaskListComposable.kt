package co.za.xdcodes.level_up.dashboard.presentation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.KeyboardArrowDown
import androidx.compose.material.icons.outlined.KeyboardArrowUp
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
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
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.annotation.KoinExperimentalAPI


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

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun TaskListComposable(
    state: DashboardState,
    onNavigateUp: () -> Unit,
    onTaskAction: (DashboardActions) -> Unit
) {
    val scope = rememberCoroutineScope()
    val pagerState = rememberPagerState(pageCount = { TaskState.entries.size }, initialPage = 1)
    val selectedTabIndex by remember {
        derivedStateOf { pagerState.currentPage }
    }

    CustomAppTopBar(
        title = "non-negotiables", modifier = Modifier.fillMaxSize().padding(horizontal = 12.dp),
        showNavigationIcon = true,
        onNavigate = onNavigateUp
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Column {
                TabRow(
                    modifier = Modifier.fillMaxWidth(),
                    selectedTabIndex = selectedTabIndex,
                    containerColor = Color.Transparent,
                    divider = { }
                ) {
                    TaskState.entries.forEachIndexed { index, tabs ->
                        Tab(
                            modifier = Modifier.fillMaxWidth(),
                            selected = selectedTabIndex == index,
                            onClick = {
                                scope.launch {
                                    pagerState.animateScrollToPage(tabs.ordinal)
                                }
                            },
                            text = { Text(tabs.text, style = TextStyle(color = Color.White)) }
                        )
                    }
                }

                HorizontalPager(
                    state = pagerState,
                ) { page ->
                    val filteredTasks =
                        state.taskForToday.filter { it.state == TaskState.entries[page] }

                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier
                            .padding(top = 8.dp)
                    ) {
                        items(filteredTasks) { task ->
                            TaskItem(
                                task = task,
                                onStart = { onTaskAction(DashboardActions.OnStartTask(it)) },
                                onComplete = { onTaskAction(DashboardActions.OnCompleteTask(it)) },
                                onDelete = { onTaskAction(DashboardActions.OnDeleteTask(it)) }
                            )
                        }
                    }
                }
            }

            FloatingActionButton(
                modifier = Modifier.align(Alignment.BottomCenter),
                onClick = {
                    onTaskAction(DashboardActions.OnShowAddTaskBottomSheet(true))
                },
                shape = CircleShape,
                containerColor = MaterialTheme.colorScheme.primary,
                content = {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add task",
                        tint = Color.White,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            )
        }
        if (state.showAddTaskBottomSheet)
            ModalBottomSheet(
                containerColor = Color(0xff1B1B1B),
                dragHandle = { BottomSheetDefaults.DragHandle() },
                onDismissRequest = { onTaskAction(DashboardActions.OnShowAddTaskBottomSheet(false)) },
            ) {
                AddTaskBottomSheet { newTask ->
                    onTaskAction(DashboardActions.OnTaskAdded(newTask))
                }
            }
    }
}

@Composable
fun TaskItem(
    task: TaskModel,
    onStart: (String) -> Unit,
    onComplete: (String) -> Unit,
    onDelete: (String) -> Unit
) {
    var isExpanded by remember { mutableStateOf(false) }

    val color = when (task.priority) {
        TaskPriority.LOW -> Color.Yellow
        TaskPriority.MEDIUM -> Color(0xFF1E88E5)
        TaskPriority.HIGH -> Color.Red
    }
    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(color.copy(alpha = 0.05f))
            .fillMaxWidth()
            .drawWithContent {
                drawContent()
                drawRect(
                    brush = Brush.verticalGradient(
                        listOf(Color.White.copy(0.12f), Color.Transparent)
                    )
                )
                val strokeWidth = 6.dp.toPx()
                drawLine(
                    color = color,
                    start = Offset(0f, 0f),
                    end = Offset(0f, size.height),
                    strokeWidth = strokeWidth
                )
            }
            .padding(start = 6.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .padding(horizontal = 8.dp, vertical = 4.dp)
                .fillMaxWidth()
                .clickable { isExpanded = !isExpanded }
        ) {
            Text(
                task.content,
                modifier = Modifier.weight(1f),
                style = MaterialTheme.typography.bodyMedium.copy(color = Color.White)
            )

            Icon(
                imageVector = if (isExpanded) Icons.Outlined.KeyboardArrowUp else Icons.Outlined.KeyboardArrowDown,
                tint = Color.White,
                contentDescription = null
            )
        }
        TaskMetaDataRow(
            task = task,
            color = color
        )

        AnimatedVisibility(visible = isExpanded) {
            Row(
                modifier = Modifier.fillMaxWidth()
                    .padding(horizontal = 26.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                TextButton(onClick = {
                    onDelete(task.id)
                }) {
                    Text("DELETE")
                }

                if (task.state == TaskState.TODO)
                    TextButton(onClick = { onStart(task.id) }) {
                        Text("START")
                    }
                else if (task.state == TaskState.STARTED)
                    TextButton(onClick = { onComplete(task.id) }) {
                        Text("DONE")
                    }
            }
        }
    }
}

@Composable
fun TaskMetaDataRow(task: TaskModel, color: Color) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .padding(start = 8.dp, end = 12.dp, top = 4.dp, bottom = 12.dp)
            .fillMaxWidth()
    ) {
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(5.dp))
                .background(color.copy(alpha = 0.2f))
                .padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
            Text(
                text = task.category.name,
                style = MaterialTheme.typography.labelSmall,
                color = color
            )
        }

        Text(
            text = "Duration: ${task.duration}",
            style = MaterialTheme.typography.labelLarge,
            color = Color.White.copy(alpha = 0.7f)
        )
    }
}


@Preview
@Composable
fun DailySummaryComposablePreview() {
    LevelUpTheme {
        Column(
            modifier = Modifier.padding(vertical = 16.dp, horizontal = 8.dp)
        ) {
            TaskItem(
                task = TaskModel(
                    id = "",
                    content = "Tetsing",
                    duration = "5hr",
                    category = TaskCategory.WORKOUT,
                    date = "",
                    state = TaskState.TODO,
                    priority = TaskPriority.HIGH
                ),
                {},
                {},
                {}
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
                        id = "",
                        content = "Tetsing",
                        duration = "5hr",
                        category = TaskCategory.WORKOUT,
                        date = "",
                        state = TaskState.TODO,
                        priority = TaskPriority.MEDIUM
                    ),

                    TaskModel(
                        id = "",
                        content = "Dummy task that has to be a bit longer than the rest ifalsdfjlaksdjas jdklasd",
                        duration = "5hr",
                        category = TaskCategory.RUNNING,
                        date = "",
                        state = TaskState.TODO,
                        priority = TaskPriority.LOW
                    ),

                    TaskModel(
                        id = "",
                        content = "df dfs fdsfhdlskfjksdhfsdhfjkdhsfjkdhskf ",
                        duration = "5hr",
                        category = TaskCategory.HEALTH,
                        date = "",
                        state = TaskState.TODO,
                        priority = TaskPriority.HIGH
                    ),
                )
            ),
            {}, {}
        )
    }
}


