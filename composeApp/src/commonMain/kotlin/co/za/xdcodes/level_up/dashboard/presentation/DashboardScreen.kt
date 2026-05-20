package co.za.xdcodes.level_up.dashboard.presentation

import androidx.compose.foundation.Image
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.KeyboardArrowRight
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.text.toUpperCase
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import co.za.xdcodes.level_up.dashboard.domain.dto.CategoryMedalTier
import co.za.xdcodes.level_up.dashboard.domain.dto.DashboardModel
import co.za.xdcodes.level_up.dashboard.domain.dto.TaskCategory
import co.za.xdcodes.level_up.dashboard.domain.dto.TaskModel
import co.za.xdcodes.level_up.dashboard.domain.dto.TaskPriority
import co.za.xdcodes.level_up.dashboard.domain.dto.TaskState
import co.za.xdcodes.level_up.theme.LevelUpTheme
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.annotation.KoinExperimentalAPI

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
                    Box(
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            progress = {
                                val completed =
                                    state.daysInChallenge.toFloat()
                                val total = state.dashboardStreaks?.goalDays?.toFloat() ?: 90f
                                (completed / total).coerceIn(0f, 1f)
                            },
                            modifier = Modifier.size(140.dp),
                            strokeWidth = 4.dp,
                            trackColor = Color.White.copy(alpha = 0.08f)
                        )
                        Image(
                            painter = painterResource(currentTier.drawable),
                            modifier = Modifier.size(130.dp),
                            contentDescription = currentTier.title
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    StreakChallengeLabelCard(state = state)
                }
//                nextTier?.let { nextTier ->
//                    val remainingDays =
//                        nextTier.daysRequired - (state.dashboardStreaks?.goalDaysCompleted ?: 0)
//
//                    Column(
//                        modifier = Modifier.fillMaxWidth()
//                            .clip(RoundedCornerShape(8.dp))
//                            .background(Color.White.copy(alpha = 0.05f))
//                            .fillMaxWidth()
//                            .drawWithContent {
//                                drawContent()
//                                drawRect(
//                                    brush = Brush.verticalGradient(
//                                        listOf(Color.White.copy(0.12f), Color.Transparent)
//                                    )
//                                )
//                            }
//                            .padding(8.dp)
//                    ) {
//                        Row(
//                            modifier = Modifier.fillMaxWidth(),
//                            horizontalArrangement = Arrangement.SpaceBetween,
//                            verticalAlignment = Alignment.Bottom
//                        ) {
//                            Text(
//                                "${nextTier.daysRequired - remainingDays}d",
//                                style = MaterialTheme.typography.labelMedium,
//                                color = Color.LightGray
//                            )
//
//                            Text(
//                                "next milestone: ${nextTier.title}",
//                                style = MaterialTheme.typography.labelLarge,
//                                color = Color.LightGray
//                            )
//                            Text(
//                                "${nextTier.daysRequired}d",
//                                style = MaterialTheme.typography.labelMedium,
//                                color = Color.LightGray
//                            )
//                        }
//
//                        LinearProgressIndicator(
//                            progress = { (nextTier.daysRequired - remainingDays) / nextTier.daysRequired.toFloat() },
//                            modifier = Modifier
//                                .fillMaxWidth()
//                                .height(4.dp)
//                                .clip(RoundedCornerShape(2.dp)),
//                            color = Color(0xFFD4AF37),
//                            trackColor = Color.White.copy(alpha = 0.1f)
//                        )
//                    }
//                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color.White.copy(alpha = 0.05f))
                    .fillMaxWidth()
                    .padding(6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                state.workoutStreaks?.first?.let {
                    CategoryMedalItem(
                        medal = it,
                        required = state.workoutStreaks.second?.requiredCount ?: 0,
                        completed = state.dashboardStreaks?.workoutStreak ?: 0
                    )
                }

                state.tradingStreaks?.first?.let {
                    CategoryMedalItem(
                        medal = it,
                        required = state.tradingStreaks.second?.requiredCount ?: 0,
                        completed = state.dashboardStreaks?.tradingDiscipline ?: 0
                    )
                }
                state.runningStreaks?.first?.let {
                    CategoryMedalItem(
                        medal = it,
                        required = state.runningStreaks.second?.requiredCount ?: 0,
                        completed = state.dashboardStreaks?.runningStreak ?: 0
                    )
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
fun StreakChallengeLabelCard(
    state: DashboardState
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(Color.White.copy(alpha = 0.05f))
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            "${state.daysInChallenge} days into the ${state.dashboardStreaks?.goalDays}-day challenge",
            style = MaterialTheme.typography.titleSmall,
            color = Color.White.copy(alpha = 0.8f),
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Row(
            modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                buildAnnotatedString {
                    withStyle(
                        style = SpanStyle(
                            fontSize = 14.sp,
                            color = Color.White.copy(alpha = 0.5f)
                        )
                    ) {
                        append("completed: ")
                    }
                    withStyle(style = SpanStyle(fontSize = 16.sp, color = Color.White)) {
                        append("${state.dashboardStreaks?.goalDaysCompleted}")
                    }
                },
                style = MaterialTheme.typography.labelMedium,
            )

            Text(
                buildAnnotatedString {
                    withStyle(
                        style = SpanStyle(
                            fontSize = 14.sp,
                            color = Color.White.copy(alpha = 0.5f)
                        )
                    ) {
                        append("missed: ")
                    }
                    withStyle(style = SpanStyle(fontSize = 16.sp, color = Color.Red)) {
                        append("${(state.daysInChallenge - (state.dashboardStreaks?.goalDaysCompleted ?: 0))}")
                    }
                },
                style = MaterialTheme.typography.labelMedium,
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
fun CategoryMedalItem(medal: CategoryMedalTier, required: Int, completed: Int) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(
                progress = {
                    val completed = completed.toFloat()
                    val total = required.toFloat()
                    (completed / total).coerceIn(0f, 1f)
                },
                modifier = Modifier.size(100.dp),
                strokeWidth = 2.dp,
                trackColor = Color.White.copy(alpha = 0.08f)
            )
            Image(
                painter = painterResource(medal.drawable),
                contentDescription = "",
                modifier = Modifier.size(90.dp)
            )
        }

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            medal.title,
            style = MaterialTheme.typography.labelSmall.copy(
                color = Color(0xFFD4AF37) // gold tier text
            )
        )
    }
}


// Bottom sheet removed - using QuickAddTaskBar instead

@Composable
@Preview
fun DashboardScreenPreview() {
    LevelUpTheme {
        StreakChallengeLabelCard(
            state = DashboardState(
                dashboardStreaks = DashboardModel(goalDaysCompleted = 5),
                daysInChallenge = 2
            )
        )
    }
}