package co.za.xdcodes.level_up.workout.presentation.create

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import co.za.xdcodes.level_up.common.widgets.CustomAppTopBar
import co.za.xdcodes.level_up.theme.LevelUpTheme
import co.za.xdcodes.level_up.workout.domain.dto.CreateWorkoutStep
import co.za.xdcodes.level_up.workout.presentation.WorkoutListViewModel
import kotlinx.coroutines.launch
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.annotation.KoinExperimentalAPI

@OptIn(KoinExperimentalAPI::class)
@Composable
fun CreateWorkoutScreenRoot(
    viewModel: CreateWorkoutViewModel = koinViewModel<CreateWorkoutViewModel>(),
    onNavigate: () -> Unit
) {
    CreateWorkoutScreen(onNavigate = onNavigate, onCreateWorkout = { viewModel.createWorkout() })
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CreateWorkoutScreen(
    onCreateWorkout: () -> Unit,
    onNavigate: () -> Unit
) {
    CustomAppTopBar(
        "Create Routine",
        modifier = Modifier.fillMaxWidth().padding(0.dp),
        showNavigationIcon = true,
        onNavigate = onNavigate
    ) {

        val steps = CreateWorkoutStep.entries
        val pagerState = rememberPagerState(pageCount = { steps.size })
        val scope = rememberCoroutineScope()

        Column(modifier = Modifier.fillMaxSize()) {
            StepIndicator(
                steps = steps,
                currentStep = pagerState.currentPage
            )

            HorizontalPager(
                state = pagerState,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                userScrollEnabled = false // IMPORTANT
            ) { page ->
                when (steps[page]) {
                    CreateWorkoutStep.CATEGORY -> CategoryStep()
                    CreateWorkoutStep.EXERCISES -> ExerciseStep()
                    CreateWorkoutStep.SETS -> SetsStep()
                    CreateWorkoutStep.DATE -> DateStep()
                    CreateWorkoutStep.REVIEW -> ReviewStep()
                }
            }

            StepNavigation(
                currentStep = pagerState.currentPage,
                totalSteps = steps.size,
                onBack = {
                    scope.launch {
                        pagerState.animateScrollToPage(pagerState.currentPage - 1)
                    }
                },
                onNext = {
                    if (pagerState.currentPage == steps.size - 1) {
                        onCreateWorkout()
                    } else {
                        scope.launch {
                            pagerState.animateScrollToPage(pagerState.currentPage + 1)
                        }
                    }
                }
            )
        }
    }
}

@Composable
fun StepIndicator(
    steps: List<CreateWorkoutStep>,
    currentStep: Int
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        steps.forEachIndexed { index, step ->
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(CircleShape)
                        .background(
                            if (index <= currentStep)
                                MaterialTheme.colorScheme.primary
                            else
                                Color.Gray.copy(alpha = 0.3f)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "${index + 1}",
                        color = Color.White,
                        style = MaterialTheme.typography.labelMedium
                    )
                }

                Text(
                    text = step.title,
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.White.copy(alpha = 0.7f),
                    maxLines = 1
                )
            }
        }
    }
}

@Composable
fun StepNavigation(
    currentStep: Int,
    totalSteps: Int,
    onBack: () -> Unit,
    onNext: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        if (currentStep > 0) {
            TextButton(onClick = onBack) {
                Text("Back")
            }
        } else {
            Spacer(modifier = Modifier.width(64.dp))
        }

        Button(
            onClick = onNext,
        ) {
            Text(
                if (currentStep == totalSteps - 1) "Create"
                else "Next"
            )
        }
    }
}

@Preview
@Composable
fun CreateWorkoutScreenPreview() {
    LevelUpTheme {
        CreateWorkoutScreen({}) { }
    }
}