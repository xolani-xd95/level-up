package co.za.xdcodes.level_up.workout.presentation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.KeyboardArrowDown
import androidx.compose.material.icons.outlined.KeyboardArrowUp
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import co.za.xdcodes.level_up.common.widgets.CustomAppTopBar
import co.za.xdcodes.level_up.dashboard.presentation.DashboardActions
import co.za.xdcodes.level_up.theme.LevelUpTheme
import co.za.xdcodes.level_up.theme.background
import co.za.xdcodes.level_up.workout.domain.dto.ExerciseCategory
import co.za.xdcodes.level_up.workout.domain.dto.WorkoutSetModel
import co.za.xdcodes.level_up.workout.domain.dto.WorkoutWithSets
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.annotation.KoinExperimentalAPI
import kotlin.uuid.ExperimentalUuidApi

@OptIn(KoinExperimentalAPI::class)
@Composable
fun WorkoutListScreenRoot(
    viewModel: WorkoutListViewModel = koinViewModel<WorkoutListViewModel>(),
    onNavigate: () -> Unit
) {
    val state by viewModel.state.collectAsState()
    WorkoutListScreen(state = state, onNavigate = onNavigate) {
        viewModel.onAction(it)
    }
}

@OptIn(ExperimentalUuidApi::class, ExperimentalMaterial3Api::class)
@Composable
fun WorkoutListScreen(
    state: WorkoutListState,
    onNavigate: () -> Unit,
    onWorkoutAction: (WorkoutListAction) -> Unit
) {
    CustomAppTopBar(
        title = "Workout",
        modifier = Modifier.fillMaxSize().padding(horizontal = 12.dp)
    ) {
        Box() {
            val groupedWorkouts = state.listOfWorkouts.groupBy { workout -> workout.category }

            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(groupedWorkouts.entries.toList(), key = { it.key.name }) { entry ->
                    val category = entry.key
                    val workouts = entry.value
                    Column(
                        modifier = Modifier
                            .padding(vertical = 12.dp)
                            .fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                category.name,

                                style = MaterialTheme.typography.titleMedium.copy(color = MaterialTheme.colorScheme.primary)
                            )

                            Text(
                                "${entry.value.size}",
                                modifier = Modifier.padding(end = 16.dp),
                                style = MaterialTheme.typography.titleMedium.copy(color = MaterialTheme.colorScheme.primary)
                            )
                        }
                        workouts.forEach { workout ->
                            DropdownListItem(
                                workout = workout,
                                modifier = Modifier.padding(top = 8.dp)
                            ) { updatedSet ->
                                onWorkoutAction(
                                    WorkoutListAction.OnWorkoutComplete(
                                        workout.workoutId,
                                        updatedSet
                                    )
                                )
                            }
                        }
                    }
                }
            }

            FloatingActionButton(
                modifier = Modifier.align(Alignment.BottomCenter),
                onClick = {
                    onNavigate()
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
    }
}

@Composable
fun DropdownListItem(
    modifier: Modifier = Modifier,
    workout: WorkoutWithSets,
    onCompleteSet: (WorkoutSetModel) -> Unit
) {
    var isExpanded by remember { mutableStateOf(false) }
    val isCompleted by remember(workout.sets) {
        derivedStateOf {
            workout.sets.isNotEmpty() &&
                    workout.sets.all { it.isComplete == true }
        }
    }

    Column(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .drawWithContent {
                drawContent()
                drawRect(
                    brush = Brush.verticalGradient(
                        listOf(Color.White.copy(0.12f), Color.Transparent)
                    )
                )
            }
            .background(Color.White.copy(alpha = 0.05f))
            .fillMaxWidth(),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth()
                .clickable { isExpanded = !isExpanded }
        ) {
            Text(
                workout.name,
                modifier = Modifier.weight(1f),
                style = MaterialTheme.typography.bodyLarge.copy(color = Color.White)
            )

            if (isCompleted)
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = "",
                    modifier = Modifier.padding(end = 8.dp),
                    tint = Color(0xFFD4AF37),
                )

            Icon(
                imageVector = if (isExpanded) Icons.Outlined.KeyboardArrowUp else Icons.Outlined.KeyboardArrowDown,
                tint = Color.White,
                contentDescription = null
            )
        }

        AnimatedVisibility(visible = isExpanded) {
            Column(
                modifier = Modifier.fillMaxWidth()
                    .padding(horizontal = 26.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        "sets",
                        modifier = Modifier.padding(start = 8.dp),
                        style = MaterialTheme.typography.bodyLarge.copy(
                            color = MaterialTheme.colorScheme.primary.copy(
                                alpha = 0.6f
                            )
                        )
                    )
                    Text(
                        "weight",
                        style = MaterialTheme.typography.bodyLarge.copy(
                            color = MaterialTheme.colorScheme.primary.copy(
                                alpha = 0.6f
                            )
                        )
                    )
                    Text(
                        "reps",
                        modifier = Modifier.padding(start = 8.dp),
                        style = MaterialTheme.typography.bodyLarge.copy(
                            color = MaterialTheme.colorScheme.primary.copy(
                                alpha = 0.6f
                            )
                        )
                    )
                    Text(
                        "done",
                        style = MaterialTheme.typography.bodyLarge.copy(
                            color = MaterialTheme.colorScheme.primary.copy(
                                alpha = 0.6f
                            )
                        )
                    )
                }
                workout.sets.forEach { set ->
                    WorkoutSetRow(set = set) { updatedSet ->
                        onCompleteSet(updatedSet)
                    }
                }
            }
        }
    }
}

@Composable
fun WorkoutSetRow(
    set: WorkoutSetModel,
    onChanged: (WorkoutSetModel) -> Unit
) {
    var txtWeight by remember { mutableStateOf("${set.weight}") }
    var txtReps by remember { mutableStateOf("${set.reps}") }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            "${set.setNumber}",
            modifier = Modifier.width(48.dp),
            color = Color.Gray.copy(alpha = 0.5f),
            textAlign = TextAlign.Center
        )

        CustomTextField("${set.weight}") { weight ->
            txtWeight = weight
        }

        CustomTextField("${set.reps}") { reps ->
            txtReps = reps
        }

        Checkbox(
            checked = set.isComplete == true,
            modifier = Modifier.size(30.dp),
            onCheckedChange = {
                onChanged(
                    WorkoutSetModel(
                        id = set.id,
                        setNumber = set.setNumber,
                        weight = txtWeight.toInt(),
                        reps = txtReps.toInt(),
                        isComplete = it
                    )
                )
            }
        )
    }
}

@Composable
fun CustomTextField(
    value: String,
    onChange: (String) -> Unit
) {
    var txtValue by remember { mutableStateOf(value) }

    Box(
        modifier = Modifier
            .border(
                width = 1.dp, shape = RoundedCornerShape(10),
                color = Color.Gray.copy(alpha = 0.5f)
            )
            .width(55.dp)
            .height(35.dp)
            .padding(start = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        BasicTextField(
            value = txtValue,
            textStyle = MaterialTheme.typography.bodyLarge.copy(color = Color.White),
            onValueChange = {
                txtValue = it
                onChange(txtValue)
            },
            singleLine = true
        )
    }
}

@Preview
@Composable
fun WorkoutListScreenPreview() {
    LevelUpTheme {
        WorkoutListScreen(
            state = WorkoutListState(
                listOfWorkouts = listOf(
                    WorkoutWithSets(
                        workoutId = "005dd188-1988-4681-ab70-13033115de56",
                        name = "Preacher Curl",
                        category = ExerciseCategory.BICEPS,
                        isCompleted = false,
                        sets = listOf(
                            WorkoutSetModel(
                                id = "set_1",
                                setNumber = 1,
                                weight = 0,
                                reps = 0,
                                isComplete = false
                            ),
                            WorkoutSetModel(
                                id = "set_2",
                                setNumber = 2,
                                weight = 0,
                                reps = 0,
                                isComplete = false
                            )
                        )
                    ),
                    WorkoutWithSets(
                        workoutId = "041544f9-59c8-434e-ac3f-030b8ce1d522",
                        name = "Dumbbell Curl",
                        category = ExerciseCategory.BICEPS,
                        isCompleted = false,
                        sets = listOf(
                            WorkoutSetModel(
                                id = "set_1",
                                setNumber = 1,
                                weight = 0,
                                reps = 0,
                                isComplete = false
                            ),
                            WorkoutSetModel(
                                id = "set_2",
                                setNumber = 2,
                                weight = 0,
                                reps = 0,
                                isComplete = false
                            ),
                            WorkoutSetModel(
                                id = "set_3",
                                setNumber = 3,
                                weight = 0,
                                reps = 0,
                                isComplete = false
                            ),
                            WorkoutSetModel(
                                id = "set_4",
                                setNumber = 4,
                                weight = 0,
                                reps = 0,
                                isComplete = false
                            )
                        )
                    ),
                    WorkoutWithSets(
                        workoutId = "1a9670ce-1221-4c2e-9852-fff610322d9b",
                        name = "Lat Pulldown",
                        category = ExerciseCategory.BACK,
                        isCompleted = false,
                        sets = listOf(
                            WorkoutSetModel(
                                id = "set_1",
                                setNumber = 1,
                                weight = 0,
                                reps = 0,
                                isComplete = false
                            ),
                            WorkoutSetModel(
                                id = "set_2",
                                setNumber = 2,
                                weight = 0,
                                reps = 0,
                                isComplete = false
                            ),
                            WorkoutSetModel(
                                id = "set_3",
                                setNumber = 3,
                                weight = 0,
                                reps = 0,
                                isComplete = false
                            ),
                            WorkoutSetModel(
                                id = "set_4",
                                setNumber = 4,
                                weight = 0,
                                reps = 0,
                                isComplete = false
                            )
                        )
                    )
                )
            ),
            {},
            {}
        )
    }
}