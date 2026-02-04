package co.za.xdcodes.level_up.finance.presentation.budget

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import co.za.xdcodes.level_up.common.widgets.SwipeListItem
import co.za.xdcodes.level_up.common.formatCurrency
import co.za.xdcodes.level_up.common.widgets.CustomAppTopBar
import co.za.xdcodes.level_up.common.widgets.SwipeActionBtn
import co.za.xdcodes.level_up.finance.domain.dto.CategoryTransaction
import co.za.xdcodes.level_up.finance.presentation.composables.BudgetCategoryCardComposable
import co.za.xdcodes.level_up.theme.LevelUpTheme
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn
import level_up.composeapp.generated.resources.Res
import level_up.composeapp.generated.resources.pay
import org.jetbrains.compose.resources.vectorResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.annotation.KoinExperimentalAPI
import kotlin.text.contains
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(KoinExperimentalAPI::class)
@Composable
fun BudgetTransactionScreenRoute(
    viewModel: BudgetScreenViewModel = koinViewModel<BudgetScreenViewModel>(),
    onNavigate: () -> Unit
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.getTransactions()
    }

    CustomAppTopBar(
        title = "transactions",
        showNavigationIcon = true,
        onNavigate = { onNavigate() }
    ) {
        BudgetTransactionScreen(state) {
            viewModel.onAction(it)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalUuidApi::class)
@Composable
fun AddTransactionBottomSheet(
    onAddTransaction: (CategoryTransaction) -> Unit
) {
    var transactionName by remember { mutableStateOf("") }
    var transactionAmount by remember { mutableStateOf("") }

    val timeZone = TimeZone.currentSystemDefault()
    val currentDate = Clock.System.todayIn(timeZone).toString()

    Column(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Add Transaction",
            style = MaterialTheme.typography.titleLarge,
            color = Color.White,
            modifier = Modifier.padding(vertical = 12.dp)
        )

        OutlinedTextField(
            value = transactionName,
            textStyle = TextStyle(color = Color.White),
            onValueChange = { transactionName = it },
            label = { Text("Transaction") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        OutlinedTextField(
            value = transactionAmount,
            textStyle = TextStyle(color = Color.White),
            onValueChange = { transactionAmount = it },
            label = { Text("Amount") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Add Button
        Button(
            onClick = {
                onAddTransaction(
                    CategoryTransaction(
                        id = Uuid.random().toString(),
                        name = transactionName,
                        amount = transactionAmount.toDouble(),
                        category = "",
                        date = currentDate,
                        month = "",
                        isPaid = false
                    )
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("Add Transaction")
        }

        Spacer(modifier = Modifier.height(8.dp))
    }
}

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun BudgetTransactionScreen(
    state: BudgetScreenState,
    onAction: (BudgetScreenActions) -> Unit
) {
    val category = state.budgetCategories.firstOrNull { category ->
        category.category.contains(
            state.selectedCategory.orEmpty()
        )
    }
    val transactions = state.categoryTransactions

    val scope = rememberCoroutineScope()
    val pagerState = rememberPagerState(pageCount = { 2 })
    val selectedTabIndex by remember {
        derivedStateOf { pagerState.currentPage }
    }

    Column(
        modifier = Modifier.padding(vertical = 8.dp, horizontal = 16.dp).fillMaxSize()
    ) {
        category?.let {
            BudgetCategoryCardComposable(category)
        }

        TabRow(
            modifier = Modifier.padding(horizontal = 8.dp),
            selectedTabIndex = selectedTabIndex,
            containerColor = Color.Transparent
        ) {
            Tab(
                modifier = Modifier.fillMaxWidth(),
                selected = selectedTabIndex == 0,
                onClick = {
                    scope.launch {
                        pagerState.animateScrollToPage(0)
                    }
                },
                text = { Text("NOT PAID", style = TextStyle(color = Color.White)) }

            )
            Tab(
                modifier = Modifier.fillMaxWidth(),
                selected = selectedTabIndex == 1,
                onClick = {
                    scope.launch {
                        pagerState.animateScrollToPage(1)
                    }
                },
                text = { Text("PAID", style = TextStyle(color = Color.White)) }

            )
        }

        HorizontalPager(
            verticalAlignment = Alignment.Top,
            modifier = Modifier.weight(1f),
            state = pagerState,
        ) { page ->
            when (page) {
                0 -> {
                    val unpaidTransactions =
                        transactions.filter { transaction -> !transaction.isPaid }
                    LazyColumn(
                        verticalArrangement = Arrangement.Top,
                        modifier = Modifier
                            .padding(top = 4.dp)
                    ) {
                        items(count = unpaidTransactions.size) { index ->
                            val transaction = unpaidTransactions[index]
                            SwipeListItem(
                                primaryText = transaction.name,
                                secondaryText = formatCurrency(transaction.amount),
                                listOfActions = {

                                    Row(
                                        modifier = Modifier.fillMaxSize(),
                                        horizontalArrangement = Arrangement.End,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        SwipeActionBtn(
                                            icon = vectorResource(Res.drawable.pay),
                                            backgroundColor = Color.Green.copy(alpha = 0.6f ),
                                            color = Color.White
                                        ) {
                                            onAction(
                                                BudgetScreenActions.OnPayTransaction(
                                                    transaction.id
                                                )
                                            )
                                        }
                                        SwipeActionBtn(
                                            icon = vectorResource(Res.drawable.pay),
                                            backgroundColor = Color.Green.copy(alpha = 0.6f ),
                                            color = Color.White
                                        ) {
                                            onAction(
                                                BudgetScreenActions.OnPayTransaction(
                                                    transaction.id
                                                )
                                            )
                                        }
                                    }
                                },
                                isComplete = false
                            )
                        }
                    }
                }

                1 -> {
                    val paidTransactions =
                        transactions.filter { transaction -> transaction.isPaid }
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier
                            .padding(top = 4.dp)
                    ) {
                        items(count = paidTransactions.size) { index ->
                            val transaction = paidTransactions[index]
                            SwipeListItem(
                                primaryText = transaction.name,
                                secondaryText = formatCurrency(transaction.amount),
                                listOfActions = {
                                    Row(
                                        modifier = Modifier.fillMaxSize(),
                                        horizontalArrangement = Arrangement.End,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        SwipeActionBtn(
                                            icon = vectorResource(Res.drawable.pay),
                                            backgroundColor = Color.Green.copy(alpha = 0.6f ),
                                            color = Color.Green
                                        ) {

                                        }
                                    }
                                },
                                isComplete = true
                            )
                        }
                    }

                }
            }
        }

        FloatingActionButton(
            modifier = Modifier.align(Alignment.End),
            onClick = {
                onAction(BudgetScreenActions.OnShowAddTransactionBottomSheet(true))
            }, content = {

            })

        if (state.showAddTransactionBottomSheet)
            ModalBottomSheet(
                dragHandle = { BottomSheetDefaults.DragHandle() },
                onDismissRequest = {
                    onAction(
                        BudgetScreenActions.OnShowAddTransactionBottomSheet(
                            false
                        )
                    )
                },
            ) {
                AddTransactionBottomSheet { transaction ->
                    onAction(BudgetScreenActions.OnAddTransaction(transaction))
                }
            }
    }
}

@Preview
@Composable
fun BudgetTransactionScreenPreview() {
    LevelUpTheme {
        BudgetTransactionScreen(
            BudgetScreenState(
                selectedCategory = "DAY-2-DAY"
            )
        ) { }
    }
}