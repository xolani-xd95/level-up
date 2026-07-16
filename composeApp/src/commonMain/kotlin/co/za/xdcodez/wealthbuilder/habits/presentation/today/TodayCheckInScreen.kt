package co.za.xdcodez.wealthbuilder.habits.presentation.today

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import co.za.xdcodez.wealthbuilder.common.formatCurrency
import co.za.xdcodez.wealthbuilder.habits.domain.getDisciplineMessage
import co.za.xdcodez.wealthbuilder.habits.domain.model.GoalType
import co.za.xdcodez.wealthbuilder.habits.domain.model.HabitKey
import co.za.xdcodez.wealthbuilder.habits.domain.model.QuarterlyGoal
import co.za.xdcodez.wealthbuilder.habits.domain.model.WeeklyRollup
import co.za.xdcodez.wealthbuilder.theme.WealthBuilderTheme
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.annotation.KoinExperimentalAPI
import wealthbuilder.composeapp.generated.resources.Res
import wealthbuilder.composeapp.generated.resources.cardio_run
import wealthbuilder.composeapp.generated.resources.focus_ring
import wealthbuilder.composeapp.generated.resources.gym_ring
import wealthbuilder.composeapp.generated.resources.ic_brain
import wealthbuilder.composeapp.generated.resources.ic_exercise
import wealthbuilder.composeapp.generated.resources.ic_finance
import wealthbuilder.composeapp.generated.resources.ic_impulse
import wealthbuilder.composeapp.generated.resources.impulse_ring

private val Gold = Color(0xFFD4AF37)

@OptIn(KoinExperimentalAPI::class)
@Composable
fun TodayCheckInScreenRoute(
    viewModel: TodayCheckInViewModel = koinViewModel<TodayCheckInViewModel>()
) {
    val state by viewModel.state.collectAsState()

    // Initialize Q3 quarter on first launch
    LaunchedEffect(Unit) {
        viewModel.initializeAndLoadQuarter("2026-Q3")
    }

    if (state.isLoading) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(modifier = Modifier.size(50.dp))
        }
    } else {
        TodayCheckInScreen(
            state = state,
            onAction = viewModel::onAction
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TodayCheckInScreen(
    state: TodayCheckInState,
    onAction: (TodayCheckInAction) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize().padding(horizontal = 12.dp),
        horizontalAlignment = CenterHorizontally
    ) {
        QuarterDayHeader(
            totalDays = state.totalQuarterDays,
            completedDays = state.completedDays,
            dayNumber = state.quarterDayNumber
        )

        state.currentDate?.let { today ->
            WeeklyProgressSection(
                today = today,
                goals = state.quarterlyGoals,
                weekRollup = state.currentWeekRollup,
                focusBlocksCompleted = state.focusBlocksCompletedToday,
                onAction = onAction
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        QuarterlyProgressSection(goals = state.quarterlyGoals)

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
fun QuarterDayHeader(
    totalDays: Int,
    completedDays: Int,
    dayNumber: Int
) {
    Column(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalAlignment = CenterHorizontally
    ) {
        Text(
            "Q3: DISCIPLINE & CONSISTENCY",
            style = MaterialTheme.typography.labelLarge,
            color = Color.White
        )
        Spacer(modifier = Modifier.height(12.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
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
                        append("wins: ")
                    }
                    withStyle(style = SpanStyle(fontSize = 18.sp, color = Gold)) {
                        append("$completedDays")
                    }
                })
            Text(
                buildAnnotatedString {
                    withStyle(style = SpanStyle(fontSize = 14.sp, color = Color(0x99FFFFFF))) {
                        append("goal: ")
                    }
                    withStyle(style = SpanStyle(fontSize = 16.sp, color = Color(0x99FFFFFF))) {
                        append("$totalDays")
                    }
                })
        }
        Spacer(modifier = Modifier.height(4.dp))
        LinearProgressIndicator(
            progress = { (completedDays.toFloat() / totalDays.toFloat()).coerceIn(0f, 1f) },
            modifier = Modifier.fillMaxWidth().height(8.dp),
            strokeCap = StrokeCap.Round,
            color = Gold,
            trackColor = Color.White.copy(alpha = 0.08f)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                horizontalAlignment = CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                Text("$dayNumber", color = Color.White)
                Text(
                    "day of $totalDays",
                    color = Color.White.copy(alpha = 0.5f),
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Column(
                horizontalAlignment = CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                val missedDays = ((dayNumber - 1) - completedDays).coerceAtLeast(0)
                Text("$missedDays", color = Color.Red)
                Text(
                    "missed days",
                    color = Color.White.copy(alpha = 0.5f),
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
        Spacer(modifier = Modifier.height(10.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .border(width = 1.dp, color = Gold, shape = RoundedCornerShape(8.dp))
                .background(Gold.copy(alpha = 0.1f))
                .padding(4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
//            Icon(
//                painter = painterResource(Res.drawable.ic_impulse),
////                imageVector = Icons.Filled.CheckCircle,
//                contentDescription = "consistency",
//                modifier = Modifier.size(20.dp),
//                tint = Color.White.copy(alpha = 0.8f)
//            )
//            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = ("🔥 " + getDisciplineMessage(completedDays)) + " 🔥",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White.copy(alpha = 0.8f)
            )
        }
    }
    Spacer(modifier = Modifier.height(10.dp))
}

@Composable
fun WeeklyProgressSection(
    today: LocalDate,
    goals: List<QuarterlyGoal>,
    weekRollup: WeeklyRollup?,
    focusBlocksCompleted: Int,
    onAction: (TodayCheckInAction) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = "Weekly",
            modifier = Modifier.padding(start = 8.dp),
            style = MaterialTheme.typography.labelLarge,
            color = Color.White.copy(alpha = 0.5f)
        )

        // 3 Circular Progress Rings
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {

            // Cardio Ring
            val cardioGoal = goals.firstOrNull { it.habitKey == HabitKey.CARDIO }
            WeeklyHabitRing(
                current = weekRollup?.cardioDays ?: 0,
                target = cardioGoal?.weeklyTarget?.toInt() ?: 4,
                label = "CARDIO",
                isComplete = cardioGoal?.isDoneToday(today) ?: false,
                onTap = { onAction(TodayCheckInAction.OnCardioTap) },
                habitKey = HabitKey.CARDIO,
                habitImage = Res.drawable.cardio_run,
                focusBlocksCompleted = 0
            )

            // Gym Ring
            val gymGoal = goals.firstOrNull { it.habitKey == HabitKey.GYM }
            WeeklyHabitRing(
                current = weekRollup?.gymDays ?: 0,
                target = gymGoal?.weeklyTarget?.toInt() ?: 4,
                label = "GYM",
                isComplete = gymGoal?.isDoneToday(today) ?: false,
                onTap = { onAction(TodayCheckInAction.OnGymTap) },
                habitKey = HabitKey.GYM,
                habitImage = Res.drawable.gym_ring,
                focusBlocksCompleted = 0
            )

            // Focus Ring
            val focusGoal = goals.firstOrNull { it.habitKey == HabitKey.FOCUS }
            WeeklyHabitRing(
                current = weekRollup?.focusDays ?: 0,
                target = focusGoal?.weeklyTarget?.toInt() ?: 5,
                label = "FOCUS",
                isComplete = focusGoal?.isDoneToday(today) ?: false,
                onTap = { onAction(TodayCheckInAction.OnFocusBlockTap(3)) },
                habitKey = HabitKey.FOCUS,
                habitImage = Res.drawable.focus_ring,
                focusBlocksCompleted = focusBlocksCompleted
            )

            // Impulse Control Ring
            val impulseGoal = goals.firstOrNull { it.habitKey == HabitKey.IMPULSE_CONTROL }
            WeeklyHabitRing(
                current = weekRollup?.impulseControlDays ?: 0,
                target = impulseGoal?.weeklyTarget?.toInt() ?: 7,
                label = "IMPULSE",
                isComplete = impulseGoal?.isDoneToday(today) ?: false,
                onTap = { onAction(TodayCheckInAction.OnImpulseTap) },
                habitKey = HabitKey.IMPULSE_CONTROL,
                habitImage = Res.drawable.impulse_ring,
                focusBlocksCompleted = 0
            )
        }
    }
}

@Composable
fun WeeklyHabitRing(
    current: Int,
    target: Int,
    label: String,
    habitImage: DrawableResource,
    habitKey: HabitKey,
    isComplete: Boolean,
    focusBlocksCompleted: Int,
    onTap: () -> Unit
) {
    val progress = ((current.toDouble()) / (target.toDouble())).toFloat().coerceIn(0f, 1f)

    Column(
        modifier = Modifier.clickable { onTap() },
        horizontalAlignment = CenterHorizontally
    ) {
        Box(contentAlignment = Alignment.Center) {
            CircularProgressIndicator(
                progress = { progress },
                modifier = Modifier.size(80.dp),
                strokeWidth = 3.dp,
                color = Gold,
                strokeCap = StrokeCap.Round,
                trackColor = Color.White.copy(alpha = 0.08f)
            )

            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape),
                contentAlignment = Alignment.Center
            ) {
                if (LocalInspectionMode.current) {
                    Icon(
                        imageVector = Icons.Filled.CheckCircle,
                        contentDescription = "goal.title",
                        tint = Color.White.copy(alpha = 0.4f),
                        modifier = Modifier.size(20.dp)
                    )
                } else {
                    Image(
                        painter = painterResource(habitImage),
                        modifier = Modifier.fillMaxSize(),
                        contentDescription = "Unranked"
                    )
                }
            }

            when (habitKey) {
                HabitKey.FOCUS -> {
                    Row(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .offset(y = 4.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFF1A1A1A))
                            .border(2.dp, Color(0xFF0B0A08), RoundedCornerShape(8.dp))
                            .padding(horizontal = 6.dp, vertical = 2.dp),
                        horizontalArrangement = Arrangement.spacedBy(3.dp)
                    ) {
                        repeat(3) { index ->
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .background(
                                        color = if (index < focusBlocksCompleted) Gold
                                        else Color.White.copy(alpha = 0.2f),
                                        shape = CircleShape
                                    )
                            )
                        }
                    }
                }

                else -> {
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .size(20.dp)
                            .clip(CircleShape)
                            .background(if (isComplete) Gold else Color(0xFF1A1A1A)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Check,
                            contentDescription = if (isComplete) "Logged" else "Log impulse control",
                            tint = if (isComplete) Color(0xFF0B0A08) else Color.White.copy(alpha = 0.4f),
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(5.dp))
        Text(buildAnnotatedString {
            withStyle(
                SpanStyle(
                    fontSize = 15.sp,
                    color = if (isComplete || focusBlocksCompleted == 3) Gold else Color(0x99FFFFFF)
                )
            ) {
                append((current).toString())
            }
            withStyle(SpanStyle(fontSize = 10.sp, color = Color(0x99FFFFFF))) {
                append(" /${(target)}")
            }
        })
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = Color.White.copy(alpha = 0.6f),
            textAlign = TextAlign.Center,
            maxLines = 1
        )
    }
}

@Composable
fun QuarterlyProgressSection(goals: List<QuarterlyGoal>) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            "Quarterly",
            style = MaterialTheme.typography.labelLarge,
            modifier = Modifier.padding(horizontal = 8.dp),
            color = Color.White.copy(alpha = 0.5f)
        )
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            val quarterGoals = goals.filter { it.habitKey != HabitKey.IMPULSE_CONTROL }
            items(quarterGoals.size) { index ->
                val goal = quarterGoals[index]
                ProgressGoalCard(goal)
            }
        }
    }
}

@Composable
fun ProgressGoalCard(goal: QuarterlyGoal) {
    val icons = when (goal.type) {
        GoalType.SAVINGS -> Res.drawable.ic_finance
        else -> {
            when (goal.habitKey) {
                HabitKey.FOCUS -> Res.drawable.ic_brain
                HabitKey.IMPULSE_CONTROL -> Res.drawable.ic_impulse
                HabitKey.GYM -> Res.drawable.ic_exercise
                else -> Res.drawable.ic_exercise
            }
        }
    }
    Row(
        modifier = Modifier.fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(Color.White.copy(alpha = 0.05f))
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (LocalInspectionMode.current) {
            Icon(
                imageVector = Icons.Filled.CheckCircle,
                contentDescription = "goal.title",
                tint = Color.White.copy(alpha = 0.4f),
                modifier = Modifier.size(44.dp).padding(end = 12.dp)
            )
        } else {
            Icon(
                painter = painterResource(icons),
                contentDescription = "goal.title",
                tint = Gold,
                modifier = Modifier.size(40.dp).padding(end = 12.dp)
            )
        }
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = goal.title,
                    color = Color.White,
                    style = MaterialTheme.typography.bodyMedium,
                )
                val progress =
                    ((goal.currentValue ?: 0.0) / (goal.targetValue ?: 0.0) * 100).toInt()
                Text(
                    text = "$progress%",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Gold
                )
            }
            when (goal.type) {
                GoalType.SAVINGS -> {
                    Text(buildAnnotatedString {
                        withStyle(SpanStyle(fontSize = 15.sp, color = Gold)) {
                            append(formatCurrency(goal.currentValue ?: 0.0))
                        }
                        withStyle(SpanStyle(fontSize = 13.sp, color = Color(0x99FFFFFF))) {
                            append(" / ${formatCurrency(goal.targetValue ?: 0.0)}")
                        }
                    })
                }

                else -> {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(buildAnnotatedString {
                            withStyle(SpanStyle(fontSize = 13.sp, color = Color(0x99FFFFFF))) {
                                append("streak: ")
                            }
                            withStyle(SpanStyle(fontSize = 15.sp, color = Gold)) {
                                append("${goal.currentValue?.toInt()}")
                            }
                        })
                        Text(buildAnnotatedString {
                            withStyle(
                                SpanStyle(
                                    fontSize = 13.sp,
                                    color = Color(0x99FFFFFF)
                                )
                            ) {
                                append("goal: ")
                            }
                            withStyle(
                                SpanStyle(
                                    fontSize = 15.sp,
                                    color = Color(0x99FFFFFF)
                                )
                            ) {
                                append("${goal.targetValue?.toInt()}")
                            }
                        })
                    }
                }
            }

            LinearProgressIndicator(
                strokeCap = StrokeCap.Round,
                progress = {
                    ((goal.currentValue ?: 0.0) / (goal.targetValue ?: 0.0)).toFloat()
                        .coerceIn(0f, 1f)
                },
                color = Gold,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp),
            )
        }
    }
}

@Composable
fun MilestoneGoalCard(
    goal: QuarterlyGoal,
    onToggle: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = goal.isComplete,
                onCheckedChange = { onToggle() }
            )

            Spacer(modifier = Modifier.width(8.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = goal.title,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium,
                    textDecoration = if (goal.isComplete) TextDecoration.LineThrough else null
                )

                goal.dueDate?.let { dueDate ->
                    Text(
                        text = "Due: $dueDate",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Preview
@Composable
fun PreviewTodayCheckInScreen() {
    WealthBuilderTheme {
        TodayCheckInScreen(
            state = TodayCheckInState(
                isLoading = false,
                quarterlyGoals = listOf(
                    QuarterlyGoal(
                        id = "1",
                        habitKey = HabitKey.GYM,
                        title = "Gym Sessions",
                        type = GoalType.METRIC_MANUAL,
                        targetValue = 50.0,
                        currentValue = 2.0,
                        quarter = "2026-Q3"
                    ),
                    QuarterlyGoal(
                        id = "2",
                        habitKey = HabitKey.IMPULSE_CONTROL,
                        title = "Smoke-Free Days",
                        type = GoalType.METRIC_MANUAL,
                        targetValue = 90.0,
                        currentValue = 60.0,
                        quarter = "2026-Q3"
                    ),
                    QuarterlyGoal(
                        id = "3",
                        habitKey = HabitKey.FOCUS,
                        title = "Focus Wins",
                        type = GoalType.METRIC_MANUAL,
                        targetValue = 90.0,
                        currentValue = 30.0,
                        quarter = "2026-Q3"
                    ),
                    QuarterlyGoal(
                        id = "4",
                        habitKey = HabitKey.FOCUS,
                        title = "Savings",
                        type = GoalType.SAVINGS,
                        targetValue = 50000.0,
                        currentValue = 10000.0,
                        quarter = "2026-Q3"
                    ),
                    QuarterlyGoal(
                        id = "4",
                        habitKey = HabitKey.FOCUS,
                        title = "Sphoshs' Birthday",
                        type = GoalType.MILESTONE,
                        targetValue = 10000.0,
                        currentValue = 1000.0,
                        quarter = "2026-Q3"
                    ),
                    QuarterlyGoal(
                        id = "4",
                        habitKey = HabitKey.FOCUS,
                        title = "Sphoshs' Birthday",
                        type = GoalType.MILESTONE,
                        dueDate = LocalDate(2026, 1, 10),
                        targetValue = 10000.0,
                        currentValue = 1000.0,
                        quarter = "2026-Q3"
                    ),
                ),
                currentWeekRollup = WeeklyRollup(
                    weekStart = Clock.System.todayIn(TimeZone.currentSystemDefault()),
                    weekEnd = Clock.System.todayIn(TimeZone.currentSystemDefault()),
                    impulseControlDays = 1,
                    gymDays = 1,
                    focusDays = 1,
                    weekNumber = 2,
                    cardioDays = 4
                ),
                currentDate = Clock.System.todayIn(TimeZone.currentSystemDefault())
            ),
            onAction = {}
        )
    }
}