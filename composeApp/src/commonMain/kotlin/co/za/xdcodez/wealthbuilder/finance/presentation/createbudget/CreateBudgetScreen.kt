package co.za.xdcodez.wealthbuilder.finance.presentation.createbudget

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.unit.dp
import co.za.xdcodez.wealthbuilder.navigation.WealthBuilderBaseScreen
import co.za.xdcodez.wealthbuilder.theme.WealthBuilderTheme
import com.binayshaw7777.kotstep.v3.KotStep
import com.binayshaw7777.kotstep.v3.model.step.StepLayoutStyle
import com.binayshaw7777.kotstep.v3.model.style.BorderStyle
import com.binayshaw7777.kotstep.v3.model.style.KotStepStyle
import com.binayshaw7777.kotstep.v3.model.style.LineStyle
import com.binayshaw7777.kotstep.v3.model.style.LineStyles
import com.binayshaw7777.kotstep.v3.model.style.StepStyle
import com.binayshaw7777.kotstep.v3.model.style.StepStyles
import com.binayshaw7777.kotstep.v3.util.ExperimentalKotStep
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.annotation.KoinExperimentalAPI

@OptIn(KoinExperimentalAPI::class)
@Composable
fun CreateBudgetScreenRoute(
    monthId: String,
    createBudgetViewModel: CreateBudgetViewModel = koinViewModel<CreateBudgetViewModel>(),
    onNavigateBack: () -> Unit
) {
    LaunchedEffect(Unit) {
        createBudgetViewModel.init(monthId)
    }

    val state by createBudgetViewModel.state.collectAsState()

    LaunchedEffect(state.isSaved) {
        if (state.isSaved) onNavigateBack()
    }

    CreateBudgetScreen(
        state = state,
        onAction = createBudgetViewModel::onAction,
        onNavigateBack = onNavigateBack
    )
}

@OptIn(ExperimentalKotStep::class)
@Composable
fun CreateBudgetScreen(
    state: CreateBudgetUiState,
    onAction: (CreateBudgetActions) -> Unit,
    onNavigateBack: () -> Unit
) {
    val showNext = when (state.currentStep) {
        1 -> state.isStep1Valid
        2 -> state.isStep2Valid
        else -> true
    }

    var currentStepper by remember { mutableStateOf(state.currentStep.toFloat()) }
    WealthBuilderBaseScreen(
        title = state.budgetPeriod?.let {
            "${
                it.endDate.month.name.take(3).lowercase().replaceFirstChar { c -> c.uppercase() }
            } Budget Setup"
        } ?: "Budget Setup",
        onBackClick = onNavigateBack
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(horizontal = 8.dp)
        ) {

            KotStep(
                modifier = Modifier.fillMaxWidth(),
                currentStep = { state.currentStep.toFloat() },
                style = KotStepStyle(
                    stepLayoutStyle = StepLayoutStyle.Horizontal,
                    stepStyle = StepStyles.default().copy(
                        onTodo = StepStyle(stepSize = 36.dp, stepColor = Color(0xFFE2E8F0)),
                        onCurrent = StepStyle(
                            stepSize = 40.dp,
                            stepColor = Color(0xFF2563EB),
                            borderStyle = BorderStyle(width = 3.dp, color = Color(0xFF93C5FD))
                        ),
                        onDone = StepStyle(stepSize = 36.dp, stepColor = Color(0xFF10B981))
                    ),
                    lineStyle = LineStyles.default().copy(
                        onDone = LineStyle(
                            lineThickness = 4.dp,
                            lineColor = Color(0xFF10B981),
                            lineStrokeCap = StrokeCap.Round
                        ),
                        onCurrent = LineStyle(
                            lineThickness = 4.dp,
                            lineColor = Color(0xFFE2E8F0),
                            progressColor = Color(0xFF2563EB),
                            lineStrokeCap = StrokeCap.Round,
                            progressStrokeCap = StrokeCap.Round
                        )
                    )
                ),
                content = {
                    step(title = "Cart", onClick = { onAction(CreateBudgetActions.NextStep)})
                    step(title = "Shipping", onClick = { onAction(CreateBudgetActions.NextStep)})
                    step(title = "Payment", onClick = { onAction(CreateBudgetActions.NextStep) })
                }
            )
            Text(
                text = "Step ${state.currentStep} of 3",
                style = MaterialTheme.typography.labelLarge,
                color = Color(0x99FFFFFF),
                modifier = Modifier.padding(bottom = 8.dp)
            )

            when (state.currentStep) {
                1 -> IncomeSourceContent(state, onAction, Modifier.weight(1f))
                2 -> CategoryAllocationContent(state, onAction, Modifier.weight(1f))
                3 -> ReviewBudgetContent(state, Modifier.weight(1f))
            }

            Row(
                modifier = Modifier
                    .padding(bottom = 12.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                if (state.currentStep > 1) {
                    Row(
                        modifier = Modifier.clickable { onAction(CreateBudgetActions.PreviousStep) },
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Default.KeyboardArrowLeft,
                            contentDescription = "",
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = "Back",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                } else {
                    Spacer(modifier = Modifier.weight(1f))
                }

                if (showNext) {
                    Row(
                        modifier = Modifier.clickable {
                            if (state.currentStep == 3) onAction(CreateBudgetActions.SaveBudget)
                            else onAction(CreateBudgetActions.NextStep)
                        },
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (state.currentStep == 3) "Save Budget" else "Next",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Icon(
                            imageVector = Icons.AutoMirrored.Default.KeyboardArrowRight,
                            contentDescription = "",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                } else {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

@Preview
@Composable
fun CreateBudgetScreenPreview() {
    WealthBuilderTheme {
        CreateBudgetScreen(
            state = CreateBudgetUiState(),
            onAction = {},
            onNavigateBack = {}
        )
    }
}