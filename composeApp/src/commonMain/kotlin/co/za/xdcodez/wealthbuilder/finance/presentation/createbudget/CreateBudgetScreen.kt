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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import co.za.xdcodez.wealthbuilder.common.widgets.CustomAppTopBar
import co.za.xdcodez.wealthbuilder.theme.WealthBuilderTheme
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

    CustomAppTopBar(
        title = state.budgetPeriod?.displayTitle ?: "",
        modifier = Modifier.padding(horizontal = 12.dp),
        showNavigationIcon = true,
        onNavigate = onNavigateBack
    ) {
        Column(modifier = Modifier.fillMaxSize().padding(horizontal = 8.dp)) {
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